package com.gempukku.swccgo.tournament;

import java.util.List;
import java.util.Map;

public interface TournamentMatchDAO {
    public void addMatch(String tournamentId, int round, String playerOne, String playerTwo);

    public void setMatchResult(String tournamentId, int round, String winner);

    public List<TournamentMatch> getMatches(String tournamentId);

    public void addBye(String tournamentId, String player, int round);

    public Map<String, Integer> getPlayerByes(String tournamentId);
}
