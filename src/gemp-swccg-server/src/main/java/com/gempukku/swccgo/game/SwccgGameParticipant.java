package com.gempukku.swccgo.game;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

public class SwccgGameParticipant {
    private String _playerId;
    private SwccgDeck _deck;

    public SwccgGameParticipant(String playerId, SwccgDeck deck) {
        _playerId = playerId;
        _deck = deck;
    }

    public String getPlayerId() {
        return _playerId;
    }

    public SwccgDeck getDeck() {
        return _deck;
    }
}
