package com.gempukku.swccgo.game;

import com.gempukku.swccgo.db.GameHistoryDAO;
import com.gempukku.swccgo.db.PlayerStatistic;
import com.gempukku.swccgo.db.vo.GameHistoryEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameHistoryService {
    private GameHistoryDAO _gameHistoryDAO;
    private Map<String, Integer> _playerGameCount = new ConcurrentHashMap<String, Integer>();

    public GameHistoryService(GameHistoryDAO gameHistoryDAO) {
        _gameHistoryDAO = gameHistoryDAO;
    }

    public void addGameHistory(String winner, String loser, String winReason, String loseReason, String winRecordingId, String loseRecordingId, String formatName, String tournament, String winnerDeckName, String loserDeckName, String winnerDeckArchetype, String loserDeckArchetype, String winningSide, Date startDate, Date endDate) {
        _gameHistoryDAO.addGameHistory(winner, loser, winReason, loseReason, winRecordingId, loseRecordingId, formatName, tournament, winnerDeckName, loserDeckName, winnerDeckArchetype, loserDeckArchetype, winningSide, startDate, endDate);
        Integer winnerCount = _playerGameCount.get(winner);
        Integer loserCount = _playerGameCount.get(loser);
        if (winnerCount != null)
            _playerGameCount.put(winner, winnerCount + 1);
        if (loserCount != null)
            _playerGameCount.put(loser, loserCount + 1);
    }

    public int getGameHistoryForPlayerCount(Player player) {
        Integer result = _playerGameCount.get(player.getName());
        if (result != null)
            return result;
        int count = _gameHistoryDAO.getGameHistoryForPlayerCount(player);
        _playerGameCount.put(player.getName(), count);
        return count;
    }

    public List<GameHistoryEntry> getGameHistoryForPlayer(Player player, int start, int count) {
        return _gameHistoryDAO.getGameHistoryForPlayer(player, start, count);
    }

    public int getActivePlayersCount() {
        return _gameHistoryDAO.getActivePlayersCount();
    }

    public int getActivePlayersCount(long from, long duration) {
        return _gameHistoryDAO.getActivePlayersCount(from, duration);
    }

    public int getGamesPlayedCount() {
        return _gameHistoryDAO.getGamesPlayedCount();
    }

    public int getGamesPlayedCount(long from, long duration) {
        return _gameHistoryDAO.getGamesPlayedCount(from, duration);
    }

    public GameHistoryStatistics getGameHistoryStatistics(long from, long duration) {
        GameHistoryStatistics stats = new GameHistoryStatistics(from, duration);
        stats.init(_gameHistoryDAO);
        return stats;
    }

    public List<PlayerStatistic> getCasualPlayerStatistics(Player player) {
        return _gameHistoryDAO.getCasualPlayerStatistics(player);
    }

    public List<PlayerStatistic> getCompetitivePlayerStatistics(Player player) {
        return _gameHistoryDAO.getCompetitivePlayerStatistics(player);
    }
}
