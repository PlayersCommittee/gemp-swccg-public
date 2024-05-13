package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

import java.util.Collection;

public interface LeagueParticipationDAO {
    public void userJoinsLeague(String leagueId, Player player, String remoteAddr);

    public Collection<String> getUsersParticipating(String leagueId);
}
