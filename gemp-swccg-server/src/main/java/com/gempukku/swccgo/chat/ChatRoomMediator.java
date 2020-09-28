package com.gempukku.swccgo.chat;

import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.game.ChatCommunicationChannel;
import org.apache.log4j.Logger;

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

    private ReadWriteLock _lock = new ReentrantReadWriteLock();

    private Map<String, ChatCommandCallback> _chatCommandCallbacks = new HashMap<String, ChatCommandCallback>();

    public ChatRoomMediator(String roomName, boolean muteJoinPartMessages, int secondsTimeoutPeriod, boolean privateRoom, Set<String> allowedPlayers, boolean allowSpectatorsToChat, boolean playtesting) {
        _logger = Logger.getLogger("chat."+roomName);
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
        _lock.writeLock().lock();
        try {
            if(_allowedPlayers != null && !_allowedPlayers.contains(playerId) && _privateRoom)
                throw new PrivateInformationException();
            if(_allowedPlayers != null && !_allowedPlayers.contains(playerId) && _playtesting && !admin && !playtester)
                throw new PrivateInformationException();

            ChatCommunicationChannel value = new ChatCommunicationChannel();
            _listeners.put(playerId, value);
            _chatRoom.joinChatRoom(playerId, _allowedPlayers != null && !_allowedPlayers.contains(playerId) && !_allowSpectatorsToChat, value);
            return value.consumeMessages(0);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public ChatCommunicationChannel getChatRoomListener(String playerId) throws SubscriptionExpiredException {
        _lock.readLock().lock();
        try {
            ChatCommunicationChannel gatheringChatRoomListener = _listeners.get(playerId);
            if (gatheringChatRoomListener == null)
                throw new SubscriptionExpiredException();
            return gatheringChatRoomListener;
        } finally {
            _lock.readLock().unlock();
        }
    }

    public void partUser(String playerId) {
        _lock.writeLock().lock();
        try {
            _chatRoom.partChatRoom(playerId);
            _listeners.remove(playerId);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    public void sendMessage(String playerId, String message, boolean admin) throws PrivateInformationException, ChatCommandErrorException {
        if (processIfKnownCommand(playerId, message, admin))
            return;

        _lock.writeLock().lock();
        try {
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
            return _chatRoom.getUsersInRoom();
        } finally {
            _lock.readLock().unlock();
        }
    }
}
