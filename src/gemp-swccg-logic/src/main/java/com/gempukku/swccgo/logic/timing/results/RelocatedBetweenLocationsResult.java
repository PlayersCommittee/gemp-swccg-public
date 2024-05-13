package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * The effect result that is emitted when a card is relocated between locations.
 */
public class RelocatedBetweenLocationsResult extends EffectResult implements MovedResult {
    private PhysicalCard _actionSource;
    private Collection<PhysicalCard> _cardsMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;

    /**
     * Creates an effect result that is emitted when cards are relocated between locations.
     * @param movedCards the cards that moved
     * @param actionSource the source of the action
     * @param playerId the performing player
     * @param moveFrom the location the card moved from
     * @param movedTo the location the card moved to
     */
    public RelocatedBetweenLocationsResult(Collection<PhysicalCard> movedCards, PhysicalCard actionSource, String playerId, PhysicalCard moveFrom, PhysicalCard movedTo) {
        super(Type.RELOCATED_BETWEEN_LOCATIONS, playerId);
        _actionSource = actionSource;
        _cardsMoved = Collections.unmodifiableCollection(movedCards);
        _movedFrom = moveFrom;
        _movedTo = movedTo;
    }

    /**
     * Gets the card that was the source of the action.
     * @return the source of the action
     */
    public PhysicalCard getActionSource() {
        return _actionSource;
    }

    /**
     * Gets the cards that moved.
     * @return the cards that moved
     */
    @Override
    public Collection<PhysicalCard> getMovedCards() {
        return _cardsMoved;
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

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Relocated " + GameUtils.getAppendedNames(_cardsMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo);
    }
}

