package com.gempukku.swccgo.async.handler;

import com.gempukku.polling.LongPollingResource;
import com.gempukku.polling.LongPollingSystem;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.ParticipantCommunicationVisitor;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.state.EventSerializer;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private SwccgoServer _swccgoServer;
    private Set<Phase> _autoPassDefault = new HashSet<Phase>();
    private LongPollingSystem _longPollingSystem;

    public GameRequestHandler(Map<Type, Object> context) {
        super(context);
        _swccgoServer = extractObject(context, SwccgoServer.class);
        _longPollingSystem = extractObject(context, LongPollingSystem.class);

        _autoPassDefault.add(Phase.ACTIVATE);
        _autoPassDefault.add(Phase.CONTROL);
        _autoPassDefault.add(Phase.DEPLOY);
        _autoPassDefault.add(Phase.BATTLE);
        _autoPassDefault.add(Phase.MOVE);
        _autoPassDefault.add(Phase.DRAW);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, MessageEvent e) throws Exception {
        if (uri.startsWith("/") && uri.endsWith("/cardInfo") && request.getMethod() == HttpMethod.GET) {
            getCardInfo(request, uri.substring(1, uri.length() - 9), responseWriter);
        }
        else if (uri.startsWith("/") && uri.endsWith("/concede") && request.getMethod() == HttpMethod.POST) {
            concede(request, uri.substring(1, uri.length() - 8), responseWriter);
        }
        else if (uri.startsWith("/") && uri.endsWith("/cancel") && request.getMethod() == HttpMethod.POST) {
            cancel(request, uri.substring(1, uri.length() - 7), responseWriter);
        }
        else if (uri.startsWith("/") && uri.endsWith("/extendGameTimer") && request.getMethod() == HttpMethod.POST) {
            extendGameTimer(request, uri.substring(1, uri.length() - 16), responseWriter);
        }
        else if (uri.startsWith("/") && uri.endsWith("/disableActionTimer") && request.getMethod() == HttpMethod.POST) {
            disableActionTimer(request, uri.substring(1, uri.length() - 19), responseWriter);
        }
        else if (uri.startsWith("/") && request.getMethod() == HttpMethod.GET) {
            getGameState(request, uri.substring(1), responseWriter);
        }
        else if (uri.startsWith("/") && request.getMethod() == HttpMethod.POST) {
            updateGameState(request, uri.substring(1), responseWriter);
        }
        else {
            responseWriter.writeError(404);
        }
    }

    private void updateGameState(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        int channelNumber = Integer.parseInt(getFormParameterSafely(postDecoder, "channelNumber"));
        Integer decisionId = null;
        String decisionIdStr = getFormParameterSafely(postDecoder, "decisionId");
        if (decisionIdStr != null)
            decisionId = Integer.parseInt(decisionIdStr);
        String decisionValue = getFormParameterSafely(postDecoder, "decisionValue");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.setPlayerAutoPassSettings(resourceOwner.getName(), getAutoPassPhases(request));

        try {
            if (decisionId != null)
                gameMediator.playerAnswered(resourceOwner, channelNumber, decisionId, decisionValue);

            GameCommunicationChannel pollableResource = gameMediator.getCommunicationChannel(resourceOwner, channelNumber);
            GameUpdateLongPollingResource pollingResource = new GameUpdateLongPollingResource(pollableResource, channelNumber, gameMediator, resourceOwner, responseWriter);
            _longPollingSystem.processLongPollingResource(pollingResource, pollableResource);
        } catch (SubscriptionConflictException exp) {
            responseWriter.writeError(409);
        } catch (PrivateInformationException e) {
            responseWriter.writeError(403);
        } catch (SubscriptionExpiredException e) {
            responseWriter.writeError(410);
        }
    }

    private class GameUpdateLongPollingResource implements LongPollingResource {
        private GameCommunicationChannel _gameCommunicationChannel;
        private SwccgGameMediator _gameMediator;
        private Player _resourceOwner;
        private int _channelNumber;
        private ResponseWriter _responseWriter;
        private boolean _processed;

        private GameUpdateLongPollingResource(GameCommunicationChannel gameCommunicationChannel, int channelNumber, SwccgGameMediator gameMediator, Player resourceOwner, ResponseWriter responseWriter) {
            _gameCommunicationChannel = gameCommunicationChannel;
            _channelNumber = channelNumber;
            _gameMediator = gameMediator;
            _resourceOwner = resourceOwner;
            _responseWriter = responseWriter;
        }

        @Override
        public synchronized boolean wasProcessed() {
            return _processed;
        }

        @Override
        public synchronized void processIfNotProcessed() {
            if (!_processed) {
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    Document doc = documentBuilder.newDocument();
                    Element update = doc.createElement("update");

                    _gameMediator.processVisitor(_gameCommunicationChannel, _channelNumber, _resourceOwner.getName(), new SerializationVisitor(doc, update));

                    doc.appendChild(update);

                    _responseWriter.writeXmlResponse(doc);
                } catch (Exception e) {
                    _responseWriter.writeError(500);
                }
                _processed = true;
            }
        }

    }

    private void cancel(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.cancel(resourceOwner);

        responseWriter.writeXmlResponse(null);
    }

    private void concede(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.concede(resourceOwner);

        responseWriter.writeXmlResponse(null);
    }

    private void extendGameTimer(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        String minutesStr = getFormParameterSafely(postDecoder, "minutesToExtend");
        int minutes = Integer.parseInt(minutesStr);
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.extendGameTimer(resourceOwner, minutes);

        responseWriter.writeXmlResponse(null);
    }

    private void disableActionTimer(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        HttpPostRequestDecoder postDecoder = new HttpPostRequestDecoder(request);
        String participantId = getFormParameterSafely(postDecoder, "participantId");
        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.disableActionTimer(resourceOwner);

        responseWriter.writeXmlResponse(null);
    }

    private void getCardInfo(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");
        String cardIdStr = getQueryParameterSafely(queryDecoder, "cardId");
        if (cardIdStr.startsWith("extra")) {
            responseWriter.writeHtmlResponse("");
        } else {
            int cardId = Integer.parseInt(cardIdStr);

            Player resourceOwner = getResourceOwnerSafely(request, participantId);

            SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);
            if (gameMediator == null)
                throw new HttpProcessingException(404);

            responseWriter.writeHtmlResponse(gameMediator.produceCardInfo(resourceOwner, cardId));
        }
    }

    private void getGameState(HttpRequest request, String gameId, ResponseWriter responseWriter) throws Exception {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(request.getUri());
        String participantId = getQueryParameterSafely(queryDecoder, "participantId");

        Player resourceOwner = getResourceOwnerSafely(request, participantId);

        SwccgGameMediator gameMediator = _swccgoServer.getGameById(gameId);

        if (gameMediator == null)
            throw new HttpProcessingException(404);

        gameMediator.setPlayerAutoPassSettings(resourceOwner.getName(), getAutoPassPhases(request));

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.newDocument();
        Element gameState = doc.createElement("gameState");

        try {
            gameMediator.signupUserForGame(resourceOwner, new SerializationVisitor(doc, gameState));
        } catch (PrivateInformationException e) {
            throw new HttpProcessingException(403);
        }

        doc.appendChild(gameState);

        responseWriter.writeXmlResponse(doc);
    }

    private Set<Phase> getAutoPassPhases(HttpRequest request) {
        CookieDecoder cookieDecoder = new CookieDecoder();
        String cookieHeader = request.getHeader(HttpHeaders.Names.COOKIE);
        if (cookieHeader != null) {
            Set<Cookie> cookies = cookieDecoder.decode(cookieHeader);
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("autoPassPhases")) {
                    final String[] phases = cookie.getValue().split("0");
                    Set<Phase> result = new HashSet<Phase>();
                    for (String phase : phases)
                        result.add(Phase.valueOf(phase));
                    return result;
                }
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("autoPass") && cookie.getValue().equals("false"))
                    return Collections.emptySet();
            }
        }
        return _autoPassDefault;
    }

    private class SerializationVisitor implements ParticipantCommunicationVisitor {
        private Document _doc;
        private Element _element;
        private EventSerializer _eventSerializer = new EventSerializer();

        private SerializationVisitor(Document doc, Element element) {
            _doc = doc;
            _element = element;
        }

        @Override
        public void visitChannelNumber(int channelNumber) {
            _element.setAttribute("cn", String.valueOf(channelNumber));
        }

        @Override
        public void visitGameEvent(GameEvent gameEvent) {
            _element.appendChild(_eventSerializer.serializeEvent(_doc, gameEvent));
        }

        @Override
        public void visitClock(Map<String, Integer> secondsLeft) {
            _element.appendChild(serializeClocks(_doc, secondsLeft));
        }
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
}
