package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that pays the cost of moving a card using location text.
 */
public class PayMoveUsingLocationTextCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _cardToMove;
    private PhysicalCard _moveTo;
    private float _baseCost;
    private float _changeInCost;

    /**
     * Creates an effect that pays the cost of moving a card using location text.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardToMove the card to move
     * @param moveTo the card to move to
     * @param baseCost base cost in amount of Force required to perform the movement
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PayMoveUsingLocationTextCostEffect(Action action, String playerId, PhysicalCard cardToMove, PhysicalCard moveTo, float baseCost, float changeInCost) {
        super(action);
        _playerId = playerId;
        _cardToMove = cardToMove;
        _moveTo = moveTo;
        _baseCost = baseCost;
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        PhysicalCard moveFromLocation = modifiersQuerying.getLocationHere(gameState, _cardToMove);
        PhysicalCard moveToLocation = modifiersQuerying.getLocationHere(gameState, _moveTo);

        SubAction subAction = new SubAction(_action);

        float moveCost = modifiersQuerying.getMoveUsingLocationTextCost(gameState, _cardToMove, moveFromLocation, moveToLocation, _baseCost, _changeInCost);
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
