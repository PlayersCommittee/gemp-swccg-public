package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put all cards from hand on Used Pile.
 */
public class PlaceHandInUsedPileEffect extends PutCardsFromHandOnUsedPileEffect {

    /**
     * Creates an effect that causes the player to put all cards from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PlaceHandInUsedPileEffect(Action action, String playerId) {
        super(action, playerId);
    }
}
