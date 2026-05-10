package com.gempukku.swccgo.db;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DbLeagueParticipationDAO implements LeagueParticipationDAO {
    private DbAccess _dbAccess;

    public DbLeagueParticipationDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    public void userJoinsLeague(String leagueId, Player player, String remoteAddr) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into league_participation (league_type, player_name, join_ip) values (?,?,?)");
                try {
                    statement.setString(1, leagueId);
                    statement.setString(2, player.getName());
                    statement.setString(3, remoteAddr);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    public Collection<String> getUsersParticipating(String leagueId) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("select player_name from league_participation where league_type=?");
                try {
                    statement.setString(1, leagueId);
                    final ResultSet rs = statement.executeQuery();
                    try {
                        Set<String> result = new HashSet<String>();
                        while (rs.next())
                            result.add(rs.getString(1));
                        return result;
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
            throw new RuntimeException(exp);
        }
    }

    /**
     * Gets the locked deck name and contents for a player in a league for a given side.
     * @param leagueId the league type identifier
     * @param playerName the player name
     * @param side LS or DS
     * @return String array of [deckName, deckContents], or null if no locked deck exists
     */
    public String[] getLockedDeck(String leagueId, String playerName, Side side) {
        String nameColumn = side == Side.LIGHT ? "locked_ls_deck_name" : "locked_ds_deck_name";
        String contentsColumn = side == Side.LIGHT ? "locked_ls_deck" : "locked_ds_deck";
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement(
                        "select " + nameColumn + ", " + contentsColumn +
                        " from league_participation where league_type=? and player_name=?");
                try {
                    statement.setString(1, leagueId);
                    statement.setString(2, playerName);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next()) {
                            String deckName = rs.getString(1);
                            String deckContents = rs.getString(2);
                            if (deckName != null && deckContents != null) {
                                return new String[] { deckName, deckContents };
                            }
                        }
                        return null;
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
            throw new RuntimeException("Unable to get locked deck", exp);
        }
    }

    /**
     * Sets the locked deck for a player in a league for a given side.
     * @param leagueId the league type identifier
     * @param playerName the player name
     * @param side LS or DS
     * @param deckName the deck name to store
     * @param deckContents the serialized deck contents to store
     */
    public void setLockedDeck(String leagueId, String playerName, Side side, String deckName, String deckContents) {
        String nameColumn = side == Side.LIGHT ? "locked_ls_deck_name" : "locked_ds_deck_name";
        String contentsColumn = side == Side.LIGHT ? "locked_ls_deck" : "locked_ds_deck";
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement(
                        "update league_participation set " + nameColumn + "=?, " + contentsColumn + "=?" +
                        " where league_type=? and player_name=?");
                try {
                    statement.setString(1, deckName);
                    statement.setString(2, deckContents);
                    statement.setString(3, leagueId);
                    statement.setString(4, playerName);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to set locked deck", exp);
        }
    }
}
