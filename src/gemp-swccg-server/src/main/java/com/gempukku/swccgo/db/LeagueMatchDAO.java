package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.LeagueMatchResult;

import java.util.Collection;

public interface LeagueMatchDAO {
    public Collection<LeagueMatchResult> getLeagueMatches(String leagueId);

    public void addPlayedMatch(String leagueId, String serieId, String winner, String loser, String winnerSide, String loserSide);
}
