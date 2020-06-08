package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.MovementDirection;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.MovedMobileEffectResult;

/**
 * An effect that moves a Mobile Effect to another location.
 */
public class MoveMobileEffectEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _mobileEffectCard;
    private PhysicalCard _moveTo;
    private MovementDirection _movementDirection;

    /**
     * Creates an effect to move the specified Mobile Effect to the specified location.
     * @param action the action performing this effect
     * @param mobileEffectCard the Mobile Effect card
     * @param moveTo the location to move the Mobile Effect to
     */
    public MoveMobileEffectEffect(Action action, PhysicalCard mobileEffectCard, PhysicalCard moveTo) {
        this(action, mobileEffectCard, moveTo, null);
    }

    /**
     * Creates an effect to move the specified Mobile Effect to the specified location and set the movement direction.
     * @param action the action performing this effect
     * @param mobileEffectCard the Mobile Effect card
     * @param moveTo the location to move the Mobile Effect to
     * @param movementDirection the movement direction to set
     */
    public MoveMobileEffectEffect(Action action, PhysicalCard mobileEffectCard, PhysicalCard moveTo, MovementDirection movementDirection) {
        super(action);
        _mobileEffectCard = mobileEffectCard;
        _moveTo = moveTo;
        _movementDirection = movementDirection;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard moveFrom = _mobileEffectCard.getAttachedTo();
        gameState.sendMessage(GameUtils.getCardLink(_mobileEffectCard) + " moves from " + GameUtils.getCardLink(moveFrom) + " to " + GameUtils.getCardLink(_moveTo));
        if (_movementDirection != null) {
            _mobileEffectCard.setMovementDirection(_movementDirection);
        }
        gameState.moveCardToAttached(_mobileEffectCard, _moveTo);

        // Emit effect result
        game.getActionsEnvironment().emitEffectResult(new MovedMobileEffectResult(_mobileEffectCard, _action.getPerformingPlayer(), moveFrom, _moveTo));
    }
}
