package com.gempukku.swccgo.async.ws;

public interface WebSocketSession {
    void onOpen();
    void onClose();
    void onTextMessage(String message);
}
