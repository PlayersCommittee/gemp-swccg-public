package com.gempukku.swccgo.game;

import com.gempukku.polling.LongPollableResource;
import com.gempukku.polling.WaitingRequest;
import com.gempukku.swccgo.chat.ChatMessage;
import com.gempukku.swccgo.chat.ChatRoomListener;

import java.util.LinkedList;
import java.util.List;

public class ChatCommunicationChannel implements ChatRoomListener, LongPollableResource {
    private List<ChatMessage> _messages = new LinkedList<ChatMessage>();
    private long _lastConsumed = System.currentTimeMillis();
    private volatile WaitingRequest _waitingRequest;

    @Override
    public synchronized void unregisterRequest(WaitingRequest waitingRequest) {
        _waitingRequest = null;
    }

    @Override
    public synchronized boolean registerRequest(WaitingRequest waitingRequest) {
        if (!_messages.isEmpty())
            return true;

        _waitingRequest = waitingRequest;
        return false;
    }

    @Override
    public synchronized void messageReceived(ChatMessage message) {
        _messages.add(message);
        if (_waitingRequest != null) {
            _waitingRequest.processRequest();
            _waitingRequest = null;
        }
    }

    public synchronized List<ChatMessage> consumeMessages(Integer latestMsgIdRcvd) {
        updateLastAccess();

        List<ChatMessage> messages = _messages;
        _messages = new LinkedList<ChatMessage>();

        if (latestMsgIdRcvd != null) {
            // Consume and keep around any messages that have not been acknowledged as received
            List<ChatMessage> messagesToConsume = new LinkedList<ChatMessage>();
            for (ChatMessage message : messages) {
                if (message.getMsgId() > latestMsgIdRcvd) {
                    messagesToConsume.add(message);
                }
            }
            messages = messagesToConsume;
            _messages.addAll(messages);
        }

        return messages;
    }

    public synchronized boolean hasMessages() {
        updateLastAccess();
        return !_messages.isEmpty();
    }

    private synchronized void updateLastAccess() {
        _lastConsumed = System.currentTimeMillis();
    }

    public synchronized long getLastAccessed() {
        return _lastConsumed;
    }
}
