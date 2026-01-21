package com.gempukku.swccgo.db;

/**
 * An interface to represent the player database access object.
 */
public interface GempSettingDAO {

    /**
     * Gets whether the privateGamesEnabled setting is on or off
     * @return true if on, false if off
     */
    boolean privateGamesEnabled();

    /**
     * Sets the privateGamesEnabled setting to a given value
     */
    void setPrivateGamesEnabled(boolean enabled);

    /**
     * Gets whether the inGameStatisticsEnabled setting is on or off
     * @return true if on, false if off
     */
    boolean inGameStatisticsEnabled();

    void setInGameStatisticsEnabled(boolean enabled);

    /**
     * Gets whether the bonusAbilities setting is on or off
     * @return true if on, false if off
     */
    boolean bonusAbilitiesEnabled();

    /**
     * Toggles the bonusAbilities setting
     */
    void setBonusAbilitiesEnabled(boolean enabled);

    boolean newAccountRegistrationEnabled();

    void setNewAccountRegistrationEnabled(boolean enabled);

    boolean aiTablesEnabled();

    void setAiTablesEnabled(boolean enabled);

    void setFlag(String name, boolean enabled);
    boolean toggleFlag(String name);
    boolean getFlag(String name);
}
