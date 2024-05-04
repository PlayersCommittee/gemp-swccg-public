package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is granted immunity to specified cards.
 */
public class ImmunityGrantedResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a card is granted immunity to specified cards.
     * @param performingPlayer the player that performed the action
     * @param card the card granted immunity
     */
    public ImmunityGrantedResult(String performingPlayer, PhysicalCard card) {
        super(Type.IMMUNITY_GRANTED, performingPlayer);
        _card = card;
    }
}
