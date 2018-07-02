package com.gempukku.swccgo.chat;

import java.util.Date;

public class ChatMessage {
    private Date _when;
    private int _msgId;
    private String _from;
    private String _message;

    public ChatMessage(Date when, int msgId, String from, String message) {
        _when = when;
        _msgId = msgId;
        _from = from;
        _message = message;
    }

    public String getFrom() {
        return _from;
    }

    public String getMessage() {
        return _message;
    }

    public int getMsgId() {
        return _msgId;
    }

    public Date getWhen() {
        return _when;
    }
}
