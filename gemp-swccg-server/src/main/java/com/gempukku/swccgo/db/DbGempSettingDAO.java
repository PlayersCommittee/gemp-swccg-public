package com.gempukku.swccgo.db;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A server setting database access object that accesses the database.
 */
public class DbGempSettingDAO implements GempSettingDAO {
    private DbAccess _dbAccess;
    private boolean _newAccountRegistrationEnabled = true;

    /**
     * Creates a gemp_setting data access object that access the database.
     * @param dbAccess the database access
     */
    public DbGempSettingDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public boolean privateGamesEnabled() {
        boolean toReturn = false;
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                ResultSet result = connection.createStatement().executeQuery("select settingValue from gemp_settings where settingName = 'privateGamesEnabled'");
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
                connection.createStatement().executeUpdate("update gemp_settings set settingValue = 1-settingValue where settingName = 'privateGamesEnabled'");
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to edit privateGamesEnabled setting", exp);
        }
    }

    @Override
    public boolean inGameStatisticsEnabled() {
        boolean toReturn = false;
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                ResultSet result = connection.createStatement().executeQuery("select settingValue from gemp_settings where settingName = 'inGameStatistics'");
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
    public void toggleInGameStatisticsEnabled() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                connection.createStatement().executeUpdate("update gemp_settings set settingValue = 1-settingValue where settingName = 'inGameStatistics'");
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to edit inGameStatistics setting", exp);
        }
    }


    @Override
    public boolean bonusAbilitiesEnabled() {
        boolean toReturn = false;
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                ResultSet result = connection.createStatement().executeQuery("select settingValue from gemp_settings where settingName = 'bonusAbilitiesEnabled'");
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
    public void toggleBonusAbilitiesEnabled() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                connection.createStatement().executeUpdate("update gemp_settings set settingValue = 1-settingValue where settingName = 'bonusAbilitiesEnabled'");
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to edit bonusAbilitiesEnabled setting", exp);
        }
    }

    @Override
    public boolean newAccountRegistrationEnabled() {
        return _newAccountRegistrationEnabled;
    }

    @Override
    public void toggleNewAccountRegitrationEnabled() {
        _newAccountRegistrationEnabled = !_newAccountRegistrationEnabled;
    }
}
