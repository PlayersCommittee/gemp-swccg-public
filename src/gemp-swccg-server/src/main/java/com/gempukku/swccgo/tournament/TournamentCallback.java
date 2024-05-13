package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

public interface TournamentCallback {
    public void createGame(String playerOne, SwccgDeck deckOne, String playerTwo, SwccgDeck deckTwo, boolean allowSpectators);

    public void broadcastMessage(String message);
}
