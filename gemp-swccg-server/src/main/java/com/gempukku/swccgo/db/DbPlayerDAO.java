package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

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
        try {
            return getPlayerFromDBByName(playerName, false);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player from DB", exp);
        }
    }

    @Override
    public synchronized boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
        boolean result = validateNewUser(playerName, remoteAddr);
        if (!result) {
            return false;
        }

        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("insert into player (name, password, type, create_ip) values (?, ?, ?, ?)");
            try {
                statement.setString(1, playerName);
                statement.setString(2, encodePassword(password));
                statement.setString(3, Player.Type.UNBANNED.getValue());
                statement.setString(4, remoteAddr);
                statement.execute();
                return true;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public synchronized Player loginPlayer(String playerName, String password) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            // Check if password was reset
            PreparedStatement statement1 = conn.prepareStatement(_selectPlayer + " where name=? and password=''" + _notDeactivated);
            try {
                statement1.setString(1, playerName);
                ResultSet rs = statement1.executeQuery();
                try {
                    if (rs.next()) {
                        // Set the new password
                        int id = rs.getInt(1);
                        PreparedStatement statement2 = conn.prepareStatement("update player set password=? where id=?");
                        try {
                            statement2.setString(1, encodePassword(password));
                            statement2.setInt(2, id);
                            statement2.execute();
                        } finally {
                            statement2.close();
                        }
                    }
                } finally {
                    rs.close();
                }
            } finally {
                statement1.close();
            }

            // Get the player
            PreparedStatement statement3 = conn.prepareStatement(_selectPlayer + " where name=? and password=?" + _notDeactivated);
            try {
                statement3.setString(1, playerName);
                statement3.setString(2, encodePassword(password));
                ResultSet rs = statement3.executeQuery();
                try {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else
                        return null;
                } finally {
                    rs.close();
                }
            } finally {
                statement3.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public synchronized boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("update player set last_ip=? where name=?");
            try {
                statement.setString(1, remoteAddr);
                statement.setString(2, playerName);
                return (statement.executeUpdate() == 1);
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public synchronized boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("update player set last_login_reward=? where id=?" + (previousReward != null ? " and last_login_reward=?" : ""));
            try {
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
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public synchronized boolean resetUserPassword(String playerName) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("update player set password='' where name=?");
            try {
                statement.setString(1, playerName);
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public synchronized boolean setPlayerAsPlaytester(String playerName, boolean playtester) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, false);
            if (player == null) {
                return false;
            }

            // Add/remove playtester type
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            if (playtester) {
                if (!types.contains(Player.Type.PLAY_TESTER)) {
                    types.add(Player.Type.PLAY_TESTER);
                }
            }
            else {
                types.remove(Player.Type.PLAY_TESTER);
            }

            PreparedStatement statement = conn.prepareStatement("update player set type=? where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Player> findPlaytesters() {
        try {
            List<Player> playtesters = new LinkedList<Player>();
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where type like '%" + Player.Type.PLAY_TESTER + "%'" + _notDeactivated);
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        while (rs.next()) {
                            playtesters.add(getPlayerFromResultSet(rs));
                        }
                        return playtesters;
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
            throw new RuntimeException("Error while retrieving playtesters", exp);
        }
    }

    @Override
    public synchronized boolean setPlayerAsCommentator(String playerName, boolean playtester) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, false);
            if (player == null) {
                return false;
            }

            // Add/remove commentator type
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            if (playtester) {
                if (!types.contains(Player.Type.COMMENTATOR)) {
                    types.add(Player.Type.COMMENTATOR);
                }
            }
            else {
                types.remove(Player.Type.COMMENTATOR);
            }

            PreparedStatement statement = conn.prepareStatement("update player set type=? where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Player> findCommentators() {
        try {
            List<Player> commentators = new LinkedList<Player>();
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where type like '%" + Player.Type.COMMENTATOR + "%'" + _notDeactivated);
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        while (rs.next()) {
                            commentators.add(getPlayerFromResultSet(rs));
                        }
                        return commentators;
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
            throw new RuntimeException("Error while retrieving commentators", exp);
        }
    }

    @Override
    public boolean setPlayerAsDeactivated(String playerName, boolean deactivate) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, !deactivate);
            if (player == null) {
                return false;
            }

            // Add/remove playtester type
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            if (deactivate) {
                if (!types.contains(Player.Type.DEACTIVATED)) {
                    types.add(Player.Type.DEACTIVATED);
                }
            }
            else {
                types.remove(Player.Type.DEACTIVATED);
            }

            PreparedStatement statement = conn.prepareStatement("update player set type=? where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public boolean banPlayerPermanently(String playerName) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, false);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            PreparedStatement statement = conn.prepareStatement("update player set type=?, banned_until=null where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, false);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to specified date)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            PreparedStatement statement = conn.prepareStatement("update player set type=?, banned_until=? where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setLong(2, dateTo);
                statement.setInt(3, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public boolean unBanPlayer(String playerName) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, false);
            if (player == null) {
                return false;
            }

            // Add unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.add(Player.Type.UNBANNED);

            PreparedStatement statement = conn.prepareStatement("update player set type=?, banned_until=null where id=?");
            try {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Player> findSimilarAccounts(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }
        Set<Player> players = new HashSet<Player>();
        players.add(player);
        int prevCount = 0;
        int curCount = players.size();
        while (curCount > prevCount) {
            players.addAll(findSimilarAccountsInner(players));
            prevCount = curCount;
            curCount = players.size();
        }
        return Collections.unmodifiableList(new LinkedList<Player>(players));
    }

    /**
     * Find all accounts that share IP addresses with the specified accounts.
     * @param players the player accounts
     * @return the similar player accounts
     */
    private Set<Player> findSimilarAccountsInner(Set<Player> players) {
        Set<Player> similarPlayers = new HashSet<Player>(players);
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                Set<String> ipAddresses = new HashSet<String>();

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

                StringBuilder whereInSb = new StringBuilder(" IN (");
                for (String ipAddr : ipAddresses) {
                    whereInSb.append("'").append(ipAddr).append("', ");
                }
                whereInSb.setLength(whereInSb.length() - 2);
                whereInSb.append(")");
                
                String sql = _selectPlayer + " where (create_ip" + whereInSb + " or last_ip" + whereInSb + ")" + _notDeactivated;

                PreparedStatement statement = conn.prepareStatement(sql);
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        //TODO: Add this back. Temporarily removed.
                        //See: https://github.com/PlayersCommittee/gemp-swccg/pull/87
                        //while (rs.next()) {
                        //    similarPlayers.add(getPlayerFromResultSet(rs));
                        //}
                        return similarPlayers;
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
            throw new RuntimeException("Unable to find similar accounts from DB", exp);
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
     * Validates the login.
     * @param playerName the player name
     * @param remoteAddr the IP address
     * @return true if valid, otherwise false
     * @throws SQLException an SQL exception
     * @throws LoginInvalidException an invalid login exception
     */
    private boolean validateNewUser(String playerName, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
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

        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            // Allow this to see deleted players so the name is not re-used
            PreparedStatement statement = conn.prepareStatement("select id, name from player where LOWER(name)=?");
            try {
                statement.setString(1, lowerCase);
                ResultSet rs = statement.executeQuery();
                try {
                    if (rs.next()) {
                        throw new LoginInvalidException();
                    }
                } finally {
                    rs.close();
                }
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }

        int tickMarks = 0;
        for (Player similarPlayer : findSimilarAccounts(new Player(0, playerName, null, null, null, null, remoteAddr, remoteAddr))) {
            if (similarPlayer.getId() != 0) {
                if (!similarPlayer.hasType(Player.Type.UNBANNED) && similarPlayer.getBannedUntil() == null){
                    tickMarks+=10;
                }
                else {
                    tickMarks++;
                }
            }
        }
        if (tickMarks >= 10) {
            throw new RegisterNotAllowedException();
        }
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
            return convertToHexString(digest.digest(password.getBytes("UTF-8")));
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
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where id=?" + _notDeactivated);
            try {
                statement.setInt(1, id);
                ResultSet rs = statement.executeQuery();
                try {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
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

    /**
     * Gets the player from the database by name.
     * @param playerName the player name
     * @param includeDeactivated true to include deactivated, otherwise false
     * @return the player, or null if no player found
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromDBByName(String playerName, boolean includeDeactivated) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where name=?" + (!includeDeactivated ? _notDeactivated : ""));
            try {
                statement.setString(1, playerName);
                ResultSet rs = statement.executeQuery();
                try {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
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
}
