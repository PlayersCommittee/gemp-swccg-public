package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to place the top card of Force Pile on top of Reserve Deck.
 */
public class PlaceTopCardOfForcePileOnTopOfReserveDeckEffect extends PlaceTopCardFromCardPileOnTopOfCardPileEffect {

    /**
     * Creates an effect to place the top card of Force Pile on top of Reserve Deck.
     * @param action the action performing this effect
     */
    public PlaceTopCardOfForcePileOnTopOfReserveDeckEffect(Action action, String cardPileOwner) {
        super(action, cardPileOwner, Zone.FORCE_PILE, Zone.RESERVE_DECK);
    }
}
