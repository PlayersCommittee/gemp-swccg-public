package com.gempukku.swccgo.async.ws;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.hall.HallChannelVisitor;
import com.gempukku.swccgo.hall.HallCommunicationChannel;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.hall.HallUpdateListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HallWebSocketSession extends AbstractWebSocketSession implements HallUpdateListener, HallChannelVisitor {
    private final HallServer _hallServer;
    private final Player _player;
    private final HallCommunicationChannel _hallChannel;
    private final AtomicBoolean _closed = new AtomicBoolean(false);
    private final long _tokenExpiresAtMs;
    private ScheduledFuture<?> _expiryTimer;
    private ScheduledFuture<?> _keepAliveTimer;
    private ScheduledFuture<?> _refreshTimer;
    private ScheduledFuture<?> _serverTimeTimer;

    public HallWebSocketSession(ChannelHandlerContext ctx, HallServer hallServer, Player player, long tokenExpiresAt) {
        super(ctx);
        _hallServer = hallServer;
        _player = player;
        _hallChannel = new WebSocketHallCommunicationChannel();
        _tokenExpiresAtMs = tokenExpiresAt > 0 ? tokenExpiresAt * 1000L : 0L;
    }

    @Override
    public void onOpen() {
        _hallServer.signupUserForHall(_player, _hallChannel, this);
        _hallServer.addHallUpdateListener(this);
        startPeriodicRefresh();
        startServerTimeTicker();
        startKeepAlive();
        scheduleTokenExpiry();
    }

    @Override
    public void onClose() {
        if (_closed.compareAndSet(false, true)) {
            stopExpiryTimer();
            stopKeepAlive();
            stopPeriodicRefresh();
            stopServerTimeTicker();
            _hallServer.removeHallUpdateListener(this);
        }
    }

    @Override
    public void onTextMessage(String message) {
    }

    @Override
    public void hallChanged() {
        sendHallUpdate();
    }

    private void sendHallUpdate() {
        if (_closed.get())
            return;
        synchronized (_sendLock) {
            _hallChannel.processCommunicationChannel(_hallServer, _player, this);
        }
    }

    private void sendEvent(String event, Map<String, Object> payload) {
        Map<String, Object> message = new LinkedHashMap<String, Object>();
        message.put("type", "hall");
        message.put("event", event);
        if (payload != null)
            message.putAll(payload);
        sendJson(message);
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

    private void startKeepAlive() {
        if (_keepAliveTimer != null) {
            _keepAliveTimer.cancel(false);
        }
        // Keep websocket alive through reverse proxies that enforce idle timeouts.
        _keepAliveTimer = _ctx.executor().scheduleAtFixedRate(() -> {
            if (_closed.get()) {
                return;
            }
            if (_ctx.channel().isActive()) {
                _ctx.writeAndFlush(new PingWebSocketFrame());
            }
        }, 25, 25, TimeUnit.SECONDS);
    }

    private void startPeriodicRefresh() {
        stopPeriodicRefresh();
        _refreshTimer = _ctx.executor().scheduleAtFixedRate(() -> {
            if (_closed.get()) {
                return;
            }
            sendHallUpdate();
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void stopKeepAlive() {
        if (_keepAliveTimer != null) {
            _keepAliveTimer.cancel(false);
            _keepAliveTimer = null;
        }
    }

    private void stopPeriodicRefresh() {
        if (_refreshTimer != null) {
            _refreshTimer.cancel(false);
            _refreshTimer = null;
        }
    }

    private void startServerTimeTicker() {
        stopServerTimeTicker();
        _serverTimeTimer = _ctx.executor().scheduleAtFixedRate(() -> {
            if (_closed.get()) {
                return;
            }
            serverTime(DateUtils.getStringDateWithHour());
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void stopServerTimeTicker() {
        if (_serverTimeTimer != null) {
            _serverTimeTimer.cancel(false);
            _serverTimeTimer = null;
        }
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

    private Map<String, Object> buildEntry(String id, Map<String, String> props) {
        Map<String, Object> entry = new LinkedHashMap<String, Object>();
        entry.put("id", id);
        entry.put("props", props);
        return entry;
    }

    @Override
    public void channelNumber(int channelNumber) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("channelNumber", channelNumber);
        sendEvent("channelNumber", payload);
    }

    @Override
    public void motdChanged(String motd) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("motd", motd);
        sendEvent("motd", payload);
    }

    @Override
    public void serverTime(String serverTime) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("serverTime", serverTime);
        sendEvent("serverTime", payload);
    }

    @Override
    public void newPlayerGame(String gameId) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("gameId", gameId);
        sendEvent("newPlayerGame", payload);
    }

    @Override
    public void addTournamentQueue(String queueId, Map<String, String> props) {
        sendEvent("addTournamentQueue", buildEntry(queueId, props));
    }

    @Override
    public void updateTournamentQueue(String queueId, Map<String, String> props) {
        sendEvent("updateTournamentQueue", buildEntry(queueId, props));
    }

    @Override
    public void removeTournamentQueue(String queueId) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("id", queueId);
        sendEvent("removeTournamentQueue", payload);
    }

    @Override
    public void addTournament(String tournamentId, Map<String, String> props) {
        sendEvent("addTournament", buildEntry(tournamentId, props));
    }

    @Override
    public void updateTournament(String tournamentId, Map<String, String> props) {
        sendEvent("updateTournament", buildEntry(tournamentId, props));
    }

    @Override
    public void removeTournament(String tournamentId) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("id", tournamentId);
        sendEvent("removeTournament", payload);
    }

    @Override
    public void addTable(String tableId, Map<String, String> props) {
        sendEvent("addTable", buildEntry(tableId, props));
    }

    @Override
    public void updateTable(String tableId, Map<String, String> props) {
        sendEvent("updateTable", buildEntry(tableId, props));
    }

    @Override
    public void removeTable(String tableId) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("id", tableId);
        sendEvent("removeTable", payload);
    }

    private class WebSocketHallCommunicationChannel extends HallCommunicationChannel {
        @Override
        public void replacedByAnotherConnection() {
            if (!_closed.get()) {
                closeWithReason(4409, "hall connection replaced");
            }
        }
    }
}
