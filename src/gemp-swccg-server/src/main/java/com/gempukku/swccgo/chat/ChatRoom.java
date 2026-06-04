package com.gempukku.swccgo.chat;

import java.util.*;

public class ChatRoom {
    private int _maxMessageHistoryCount = 100;
    private int _latestMsgId = 1;
    private LinkedList<ChatMessage> _lastMessages = new LinkedList<ChatMessage>();
    private Map<String, ChatRoomListener> _chatRoomListeners = new TreeMap<String, ChatRoomListener>(
            new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
    private boolean _muteJoinPartMessages;
    private Set<String> _usersToMute = new HashSet<>();
    private static final String LIBRARIAN = "Librarian";

    public ChatRoom(boolean muteJoinPartMessages) {
        _muteJoinPartMessages = muteJoinPartMessages;
    }

    private int getLatestMsgIdAndIncrement() {
        return _latestMsgId++;
    }

    private void postMessage(String from, String message, boolean addToHistory) {
        ChatMessage chatMessage = new ChatMessage(new Date(), getLatestMsgIdAndIncrement(), from, message);
        if (addToHistory) {
            _lastMessages.add(chatMessage);
            shrinkLastMessages();
        }
        for (Map.Entry<String, ChatRoomListener> listeners : _chatRoomListeners.entrySet())
            listeners.getValue().messageReceived(chatMessage);
    }

    public void postMessage(String from, String message) {
        postMessage(from, message, true);
    }

    public void joinChatRoom(String playerId, boolean mutePlayer, ChatRoomListener listener) {
        boolean wasInRoom = _chatRoomListeners.containsKey(playerId);
        _chatRoomListeners.put(playerId, listener);
        for (ChatMessage lastMessage : _lastMessages) {
            listener.messageReceived(lastMessage);
        }
        if (mutePlayer) {
            _usersToMute.add(playerId);
        }
        if (!wasInRoom && !_muteJoinPartMessages && !mutePlayer && !playerId.equals(LIBRARIAN)) {
            postMessage("System", playerId + " joined the room", false);
        }
    }

    public void partChatRoom(String playerId) {
        boolean wasInRoom = (_chatRoomListeners.remove(playerId) != null);
        if (wasInRoom && !_muteJoinPartMessages && !_usersToMute.contains(playerId) && !playerId.equals(LIBRARIAN)) {
            postMessage("System", playerId + " left the room", false);
        }
        _usersToMute.remove(playerId);
    }

    public Collection<String> getUsersInRoom() {
        List<String> users = new ArrayList<String>(_chatRoomListeners.keySet());
        users.remove(LIBRARIAN);
        return users;
    }

    private void shrinkLastMessages() {
        while (_lastMessages.size() > _maxMessageHistoryCount) {
            _lastMessages.removeFirst();
        }
    }
}
