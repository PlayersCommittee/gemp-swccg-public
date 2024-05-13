package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;

/**
 * Interface the defines the methods that effect results that are emitted when a card begins to move must implement.
 */
public interface MovingResult {

    /**
     * Gets the card that is moving.
     * @return the card that is moving
     */
    PhysicalCard getCardMoving();

    /**
     * Gets the location the card is moving from.
     * @return the location the card is moving from, or null if mobile system is moving
     */
    PhysicalCard getMovingFrom();

    /**
     * Gets the location the card is moving to.
     * @return the location the card is moving to, or null if mobile system is moving
     */
    PhysicalCard getMovingTo();

    /**
     * Determine if movement is a 'react'.
     * @return true or false
     */
    boolean isReact();

    /**
     * Determine if movement is a 'move away'.
     * @return true or false
     */
    boolean isMoveAway();

    /**
     * Gets the interface that can be used to prevent the card from moving, or null.
     * @return the interface
     */
    PreventableCardEffect getPreventableCardEffect();
}
