package com.gempukku.swccgo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DbIpBanDAO implements IpBanDAO {
    private DbAccess _dbAccess;

    public DbIpBanDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void addIpBan(String ip) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("insert into ip_ban (ip, prefix) values (?, 0)");
                try {
                    statement.setString(1, ip);

                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to add an IP ban", exp);
        }
    }

    @Override
    public void addIpPrefixBan(String ipPrefix) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("insert into ip_ban (ip, prefix) values (?, 1)");
                try {
                    statement.setString(1, ipPrefix);

                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to add an IP prefix ban", exp);
        }
    }

    @Override
    public Set<String> getIpBans() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select ip from ip_ban where prefix=0");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        Set<String> result = new HashSet<String>();
                        while (rs.next()) {
                            String ip = rs.getString(1);

                            result.add(ip);
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of player games", exp);
        }
    }

    @Override
    public Set<String> getIpPrefixBans() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select ip from ip_ban where prefix=1");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        Set<String> result = new HashSet<String>();
                        while (rs.next()) {
                            String ip = rs.getString(1);

                            result.add(ip);
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get count of player games", exp);
        }
    }
}
