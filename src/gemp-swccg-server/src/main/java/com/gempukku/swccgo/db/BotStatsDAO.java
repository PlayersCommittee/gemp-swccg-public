package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.BotPlayerStats;
import com.gempukku.swccgo.db.vo.LeaderboardEntry;

import java.util.List;
import java.util.Set;

/**
 * Data Access Object for bot player statistics and achievements.
 *
 * Tracks player stats when playing against the bot, including wins/losses,
 * route scores (Astrogator meta-game), personal bests, and achievements.
 */
public interface BotStatsDAO {

    /**
     * Get player stats by player ID.
     *
     * @param playerId the player's database ID
     * @return the player's bot stats, or null if not found
     */
    BotPlayerStats getPlayerStats(int playerId);

    /**
     * Get player stats by player name.
     *
     * @param playerName the player's username
     * @return the player's bot stats, or null if not found
     */
    BotPlayerStats getPlayerStatsByName(String playerName);

    /**
     * Create initial stats record for a player.
     *
     * @param playerId the player's database ID
     */
    void createPlayerStats(int playerId);

    /**
     * Record a game result against the bot.
     *
     * @param playerId the player's database ID
     * @param won true if the player beat the bot
     * @param routeScore the Astrogator route score (only meaningful if won)
     * @param damage damage dealt to the bot
     * @param forceRemaining player's force pile at game end
     * @param timeSeconds game duration in seconds
     */
    void recordGameResult(int playerId, boolean won, int routeScore,
                         int damage, int forceRemaining, int timeSeconds);

    /**
     * Unlock an achievement for a player.
     *
     * @param playerId the player's database ID
     * @param achievementBit the bit position of the achievement (0-143)
     * @return true if newly unlocked, false if already had it
     */
    boolean unlockAchievement(int playerId, int achievementBit);

    /**
     * Check if a player has a specific achievement.
     *
     * @param playerId the player's database ID
     * @param achievementBit the bit position of the achievement (0-143)
     * @return true if the player has the achievement
     */
    boolean hasAchievement(int playerId, int achievementBit);

    /**
     * Get all unlocked achievement bit positions for a player.
     *
     * @param playerId the player's database ID
     * @return set of unlocked achievement bit positions
     */
    Set<Integer> getPlayerAchievements(int playerId);

    /**
     * Get the count of achievements unlocked by a player.
     *
     * @param playerId the player's database ID
     * @return number of achievements unlocked
     */
    int getAchievementCount(int playerId);

    /**
     * Get the global best damage record holder.
     *
     * @return the leaderboard entry with highest damage, or null if no games played
     */
    LeaderboardEntry getBestDamageRecord();

    /**
     * Get the global best route score record holder.
     *
     * @return the leaderboard entry with highest route score, or null if no wins
     */
    LeaderboardEntry getBestRouteScoreRecord();

    /**
     * Get the global fastest win record holder.
     *
     * @return the leaderboard entry with fastest win time, or null if no wins
     */
    LeaderboardEntry getFastestWinRecord();

    /**
     * Get top players by total astrogation score.
     *
     * @param limit max number of entries to return
     * @return list of leaderboard entries sorted by total_ast_score descending
     */
    List<LeaderboardEntry> getTopPlayersByAstScore(int limit);

    /**
     * Get top players by win count.
     *
     * @param limit max number of entries to return
     * @return list of leaderboard entries sorted by wins descending
     */
    List<LeaderboardEntry> getTopPlayersByWins(int limit);

    /**
     * Get top players by games played.
     *
     * @param limit max number of entries to return
     * @return list of leaderboard entries sorted by games_played descending
     */
    List<LeaderboardEntry> getTopPlayersByGamesPlayed(int limit);
}
