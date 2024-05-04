package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;
import com.mysql.cj.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A player database access object that accesses the database.
 */
public class DbPlayerDAO implements PlayerDAO {
    private final String _selectPlayer = "select id, name, password, type, last_login_reward, banned_until, create_ip, last_ip from player";
    private final String _notDeactivated = " and not type like '%" + Player.Type.DEACTIVATED + "%'";
    private DbAccess _dbAccess;

    /**
     * Creates a player data access object that access the database.
     * @param dbAccess the database access
     */
    public DbPlayerDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public Player getPlayer(int id) {
        try {
            return getPlayerFromDBById(id);
        } catch (SQLException exp) {
            throw new RuntimeException("Error while retrieving player", exp);
        }
    }

    @Override
    public Player getPlayer(String playerName) {
        return getPlayer(playerName, false);
    }

    @Override
    public Player getPlayer(String playerName, boolean includeDeactivated) {
        try {
            return getPlayerFromDBByName(playerName, includeDeactivated);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player from DB", exp);
        }
    }

    @Override
    public synchronized boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {

        Player player = getPlayer(playerName);
        if(player != null && StringUtils.isNullOrEmpty(player.getPassword())) {
            //This player has had their password reset and just needs to get it re-defined.

            try (Connection conn = _dbAccess.getDataSource().getConnection()) {
                try (PreparedStatement statement = conn.prepareStatement(
                        "UPDATE player SET password=? WHERE name=?")) {
                    statement.setString(1, encodePassword(password));
                    statement.setString(2, playerName);
                    statement.executeUpdate();
                    return true;
                }
            }
        }

        if (!validateNewUser(playerName)) {
            return false;
        }

        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO player (name, password, type, create_ip) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, playerName);
                statement.setString(2, encodePassword(password));
                statement.setString(3, Player.Type.UNBANNED.getValue());
                statement.setString(4, remoteAddr);
                statement.execute();
                return true;
            }
        }
    }

    @Override
    public synchronized Player loginPlayer(String playerName, String password) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement1 = conn.prepareStatement(
                    _selectPlayer + " WHERE name=? AND (password='' OR password=?)" + _notDeactivated)) {
                statement1.setString(1, playerName);
                statement1.setString(2, encodePassword(password));
                try (ResultSet rs = statement1.executeQuery()) {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else
                        return null;
                }
            }
        }
    }

    @Override
    public synchronized boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("update player set last_ip=? where name=?")) {
                statement.setString(1, remoteAddr);
                statement.setString(2, playerName);
                return (statement.executeUpdate() == 1);
            }
        }
    }

    @Override
    public synchronized boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set last_login_reward=? where id=?" + (previousReward != null ? " and last_login_reward=?" : ""))) {
                statement.setInt(1, currentReward);
                statement.setInt(2, player.getId());
                if (previousReward != null) {
                    statement.setInt(3, previousReward);
                }
                if (statement.executeUpdate() == 1) {
                    player.setLastLoginReward(currentReward);
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public synchronized boolean resetUserPassword(String playerName) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("UPDATE player SET password='' WHERE name=?")) {
                statement.setString(1, playerName);
                return statement.executeUpdate() == 1;
            }
        }
    }


    @Override
    public synchronized boolean setPlayerFlag(String playerName, Player.Type flag, boolean status) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Add/remove type
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            if (status) {
                if (!types.contains(flag)) {
                    types.add(flag);
                }
            } else {
                types.remove(flag);
            }

            try (PreparedStatement statement = conn.prepareStatement("UPDATE player SET type=? WHERE id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public List<Player> findPlayersWithFlag(Player.Type flag) {
        try {
            List<Player> players = new LinkedList<Player>();
            try (Connection conn = _dbAccess.getDataSource().getConnection()) {
                try (PreparedStatement statement = conn.prepareStatement(
                        _selectPlayer + " WHERE type LIKE '%" + flag + "%'" )) {
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            players.add(getPlayerFromResultSet(rs));
                        }
                        return players;
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Error while retrieving players with flag " + flag, exp);
        }
    }

    @Override
    public boolean banPlayerPermanently(String playerName) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=null where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to specified date)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=? where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setLong(2, dateTo);
                statement.setInt(3, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public boolean unBanPlayer(String playerName) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Add unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.add(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=null where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Player> findSimilarAccounts(String playerName) {
        try {
            Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return Collections.emptyList();
            }
            Set<Player> players = new HashSet<>();
            players.add(player);
            int prevCount = 0;
            int curCount = players.size();
            while (curCount > prevCount) {
                players.addAll(findSimilarAccountsInner(players));
                prevCount = curCount;
                curCount = players.size();
            }
            return Collections.unmodifiableList(new LinkedList<>(players));
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to find similar accounts from DB", exp);
        }
    }

    /**
     * Find all accounts that share various factors with the given player.  Looks for matching passwords and matching
     * creation/login IP addresses.
     * This is intended to be used progressively, so that the return value is fed back into the function until no further
     * accounts are found.  This is to find similar accounts, and accounts similar to those accounts, and so on until
     * no further suspicious activity is found.
     * This was temporarily (for five years) kneecapped due to IP addresses not being forwarded properly, meaning that
     * the server's IP was used instead of the user's IP.
     * This has likely poisoned the player db to the point that automatic signup prevention probably cannot be used with
     * this function. See: https://github.com/PlayersCommittee/gemp-swccg/pull/87
     * @param players the player accounts to compare against the existing player base
     * @return the similar player accounts found
     */
    private Set<Player> findSimilarAccountsInner(Set<Player> players) throws SQLException {
        Set<Player> similarPlayers = new HashSet<>(players);
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            Set<String> ipAddresses = new HashSet<>();

            // Determine IP addresses to compare
            for (Player player : players) {
                if (player.getCreateIp() != null) {
                    ipAddresses.add(player.getCreateIp());
                }
                if (player.getLastIp() != null) {
                    ipAddresses.add(player.getLastIp());
                }
            }
            if (ipAddresses.isEmpty()) {
                return similarPlayers;
            }

            //Build the where clause fragments for ip address comparison
            StringBuilder whereIP = new StringBuilder(" IN (");
            for (String ipAddr : ipAddresses) {
                whereIP.append("'").append(ipAddr).append("', ");
            }
            whereIP.setLength(whereIP.length() - 2);
            whereIP.append(")");

            //Build the where clause fragments for password comparison
            StringBuilder wherePassword = new StringBuilder(" IN (");
            for (String ipAddr : ipAddresses) {
                wherePassword.append("'").append(ipAddr).append("', ");
            }
            wherePassword.setLength(wherePassword.length() - 2);
            wherePassword.append(")");

            String sql = _selectPlayer + " WHERE password" + wherePassword +
                    " OR (create_ip" + whereIP + " or last_ip" + whereIP + ")" +
                    " LIMIT 200";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        similarPlayers.add(getPlayerFromResultSet(rs));
                    }
                    return similarPlayers;
                }
            }
        }
    }

    /**
     * Generate a player from the result set.
     * @param rs the result set.
     * @return the player
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        String password = rs.getString(3);
        String type = rs.getString(4);
        Integer lastLoginReward = rs.getInt(5);
        if (rs.wasNull())
            lastLoginReward = null;
        Long bannedUntilLong = rs.getLong(6);
        if (rs.wasNull())
            bannedUntilLong = null;

        Date bannedUntil = null;
        if (bannedUntilLong != null)
            bannedUntil = new Date(bannedUntilLong);
        String createIp = rs.getString(7);
        String lastIp = rs.getString(8);

        return new Player(id, name, password, type, lastLoginReward, bannedUntil, createIp, lastIp);
    }

    /**
     * Checks to see if the requested username is a valid new player name.
     * @param playerName the player name
     * @return true if valid, otherwise false
     * @throws SQLException an SQL exception
     * @throws LoginInvalidException an invalid login exception
     */
    private boolean validateNewUser(String playerName) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
        final String validLoginChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        if (playerName.length() < 2 || playerName.length() > 10) {
            throw new LoginInvalidException();
        }

        for (int i = 0; i < playerName.length(); i++) {
            char c = playerName.charAt(i);
            if (!validLoginChars.contains("" + c)) {
                throw new LoginInvalidException();
            }
        }

        String lowerCase = playerName.toLowerCase();
        if (lowerCase.startsWith("admin") || lowerCase.startsWith("guest") || lowerCase.startsWith("system") || lowerCase.startsWith("bye")) {
            return false;
        }

        if(getPlayerFromDBByName(playerName, true) != null)
            throw new LoginInvalidException();

        //Deactivating the automatic rejection.  Practically everyone has the same server IP as their origin, so
        // this comparison doesn't work.

//        int tickMarks = 0;
//        for (Player similarPlayer : findSimilarAccounts(playerName)) {
//            if (similarPlayer.getId() != 0) {
//                if (!similarPlayer.hasType(Player.Type.UNBANNED) && similarPlayer.getBannedUntil() == null){
//                    tickMarks+=10;
//                }
//                else {
//                    tickMarks++;
//                }
//            }
//        }

//        if (tickMarks >= 10) {
//            throw new RegisterNotAllowedException();
//        }
        return true;
    }

    /**
     * Encodes the password using SHA-256 hash.
     * @param password the password
     * @return the encoded password
     */
    private String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return convertToHexString(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts the byte array to a string of hex characters.
     * @param bytes the type array
     * @return the string of hex characters
     */
    private String convertToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Gets the player from the database by ID.
     * @param id the ID
     * @return the player, or null if no player found
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromDBById(int id) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where id=?" + _notDeactivated)) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Gets the player from the database by name.
     * @param playerName the player name
     * @param includeDeactivated true to include deactivated, otherwise false
     * @return the player, or null if no player found
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromDBByName(String playerName, boolean includeDeactivated) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    _selectPlayer + " where name=?" + (!includeDeactivated ? _notDeactivated : ""))) {
                statement.setString(1, playerName);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
