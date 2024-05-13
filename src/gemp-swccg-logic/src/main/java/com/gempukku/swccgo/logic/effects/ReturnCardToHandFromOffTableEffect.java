package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card not on table (e.g. in a card pile, etc.) to be returned to hand.
 */
public class ReturnCardToHandFromOffTableEffect extends ReturnCardsToHandFromOffTableEffect {

    /**
     * Creates an effect that causes the specified card not on table (e.g. in a card pile, etc.) to be returned to hand.
     * @param action the action performing this effect
     * @param card the card
     */
    public ReturnCardToHandFromOffTableEffect(Action action, PhysicalCard card) {
        super(action, Collections.singletonList(card));
    }
}
