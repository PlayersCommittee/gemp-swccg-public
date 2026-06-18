package com.gempukku.swccgo.chat;

import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.game.ChatCommunicationChannel;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChatRoomMediator {
    private Logger _logger;
    private ChatRoom _chatRoom;

    private Map<String, ChatCommunicationChannel> _listeners = new HashMap<String, ChatCommunicationChannel>();

    private final int _channelInactivityTimeoutPeriod;
    private boolean _privateRoom;
    private Set<String> _allowedPlayers;
    private boolean _allowSpectatorsToChat;
    private boolean _playtesting;
    private boolean _destroyed;

    private ReadWriteLock _lock = new ReentrantReadWriteLock();

    private Map<String, ChatCommandCallback> _chatCommandCallbacks = new HashMap<String, ChatCommandCallback>();

    public ChatRoomMediator(String roomName, boolean muteJoinPartMessages, int secondsTimeoutPeriod, boolean privateRoom, Set<String> allowedPlayers, boolean allowSpectatorsToChat, boolean playtesting) {
        _logger = LogManager.getLogger("chat."+roomName);
        _privateRoom = privateRoom;
        _allowedPlayers = allowedPlayers;
        _allowSpectatorsToChat = allowSpectatorsToChat;
        _playtesting = playtesting;
        _channelInactivityTimeoutPeriod = 1000 * secondsTimeoutPeriod;
        _chatRoom = new ChatRoom(muteJoinPartMessages);
    }

    public void addChatCommandCallback(String command, ChatCommandCallback callback) {
        _chatCommandCallbacks.put(command.toLowerCase(), callback);
    }

    public List<ChatMessage> joinUser(String playerId, boolean admin, boolean playtester) throws PrivateInformationException {
        return joinUser(playerId, admin, playtester, new ChatCommunicationChannel());
    }

    public List<ChatMessage> joinUser(String playerId, boolean admin, boolean playtester, ChatCommunicationChannel listener) throws PrivateInformationException {
        _lock.writeLock().lock();
        try {
            if (_destroyed)
                throw new PrivateInformationException();
            if(_allowedPlayers != null && !_allowedPlayers.contains(playerId) && _privateRoom)
                throw new PrivateInformationException();
            if(_allowedPlayers != null && !_allowedPlayers.contains(playerId) && _playtesting && !admin && !playtester)
                throw new PrivateInformationException();

            ChatCommunicationChannel replacedListener = _listeners.put(playerId, listener);
            _chatRoom.joinChatRoom(playerId, _allowedPlayers != null && !_allowedPlayers.contains(playerId) && !_allowSpectatorsToChat, listener);
            if (replacedListener != null && replacedListener != listener) {
                replacedListener.replacedByAnotherConnection();
            }
            return listener.consumeMessages(0);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public ChatCommunicationChannel getChatRoomListener(String playerId) throws SubscriptionExpiredException {
        _lock.readLock().lock();
        try {
            if (_destroyed)
                throw new SubscriptionExpiredException();
            ChatCommunicationChannel gatheringChatRoomListener = _listeners.get(playerId);
            if (gatheringChatRoomListener == null)
                throw new SubscriptionExpiredException();
            return gatheringChatRoomListener;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void partUser(String playerId) {
        partUser(playerId, null);
    }

    public void partUser(String playerId, ChatCommunicationChannel listener) {
        _lock.writeLock().lock();
        try {
            if (listener != null && _listeners.get(playerId) != listener)
                return;
            _chatRoom.partChatRoom(playerId);
            _listeners.remove(playerId);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public void sendMessage(String playerId, String message, boolean admin) throws PrivateInformationException, ChatCommandErrorException {
        _lock.readLock().lock();
        try {
            if (_destroyed)
                return;
        } finally {
            _lock.readLock().unlock();
        }

        if (processIfKnownCommand(playerId, message, admin))
            return;

        _lock.writeLock().lock();
        try {
            if (_destroyed)
                return;
            if (admin || _allowedPlayers == null || _allowedPlayers.contains(playerId) || _allowSpectatorsToChat) {
                _logger.trace(playerId + ": " + message);
                _chatRoom.postMessage(playerId, message);
            }
            else if (_privateRoom) {
                throw new PrivateInformationException();
            }
        } finally {
            _lock.writeLock().unlock();
        }
    }

    private boolean processIfKnownCommand(String playerId, String message, boolean admin) throws ChatCommandErrorException {
        if (message.startsWith("/")) {
            // Maybe it's a known command
            String commandString = message.substring(1);
            int spaceIndex = commandString.indexOf(" ");
            String commandName;
            String commandParameters="";
            if (spaceIndex>-1) {
                commandName = commandString.substring(0, spaceIndex);
                commandParameters = commandString.substring(spaceIndex+1);
            } else {
                commandName = commandString;
            }
            final ChatCommandCallback callbackForCommand = _chatCommandCallbacks.get(commandName.toLowerCase());
            if (callbackForCommand != null) {
                callbackForCommand.commandReceived(playerId, commandParameters, admin);
                return true;
            }
        }
        return false;
    }

    public void cleanup() {
        _lock.writeLock().lock();
        try {
            if (_destroyed)
                return;
            long currentTime = System.currentTimeMillis();
            Map<String, ChatCommunicationChannel> copy = new HashMap<String, ChatCommunicationChannel>(_listeners);
            for (Map.Entry<String, ChatCommunicationChannel> playerListener : copy.entrySet()) {
                String playerId = playerListener.getKey();
                ChatCommunicationChannel listener = playerListener.getValue();
                if (currentTime > (listener.getLastAccessed() + _channelInactivityTimeoutPeriod)) {
                    _chatRoom.partChatRoom(playerId);
                    _listeners.remove(playerId);
                }
            }
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public Collection<String> getUsersInRoom() {
        _lock.readLock().lock();
        try {
            if (_destroyed)
                return Collections.emptyList();
            return _chatRoom.getUsersInRoom();
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void destroy() {
        List<ChatCommunicationChannel> listeners;
        _lock.writeLock().lock();
        try {
            if (_destroyed)
                return;
            _destroyed = true;
            listeners = new ArrayList<ChatCommunicationChannel>(_listeners.values());
            _listeners.clear();
            _chatRoom.close();
        } finally {
            _lock.writeLock().unlock();
        }

        for (ChatCommunicationChannel listener : listeners)
            listener.closedByServer();
    }
}
