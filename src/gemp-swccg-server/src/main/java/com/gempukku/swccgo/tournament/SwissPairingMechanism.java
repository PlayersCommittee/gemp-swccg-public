package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;

import java.util.*;

public class SwissPairingMechanism implements PairingMechanism {
    private String _registryRepresentation;

    public SwissPairingMechanism(String registryRepresentation) {
        _registryRepresentation = registryRepresentation;
    }

    @Override
    public String getRegistryRepresentation() {
        return _registryRepresentation;
    }

    @Override
    public String getPlayOffSystem() {
        return "Swiss";
    }

    @Override
    public boolean shouldDropLoser() {
        return false;
    }

    @Override
    public boolean isFinished(int round, Set<String> players, Set<String> droppedPlayers) {
        return round >= getRoundCountBasedOnNumberOfPlayers(players.size());
    }

    @Override
    public boolean pairPlayers(int round, Set<String> players, Set<String> droppedPlayers, Map<String, Integer> playerByes, List<PlayerStanding> currentStandings,
                               Map<String, Set<String>> previouslyPaired, Map<String, String> pairingResults, Set<String> byeResults) {
        int maxNumberOfPoints = determineMaximumNumberOfPoints(droppedPlayers, currentStandings);

        List<List<String>> playersGroupedByBracket = groupPlayersByPointBracket(droppedPlayers, currentStandings, maxNumberOfPoints);

        shufflePlayersWithinBrackets(playersGroupedByBracket);

        Set<String> playersWithByes = getPlayersWithByes(playerByes);

        boolean success = tryPairBracketAndFurther(0, new HashSet<String>(), new HashSet<String>(), playersGroupedByBracket, playersWithByes, previouslyPaired, pairingResults, byeResults);
        // Managed to pair with this carry over count - proceed with the pairings
        if (success)
            return false;

        // We can't pair, just finish the tournament
        return true;
    }

    private boolean tryPairBracketAndFurther(int bracketIndex, Set<String> carryOverPlayers, Set<String> carryOverFromThisBracket, List<List<String>> playersGroupedByBracket, Set<String> playersWithByes,
                                             Map<String, Set<String>> previouslyPaired, Map<String, String> pairingsResult, Set<String> byes) {
        List<String> playersInBracket = playersGroupedByBracket.get(bracketIndex);

        // First try to pair carried over players
        while (carryOverPlayers.size() > 0) {
            String firstCarryOver = carryOverPlayers.iterator().next();
            carryOverPlayers.remove(firstCarryOver);

            for (int index = 0; index < playersInBracket.size(); index++) {
                String player = playersInBracket.remove(index);
                if (!previouslyPaired.get(firstCarryOver).contains(player)) {
                    // This might be a good pairing
                    pairingsResult.put(firstCarryOver, player);
                    // Lets give it a try
                    boolean success = tryPairBracketAndFurther(bracketIndex, carryOverPlayers, carryOverFromThisBracket, playersGroupedByBracket, playersWithByes, previouslyPaired, pairingsResult, byes);
                    if (success)
                        return true;
                    // Naah, it didn't work out
                    pairingsResult.remove(firstCarryOver);
                }
                playersInBracket.add(index, player);
            }

            carryOverFromThisBracket.add(firstCarryOver);
        }

        if (playersInBracket.size() > 1) {
            // Pair whatever we manage within a bracket
            for (int index = 0; index < playersInBracket.size() - 1; index++) {
                String firstPlayer = playersInBracket.remove(index);
                for (int index2 = index; index2 < playersInBracket.size(); index2++) {
                    String secondPlayer = playersInBracket.remove(index2);
                    if (!previouslyPaired.get(firstPlayer).contains(secondPlayer)) {
                        // This pairing might work
                        pairingsResult.put(firstPlayer, secondPlayer);
                        // Lets give it a try
                        boolean success = tryPairBracketAndFurther(bracketIndex, Collections.<String>emptySet(), carryOverFromThisBracket, playersGroupedByBracket, playersWithByes, previouslyPaired, pairingsResult, byes);
                        if (success)
                            return true;
                        // Naah, it didn't work out
                        pairingsResult.remove(firstPlayer);
                    }
                    playersInBracket.add(index2, secondPlayer);
                }
                playersInBracket.add(index, firstPlayer);
            }
        }

        // We have to go to next bracket
        if (bracketIndex+1 < playersGroupedByBracket.size()) {
            // Remaining players can't be paired within this bracket
            Set<String> carryOverForNextBracket = new HashSet<String>(carryOverFromThisBracket);
            carryOverForNextBracket.addAll(playersInBracket);

            return tryPairBracketAndFurther(bracketIndex+1, carryOverForNextBracket, new HashSet<String>(), playersGroupedByBracket, playersWithByes, previouslyPaired, pairingsResult, byes);
        } else {
            // There is no more brackets left, whatever is left, has to get a bye
            Set<String> leftoverPlayers = new HashSet<String>(carryOverFromThisBracket);
            leftoverPlayers.addAll(playersInBracket);

            // We only accept one bye
            int playersLeftWithoutPair = leftoverPlayers.size();
            switch(playersLeftWithoutPair) {
                case 0:
                        return true;
                case 1: {
                    String lastPlayer = leftoverPlayers.iterator().next();
                    if (playersWithByes.contains(lastPlayer)) {
                        // The last remaining player already has a bye
                        return false;
                    } else {
                        byes.add(lastPlayer);
                        return true;
                    }
                }
                default:
                    return false;
            }
        }
    }

    private Set<String> getPlayersWithByes(Map<String, Integer> playerByes) {
        Set<String> playersWithByes = new HashSet<String>();
        for (Map.Entry<String, Integer> playerByeCount : playerByes.entrySet()) {
            if (playerByeCount.getValue() != null && playerByeCount.getValue() > 0)
                playersWithByes.add(playerByeCount.getKey());
        }
        return playersWithByes;
    }

    private void shufflePlayersWithinBrackets(List<List<String>> playersGroupedByPoints) {
        for (List<String> playersByPoint : playersGroupedByPoints)
            Collections.shuffle(playersByPoint);
    }

    private List<List<String>> groupPlayersByPointBracket(Set<String> droppedPlayers, List<PlayerStanding> currentStandings, int maxNumberOfPoints) {
        List<String>[] playersByPoints = new List[maxNumberOfPoints + 1];
        for (PlayerStanding currentStanding : currentStandings) {
            String playerName = currentStanding.getPlayerName();
            if (!droppedPlayers.contains(playerName)) {
                int points = currentStanding.getPoints();
                List<String> playersByPoint = playersByPoints[maxNumberOfPoints - points];
                if (playersByPoint == null) {
                    playersByPoint = new ArrayList<String>();
                    playersByPoints[maxNumberOfPoints - points] = playersByPoint;
                }
                playersByPoint.add(playerName);
            }
        }

        List<List<String>> result = new ArrayList<List<String>>();
        for (List<String> playersByPoint : playersByPoints) {
            if (playersByPoint != null)
                result.add(playersByPoint);
        }

        return result;
    }

    private int determineMaximumNumberOfPoints(Set<String> droppedPlayers, List<PlayerStanding> currentStandings) {
        int maxNumberOfPoints = 0;
        for (PlayerStanding currentStanding : currentStandings) {
            if (!droppedPlayers.contains(currentStanding.getPlayerName()))
                maxNumberOfPoints = Math.max(currentStanding.getPoints(), maxNumberOfPoints);
        }
        return maxNumberOfPoints;
    }

    private static int getRoundCountBasedOnNumberOfPlayers(int numberOfPlayers) {
        return (int) (Math.ceil(Math.log(numberOfPlayers) / Math.log(2))) + 1;
    }

    public static void main(String[] args) {
        System.out.println(getRoundCountBasedOnNumberOfPlayers(11));
        System.out.println(getRoundCountBasedOnNumberOfPlayers(9));
        System.out.println(getRoundCountBasedOnNumberOfPlayers(8));
    }
}
