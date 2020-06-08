package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to place Used Pile on top of Reserve Deck.
 */
public class PlaceUsedPileOnReserveDeckEffect extends PlaceCardPileOnTopOfCardPileEffect {

    /**
     * Creates an effect to place Used Pile on top of Reserve Deck.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     */
    public PlaceUsedPileOnReserveDeckEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Zone.USED_PILE, Zone.RESERVE_DECK);
    }
}
