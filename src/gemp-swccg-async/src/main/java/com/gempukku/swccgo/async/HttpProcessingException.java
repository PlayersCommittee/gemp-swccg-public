package com.gempukku.swccgo.async;

public class HttpProcessingException extends Exception {
    private int _status;

    private final String _message;

    public HttpProcessingException(int status) {
        this(status, "");
    }

    public HttpProcessingException(int status, String message) {
        _status = status;
        _message = message;
    }

    public int getStatus() {
        return _status;
    }

    public String getMessage() {
        return _message;
    }
}
