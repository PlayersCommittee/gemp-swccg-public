package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card to be lost from hand.
 */
public class LoseCardFromHandEffect extends LoseCardsFromHandEffect {

    /**
     * Creates an effect that causes the specified card to be lost from hand.
     * @param action the action performing this effect
     * @param card the card
     */
    public LoseCardFromHandEffect(Action action, PhysicalCard card) {
        super(action, card.getZoneOwner(), Collections.singleton(card));
    }
}
