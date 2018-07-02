package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * The effect result that is emitted when ship-docking is complete.
 */
public class ShipdockedResult extends EffectResult implements MovedResult {
    private PhysicalCard _cardMoved;

    /**
     * Creates an effect result that is emitted when ship-docking is complete.
     * @param movedCard the card that moved
     * @param playerId the performing player
     */
    public ShipdockedResult(PhysicalCard movedCard, String playerId) {
        super(Type.SHIPDOCKED, playerId);
        _cardMoved = movedCard;
    }

    /**
     * Gets the cards that moved.
     * @return the cards that moved
     */
    @Override
    public Collection<PhysicalCard> getMovedCards() {
        return Collections.singletonList(_cardMoved);
    }

    /**
     * Gets the card the card moved from.
     * @return the card the card moved from
     */
    @Override
    public PhysicalCard getMovedFrom() {
        return null;
    }

    /**
     * Gets the card the card moved to.
     * @return the card the card moved to
     */
    @Override
    public PhysicalCard getMovedTo() {
        return null;
    }

    /**
     * Determine if movement was a 'react'.
     * @return true or false
     */
    @Override
    public boolean isReact() {
        return false;
    }

    /**
     * Determines if the initial movement.
     * @return true or false
     */
    @Override
    public boolean isInitialMove() {
        return true;
    }

    /**
     * Determines if the movement completed.
     * @return true or false
     */
    @Override
    public boolean isMoveComplete() {
        return true;
    }
}
