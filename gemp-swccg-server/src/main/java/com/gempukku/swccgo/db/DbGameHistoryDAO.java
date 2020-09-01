package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.GameHistoryEntry;
import com.gempukku.swccgo.game.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DbGameHistoryDAO implements GameHistoryDAO {
    private DbAccess _dbAccess;

    public DbGameHistoryDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    public void addGameHistory(String winner, String loser, String winReason, String loseReason, String winRecordingId, String loseRecordingId, String formatName, String tournament, String winnerDeckName, String loserDeckName, String winnerDeckArchetype, String loserDeckArchetype, String winnerSide, Date startDate, Date endDate) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("insert into game_history (winner, loser, win_reason, lose_reason, win_recording_id, lose_recording_id, format_name, tournament, winner_deck_name, loser_deck_name, winner_deck_archetype, loser_deck_archetype, winner_side, start_date, end_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                try {
                    statement.setString(1, winner);
                    statement.setString(2, loser);
                    statement.setString(3, winReason);
                    statement.setString(4, loseReason);
                    statement.setString(5, winRecordingId);
                    statement.setString(6, loseRecordingId);
                    statement.setString(7, formatName);
                    statement.setString(8, tournament);
                    statement.setString(9, winnerDeckName);
                    statement.setString(10, loserDeckName);
                    statement.setString(11, (winnerDeckArchetype==null?"":winnerDeckArchetype));
                    statement.setString(12, (loserDeckArchetype==null?"":loserDeckArchetype));
                    statement.setString(13, winnerSide);
                    statement.setLong(14, startDate.getTime());
                    statement.setLong(15, endDate.getTime());

                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of player games", exp);
        }
    }

    public List<GameHistoryEntry> getGameHistoryForPlayer(Player player, int start, int count) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select winner, loser, win_reason, lose_reason, win_recording_id, lose_recording_id, format_name, tournament, winner_deck_name, loser_deck_name, start_date, end_date from game_history where winner=? or loser=? order by end_date desc limit ?, ?");
                try {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getName());
                    statement.setInt(3, start);
                    statement.setInt(4, count);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<GameHistoryEntry> result = new LinkedList<GameHistoryEntry>();
                        while (rs.next()) {
                            String winner = rs.getString(1);
                            String loser = rs.getString(2);
                            String winReason = rs.getString(3);
                            String loseReason = rs.getString(4);
                            String winRecordingId = rs.getString(5);
                            String loseRecordingId = rs.getString(6);
                            String formatName = rs.getString(7);
                            String tournament = rs.getString(8);
                            String winnerDeckName = rs.getString(9);
                            String loserDeckName = rs.getString(10);
                            Date startDate = new Date(rs.getLong(11));
                            Date endDate = new Date(rs.getLong(12));

                            GameHistoryEntry entry = new GameHistoryEntry(winner, winReason, winRecordingId, loser, loseReason, loseRecordingId, formatName, tournament, winnerDeckName, loserDeckName, startDate, endDate);
                            result.add(entry);
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of player games", exp);
        }
    }

    public int getGameHistoryForPlayerCount(Player player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select count(*) from game_history where winner=? or loser=?");
                try {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getName());
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return rs.getInt(1);
                        else
                            return -1;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of player games", exp);
        }
    }

    public int getActivePlayersCount() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select count(*) from (SELECT winner FROM game_history union select loser from game_history) as u");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return rs.getInt(1);
                        else
                            return -1;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of active players", exp);
        }
    }

    public int getActivePlayersCount(long from, long duration) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select count(*) from (SELECT winner FROM game_history where end_date>=? and end_date<? union select loser from game_history where end_date>=? and end_date<?) as u");
                try {
                    statement.setLong(1, from);
                    statement.setLong(2, from + duration);
                    statement.setLong(3, from);
                    statement.setLong(4, from + duration);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return rs.getInt(1);
                        else
                            return -1;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of active players", exp);
        }
    }

    public int getGamesPlayedCount() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select count(*) from game_history");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return rs.getInt(1);
                        else
                            return -1;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of games played", exp);
        }
    }

    public int getGamesPlayedCount(long from, long duration) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select count(*) from game_history where end_date>=? and end_date<?");
                try {
                    statement.setLong(1, from);
                    statement.setLong(2, from + duration);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next())
                            return rs.getInt(1);
                        else
                            return -1;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of games played", exp);
        }
    }

    public Map<String, Integer> getCasualGamesPlayedPerFormat(long from, long duration) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select count(*), format_name from game_history where (tournament is null or tournament = 'Casual') and end_date>=? and end_date<? group by format_name");
                try {
                    statement.setLong(1, from);
                    statement.setLong(2, from + duration);
                    ResultSet rs = statement.executeQuery();
                    Map<String, Integer> result = new HashMap<String, Integer>();
                    try {
                        while (rs.next()) {
                            result.put(rs.getString(2), rs.getInt(1));
                        }
                    } finally {
                        rs.close();
                    }
                    return result;
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of games played", exp);
        }
    }

    public List<PlayerStatistic> getCasualPlayerStatistics(Player player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select deck_name, format_name, sum(win), sum(lose) from" +
                                " (select winner_deck_name as deck_name, format_name, 1 as win, 0 as lose from game_history where winner=? and (tournament is null or tournament = 'Casual') and (win_reason <> 'Game cancelled due to error')" +
                                " union all select loser_deck_name as deck_name, format_name, 0 as win, 1 as lose from game_history where loser=? and (tournament is null or tournament = 'Casual') and (win_reason <> 'Game cancelled due to error')) as u" +
                                " group by deck_name, format_name order by format_name, deck_name");
                try {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getName());
                    ResultSet rs = statement.executeQuery();
                    List<PlayerStatistic> result = new LinkedList<PlayerStatistic>();
                    try {
                        while (rs.next())
                            result.add(new PlayerStatistic(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
                    } finally {
                        rs.close();
                    }
                    return result;
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of games played", exp);
        }
    }

    public List<PlayerStatistic> getCompetitivePlayerStatistics(Player player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement(
                        "select deck_name, format_name, sum(win), sum(lose) from" +
                                " (select winner_deck_name as deck_name, format_name, 1 as win, 0 as lose from game_history where winner=? and (tournament is not null and tournament <> 'Casual') and (win_reason <> 'Game cancelled due to error')" +
                                " union all select loser_deck_name as deck_name, format_name, 0 as win, 1 as lose from game_history where loser=? and (tournament is not null and tournament <> 'Casual') and (win_reason <> 'Game cancelled due to error')) as u" +
                                " group by deck_name, format_name order by format_name, deck_name");
                try {
                    statement.setString(1, player.getName());
                    statement.setString(2, player.getName());
                    ResultSet rs = statement.executeQuery();
                    List<PlayerStatistic> result = new LinkedList<PlayerStatistic>();
                    try {
                        while (rs.next())
                            result.add(new PlayerStatistic(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4)));
                    } finally {
                        rs.close();
                    }
                    return result;
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of games played", exp);
        }
    }
}
