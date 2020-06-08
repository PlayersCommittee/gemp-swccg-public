package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface to represent the player database access object.
 */
public interface PlayerDAO {

    /**
     * Gets the player from the player ID.
     * @param id the player ID
     * @return the player
     */
    Player getPlayer(int id);

    /**
     * Gets the player from the player name.
     * @param playerName the player name
     * @return the player
     */
    Player getPlayer(String playerName);

    /**
     * Registers a new player.
     * @param playerName the player name
     * @param password the password
     * @param remoteAddr the IP address
     * @return true if registered, otherwise false
     * @throws SQLException an SQL exception
     * @throws LoginInvalidException an invalid login exception
     * @throws RegisterNotAllowedException a register not allowed exception
     */
    boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException;

    /**
     * Performs login of the player.
     * @param playerName the player name
     * @param password the password
     * @return the player if successful, otherwise null
     * @throws SQLException an SQL exception
     */
    Player loginPlayer(String playerName, String password) throws SQLException;

    /**
     * Updates the IP address that the player last used to login.
     * @param playerName the player name
     * @param remoteAddr the IP address
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException;

    /**
     * Updates the date indicator of the last time the player received a reward for logging in.
     * @param player the player
     * @param previousReward the previous date indicator, or null
     * @param currentReward the current date indicator
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException;

    /**
     * Resets the player's password so it can be changed on next login.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean resetUserPassword(String playerName) throws SQLException;

    /**
     * Sets whether a player is considered a playtester.
     * @param playerName the player name
     * @param playtester true if playtester, false if not playtester
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean setPlayerAsPlaytester(String playerName, boolean playtester) throws SQLException;

    /**
     * Gets a list of players that are playtesters.
     * @return the list of play testers
     */
    List<Player> findPlaytesters();

    /**
     * Deactivate the specified player.
     * @param playerName the player name
     * @param deactivate true to deactivate, false to reactivate
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean setPlayerAsDeactivated(String playerName, boolean deactivate) throws SQLException;

    /**
     * Permanently ban the specified player.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean banPlayerPermanently(String playerName) throws SQLException;

    /**
     * Temporarily ban the specified player.
     * @param playerName the player name
     * @param dateTo the date until which the player is banned
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException;

    /**
     * Removes the ban of the specified player.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean unBanPlayer(String playerName) throws SQLException;

    /**
     * Gets a list of players that appear to be similar (same IP address, etc.) to the specified player.
     * @param player the player
     * @return the list of similar players
     */
    List<Player> findSimilarAccounts(Player player);
}
