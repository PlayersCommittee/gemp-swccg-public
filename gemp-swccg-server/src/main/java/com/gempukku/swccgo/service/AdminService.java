package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;

public class AdminService {
    public static final int DAY_IN_MILIS = 1000 * 60 * 60 * 24;
    private PlayerDAO _playerDAO;
    private LoggedUserHolder _loggedUserHolder;
    private IpBanDAO _ipBanDAO;

    public AdminService(PlayerDAO playerDAO, IpBanDAO ipBanDAO, LoggedUserHolder loggedUserHolder) {
        _playerDAO = playerDAO;
        _ipBanDAO = ipBanDAO;
        _loggedUserHolder = loggedUserHolder;
    }

    public boolean setUserAsPlaytester(String login, boolean playtester) {
        try {
            return _playerDAO.setPlayerAsPlaytester(login, playtester);
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean setUserAsCommentator(String login, boolean commentator) {
        try {
            return _playerDAO.setPlayerAsCommentator(login, commentator);
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean resetUserPassword(String login) {
        try {
            final boolean success = _playerDAO.setPlayerAsDeactivated(login, false);
            return success && _playerDAO.resetUserPassword(login);
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean deactivateUser(String login) {
        try {
            final boolean success = _playerDAO.setPlayerAsDeactivated(login, true);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
            return true;
        } catch (SQLException exp) {
            return false;
        }
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
            final boolean success = _playerDAO.banPlayerTemporarily(login, System.currentTimeMillis() + days * DAY_IN_MILIS);
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
        final Player player = _playerDAO.getPlayer(login);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        
        _ipBanDAO.addIpBan(lastIp);
        
        return banUser(login);
    }

    public boolean banIpPrefix(String login) {
        final Player player = _playerDAO.getPlayer(login);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        String lastIpPrefix = lastIp.substring(0, lastIp.lastIndexOf(".")+1);

        _ipBanDAO.addIpPrefixBan(lastIpPrefix);

        return banUser(login);
    }
}
