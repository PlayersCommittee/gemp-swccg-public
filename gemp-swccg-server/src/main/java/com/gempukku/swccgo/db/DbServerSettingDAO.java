package com.gempukku.swccgo.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A server setting database access object that accesses the database.
 */
public class DbServerSettingDAO implements ServerSettingDAO {
    private DbAccess _dbAccess;

    /**
     * Creates a player data access object that access the database.
     * @param dbAccess the database access
     */
    public DbServerSettingDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public boolean privateGamesEnabled() {
        boolean toReturn = false;
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                ResultSet result = connection.createStatement().executeQuery("select value from server_settings where id = 'privateGamesEnabled'");
                try {
                    while(result.next()) {
                        toReturn = result.getBoolean(1);
                    }
                } finally {
                    result.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get server setting", exp);
        }
        return toReturn;
    }

    @Override
    public void togglePrivateGamesEnabled() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                connection.createStatement().executeQuery("update server_settings set value = 1-value where setting = 'privateGamesEnabled'");
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to edit server setting", exp);
        }
    }
}
