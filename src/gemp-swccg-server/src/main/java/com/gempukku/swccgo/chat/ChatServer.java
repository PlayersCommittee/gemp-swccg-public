package com.gempukku.swccgo.chat;

import com.gempukku.swccgo.AbstractServer;
import com.gempukku.swccgo.PrivateInformationException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer extends AbstractServer {
    public static final String GAME_HALL_ROOM_NAME = "Game Hall";

    private Map<String, ChatRoomMediator> _chatRooms = new ConcurrentHashMap<String, ChatRoomMediator>();
    private volatile java.util.function.Predicate<String> _gameHallKeepAlive;

    public ChatRoomMediator createChatRoom(String name, boolean muteJoinPartMessages, int secondsTimeoutPeriod, Set<String> allowedUsers, boolean allowSpectatorsToChat, boolean playtesting) {
        ChatRoomMediator chatRoom = new ChatRoomMediator(name, muteJoinPartMessages, secondsTimeoutPeriod, false, allowedUsers, allowSpectatorsToChat, playtesting);
        try {
            chatRoom.sendMessage("System", "Welcome to room: " + name, true);
        } catch (PrivateInformationException exp) {
            // Ignore, sent as admin
        } catch (ChatCommandErrorException e) {
            // Ignore, no command
        }
        _chatRooms.put(name, chatRoom);
        return chatRoom;
    }

    public ChatRoomMediator createPrivateChatRoom(String name, boolean muteJoinPartMessages, Set<String> allowedUsers, int secondsTimeoutPeriod) {
        ChatRoomMediator chatRoom = new ChatRoomMediator(name, muteJoinPartMessages, secondsTimeoutPeriod, true, allowedUsers, false, false);
        try {
            chatRoom.sendMessage("System", "Welcome to private room: " + name, true);
        } catch (PrivateInformationException exp) {
            // Ignore, sent as admin
        } catch (ChatCommandErrorException e) {
            // Ignore, no command
        }
        _chatRooms.put(name, chatRoom);
        return chatRoom;
    }

    public void sendSystemMessageToAllChatRooms(String message) {
        try {
            for (ChatRoomMediator chatRoomMediator : _chatRooms.values())
                chatRoomMediator.sendMessage("System", message, true);
        } catch (PrivateInformationException exp) {
            // Ignore, sent as admin
        } catch (ChatCommandErrorException e) {
            // Ignore, no command
        }
    }

    public ChatRoomMediator getChatRoom(String name) {
        return _chatRooms.get(name);
    }

    public void setGameHallKeepAlive(java.util.function.Predicate<String> gameHallKeepAlive) {
        _gameHallKeepAlive = gameHallKeepAlive;
    }

    public void destroyChatRoom(String name) {
        _chatRooms.remove(name);
    }

    protected void cleanup() {
        java.util.function.Predicate<String> keepAlive = _gameHallKeepAlive;
        for (Map.Entry<String, ChatRoomMediator> entry : _chatRooms.entrySet()) {
            if (GAME_HALL_ROOM_NAME.equals(entry.getKey()) && keepAlive != null) {
                entry.getValue().cleanup(keepAlive);
            } else {
                entry.getValue().cleanup();
            }
        }
    }
}
