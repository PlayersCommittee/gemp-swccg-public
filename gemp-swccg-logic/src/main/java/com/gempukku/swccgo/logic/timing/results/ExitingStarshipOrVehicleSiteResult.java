package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card begins to exit a starship/vehicle site to the site the related starship/vehicle
 * is present at.
 */
public class ExitingStarshipOrVehicleSiteResult extends EffectResult implements MovingResult {
    private PhysicalCard _cardMoving;
    private PhysicalCard _movingFrom;
    private PhysicalCard _movingTo;
    private boolean _asReact;

    /**
     * Creates an effect result that is emitted when a card begins to exit a starship/vehicle site to the site the related starship/vehicle
     * is present at.
     * @param cardMoving the card that is moving
     * @param playerId the performing player
     * @param movingFrom the location the card is moving from
     * @param movingTo the location the card is moving to
     * @param asReact true if moved as 'react', otherwise false
     */
    public ExitingStarshipOrVehicleSiteResult(PhysicalCard cardMoving, String playerId, PhysicalCard movingFrom, PhysicalCard movingTo, boolean asReact) {
        super(Type.EXITING_STARSHIP_OR_VEHICLE_SITE, playerId);
        _cardMoving = cardMoving;
        _movingFrom = movingFrom;
        _movingTo = movingTo;
        _asReact = asReact;
    }

    /**
     * Gets the card that is moving.
     * @return the card that is moving
     */
    @Override
    public PhysicalCard getCardMoving() {
        return _cardMoving;
    }

    /**
     * Gets the location the card is moving from.
     * @return the location the card is moving from
     */
    @Override
    public PhysicalCard getMovingFrom() {
        return _movingFrom;
    }

    /**
     * Gets the location the card is moving to.
     * @return the location the card is moving to
     */
    @Override
    public PhysicalCard getMovingTo() {
        return _movingTo;
    }

    /**
     * Determine if movement is a 'react'.
     * @return true or false
     */
    @Override
    public boolean isReact() {
        return _asReact;
    }

    /**
     * Determine if movement is a 'move away'.
     * @return true or false
     */
    @Override
    public boolean isMoveAway() {
        return false;
    }

    /**
     * Gets the interface that can be used to prevent the card from moving, or null.
     * @return the interface
     */
    @Override
    public PreventableCardEffect getPreventableCardEffect() {
        return null;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Moving " + GameUtils.getCardLink(_cardMoving) + " from " + GameUtils.getCardLink(_movingFrom) + " to " + GameUtils.getCardLink(_movingTo) + (_asReact ? " as a 'react'" : "");
    }
}
