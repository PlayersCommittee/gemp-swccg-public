package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to place the top card of Used Pile on top of Reserve Deck.
 */
public class PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect extends PlaceTopCardFromCardPileOnTopOfCardPileEffect {

    /**
     * Creates an effect to place the top card of Used Pile on top of Reserve Deck.
     * @param action the action performing this effect
     */
    public PlaceTopCardOfUsedPileOnTopOfReserveDeckEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Zone.USED_PILE, Zone.RESERVE_DECK);
    }
}
