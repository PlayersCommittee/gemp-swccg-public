package com.gempukku.swccgo.db;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.game.Player;
import org.apache.commons.collections.map.LRUMap;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A cached player database access object to help avoid unnecessary data access.
 */
public class CachedPlayerDAO implements PlayerDAO, Cached {
    private PlayerDAO _delegate;
    private Map<Integer, Player> _playerById = Collections.synchronizedMap(new LRUMap(500));
    private Map<String, Player> _playerByName = Collections.synchronizedMap(new LRUMap(500));
    private Map<String, List<String>> _similarAccountsByName = Collections.synchronizedMap(new LRUMap(500));

    /**
     * Creates a cached player database access object.
     * @param delegate the player database access object
     */
    public CachedPlayerDAO(PlayerDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _playerById.clear();
        _playerByName.clear();
        _similarAccountsByName.clear();
    }

    @Override
    public int getItemCount() {
        return _playerById.size() + _playerByName.size() + _similarAccountsByName.size();
    }

    @Override
    public Player getPlayer(int id) {
        Player player = _playerById.get(id);
        if (player == null) {
            player = _delegate.getPlayer(id);
            if (player != null) {
                _playerById.put(id, player);
                _playerByName.put(player.getName(), player);
            }
        }
        return player;
    }

    @Override
    public Player getPlayer(String playerName) {
        Player player = _playerByName.get(playerName);
        if (player == null) {
            player = _delegate.getPlayer(playerName);
            if (player != null) {
                _playerById.put(player.getId(), player);
                _playerByName.put(player.getName(), player);
            }
        }
        return player;
    }

    @Override
    public boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
        boolean registered = _delegate.registerPlayer(playerName, password, remoteAddr);
        if (registered) {
            removePlayerFromCacheByName(playerName);
        }
        return registered;
    }

    @Override
    public Player loginPlayer(String playerName, String password) throws SQLException {
        Player player = _delegate.loginPlayer(playerName, password);
        if (player != null) {
            _playerById.put(player.getId(), player);
            _playerByName.put(player.getName(), player);
        }
        return player;
    }

    @Override
    public boolean resetUserPassword(String playerName) throws SQLException {
        final boolean success = _delegate.resetUserPassword(playerName);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException {
        boolean success = _delegate.updateLastLoginIp(playerName, remoteAddr);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException {
        boolean success = _delegate.updateLastReward(player, previousReward, currentReward);
        if (success) {
            removePlayerFromCacheByName(player.getName());
        }
        return success;
    }

    @Override
    public boolean setPlayerAsPlaytester(String playerName, boolean playtester) throws SQLException {
        final boolean success = _delegate.setPlayerAsPlaytester(playerName, playtester);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public List<Player> findPlaytesters() {
        return _delegate.findPlaytesters();
    }

    @Override
    public boolean setPlayerAsCommentator(String playerName, boolean commentator) throws SQLException {
        final boolean success = _delegate.setPlayerAsCommentator(playerName, commentator);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public List<Player> findCommentators() {
        return _delegate.findCommentators();
    }

    @Override
    public boolean setPlayerAsDeactivated(String playerName, boolean deactivate) throws SQLException {
        final boolean success = _delegate.setPlayerAsDeactivated(playerName, deactivate);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public boolean banPlayerPermanently(String playerName) throws SQLException {
        final boolean success = _delegate.banPlayerPermanently(playerName);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException {
        final boolean success = _delegate.banPlayerTemporarily(playerName, dateTo);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public boolean unBanPlayer(String playerName) throws SQLException {
        final boolean success = _delegate.unBanPlayer(playerName);
        if (success) {
            removePlayerFromCacheByName(playerName);
        }
        return success;
    }

    @Override
    public List<Player> findSimilarAccounts(Player player) {
        List<Player> similarAccounts = null;
        List<String> similarAccountNames = _similarAccountsByName.get(player.getName());
        if (similarAccountNames == null) {
            similarAccountNames = new LinkedList<String>();
            similarAccounts = _delegate.findSimilarAccounts(player);
            for (Player similarAccount : similarAccounts) {
                similarAccountNames.add(similarAccount.getName());
            }
            _similarAccountsByName.put(player.getName(), Collections.unmodifiableList(similarAccountNames));
        }
        if (similarAccounts == null) {
            similarAccounts = new LinkedList<Player>();
            for (String similarAccountName : similarAccountNames) {
                similarAccounts.add(getPlayer(similarAccountName));
            }
        }
        return Collections.unmodifiableList(similarAccounts);
    }

    /**
     * Removes a player from the cache by player name and clears the similar accounts cache.
     * @param playerName the player name
     */
    private void removePlayerFromCacheByName(String playerName) {
        Player player = _playerByName.get(playerName);
        if (player != null) {
            _playerById.get(player.getId());
            _playerByName.remove(playerName);
        }
        _similarAccountsByName.clear();
    }
}
