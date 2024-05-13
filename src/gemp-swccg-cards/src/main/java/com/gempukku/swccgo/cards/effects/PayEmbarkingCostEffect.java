package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that pays the cost to embark on a card.
 */
public class PayEmbarkingCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardToMove;
    private PhysicalCard _moveTo;
    private float _changeInCost;

    /**
     * Creates an effect that pays the cost to embark on a card.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardToMove the card to move
     * @param moveTo the card to embark on
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PayEmbarkingCostEffect(Action action, String playerId, PhysicalCard cardToMove, PhysicalCard moveTo, float changeInCost) {
        super(action);
        _playerId = playerId;
        _cardToMove = cardToMove;
        _moveTo = moveTo;
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        float moveCost = game.getModifiersQuerying().getEmbarkingCost(game.getGameState(), _cardToMove, _moveTo, _changeInCost);
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
