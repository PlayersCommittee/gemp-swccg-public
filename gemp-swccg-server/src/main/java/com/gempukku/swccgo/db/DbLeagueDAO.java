package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbLeagueDAO implements LeagueDAO {
    private DbAccess _dbAccess;

    public DbLeagueDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    public void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes) throws SQLException, IOException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("insert into league (name, type, class, parameters, start, end, status, cost, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds, timePerPlayerMinutes, invitationOnly, registrationInfo) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try {
                statement.setString(1, name);
                statement.setString(2, type);
                statement.setString(3, clazz);
                statement.setString(4, parameters);
                statement.setInt(5, start);
                statement.setInt(6, endTime);
                statement.setInt(7, 0);
                statement.setInt(8, cost);
                statement.setBoolean(9, allowSpectators);
                statement.setBoolean(10, allowTimeExtensions);
                statement.setBoolean(11, showPlayerNames);
                statement.setInt(12, decisionTimeoutSeconds);
                statement.setInt(13, timePerPlayerMinutes);
                statement.setBoolean(14, invitationOnly);
                statement.setString(15, registrationInfo);
                statement.execute();
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    public List<League> loadActiveLeagues(SwccgCardBlueprintLibrary library, int currentTime) throws SQLException, IOException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("select name, type, class, parameters, status, cost, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds, timePerPlayerMinutes, invitationOnly, registrationInfo from league where end>=? order by start desc");
            try {
                statement.setInt(1, currentTime);
                ResultSet rs = statement.executeQuery();
                try {
                    List<League> activeLeagues = new ArrayList<League>();
                    while (rs.next()) {
                        String name = rs.getString(1);
                        String type = rs.getString(2);
                        String clazz = rs.getString(3);
                        String parameters = rs.getString(4);
                        int status = rs.getInt(5);
                        int cost = rs.getInt(6);
                        boolean allowSpectators = rs.getBoolean(7);
                        boolean allowTimeExtensions = rs.getBoolean(8);
                        boolean showPlayerNames = rs.getBoolean(9);
                        int decisionTimeoutSeconds = rs.getInt(10);
                        int timePerPlayerMinutes = rs.getInt(11);
                        boolean invitationOnly = rs.getBoolean(12);
                        String registrationInfo = rs.getString(13);
                        activeLeagues.add(new League(library, cost, name, type, clazz, parameters, status, allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo, decisionTimeoutSeconds, timePerPlayerMinutes));
                    }
                    return activeLeagues;
                } finally {
                    rs.close();
                }
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    public void setStatus(League league, int newStatus) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                String sql = "update league set status=? where type=?";

                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setInt(1, newStatus);
                    statement.setString(2, league.getType());
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update league status", exp);
        }
    }
}
