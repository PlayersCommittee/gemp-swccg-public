package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;

public class AdminService {
    public static final int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private PlayerDAO _playerDAO;
    private LoggedUserHolder _loggedUserHolder;
    private IpBanDAO _ipBanDAO;

    public AdminService(PlayerDAO playerDAO, IpBanDAO ipBanDAO, LoggedUserHolder loggedUserHolder) {
        _playerDAO = playerDAO;
        _ipBanDAO = ipBanDAO;
        _loggedUserHolder = loggedUserHolder;
    }

    public boolean setUserFlag(String login, Player.Type flag, boolean status) throws SQLException {
        return _playerDAO.setPlayerFlag(login, flag, status);
    }

    public boolean resetUserPassword(String login) throws SQLException {
        boolean success = _playerDAO.setPlayerFlag(login, Player.Type.DEACTIVATED, false);
        success = success && _playerDAO.resetUserPassword(login);
        if(!success)
            return false;

        _loggedUserHolder.forceLogoutUser(login);
        return true;
    }

    public boolean deactivateUser(String login) throws SQLException {
        final boolean success = _playerDAO.setPlayerFlag(login, Player.Type.DEACTIVATED, true);
        if (!success) {
            return false;
        }
        _loggedUserHolder.forceLogoutUser(login);
        return true;
    }

    public boolean banUser(String login) {
        try {
            final boolean success = _playerDAO.banPlayerPermanently(login);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
            return true;
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean banUserTemp(String login, int days) {
        try {
            final boolean success = _playerDAO.banPlayerTemporarily(login, System.currentTimeMillis() + days * DAY_IN_MILLIS);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
            return true;
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean unBanUser(String login) {
        try {
            return _playerDAO.unBanPlayer(login);
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean banIp(String login) {
        final Player player = _playerDAO.getPlayer(login, true);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        
        _ipBanDAO.addIpBan(lastIp);
        
        return banUser(login);
    }

    public boolean banIpPrefix(String login) {
        final Player player = _playerDAO.getPlayer(login, true);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        String lastIpPrefix = lastIp.substring(0, lastIp.lastIndexOf(".")+1);

        _ipBanDAO.addIpPrefixBan(lastIpPrefix);

        return banUser(login);
    }
}
