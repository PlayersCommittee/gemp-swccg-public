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
     * toggles the privateGamesEnabled setting
     */
    void togglePrivateGamesEnabled();

    /**
     * Gets whether the inGameStatisticsEnabled setting is on or off
     * @return true if on, false if off
     */
    boolean inGameStatisticsEnabled();

    /**
     * toggles the inGameStatisticsEnabled setting
     */
    void toggleInGameStatisticsEnabled();

    /**
     * Gets whether the bonusAbilities setting is on or off
     * @return true if on, false if off
     */
    boolean bonusAbilitiesEnabled();

    /**
     * Toggles the bonusAbilities setting
     */
    void toggleBonusAbilitiesEnabled();

    boolean newAccountRegistrationEnabled();

    void toggleNewAccountRegitrationEnabled();
}
