package com.gempukku.swccgo.db;

import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DbTransferDAO implements TransferDAO {
    private DbAccess _dbAccess;

    public DbTransferDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void addTransferFrom(String player, String reason, String collectionName, int currency, CardCollection items) {
        if (currency > 0 || items.getAll().size() > 0) {
            try {
                Connection connection = _dbAccess.getDataSource().getConnection();
                try {
                    String sql = "insert into transfer (notify, player, reason, name, currency, collection, transfer_date, direction) values (?, ?, ?, ?, ?, ?, ?, 'from')";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    try {
                        statement.setInt(1, 0);
                        statement.setString(2, player);
                        statement.setString(3, reason);
                        statement.setString(4, collectionName);
                        statement.setInt(5, currency);
                        statement.setString(6, serializeCollection(items));
                        statement.setLong(7, System.currentTimeMillis());
                        statement.execute();
                    } finally {
                        statement.close();
                    }
                } finally {
                    connection.close();
                }
            } catch (SQLException exp) {
                throw new RuntimeException("Unable to add transfer from", exp);
            }
        }
    }

    @Override
    public void addTransferTo(boolean notifyPlayer, String player, String reason, String collectionName, int currency, CardCollection items) {
        if (currency > 0 || items.getAll().size() > 0) {
            try {
                Connection connection = _dbAccess.getDataSource().getConnection();
                try {
                    String sql = "insert into transfer (notify, player, reason, name, currency, collection, transfer_date, direction) values (?, ?, ?, ?, ?, ?, ?, 'to')";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    try {
                        statement.setInt(1, notifyPlayer ? 1 : 0);
                        statement.setString(2, player);
                        statement.setString(3, reason);
                        statement.setString(4, collectionName);
                        statement.setInt(5, currency);
                        statement.setString(6, serializeCollection(items));
                        statement.setLong(7, System.currentTimeMillis());
                        statement.execute();
                    } finally {
                        statement.close();
                    }
                } finally {
                    connection.close();
                }
            } catch (SQLException exp) {
                throw new RuntimeException("Unable to add transfer to", exp);
            }
        }
    }

    @Override
    public boolean hasUndeliveredPackages(String player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                String sql = "select count(*) from transfer where player=? and notify=1";

                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setString(1, player);
                    ResultSet resultSet = statement.executeQuery();
                    try {
                        if (resultSet.next())
                            return resultSet.getInt(1) > 0;
                        else
                            return false;
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to check if there are any undelivered packages", exp);
        }
    }

    // For now, very naive synchronization
    @Override
    public synchronized Map<String, ? extends CardCollection> consumeUndeliveredPackages(String player) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                Map<String, DefaultCardCollection> result = new HashMap<String, DefaultCardCollection>();

                String sql = "select name, currency, collection from transfer where player=? and notify=1";

                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setString(1, player);
                    ResultSet resultSet = statement.executeQuery();
                    try {
                        while (resultSet.next()) {
                            String name = resultSet.getString(1);

                            DefaultCardCollection cardCollection = result.get(name);
                            if (cardCollection == null)
                                cardCollection = new DefaultCardCollection();

                            cardCollection.addCurrency(resultSet.getInt(2));
                            CardCollection retrieved = deserializeCollection(resultSet.getString(3));
                            for (CardCollection.Item item : retrieved.getAll().values())
                                cardCollection.addItem(item.getBlueprintId(), item.getCount());
                            result.put(name, cardCollection);
                        }
                    } finally {
                        resultSet.close();
                    }
                } finally {
                    statement.close();
                }

                sql = "update transfer set notify=0 where player=?";
                statement = connection.prepareStatement(sql);
                try {
                    statement.setString(1, player);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
                return result;
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to consume undelivered packages", exp);
        }
    }

    private String serializeCollection(CardCollection cardCollection) {
        StringBuilder sb = new StringBuilder();
        for (CardCollection.Item item : cardCollection.getAll().values())
            sb.append(item.getCount()).append("x").append(item.getBlueprintId()).append(",");
        return sb.toString();
    }

    private CardCollection deserializeCollection(String collection) {
        DefaultCardCollection cardCollection = new DefaultCardCollection();
        for (String item : collection.split(",")) {
            if (item.length() > 0) {
                String[] itemSplit = item.split("x", 2);
                int count = Integer.parseInt(itemSplit[0]);
                String blueprintId = itemSplit[1];
                cardCollection.addItem(blueprintId, count);
            }
        }

        return cardCollection;
    }
}
