package com.gempukku.swccgo.tournament;

public class SwissTournament {
//    private TournamentMatchDAO _tournamentMatchDao;
//
//    private Map<String, SwccgoDeck> _players;
//    private Set<String>[] _droppedPlayers;
//
//    private int _roundCount;
//    // Current round
//    private int _currentRound;
//    private String _id;
//    private SwccgoFormat _format;
//
//    private Set<TournamentMatch>[] _finishedMatches;
//
//    private Map<String, String> _currentRoundPairings;
//    private Set<String> _playersWithByes;
//
//    private TournamentTask _nextTournamentTask;
//
//    private List<PlayerStanding> _currentStandings;
//
//    public SwissTournament(TournamentMatchDAO tournamentMatchDao, Map<String, SwccgoDeck> players, Set<String>[] droppedPlayers, int roundCount, int status, String id, SwccgoFormat format) {
//        _tournamentMatchDao = tournamentMatchDao;
//        _droppedPlayers = droppedPlayers;
//
//        _roundCount = roundCount;
//        _currentRound = status;
//        _id = id;
//        _format = format;
//
//        _players = new ConcurrentHashMap<String, SwccgoDeck>(players);
//        for (int i = 0; i < _currentRound; i++) {
//            Set<String> allPlayers = new HashSet<String>(players.keySet());
//            // Remove all players dropped so far
//            for (int j = 0; j <= i; j++)
//                allPlayers.removeAll(droppedPlayers[j]);
//
//            // Fill the information about all the matches finished or scheduled so far
//            for (TournamentMatch tournamentMatch : _tournamentMatchDao.getMatches(_id, i + 1)) {
//                if (tournamentMatch.getWinner() != null)
//                    _finishedMatches[i].add(tournamentMatch);
//                else
//                    _currentRoundPairings.put(tournamentMatch.getPlayerOne(), tournamentMatch.getPlayerTwo());
//                // Removed playing (or played) players
//                allPlayers.remove(tournamentMatch.getPlayerOne());
//                allPlayers.remove(tournamentMatch.getPlayerTwo());
//            }
//            // Remaining players had a bye
//            _playersWithByes.addAll(allPlayers);
//        }
//
//        if (_currentRoundPairings.isEmpty()) {
//            _nextTournamentTask = new DelayedTournamentTask() {
//                @Override
//                public int executeTask(TournamentCallback tournamentCallback) {
//                    return roundFinished(tournamentCallback);
//                }
//            };
//        } else {
//            _nextTournamentTask = new DelayedTournamentTask() {
//                @Override
//                public int executeTask(TournamentCallback tournamentCallback) {
//                    tournamentCallback.createGame(_currentRoundPairings, _players);
//                    return _currentRound;
//                }
//            };
//        }
//    }
//
//    private int roundFinished(TournamentCallback tournamentCallback) {
//        if (_currentRound < _roundCount) {
//            _currentRound++;
//            boolean success = tryCreatingNewPairings();
//            if (success) {
//                Map<String, String> currentRoundPairings = _currentRoundPairings;
//                for (Map.Entry<String, String> pairing : currentRoundPairings.entrySet())
//                    _tournamentMatchDao.addMatch(_id, _currentRound, pairing.getKey(), pairing.getValue());
//
//                tournamentCallback.createGame(currentRoundPairings, _players);
//                return _currentRound;
//            } else {
//                return distributePrizes(tournamentCallback);
//            }
//        } else if (_currentRound == _roundCount) {
//            return distributePrizes(tournamentCallback);
//        } else {
//            return _currentRound;
//        }
//    }
//
//    private boolean tryCreatingNewPairings() {
//        return false;
//    }
//
//    private int distributePrizes(TournamentCallback tournamentCallback) {
//        _currentRound = _roundCount + 1;
//
//        // Distribute prizes
//
//        return _currentRound;
//    }
//
//    public synchronized void dropPlayerBeforeRound(int round, String playerName) {
//        if (_droppedPlayers[round] == null)
//            _droppedPlayers[round] = new CopyOnWriteArraySet<String>();
//        _droppedPlayers[round].add(playerName);
//    }
//
//    public synchronized void executePendingTasks(TournamentCallback tournamentCallback) {
//        if (_nextTournamentTask != null && _nextTournamentTask.getExecuteAfter() < System.currentTimeMillis()) {
//            TournamentTask task = _nextTournamentTask;
//            _nextTournamentTask = null;
//            task.executeTask(tournamentCallback);
//        }
//    }
//
//    public List<PlayerStanding> getCurrentStandings() {
//        List<PlayerStanding> result = _currentStandings;
//        if (result != null)
//            return result;
//
//        synchronized (this) {
//            if (_currentStandings == null)
//                calculateCurrentStandings();
//
//            return _currentStandings;
//        }
//    }
//
//    private void calculateCurrentStandings() {
//        Set<TournamentMatch> standingMatches = new HashSet<TournamentMatch>();
//        for (Set<TournamentMatch> finishedMatchesInRound : _finishedMatches) {
//            if (finishedMatchesInRound != null)
//                standingMatches.addAll(finishedMatchesInRound);
//        }
//
//        _currentStandings = StandingsProducer.produceStandings(_players.keySet(), standingMatches, 1, 0, _playersWithByes);
//    }
//
//    private interface TournamentTask {
//        public int executeTask(TournamentCallback tournamentCallback);
//
//        public long getExecuteAfter();
//    }
//
//    private abstract class DelayedTournamentTask implements TournamentTask {
//        private final long _time;
//
//        private DelayedTournamentTask() {
//            _time = System.currentTimeMillis();
//        }
//
//        @Override
//        public final long getExecuteAfter() {
//            return _time + 1000 * 60 * 2;
//        }
//    }
}
