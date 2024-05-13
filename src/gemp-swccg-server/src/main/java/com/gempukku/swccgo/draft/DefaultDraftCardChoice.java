package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.game.CardCollection;

public class DefaultDraftCardChoice implements DraftCardChoice {
    private CardCollection _cardCollection;
    private long _pickEnd;

    public DefaultDraftCardChoice(CardCollection cardCollection, long pickEnd) {
        _cardCollection = cardCollection;
        _pickEnd = pickEnd;
    }

    @Override
    public long getTimeLeft() {
        return _pickEnd - System.currentTimeMillis();
    }

    @Override
    public CardCollection getCardCollection() {
        return _cardCollection;
    }
}
