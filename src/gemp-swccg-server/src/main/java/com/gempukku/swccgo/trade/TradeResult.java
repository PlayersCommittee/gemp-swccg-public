package com.gempukku.swccgo.trade;

import com.gempukku.swccgo.game.CardCollection;

public class TradeResult {
    private String _initiatingPlayer;
    private String _joiningPlayer;
    private CardCollection _initiatingPlayerItems;
    private CardCollection _joiningPlayerItems;

    public TradeResult(String initiatingPlayer, CardCollection initiatingPlayerItems, String joiningPlayer, CardCollection joiningPlayerItems) {
        _initiatingPlayer = initiatingPlayer;
        _initiatingPlayerItems = initiatingPlayerItems;
        _joiningPlayer = joiningPlayer;
        _joiningPlayerItems = joiningPlayerItems;
    }

    public String getInitiatingPlayer() {
        return _initiatingPlayer;
    }

    public CardCollection getInitiatingPlayerItems() {
        return _initiatingPlayerItems;
    }

    public String getJoiningPlayer() {
        return _joiningPlayer;
    }

    public CardCollection getJoiningPlayerItems() {
        return _joiningPlayerItems;
    }
}
