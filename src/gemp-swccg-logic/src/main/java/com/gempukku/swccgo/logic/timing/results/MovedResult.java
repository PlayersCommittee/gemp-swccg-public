package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.Collection;

/**
 * Interface the defines the methods that effect results that are emitted when a card have moved must implement.
 */
public interface MovedResult {

    /**
     * Gets the cards that moved.
     * @return the cards that moved
     */
    Collection<PhysicalCard> getMovedCards();

    /**
     * Gets the location the card moved from.
     * @return the location the card moved from, or null if mobile system moved or if card did not move between locations
     */
    PhysicalCard getMovedFrom();

    /**
     * Gets the location the card moved to.
     * @return the location the card moved to, or null if mobile system moved or if card did not move between locations
     */
    PhysicalCard getMovedTo();

    /**
     * Determine if movement was a 'react'.
     * @return true or false
     */
    boolean isReact();

    /**
     * Determines if the movement from the original location.
     * @return true or false
     */
    boolean isInitialMove();

    /**
     * Determines if the movement completed.
     * @return true or false
     */
    boolean isMoveComplete();
}
