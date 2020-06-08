package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card to be forfeited from hand.
 */
public class ForfeitCardFromHandEffect extends ForfeitCardsFromHandEffect {

    /**
     * Creates an effect that causes the specified card to be forfeited from hand.
     * @param action the action performing this effect
     * @param card the card
     */
    public ForfeitCardFromHandEffect(Action action, PhysicalCard card) {
        super(action, Collections.singleton(card));
    }
}
