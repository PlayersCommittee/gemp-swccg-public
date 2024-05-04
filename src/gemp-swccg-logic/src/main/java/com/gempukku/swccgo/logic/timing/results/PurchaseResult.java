package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

public class PurchaseResult extends EffectResult {
    private PhysicalCard _cardPurchased;

    public PurchaseResult(String performingPlayerId, PhysicalCard cardPurchased) {
        super(Type.FOR_EACH_PURCHASED, performingPlayerId);
        _cardPurchased = cardPurchased;
    }

    public PhysicalCard getPurchasedCard() {
        return _cardPurchased;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Purchased' " + GameUtils.getCardLink(_cardPurchased);
    }
}
