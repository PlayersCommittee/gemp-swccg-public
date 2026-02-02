package com.gempukku.swccgo.db.vo;

import java.time.LocalDateTime;

/**
 * Value object representing a player's statistics when playing against the bot.
 *
 * Contains win/loss record, route scores (Astrogator meta-game), personal bests,
 * and a bitfield for tracking 144 achievements.
 */
public class BotPlayerStats {
    private int _playerId;
    private int _wins;
    private int _losses;
    private int _gamesPlayed;
    private int _totalAstScore;     // Cumulative route score across all wins
    private int _bestRouteScore;    // Single game best route score
    private int _bestDamage;        // Personal best damage dealt
    private int _bestForceRemaining; // Personal best force remaining at end
    private Integer _bestTimeSeconds; // Fastest win in seconds (null if never won)
    private byte[] _achievements;   // 18-byte (144-bit) bitfield for achievements
    private LocalDateTime _firstSeen;
    private LocalDateTime _lastSeen;

    public BotPlayerStats() {
        // Default constructor for DAO
    }

    public BotPlayerStats(int playerId, int wins, int losses, int gamesPlayed,
                         int totalAstScore, int bestRouteScore, int bestDamage,
                         int bestForceRemaining, Integer bestTimeSeconds,
                         byte[] achievements, LocalDateTime firstSeen, LocalDateTime lastSeen) {
        _playerId = playerId;
        _wins = wins;
        _losses = losses;
        _gamesPlayed = gamesPlayed;
        _totalAstScore = totalAstScore;
        _bestRouteScore = bestRouteScore;
        _bestDamage = bestDamage;
        _bestForceRemaining = bestForceRemaining;
        _bestTimeSeconds = bestTimeSeconds;
        _achievements = achievements;
        _firstSeen = firstSeen;
        _lastSeen = lastSeen;
    }

    public int getPlayerId() {
        return _playerId;
    }

    public void setPlayerId(int playerId) {
        _playerId = playerId;
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

    public int getBestRouteScore() {
        return _bestRouteScore;
    }

    public void setBestRouteScore(int bestRouteScore) {
        _bestRouteScore = bestRouteScore;
    }

    public int getBestDamage() {
        return _bestDamage;
    }

    public void setBestDamage(int bestDamage) {
        _bestDamage = bestDamage;
    }

    public int getBestForceRemaining() {
        return _bestForceRemaining;
    }

    public void setBestForceRemaining(int bestForceRemaining) {
        _bestForceRemaining = bestForceRemaining;
    }

    public Integer getBestTimeSeconds() {
        return _bestTimeSeconds;
    }

    public void setBestTimeSeconds(Integer bestTimeSeconds) {
        _bestTimeSeconds = bestTimeSeconds;
    }

    public byte[] getAchievements() {
        return _achievements;
    }

    public void setAchievements(byte[] achievements) {
        _achievements = achievements;
    }

    public LocalDateTime getFirstSeen() {
        return _firstSeen;
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        _firstSeen = firstSeen;
    }

    public LocalDateTime getLastSeen() {
        return _lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        _lastSeen = lastSeen;
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

    /**
     * Check if a specific achievement bit is set.
     *
     * @param bitPosition the bit position (0-143)
     * @return true if the achievement is unlocked
     */
    public boolean hasAchievement(int bitPosition) {
        if (_achievements == null || bitPosition < 0 || bitPosition >= 144) {
            return false;
        }
        int byteIndex = bitPosition / 8;
        int bitIndex = bitPosition % 8;
        if (byteIndex >= _achievements.length) {
            return false;
        }
        return (_achievements[byteIndex] & (1 << bitIndex)) != 0;
    }

    /**
     * Set a specific achievement bit.
     *
     * @param bitPosition the bit position (0-143)
     */
    public void setAchievement(int bitPosition) {
        if (_achievements == null) {
            _achievements = new byte[18]; // 144 bits = 18 bytes
        }
        if (bitPosition < 0 || bitPosition >= 144) {
            return;
        }
        int byteIndex = bitPosition / 8;
        int bitIndex = bitPosition % 8;
        if (byteIndex < _achievements.length) {
            _achievements[byteIndex] |= (byte) (1 << bitIndex);
        }
    }

    /**
     * Count the number of achievements unlocked.
     *
     * @return count of achievements unlocked
     */
    public int countAchievements() {
        if (_achievements == null) {
            return 0;
        }
        int count = 0;
        for (byte b : _achievements) {
            count += Integer.bitCount(b & 0xFF);
        }
        return count;
    }
}
