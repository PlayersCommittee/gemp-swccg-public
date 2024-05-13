package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.LeagueMatchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DbLeagueMatchDAO implements LeagueMatchDAO {
    private DbAccess _dbAccess;

    public DbLeagueMatchDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public Collection<LeagueMatchResult> getLeagueMatches(String leagueId) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("select winner, loser, season_type, winner_side, loser_side from league_match where league_type=?");
                try {
                    statement.setString(1, leagueId);
                    ResultSet rs = statement.executeQuery();
                    try {
                        Set<LeagueMatchResult> result = new HashSet<LeagueMatchResult>();
                        while (rs.next()) {
                            String winner = rs.getString(1);
                            String loser = rs.getString(2);
                            String serie = rs.getString(3);
                            String winnerSide = rs.getString(4);
                            String loserSide = rs.getString(5);

                            result.add(new LeagueMatchResult(serie, winner, loser, winnerSide, loserSide));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
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
    public void addPlayedMatch(String leagueId, String serieId, String winner, String loser, String winnerSide, String loserSide) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into league_match (league_type, season_type, winner, loser, winner_side, loser_side) values (?, ?, ?, ?, ?, ?)");
                try {
                    statement.setString(1, leagueId);
                    statement.setString(2, serieId);
                    statement.setString(3, winner);
                    statement.setString(4, loser);
                    statement.setString(5, winnerSide);
                    statement.setString(6, loserSide);
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
}
