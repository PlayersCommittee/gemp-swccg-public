package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that allows the player to peek at and reorder the cards in the specified Force Pile.
 */
public class PeekAtAndReorderForcePileEffect extends PeekAtAndReorderCardPileEffect {

    /**
     * Create an effect that allows the performing player to peek at and reorder the cards in the specified player's Force Pile.
     * @param action the action performing this effect
     * @param cardPileOwner the card pile owner
     */
    public PeekAtAndReorderForcePileEffect(Action action, String cardPileOwner) {
        super(action, action.getPerformingPlayer(), Zone.FORCE_PILE, cardPileOwner);
    }
}
