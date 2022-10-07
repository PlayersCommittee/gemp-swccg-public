package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to choose a combination of cards from hand and/or Reserve Deck.
 */
public abstract class ChooseCardCombinationFromHandAndOrReserveDeckEffect extends ChooseCardCombinationFromHandAndOrCardPileEffect {

    /**
     * Creates an effect that causes the player to choose a combination of cards from hand and/or Reserve Deck.
     * @param action the action performing this effect
     */
    public ChooseCardCombinationFromHandAndOrReserveDeckEffect(Action action) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, true);
    }
}
