package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The effect result that is emitted when a card moves using landspeed.
 */
public class MovedUsingLandspeedResult extends EffectResult implements MovedResult {
    private PhysicalCard _cardMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;
    private List<PhysicalCard> _locationsAlongPath;
    private boolean _asReact;
    private boolean _initialMove;
    private boolean _moveComplete;

    /**
     * Creates an effect result that is emitted when a card moves using landspeed.
     * @param movedCard the card that moved
     * @param playerId the performing player
     * @param moveFrom the location the card moved from
     * @param movedTo the location the card moved to
     * @param asReact true if moved as 'react', otherwise false
     * @param initialMove true if move from original location, otherwise false when moving from intermediate location
     * @param moveComplete true if move completed to destination, otherwise false when moving to intermediate location
     */
    public MovedUsingLandspeedResult(PhysicalCard movedCard, String playerId, PhysicalCard moveFrom, PhysicalCard movedTo, List<PhysicalCard> locationsAlongPath, boolean asReact, boolean initialMove, boolean moveComplete) {
        super(EffectResult.Type.MOVED_USING_LANDSPEED, playerId);
        _cardMoved = movedCard;
        _movedFrom = moveFrom;
        _movedTo = movedTo;
        _asReact = asReact;
        _initialMove = initialMove;
        _moveComplete = moveComplete;
        _locationsAlongPath = new LinkedList<>(locationsAlongPath);
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

    public List<PhysicalCard> getLocationsAlongPath() {
        return _locationsAlongPath;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Moved " + GameUtils.getCardLink(_cardMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo) + " using landspeed" + (_asReact ? " as a 'react'" : "");
    }
}
