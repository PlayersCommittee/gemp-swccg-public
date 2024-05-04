package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to place the just drawn destiny card on top of Reserve Deck.
 */
public class PlaceDestinyCardOnTopOfReserveDeckEffect extends PlaceDestinyCardOnTopOfCardPileEffect {

    /**
     * Creates an effect that causes the player performing the action to place the just drawn destiny card on top of Reserve Deck.
     * @param action the action performing this effect
     */
    public PlaceDestinyCardOnTopOfReserveDeckEffect(Action action) {
        super(action, Zone.RESERVE_DECK);
    }
}
