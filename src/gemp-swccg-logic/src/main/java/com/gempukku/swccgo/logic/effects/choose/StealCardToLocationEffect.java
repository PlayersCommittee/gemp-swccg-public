package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to steal a specified card on table and relocate it to the new owner's side of the location.
 */
public class StealCardToLocationEffect extends StealCardsToLocationEffect {

    /**
     * Creates an effect to steal a specified card on table and relocate it to the new owner's side of the location.
     * @param action the action performing this effect
     * @param cardToSteal the card to steal
     */
    public StealCardToLocationEffect(Action action, PhysicalCard cardToSteal) {
        super(action, action.getPerformingPlayer(), Collections.singleton(cardToSteal));
    }

    /**
     * Creates an effect to steal a specified card on table and relocate it to the new owner's side of the location.
     * @param action the action performing this effect
     * @param playerId the player to steal the card
     * @param cardToSteal the card to steal
     */
    public StealCardToLocationEffect(Action action, String playerId, PhysicalCard cardToSteal) {
        super(action, playerId, Collections.singleton(cardToSteal));
    }
}
