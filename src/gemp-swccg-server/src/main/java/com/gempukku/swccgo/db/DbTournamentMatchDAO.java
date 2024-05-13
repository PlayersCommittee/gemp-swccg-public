package com.gempukku.swccgo.db;

import com.gempukku.swccgo.tournament.TournamentMatch;
import com.gempukku.swccgo.tournament.TournamentMatchDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbTournamentMatchDAO implements TournamentMatchDAO {
    private DbAccess _dbAccess;

    public DbTournamentMatchDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void addMatch(String tournamentId, int round, String playerOne, String playerTwo) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into tournament_match (tournament_id, round, player_one, player_two) values (?, ?, ?, ?)");
                try {
                    statement.setString(1, tournamentId);
                    statement.setInt(2, round);
                    statement.setString(3, playerOne);
                    statement.setString(4, playerTwo);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void setMatchResult(String tournamentId, int round, String winner) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update tournament_match set winner=? where tournament_id=? and (player_one=? or player_two=?)");
                try {
                    statement.setString(1, winner);
                    statement.setString(2, tournamentId);
                    statement.setString(3, winner);
                    statement.setString(4, winner);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public List<TournamentMatch> getMatches(String tournamentId) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select player_one, player_two, winner, round from tournament_match where tournament_id=? and player_two <> 'bye'");
                try {
                    statement.setString(1, tournamentId);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentMatch> result = new ArrayList<TournamentMatch>();
                        while (rs.next()) {
                            String playerOne = rs.getString(1);
                            String playerTwo = rs.getString(2);
                            String winner = rs.getString(3);
                            int round = rs.getInt(4);

                            result.add(new TournamentMatch(playerOne, playerTwo, winner, round));
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
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void addBye(String tournamentId, String player, int round) {
        addMatch(tournamentId, round, player, "bye");
    }

    @Override
    public Map<String, Integer> getPlayerByes(String tournamentId) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select player_one from tournament_match where tournament_id=? and player_two = 'bye'");
                try {
                    statement.setString(1, tournamentId);
                    ResultSet rs = statement.executeQuery();
                    try {
                        Map<String, Integer> result = new HashMap<String, Integer>();
                        while (rs.next()) {
                            String player = rs.getString(1);
                            Integer count = result.get(player);
                            if (count == null)
                                count = 0;
                            result.put(player, count+1);
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
            throw new RuntimeException(exp);
        }
    }
}
