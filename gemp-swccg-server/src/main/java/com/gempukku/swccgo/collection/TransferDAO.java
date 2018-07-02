package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.game.CardCollection;

import java.util.Map;

public interface TransferDAO {
    boolean hasUndeliveredPackages(String player);
    Map<String, ? extends CardCollection> consumeUndeliveredPackages(String player);

    void addTransferTo(boolean notifyPlayer, String player, String reason, String collectionName, int currency, CardCollection items);
    void addTransferFrom(String player, String reason, String collectionName, int currency, CardCollection items);
}
