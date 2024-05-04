package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.CardCollection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public interface CollectionDAO {
    public Map<Integer, CardCollection> getPlayerCollectionsByType(String type) throws SQLException, IOException;

    public CardCollection getPlayerCollection(int playerId, String type) throws SQLException, IOException;

    public void setPlayerCollection(int playerId, String type, CardCollection collection) throws SQLException, IOException;
}
