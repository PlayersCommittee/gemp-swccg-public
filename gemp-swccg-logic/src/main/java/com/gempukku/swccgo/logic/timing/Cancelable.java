package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * An interface that defines the methods that all cancelable classes must implement.
 */
public interface Cancelable {

    /**
     * This method is called to cancel the cancelable.
     * @param canceledByCard the card that is the source of the cancel, or null
     */
    void cancel(PhysicalCard canceledByCard);

    /**
     * Determines if the cancelable is canceled.
     * @return true if canceled, otherwise false
     */
    boolean isCanceled();

    /**
     * Gets the card that was the source of the canceling, or null if the game (and not a specific card) performed the cancel.
     * @return the source card of the cancel, or null
     */
    PhysicalCard getCanceledByCard();
}
