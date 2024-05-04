package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PairingMechanism {
    public boolean shouldDropLoser();

    public boolean isFinished(int round, Set<String> players, Set<String> droppedPlayers);

    public boolean pairPlayers(int round, Set<String> players, Set<String> droppedPlayers, Map<String, Integer> playerByes,
                               List<PlayerStanding> currentStandings, Map<String, Set<String>> previouslyPaired, Map<String, String> pairingResults, Set<String> byeResults);

    public String getPlayOffSystem();

    public String getRegistryRepresentation();
}
