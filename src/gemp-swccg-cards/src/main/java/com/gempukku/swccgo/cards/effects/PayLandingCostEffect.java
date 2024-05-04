package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that pays the cost of landing a starship or vehicle.
 */
public class PayLandingCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardToMove;
    private PhysicalCard _moveFrom;
    private PhysicalCard _moveTo;
    private boolean _asReact;
    private float _changeInCost;

    /**
     * Creates an effect that pays the cost of landing a starship or vehicle.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardToMove the card to move
     * @param moveTo the location the card is moving to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PayLandingCostEffect(Action action, String playerId, PhysicalCard cardToMove, PhysicalCard moveTo, boolean asReact, float changeInCost) {
        super(action);
        _playerId = playerId;
        _cardToMove = cardToMove;
        _moveFrom = cardToMove.getAtLocation();
        _moveTo = moveTo;
        _asReact = asReact;
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        float moveCost = game.getModifiersQuerying().getLandingCost(game.getGameState(), _cardToMove, _moveFrom, _moveTo, _asReact, _changeInCost);
        if (moveCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _playerId, moveCost));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
