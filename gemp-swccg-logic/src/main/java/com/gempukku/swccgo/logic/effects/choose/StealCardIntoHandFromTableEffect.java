package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to steal the specified card on table into hand.
 */
public class StealCardIntoHandFromTableEffect extends StealCardsIntoHandFromTableEffect {

    /**
     * Creates an effect to steal the specified card on table into hand.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealCardIntoHandFromTableEffect(Action action, PhysicalCard cardToSteal) {
        super(action, Collections.singleton(cardToSteal));
    }
}
