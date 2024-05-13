package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;

/**
 * The effect result that is emitted when a card moves using docking bay transit.
 */
public class DockingBayTransitedResult extends EffectResult implements MovedResult {
    private Collection<PhysicalCard> _cardsMoved;
    private PhysicalCard _movedFrom;
    private PhysicalCard _movedTo;

    /**
     * Creates an effect result that is emitted when a card moves using docking bay transit.
     * @param movedCards the cards that moved
     * @param playerId the performing player
     * @param moveFrom the docking bay the card moved from
     * @param movedTo the docking bay the card moved to
     */
    public DockingBayTransitedResult(Collection<PhysicalCard> movedCards, String playerId, PhysicalCard moveFrom, PhysicalCard movedTo) {
        super(Type.DOCKING_BAY_TRANSITED, playerId);
        _cardsMoved = Collections.unmodifiableCollection(movedCards);
        _movedFrom = moveFrom;
        _movedTo = movedTo;
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
        return "Docking bay transited " + GameUtils.getAppendedNames(_cardsMoved) + " from " + GameUtils.getCardLink(_movedFrom) + " to " + GameUtils.getCardLink(_movedTo);
    }
}
