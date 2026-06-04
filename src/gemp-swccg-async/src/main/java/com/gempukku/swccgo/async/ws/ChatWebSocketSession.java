package com.gempukku.swccgo.async.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.async.util.ChatUserListFormatter;
import com.gempukku.swccgo.chat.ChatCommandErrorException;
import com.gempukku.swccgo.chat.ChatMessage;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.ChatCommunicationChannel;
import com.gempukku.swccgo.game.Player;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.text.StringEscapeUtils;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatWebSocketSession extends AbstractWebSocketSession {
    private final ChatRoomMediator _chatRoom;
    private final Player _player;
    private final PlayerDAO _playerDao;
    private final String _room;
    private final AtomicBoolean _closed = new AtomicBoolean(false);
    private final WebSocketChatChannel _channel;
    private ScheduledFuture<?> _keepAlive;
    private ScheduledFuture<?> _expiryTimer;
    private final long _tokenExpiresAtMs;
    private long _lastUsersPushAt;

    public ChatWebSocketSession(ChannelHandlerContext ctx, ChatRoomMediator chatRoom, Player player, PlayerDAO playerDao, String room, long tokenExpiresAt) {
        super(ctx);
        _chatRoom = chatRoom;
        _player = player;
        _playerDao = playerDao;
        _room = room;
        _channel = new WebSocketChatChannel();
        _tokenExpiresAtMs = tokenExpiresAt > 0 ? tokenExpiresAt * 1000L : 0L;
    }

    @Override
    public void onOpen() {
        try {
            List<ChatMessage> messages = _chatRoom.joinUser(_player.getName(), _player.hasType(Player.Type.ADMIN),
                    _player.hasType(Player.Type.PLAYTESTER), _channel);
            sendSnapshot(messages);
            _channel.enableForwarding();
            startKeepAlive();
            scheduleTokenExpiry();
        } catch (PrivateInformationException exp) {
            sendError("Access denied.");
            _ctx.close();
        }
    }

    @Override
    public void onClose() {
        if (_closed.compareAndSet(false, true)) {
            stopKeepAlive();
            stopExpiryTimer();
            _chatRoom.partUser(_player.getName(), _channel);
        }
    }

    @Override
    public void onTextMessage(String message) {
        String text = extractMessage(message);
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        try {
            _chatRoom.sendMessage(_player.getName(), StringEscapeUtils.escapeHtml3(text), _player.hasType(Player.Type.ADMIN));
        } catch (PrivateInformationException exp) {
            sendError("You do not have permission to post in this room.");
        } catch (ChatCommandErrorException exp) {
            sendError("Chat command failed.");
        }
    }

    private String extractMessage(String payload) {
        if (payload == null) {
            return null;
        }
        String trimmed = payload.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            try {
                JSONObject json = JSON.parseObject(trimmed);
                String type = json.getString("type");
                if ("presence".equals(type)) {
                    sendUsersEvent();
                    return null;
                }
                if (type == null || "message".equals(type)) {
                    String text = json.getString("text");
                    if (text == null) {
                        text = json.getString("message");
                    }
                    return text;
                }
            } catch (Exception ignored) {
                // fall through to raw
            }
        }
        return payload;
    }

    private void sendSnapshot(List<ChatMessage> messages) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("room", _room);
        payload.put("messages", serializeMessages(messages));
        payload.put("users", buildUsers());
        sendEvent("snapshot", payload);
    }

    private void sendMessageEvent(ChatMessage chatMessage) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("message", serializeMessage(chatMessage));
        payload.put("users", buildUsers());
        sendEvent("message", payload);
    }

    private void sendUsersEvent() {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("users", buildUsers());
        sendEvent("users", payload);
    }

    private void sendError(String message) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("message", message);
        sendEvent("error", payload);
    }

    private void sendEvent(String event, Map<String, Object> payload) {
        Map<String, Object> message = new LinkedHashMap<String, Object>();
        message.put("type", "chat");
        message.put("event", event);
        if (payload != null) {
            message.putAll(payload);
        }
        sendJson(message);
    }

    private void startKeepAlive() {
        stopKeepAlive();
        _keepAlive = _ctx.executor().scheduleAtFixedRate(() -> {
            if (_closed.get()) {
                return;
            }
            _channel.touch();
            maybeSendUsers();
        }, 5, 10, TimeUnit.SECONDS);
    }

    private void stopKeepAlive() {
        if (_keepAlive != null) {
            _keepAlive.cancel(false);
            _keepAlive = null;
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

    private void maybeSendUsers() {
        long now = System.currentTimeMillis();
        if (now - _lastUsersPushAt >= 30000L) {
            _lastUsersPushAt = now;
            sendUsersEvent();
        }
    }

    private List<Map<String, Object>> serializeMessages(List<ChatMessage> messages) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (ChatMessage message : messages) {
            result.add(serializeMessage(message));
        }
        return result;
    }

    private Map<String, Object> serializeMessage(ChatMessage message) {
        Map<String, Object> entry = new LinkedHashMap<String, Object>();
        entry.put("id", message.getMsgId());
        entry.put("from", message.getFrom());
        entry.put("date", message.getWhen().getTime());
        entry.put("text", message.getMessage());
        return entry;
    }

    private List<String> buildUsers() {
        return ChatUserListFormatter.formatAndSortUsers(_chatRoom.getUsersInRoom(), _playerDao);
    }

    private class WebSocketChatChannel extends ChatCommunicationChannel {
        private volatile boolean forward;

        void enableForwarding() {
            forward = true;
        }

        void touch() {
            if (forward) {
                consumeMessages(null);
            }
        }

        @Override
        public void replacedByAnotherConnection() {
            if (!_closed.get()) {
                closeWithReason(4409, "chat connection replaced");
            }
        }

        @Override
        public void closedByServer() {
            if (!_closed.get()) {
                closeWithReason(4404, "chat room closed");
            }
        }

        @Override
        public synchronized void messageReceived(ChatMessage message) {
            super.messageReceived(message);
            if (!forward) {
                return;
            }
            sendMessageEvent(message);
            consumeMessages(null);
        }
    }
}
