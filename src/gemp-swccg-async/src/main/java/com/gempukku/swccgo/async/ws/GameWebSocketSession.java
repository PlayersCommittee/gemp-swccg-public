package com.gempukku.swccgo.async.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.polling.WaitingRequest;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.ParticipantCommunicationVisitor;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.state.EventSerializer;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameWebSocketSession extends AbstractWebSocketSession {
    private final SwccgoServer _swccgoServer;
    private final PlayerDAO _playerDao;
    private final Player _viewer;
    private final String _gameId;
    private final String _participantId;
    private final Integer _requestedChannelNumber;
    private final EventSerializer _eventSerializer = new EventSerializer();
    private static final Set<Phase> AUTO_PASS_DEFAULT = Collections.unmodifiableSet(EnumSet.of(
            Phase.ACTIVATE, Phase.CONTROL, Phase.DEPLOY, Phase.BATTLE, Phase.MOVE, Phase.DRAW
    ));
    private final AtomicBoolean _closed = new AtomicBoolean(false);
    private final AtomicBoolean _sending = new AtomicBoolean(false);
    private final long _tokenExpiresAtMs;

    private SwccgGameMediator _gameMediator;
    private GameCommunicationChannel _gameChannel;
    private int _channelNumber = -1;
    private Player _resourceOwner;
    private ScheduledFuture<?> _tickTask;
    private ScheduledFuture<?> _expiryTimer;
    private final WaitingRequest _waitingRequest = new GameWaitingRequest();

    public GameWebSocketSession(ChannelHandlerContext ctx, SwccgoServer swccgoServer, PlayerDAO playerDao, Player viewer, String gameId, String participantId, Integer channelNumber, long tokenExpiresAt) {
        super(ctx);
        _swccgoServer = swccgoServer;
        _playerDao = playerDao;
        _viewer = viewer;
        _gameId = gameId;
        _participantId = participantId;
        _requestedChannelNumber = channelNumber;
        _tokenExpiresAtMs = tokenExpiresAt > 0 ? tokenExpiresAt * 1000L : 0L;
    }

    @Override
    public void onOpen() {
        _gameMediator = _swccgoServer.getGameById(_gameId);
        if (_gameMediator == null || _gameMediator.isDestroyed()) {
            sendError("Game not found.", 404);
            _ctx.close();
            return;
        }

        _resourceOwner = resolveResourceOwner();
        if (_resourceOwner == null) {
            sendError("Player not found.", 401);
            _ctx.close();
            return;
        }

        boolean needsSignup = _requestedChannelNumber == null;
        if (!needsSignup) {
            try {
                _channelNumber = _requestedChannelNumber.intValue();
                _gameChannel = _gameMediator.getCommunicationChannel(_resourceOwner, _channelNumber);
            } catch (PrivateInformationException exp) {
                sendError("Access denied.", 403);
                _ctx.close();
                return;
            } catch (SubscriptionConflictException exp) {
                sendError("Channel conflict.", 409);
                _ctx.close();
                return;
            } catch (SubscriptionExpiredException exp) {
                needsSignup = true;
            }
        }

        Document snapshotDoc;
        XmlGameVisitor visitor;
        try {
            snapshotDoc = createGameDocument(needsSignup ? "gameState" : "update");
            visitor = new XmlGameVisitor(snapshotDoc, snapshotDoc.getDocumentElement());
            if (_channelNumber >= 0) {
                snapshotDoc.getDocumentElement().setAttribute("cn", String.valueOf(_channelNumber));
            }
        } catch (Exception exp) {
            sendError("Failed to initialize game session.", 500);
            _ctx.close();
            return;
        }

        if (needsSignup) {
            try {
                _gameMediator.signupUserForGame(_resourceOwner, visitor);
            } catch (PrivateInformationException exp) {
                sendError("Access denied.", 403);
                _ctx.close();
                return;
            }
            _channelNumber = visitor.getChannelNumber();
            try {
                _gameChannel = _gameMediator.getCommunicationChannel(_resourceOwner, _channelNumber);
            } catch (PrivateInformationException exp) {
                sendError("Access denied.", 403);
                _ctx.close();
                return;
            } catch (SubscriptionConflictException exp) {
                sendError("Channel conflict.", 409);
                _ctx.close();
                return;
            } catch (SubscriptionExpiredException exp) {
                sendError("Subscription expired.", 410);
                _ctx.close();
                return;
            }
        } else {
            _gameMediator.processVisitor(_gameChannel, _channelNumber, _resourceOwner.getName(), visitor);
        }

        sendSnapshot(snapshotDoc);
        registerForUpdates();
        startTicker();
        scheduleTokenExpiry();
    }

    @Override
    public void onClose() {
        if (_closed.compareAndSet(false, true)) {
            stopTicker();
            stopExpiryTimer();
            if (_gameChannel != null) {
                _gameChannel.unregisterRequest(_waitingRequest);
            }
        }
    }

    @Override
    public void onTextMessage(String message) {
        JSONObject payload;
        try {
            payload = JSON.parseObject(message);
        } catch (Exception exp) {
            return;
        }
        if (payload == null) {
            return;
        }

        String type = payload.getString("type");
        if (!"decision".equals(type)) {
            return;
        }
        handleDecision(payload);
    }

    private Player resolveResourceOwner() {
        if (_participantId != null && !_participantId.isEmpty() && _viewer.hasType(Player.Type.ADMIN)) {
            return _playerDao.getPlayer(_participantId);
        }
        return _viewer;
    }

    private void handleDecision(JSONObject payload) {
        Integer decisionId = payload.getInteger("decisionId");
        String decisionValue = payload.getString("decisionValue");
        Integer channelNumber = payload.getInteger("channelNumber");

        if (decisionId == null) {
            sendError("Missing decisionId.", 400);
            return;
        }
        if (decisionValue == null) {
            sendError("Missing decisionValue.", 400);
            return;
        }

        int channel = channelNumber != null ? channelNumber : _channelNumber;
        if (channel < 0) {
            sendError("Missing channelNumber.", 400);
            return;
        }

        Set<Phase> autoPassPhases = resolveAutoPassPhases(payload);
        _gameMediator.setPlayerAutoPassSettings(_resourceOwner.getName(), autoPassPhases);

        try {
            _gameMediator.playerAnswered(_resourceOwner, channel, decisionId, decisionValue);
        } catch (SubscriptionConflictException exp) {
            sendError("Channel conflict.", 409);
            return;
        } catch (SubscriptionExpiredException exp) {
            sendError("Subscription expired.", 410);
            return;
        }

        sendAck("decision");
        requestUpdate();
    }

    private Set<Phase> resolveAutoPassPhases(JSONObject payload) {
        if (payload == null) {
            return AUTO_PASS_DEFAULT;
        }

        Boolean enabled = payload.getBoolean("autoPassEnabled");
        if (enabled != null && !enabled) {
            return Collections.emptySet();
        }

        Object rawPhases = payload.get("autoPassPhases");
        if (rawPhases instanceof Iterable) {
            Set<Phase> phases = new HashSet<Phase>();
            for (Object phaseObj : (Iterable<?>) rawPhases) {
                if (phaseObj == null) {
                    continue;
                }
                try {
                    phases.add(Phase.valueOf(phaseObj.toString()));
                } catch (IllegalArgumentException ignored) {
                    sendError("Invalid autoPassPhases.", 400);
                    return AUTO_PASS_DEFAULT;
                }
            }
            return phases;
        }

        return AUTO_PASS_DEFAULT;
    }

    private void sendSnapshot(Document snapshotDoc) {
        sendXmlDocument(snapshotDoc);
    }

    private void requestUpdate() {
        if (_closed.get() || _gameMediator == null || _gameChannel == null) {
            return;
        }
        if (_gameMediator.isDestroyed()) {
            closeForRemovedGame();
            return;
        }
        if (!_sending.compareAndSet(false, true)) {
            return;
        }
        try {
            _gameChannel.unregisterRequest(_waitingRequest);
            Document updateDoc = createGameDocument("update");
            updateDoc.getDocumentElement().setAttribute("cn", String.valueOf(_channelNumber));
            XmlGameVisitor visitor = new XmlGameVisitor(updateDoc, updateDoc.getDocumentElement());
            _gameMediator.processVisitor(_gameChannel, _channelNumber, _resourceOwner.getName(), visitor);
            sendXmlDocument(updateDoc);
        } catch (Exception exp) {
            sendError("Failed to process game update.", 500);
        } finally {
            _sending.set(false);
            registerForUpdates();
        }
    }

    private void registerForUpdates() {
        if (_closed.get() || _gameChannel == null) {
            return;
        }
        if (_gameChannel.registerRequest(_waitingRequest)) {
            requestUpdate();
        }
    }

    private void startTicker() {
        stopTicker();
        // Safety net: poll the game channel periodically so clients still progress
        // if a channel wake-up is missed by infrastructure/proxies.
        _tickTask = _ctx.executor().scheduleAtFixedRate(() -> {
            if (_closed.get()) {
                return;
            }
            requestUpdate();
        }, 2, 2, TimeUnit.SECONDS);
    }

    private void stopTicker() {
        if (_tickTask != null) {
            _tickTask.cancel(false);
            _tickTask = null;
        }
    }

    private void scheduleTokenExpiry() {
        if (_tokenExpiresAtMs <= 0) {
            return;
        }
        long delay = _tokenExpiresAtMs - System.currentTimeMillis();
        if (delay <= 0) {
            closeForTokenExpiry();
            return;
        }
        _expiryTimer = _ctx.executor().schedule(this::closeForTokenExpiry, delay, TimeUnit.MILLISECONDS);
    }

    private void stopExpiryTimer() {
        if (_expiryTimer != null) {
            _expiryTimer.cancel(false);
            _expiryTimer = null;
        }
    }

    private void closeForTokenExpiry() {
        if (_closed.get()) {
            return;
        }
        onClose();
        closeWithReason(4401, "token expired");
    }

    private void closeForRemovedGame() {
        if (_closed.get()) {
            return;
        }
        onClose();
        closeWithReason(4404, "game removed");
    }

    private Document createGameDocument(String rootName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element root = doc.createElement(rootName);
        doc.appendChild(root);
        return doc;
    }

    private Node serializeClocks(Document doc, Map<String, Integer> secondsLeft) {
        Element clocks = doc.createElement("clocks");
        for (Map.Entry<String, Integer> userClock : secondsLeft.entrySet()) {
            Element clock = doc.createElement("clock");
            clock.setAttribute("participantId", userClock.getKey());
            clock.appendChild(doc.createTextNode(userClock.getValue().toString()));
            clocks.appendChild(clock);
        }
        return clocks;
    }

    private void sendXmlDocument(Document document) {
        final String xml;
        try {
            xml = serializeXml(document);
        } catch (Exception exp) {
            sendError("Failed to serialize game update.", 500);
            return;
        }
        sendText(xml);
    }

    private String serializeXml(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private void sendAck(String action) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("action", action);
        sendEvent("ack", payload);
    }

    private void sendError(String message, Integer status) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("message", message);
        if (status != null) {
            payload.put("status", status);
        }
        sendEvent("error", payload);
    }

    private void sendEvent(String event, Map<String, Object> payload) {
        Map<String, Object> message = new LinkedHashMap<String, Object>();
        message.put("type", "game");
        message.put("event", event);
        if (_gameId != null) {
            message.put("gameId", _gameId);
        }
        if (_viewer != null) {
            message.put("viewerId", _viewer.getName());
        }
        if (_resourceOwner != null) {
            message.put("recipientId", _resourceOwner.getName());
        }
        if (_channelNumber >= 0) {
            message.put("channelNumber", _channelNumber);
        }
        if (payload != null) {
            message.putAll(payload);
        }
        sendJson(message);
    }

    private class XmlGameVisitor implements ParticipantCommunicationVisitor {
        private final Document _doc;
        private final Element _root;
        private int _channelNumber = -1;

        private XmlGameVisitor(Document doc, Element root) {
            _doc = doc;
            _root = root;
        }

        @Override
        public void visitChannelNumber(int channelNumber) {
            _channelNumber = channelNumber;
            _root.setAttribute("cn", String.valueOf(channelNumber));
        }

        @Override
        public void visitGameEvent(GameEvent gameEvent) {
            _root.appendChild(_eventSerializer.serializeEvent(_doc, gameEvent));
        }

        @Override
        public void visitClock(Map<String, Integer> secondsLeft) {
            _root.appendChild(serializeClocks(_doc, secondsLeft));
        }

        private int getChannelNumber() {
            return _channelNumber;
        }
    }

    private class GameWaitingRequest implements WaitingRequest {
        @Override
        public void processRequest() {
            requestUpdate();
        }
    }
}
