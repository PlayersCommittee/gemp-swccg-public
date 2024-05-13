package com.gempukku.swccgo.trade;

public class TradeRequest {
    private String _from;
    private String _to;
    private long _createdDate;

    public TradeRequest(String from, String to) {
        _from = from;
        _to = to;
        _createdDate = System.currentTimeMillis();
    }

    public String getFrom() {
        return _from;
    }

    public String getTo() {
        return _to;
    }

    public long getCreatedDate() {
        return _createdDate;
    }
}
