package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DbLeagueParticipationDAO implements LeagueParticipationDAO {
    private DbAccess _dbAccess;

    public DbLeagueParticipationDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    public void userJoinsLeague(String leagueId, Player player, String remoteAddr) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into league_participation (league_type, player_name, join_ip) values (?,?,?)");
                try {
                    statement.setString(1, leagueId);
                    statement.setString(2, player.getName());
                    statement.setString(3, remoteAddr);
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

    public Collection<String> getUsersParticipating(String leagueId) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("select player_name from league_participation where league_type=?");
                try {
                    statement.setString(1, leagueId);
                    final ResultSet rs = statement.executeQuery();
                    try {
                        Set<String> result = new HashSet<String>();
                        while (rs.next())
                            result.add(rs.getString(1));
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
}
