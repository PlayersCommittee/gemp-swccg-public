package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * The effect result that is emitted when a starship or vehicle moves using sector movement.
 */
public class MovedUsingSectorMovementResult extends EffectResult implements MovedResult {
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private boolean _asReact;
    private boolean _initialMove;
    private boolean _moveComplete;

    /**
     * Creates an effect result that is emitted when a starship or vehicle moves using sector movement.
     * @param movedCard the card that moved
     * @param playerId the performing player
     * @param moveFrom the location the card moved from
     * @param movedTo the location the card moved to
     * @param asReact true if moved as 'react', otherwise false
     * @param initialMove true if move from original location, otherwise false when moving from intermediate location
     * @param moveComplete true if move completed to destination, otherwise false when moving to intermediate location
     */
    public MovedUsingSectorMovementResult(PhysicalCard movedCard, String playerId, PhysicalCard moveFrom, PhysicalCard movedTo, boolean asReact, boolean initialMove, boolean moveComplete) {
        super(Type.MOVED_USING_SECTOR_MOVEMENT, playerId);
        _cardMoved = movedCard;
        _movedFrom = moveFrom;
        _movedTo = movedTo;
        _asReact = asReact;
        _initialMove = initialMove;
        _moveComplete = moveComplete;
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
     * @return the location the card moved from
     */
    @Override
    public PhysicalCard getMovedFrom() {
        return _movedFrom;
    }

    /**
     * Gets the location the card moved to.
     * @return the location the card moved to
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
        return _initialMove;
    }

    /**
     * Determines if the movement completed.
     * @return true or false
     */
    @Override
    public boolean isMoveComplete() {
        return _moveComplete;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Moved " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " using sector movement" + (_asReact ? " as a 'react'" : "");
    }
}
