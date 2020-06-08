package com.gempukku.swccgo.async;

public class HttpProcessingException extends Exception {
    private int _status;

    public HttpProcessingException(int status) {
        _status = status;
    }

    public int getStatus() {
        return _status;
    }
}
