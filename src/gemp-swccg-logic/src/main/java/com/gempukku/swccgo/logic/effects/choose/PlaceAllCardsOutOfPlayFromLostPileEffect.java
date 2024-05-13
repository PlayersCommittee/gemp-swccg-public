package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to place cards out of play all cards from the specified Lost Pile.
 */
public class PlaceAllCardsOutOfPlayFromLostPileEffect extends PlaceAllCardsOutOfPlayFromPileEffect {

    /**
     * Creates an effect that causes the player to place cards out of play from the specified Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     */
    public PlaceAllCardsOutOfPlayFromLostPileEffect(Action action, String playerId, String cardPileOwner) {
        super(action, playerId, Zone.LOST_PILE, cardPileOwner);
    }
}
