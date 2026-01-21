package com.gempukku.swccgo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A server setting database access object that accesses the database.
 */
public class DbGempSettingDAO implements GempSettingDAO {
    private final DbAccess _dbAccess;

    public static String PrivateGamesEnabledFlag = "privateGamesEnabled";
    public static String InGameStatisticsFlag = "inGameStatistics";
    public static String BonusAbilitiesEnabledFlag = "bonusAbilitiesEnabled";
    public static String NewAccountRegistrationEnabled = "newAccountRegistrationEnabled";
    public static String AiTablesEnabledFlag = "aiTablesEnabled";

    /**
     * Creates a gemp_setting data access object that access the database.
     * @param dbAccess the database access
     */
    public DbGempSettingDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public boolean privateGamesEnabled() {
        return getFlag(PrivateGamesEnabledFlag);
    }

    @Override
    public void setPrivateGamesEnabled(boolean enabled) {
        setFlag(PrivateGamesEnabledFlag, enabled);
    }

    @Override
    public boolean inGameStatisticsEnabled() {
        return getFlag(InGameStatisticsFlag);
    }

    @Override
    public void setInGameStatisticsEnabled(boolean enabled) {
        setFlag(InGameStatisticsFlag, enabled);
    }

    @Override
    public boolean bonusAbilitiesEnabled() {
        return getFlag(BonusAbilitiesEnabledFlag);
    }

    @Override
    public void setBonusAbilitiesEnabled(boolean enabled) {
        setFlag(BonusAbilitiesEnabledFlag, enabled);
    }

    @Override
    public boolean newAccountRegistrationEnabled() {
        return getFlag(NewAccountRegistrationEnabled);
    }

    @Override
    public void setNewAccountRegistrationEnabled(boolean enabled) {
        setFlag(NewAccountRegistrationEnabled, enabled);
    }

    @Override
    public boolean aiTablesEnabled() {
        return getFlagOrDefault(AiTablesEnabledFlag, false);
    }

    @Override
    public void setAiTablesEnabled(boolean enabled) {
        setFlag(AiTablesEnabledFlag, enabled);
    }

    @Override
    public void setFlag(String name, boolean enabled) {
        try {
            try (Connection connection = _dbAccess.getDataSource().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO gemp_settings (settingName, settingValue) VALUES(?, ?) " +
                            "ON DUPLICATE KEY UPDATE settingValue = ?")) {
                    statement.setString(1, name);
                    statement.setBoolean(2, enabled);
                    statement.setBoolean(3, enabled);

                    statement.executeUpdate();
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to set '" + name + "' setting", exp);
        }
    }

    @Override
    public boolean toggleFlag(String name) {
        try {
            try (Connection connection = _dbAccess.getDataSource().getConnection()) {
                connection.createStatement().executeUpdate(
                        "UPDATE gemp_settings SET settingValue = 1-settingValue WHERE settingName = '" + name + "'");
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to toggle '" + name + "' setting", exp);
        }

        return getFlag(name);
    }

    @Override
    public boolean getFlag(String name) {
        boolean enabled = false;
        try {
            try (Connection connection = _dbAccess.getDataSource().getConnection()) {
                try (ResultSet result = connection.createStatement().executeQuery(
                        "SELECT settingValue FROM gemp_settings WHERE settingName = '" + name + "'")) {
                    while (result.next()) {
                        enabled = result.getBoolean(1);
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get setting '" + name + "': ", exp);
        }
        return enabled;
    }

    private boolean getFlagOrDefault(String name, boolean defaultValue) {
        boolean enabled = defaultValue;
        boolean hasValue = false;
        try {
            try (Connection connection = _dbAccess.getDataSource().getConnection()) {
                try (ResultSet result = connection.createStatement().executeQuery(
                        "SELECT settingValue FROM gemp_settings WHERE settingName = '" + name + "'")) {
                    while (result.next()) {
                        enabled = result.getBoolean(1);
                        hasValue = true;
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get setting '" + name + "': ", exp);
        }
        return hasValue ? enabled : defaultValue;
    }
}
