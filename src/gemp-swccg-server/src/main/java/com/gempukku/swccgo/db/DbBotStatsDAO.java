package com.gempukku.swccgo.db;

import com.gempukku.swccgo.bot.Achievement;
import com.gempukku.swccgo.db.vo.BotPlayerStats;
import com.gempukku.swccgo.db.vo.LeaderboardEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * JDBC implementation of BotStatsDAO.
 *
 * Stores player statistics in the bot_player_stats table.
 * Achievements are stored as a bitfield in BINARY(18) column.
 */
public class DbBotStatsDAO implements BotStatsDAO {
    private final DbAccess _dbAccess;

    public DbBotStatsDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public BotPlayerStats getPlayerStats(int playerId) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT player_id, wins, losses, games_played, total_ast_score, " +
                    "best_route_score, best_damage, best_force_remaining, best_time_seconds, " +
                    "achievements, first_seen, last_seen " +
                    "FROM bot_player_stats WHERE player_id = ?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return extractStats(rs);
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get bot player stats", exp);
        }
        return null;
    }

    @Override
    public BotPlayerStats getPlayerStatsByName(String playerName) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, b.wins, b.losses, b.games_played, b.total_ast_score, " +
                    "b.best_route_score, b.best_damage, b.best_force_remaining, b.best_time_seconds, " +
                    "b.achievements, b.first_seen, b.last_seen " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE p.name = ?")) {
                statement.setString(1, playerName);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return extractStats(rs);
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get bot player stats by name", exp);
        }
        return null;
    }

    private BotPlayerStats extractStats(ResultSet rs) throws SQLException {
        BotPlayerStats stats = new BotPlayerStats();
        stats.setPlayerId(rs.getInt("player_id"));
        stats.setWins(rs.getInt("wins"));
        stats.setLosses(rs.getInt("losses"));
        stats.setGamesPlayed(rs.getInt("games_played"));
        stats.setTotalAstScore(rs.getInt("total_ast_score"));
        stats.setBestRouteScore(rs.getInt("best_route_score"));
        stats.setBestDamage(rs.getInt("best_damage"));
        stats.setBestForceRemaining(rs.getInt("best_force_remaining"));

        int bestTime = rs.getInt("best_time_seconds");
        stats.setBestTimeSeconds(rs.wasNull() ? null : bestTime);

        byte[] achievements = rs.getBytes("achievements");
        if (achievements == null) {
            achievements = new byte[Achievement.BYTES_REQUIRED];
        }
        stats.setAchievements(achievements);

        Timestamp firstSeen = rs.getTimestamp("first_seen");
        if (firstSeen != null) {
            stats.setFirstSeen(firstSeen.toLocalDateTime());
        }

        Timestamp lastSeen = rs.getTimestamp("last_seen");
        if (lastSeen != null) {
            stats.setLastSeen(lastSeen.toLocalDateTime());
        }

        return stats;
    }

    @Override
    public synchronized void createPlayerStats(int playerId) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT IGNORE INTO bot_player_stats (player_id, achievements) VALUES (?, ?)")) {
                statement.setInt(1, playerId);
                statement.setBytes(2, new byte[Achievement.BYTES_REQUIRED]);
                statement.executeUpdate();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to create bot player stats", exp);
        }
    }

    @Override
    public synchronized void recordGameResult(int playerId, boolean won, int routeScore,
                                              int damage, int forceRemaining, int timeSeconds) {
        // Ensure stats record exists
        createPlayerStats(playerId);

        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            if (won) {
                // Update wins, cumulative score, and potentially best records
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE bot_player_stats SET " +
                        "wins = wins + 1, " +
                        "games_played = games_played + 1, " +
                        "total_ast_score = total_ast_score + ?, " +
                        "best_route_score = GREATEST(best_route_score, ?), " +
                        "best_damage = GREATEST(best_damage, ?), " +
                        "best_force_remaining = GREATEST(best_force_remaining, ?), " +
                        "best_time_seconds = CASE " +
                        "  WHEN best_time_seconds IS NULL THEN ? " +
                        "  WHEN ? < best_time_seconds THEN ? " +
                        "  ELSE best_time_seconds END, " +
                        "last_seen = CURRENT_TIMESTAMP " +
                        "WHERE player_id = ?")) {
                    statement.setInt(1, routeScore);
                    statement.setInt(2, routeScore);
                    statement.setInt(3, damage);
                    statement.setInt(4, forceRemaining);
                    statement.setInt(5, timeSeconds);
                    statement.setInt(6, timeSeconds);
                    statement.setInt(7, timeSeconds);
                    statement.setInt(8, playerId);
                    statement.executeUpdate();
                }
            } else {
                // Update losses and damage/force records (can still set personal bests in a loss)
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE bot_player_stats SET " +
                        "losses = losses + 1, " +
                        "games_played = games_played + 1, " +
                        "best_damage = GREATEST(best_damage, ?), " +
                        "best_force_remaining = GREATEST(best_force_remaining, ?), " +
                        "last_seen = CURRENT_TIMESTAMP " +
                        "WHERE player_id = ?")) {
                    statement.setInt(1, damage);
                    statement.setInt(2, forceRemaining);
                    statement.setInt(3, playerId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to record game result", exp);
        }
    }

    @Override
    public synchronized boolean unlockAchievement(int playerId, int achievementBit) {
        if (achievementBit < 0 || achievementBit >= Achievement.TOTAL_ACHIEVEMENTS) {
            return false;
        }

        // First check if already unlocked
        if (hasAchievement(playerId, achievementBit)) {
            return false;
        }

        // Ensure stats record exists
        createPlayerStats(playerId);

        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            // Get current achievements
            byte[] achievements = null;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT achievements FROM bot_player_stats WHERE player_id = ?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        achievements = rs.getBytes("achievements");
                    }
                }
            }

            if (achievements == null) {
                achievements = new byte[Achievement.BYTES_REQUIRED];
            }

            // Set the bit
            int byteIndex = achievementBit / 8;
            int bitIndex = achievementBit % 8;
            if (byteIndex < achievements.length) {
                achievements[byteIndex] |= (byte) (1 << bitIndex);
            }

            // Update
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE bot_player_stats SET achievements = ? WHERE player_id = ?")) {
                statement.setBytes(1, achievements);
                statement.setInt(2, playerId);
                statement.executeUpdate();
            }

            return true;
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to unlock achievement", exp);
        }
    }

    @Override
    public boolean hasAchievement(int playerId, int achievementBit) {
        if (achievementBit < 0 || achievementBit >= Achievement.TOTAL_ACHIEVEMENTS) {
            return false;
        }

        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT achievements FROM bot_player_stats WHERE player_id = ?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        byte[] achievements = rs.getBytes("achievements");
                        if (achievements == null) {
                            return false;
                        }
                        int byteIndex = achievementBit / 8;
                        int bitIndex = achievementBit % 8;
                        if (byteIndex >= achievements.length) {
                            return false;
                        }
                        return (achievements[byteIndex] & (1 << bitIndex)) != 0;
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to check achievement", exp);
        }
        return false;
    }

    @Override
    public Set<Integer> getPlayerAchievements(int playerId) {
        Set<Integer> result = new HashSet<>();
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT achievements FROM bot_player_stats WHERE player_id = ?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        byte[] achievements = rs.getBytes("achievements");
                        if (achievements != null) {
                            for (int i = 0; i < Achievement.TOTAL_ACHIEVEMENTS; i++) {
                                int byteIndex = i / 8;
                                int bitIndex = i % 8;
                                if (byteIndex < achievements.length &&
                                    (achievements[byteIndex] & (1 << bitIndex)) != 0) {
                                    result.add(i);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player achievements", exp);
        }
        return result;
    }

    @Override
    public int getAchievementCount(int playerId) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT achievements FROM bot_player_stats WHERE player_id = ?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        byte[] achievements = rs.getBytes("achievements");
                        if (achievements == null) {
                            return 0;
                        }
                        int count = 0;
                        for (byte b : achievements) {
                            count += Integer.bitCount(b & 0xFF);
                        }
                        return count;
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get achievement count", exp);
        }
        return 0;
    }

    @Override
    public LeaderboardEntry getBestDamageRecord() {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.best_damage, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.best_damage > 0 " +
                    "ORDER BY b.best_damage DESC LIMIT 1")) {
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return extractLeaderboardEntry(rs, "best_damage");
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get best damage record", exp);
        }
        return null;
    }

    @Override
    public LeaderboardEntry getBestRouteScoreRecord() {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.best_route_score, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.best_route_score > 0 " +
                    "ORDER BY b.best_route_score DESC LIMIT 1")) {
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return extractLeaderboardEntry(rs, "best_route_score");
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get best route score record", exp);
        }
        return null;
    }

    @Override
    public LeaderboardEntry getFastestWinRecord() {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.best_time_seconds, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.best_time_seconds IS NOT NULL " +
                    "ORDER BY b.best_time_seconds ASC LIMIT 1")) {
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return extractLeaderboardEntry(rs, "best_time_seconds");
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get fastest win record", exp);
        }
        return null;
    }

    @Override
    public List<LeaderboardEntry> getTopPlayersByAstScore(int limit) {
        List<LeaderboardEntry> result = new ArrayList<>();
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.total_ast_score, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.total_ast_score > 0 " +
                    "ORDER BY b.total_ast_score DESC LIMIT ?")) {
                statement.setInt(1, limit);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        result.add(extractLeaderboardEntry(rs, "total_ast_score"));
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get top players by ast score", exp);
        }
        return result;
    }

    @Override
    public List<LeaderboardEntry> getTopPlayersByWins(int limit) {
        List<LeaderboardEntry> result = new ArrayList<>();
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.wins, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.wins > 0 " +
                    "ORDER BY b.wins DESC LIMIT ?")) {
                statement.setInt(1, limit);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        result.add(extractLeaderboardEntry(rs, "wins"));
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get top players by wins", exp);
        }
        return result;
    }

    @Override
    public List<LeaderboardEntry> getTopPlayersByGamesPlayed(int limit) {
        List<LeaderboardEntry> result = new ArrayList<>();
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT b.player_id, p.name, b.games_played, b.wins, b.losses, " +
                    "b.games_played, b.total_ast_score " +
                    "FROM bot_player_stats b " +
                    "JOIN player p ON b.player_id = p.id " +
                    "WHERE b.games_played > 0 " +
                    "ORDER BY b.games_played DESC LIMIT ?")) {
                statement.setInt(1, limit);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        result.add(extractLeaderboardEntry(rs, "games_played"));
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get top players by games played", exp);
        }
        return result;
    }

    private LeaderboardEntry extractLeaderboardEntry(ResultSet rs, String valueColumn) throws SQLException {
        LeaderboardEntry entry = new LeaderboardEntry();
        entry.setPlayerId(rs.getInt("player_id"));
        entry.setPlayerName(rs.getString("name"));
        entry.setValue(rs.getInt(valueColumn));
        entry.setWins(rs.getInt("wins"));
        entry.setLosses(rs.getInt("losses"));
        entry.setGamesPlayed(rs.getInt("games_played"));
        entry.setTotalAstScore(rs.getInt("total_ast_score"));
        return entry;
    }
}
