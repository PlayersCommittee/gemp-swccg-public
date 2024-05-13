package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card on table to be forfeited.
 */
public class ForfeitCardFromTableEffect extends ForfeitCardsFromTableEffect {

    /**
     * Creates an effect that causes the specified card on table to be forfeited.
     * @param action the action performing this effect
     * @param card the card
     */
    public ForfeitCardFromTableEffect(Action action, PhysicalCard card) {
        super(action, Collections.singleton(card));
    }
}
