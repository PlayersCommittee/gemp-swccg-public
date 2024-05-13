package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to steal the specified card on table and attach it to a specified card.
 */
public class StealCardAndAttachFromTableEffect extends StealCardsAndAttachFromTableEffect {

    /**
     * Creates an effect to steal the specified card on table and attach it to a specified card.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     * @param attachTo the card to attach the stolen card to
     */
    public StealCardAndAttachFromTableEffect(Action action, PhysicalCard cardToSteal, PhysicalCard attachTo) {
        super(action, Collections.singleton(cardToSteal), attachTo);
    }
}
