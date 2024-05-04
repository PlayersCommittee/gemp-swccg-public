package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.game.CardCollection;

public interface DraftCardChoice {
    public long getTimeLeft();

    public CardCollection getCardCollection();
}
