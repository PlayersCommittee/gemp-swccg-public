package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.game.CardCollection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CachedTransferDAO implements TransferDAO, Cached {
    private TransferDAO _delegate;
    private Set<String> _playersWithoutDelivery = Collections.synchronizedSet(new HashSet<String>());

    public CachedTransferDAO(TransferDAO delegate) {
        _delegate = delegate;
    }

    @Override
    public void clearCache() {
        _playersWithoutDelivery.clear();
    }

    @Override
    public int getItemCount() {
        return _playersWithoutDelivery.size();
    }

    public boolean hasUndeliveredPackages(String player) {
        if (_playersWithoutDelivery.contains(player))
            return false;
        boolean value = _delegate.hasUndeliveredPackages(player);
        if (!value)
            _playersWithoutDelivery.add(player);
        return value;
    }

    public Map<String, ? extends CardCollection> consumeUndeliveredPackages(String player) {
        return _delegate.consumeUndeliveredPackages(player);
    }

    public void addTransferTo(boolean notifyPlayer, String player, String reason, String collectionName, int currency, CardCollection items) {
        if (notifyPlayer)
            _playersWithoutDelivery.remove(player);
        _delegate.addTransferTo(notifyPlayer, player, reason, collectionName, currency, items);
    }

    public void addTransferFrom(String player, String reason, String collectionName, int currency, CardCollection items) {
        _delegate.addTransferFrom(player, reason, collectionName, currency, items);
    }
}
