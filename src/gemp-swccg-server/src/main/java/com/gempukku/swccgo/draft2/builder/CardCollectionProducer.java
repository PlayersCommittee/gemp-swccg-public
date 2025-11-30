package com.gempukku.swccgo.draft2.builder;

import com.gempukku.swccgo.game.CardCollection;

public interface CardCollectionProducer {
    CardCollection getCardCollection(long seed);
}
