package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An interface to define the methods that effects that can exclude cards from being affected need to implement.
 */
public interface PreventableCardEffect extends StandardEffect {

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    void preventEffectOnCard(PhysicalCard card);

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    boolean isEffectOnCardPrevented(PhysicalCard card);
}
