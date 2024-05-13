package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.Map;
import java.util.Set;

public interface TournamentPlayerDAO {
    public void addPlayer(String tournamentId, String playerName, SwccgDeck deck);

    public void updatePlayerDeck(String tournamentId, String playerName, SwccgDeck deck);

    public void dropPlayer(String tournamentId, String playerName);

    public Map<String, SwccgDeck> getPlayerDecks(String tournamentId);

    public Set<String> getDroppedPlayers(String tournamentId);

    public SwccgDeck getPlayerDeck(String tournamentId, String playerName);

    public Set<String> getPlayers(String tournamentId);
}
