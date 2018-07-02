package com.gempukku.swccgo.chat;

public interface ChatCommandCallback {
    void commandReceived(String from, String parameters, boolean admin) throws ChatCommandErrorException;
}
