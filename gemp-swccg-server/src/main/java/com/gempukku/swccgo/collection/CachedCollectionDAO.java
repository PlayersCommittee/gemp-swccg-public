package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.db.CollectionDAO;
import com.gempukku.swccgo.game.CardCollection;
import org.apache.commons.collections.map.LRUMap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class CachedCollectionDAO implements CollectionDAO, Cached {
    private CollectionDAO _delegate;
    private Map<String, CardCollection> _playerCollections = Collections.synchronizedMap(new LRUMap(100));

    public CachedCollectionDAO(CollectionDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _playerCollections.clear();
    }

    @Override
    public int getItemCount() {
        return _playerCollections.size();
    }

    @Override
    public CardCollection getPlayerCollection(int playerId, String type) throws SQLException, IOException {
        String key = constructCacheKey(playerId, type);
        CardCollection collection = (CardCollection) _playerCollections.get(key);
        if (collection == null) {
            collection = _delegate.getPlayerCollection(playerId, type);
            _playerCollections.put(key, collection);
        }
        return collection;
    }

    private String constructCacheKey(int playerId, String type) {
        return playerId +"-"+type;
    }

    @Override
    public Map<Integer, CardCollection> getPlayerCollectionsByType(String type) throws SQLException, IOException {
        return _delegate.getPlayerCollectionsByType(type);
    }

    @Override
    public void setPlayerCollection(int playerId, String type, CardCollection collection) throws SQLException, IOException {
        _delegate.setPlayerCollection(playerId, type, collection);
        _playerCollections.put(constructCacheKey(playerId, type), collection);
    }
}
