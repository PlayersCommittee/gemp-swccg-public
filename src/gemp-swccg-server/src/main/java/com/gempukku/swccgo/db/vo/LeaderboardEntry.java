package com.gempukku.swccgo.db.vo;

/**
 * Value object representing a leaderboard entry for bot stats.
 *
 * Used for global leaderboards (best damage, best route score, most wins, etc.)
 */
public class LeaderboardEntry {
    private int _playerId;
    private String _playerName;
    private int _value;           // The metric value (damage, score, wins, etc.)
    private int _wins;
    private int _losses;
    private int _gamesPlayed;
    private int _totalAstScore;

    public LeaderboardEntry() {
        // Default constructor
    }

    public LeaderboardEntry(int playerId, String playerName, int value) {
        _playerId = playerId;
        _playerName = playerName;
        _value = value;
    }

    public LeaderboardEntry(int playerId, String playerName, int value,
                           int wins, int losses, int gamesPlayed, int totalAstScore) {
        _playerId = playerId;
        _playerName = playerName;
        _value = value;
        _wins = wins;
        _losses = losses;
        _gamesPlayed = gamesPlayed;
        _totalAstScore = totalAstScore;
    }

    public int getPlayerId() {
        return _playerId;
    }

    public void setPlayerId(int playerId) {
        _playerId = playerId;
    }

    public String getPlayerName() {
        return _playerName;
    }

    public void setPlayerName(String playerName) {
        _playerName = playerName;
    }

    public int getValue() {
        return _value;
    }

    public void setValue(int value) {
        _value = value;
    }

    public int getWins() {
        return _wins;
    }

    public void setWins(int wins) {
        _wins = wins;
    }

    public int getLosses() {
        return _losses;
    }

    public void setLosses(int losses) {
        _losses = losses;
    }

    public int getGamesPlayed() {
        return _gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        _gamesPlayed = gamesPlayed;
    }

    public int getTotalAstScore() {
        return _totalAstScore;
    }

    public void setTotalAstScore(int totalAstScore) {
        _totalAstScore = totalAstScore;
    }

    /**
     * Get the win rate as a percentage.
     *
     * @return win rate percentage (0-100), or 0 if no games played
     */
    public double getWinRate() {
        if (_gamesPlayed == 0) {
            return 0.0;
        }
        return (double) _wins / _gamesPlayed * 100.0;
    }
}
