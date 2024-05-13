package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;

import java.util.*;

public class SingleEliminationPairing implements PairingMechanism {
    private String _registryRepresentation;

    public SingleEliminationPairing(String registryRepresentation) {
        _registryRepresentation = registryRepresentation;
    }

    @Override
    public String getRegistryRepresentation() {
        return _registryRepresentation;
    }

    @Override
    public boolean isFinished(int round, Set<String> players, Set<String> droppedPlayers) {
        return players.size() - droppedPlayers.size() < 2;
    }

    @Override
    public String getPlayOffSystem() {
        return "Single elimination";
    }

    @Override
    public boolean pairPlayers(int round, Set<String> players, Set<String> droppedPlayers, Map<String, Integer> playerByes, List<PlayerStanding> currentStandings,
                               Map<String, Set<String>> previouslyPaired,
                               Map<String, String> pairingResults, Set<String> byeResults) {
        if (isFinished(round, players, droppedPlayers))
            return true;

        Set<String> playersInContention = new HashSet<String>(players);
        playersInContention.removeAll(droppedPlayers);

        int maxByes = 0;
        for (Map.Entry<String, Integer> playerByeCount : playerByes.entrySet()) {
            String player = playerByeCount.getKey();
            if (playersInContention.contains(player))
                maxByes = Math.max(maxByes, playerByeCount.getValue());
        }

        List<String>[] playersGroupedByByes = new List[maxByes+1];
        for (Map.Entry<String, Integer> playerByeCount : playerByes.entrySet()) {
            String player = playerByeCount.getKey();
            if (playersInContention.contains(player)) {
                int count = playerByeCount.getValue();
                List<String> playersWithThisNumberOfByes = playersGroupedByByes[maxByes - count];
                if (playersWithThisNumberOfByes == null) {
                    playersWithThisNumberOfByes = new ArrayList<String>();
                    playersGroupedByByes[maxByes - count] = playersWithThisNumberOfByes;
                }
                playersWithThisNumberOfByes.add(player);
                playersInContention.remove(player);
            }
        }

        List<String> playersWithNoByes = playersGroupedByByes[maxByes];
        if (playersWithNoByes == null) {
            playersWithNoByes = new ArrayList<String>();
            playersGroupedByByes[maxByes] = playersWithNoByes;
        }
        playersWithNoByes.addAll(playersInContention);

        List<String> playersRandomized = new ArrayList<String>();

        for (List<String> playersGroupedByBye : playersGroupedByByes) {
            if (playersGroupedByBye != null) {
                Collections.shuffle(playersGroupedByBye);
                playersRandomized.addAll(playersGroupedByBye);
            }
        }

        Iterator<String> playerIterator = playersRandomized.iterator();
        while (playerIterator.hasNext()) {
            String playerOne = playerIterator.next();
            if (playerIterator.hasNext()) {
                String playerTwo = playerIterator.next();
                pairingResults.put(playerOne, playerTwo);
            } else {
                byeResults.add(playerOne);
            }
        }

        return false;
    }

    @Override
    public boolean shouldDropLoser() {
        return true;
    }
}
