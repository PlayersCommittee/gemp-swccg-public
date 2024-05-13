package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * The effect result that is emitted when a card moved using hyperspeed.
 */
public class MovedUsingHyperspeedResult extends EffectResult implements MovedResult {
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;

    /**
     * Creates an effect result that is emitted when a card moved using hyperspeed.
     * @param movedCard the card that moved
     * @param playerId the performing player
     * @param moveFrom the location the card moved from, or null if mobile system moved
     * @param movedTo the location the card moved to, or null if mobile system moved
     * @param asReact true if moved as 'react', otherwise false
     */
    public MovedUsingHyperspeedResult(PhysicalCard movedCard, String playerId, PhysicalCard moveFrom, PhysicalCard movedTo, boolean asReact) {
        super(EffectResult.Type.MOVED_USING_HYPERSPEED, playerId);
        _cardMoved = movedCard;
        _movedFrom = moveFrom;
        _movedTo = movedTo;
        _asReact = asReact;
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
     * Gets the location the card moved from.
     * @return the location the card moved from, or null if mobile system moved
     */
    @Override
    public PhysicalCard getMovedFrom() {
        return _movedFrom;
    }

    /**
     * Gets the location the card moved to.
     * @return the location the card moved to, or null if mobile system moved
     */
    @Override
    public PhysicalCard getMovedTo() {
        return _movedTo;
    }

    /**
     * Determine if movement was a 'react'.
     * @return true or false
     */
    @Override
    public boolean isReact() {
        return _asReact;
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

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Moved " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " using hyperspeed" + (_asReact ? " as a 'react'" : "");
    }
}
