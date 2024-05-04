package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to put cards from a specified player's Used Pile in Lost Pile.
 */
public class PutCardsFromUsedPileInLostPileEffect extends PutCardsFromCardPileInCardPileEffect {

    /**
     * Creates an effect that causes the player put cards from a specified player's Used Pile in Lost Pile.
     * @param action the action performing this effect
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     */
    public PutCardsFromUsedPileInLostPileEffect(Action action, String cardPileOwner, Filterable filters) {
        super(action, action.getPerformingPlayer(), cardPileOwner, Zone.USED_PILE, Zone.LOST_PILE, false, filters);
    }
}
