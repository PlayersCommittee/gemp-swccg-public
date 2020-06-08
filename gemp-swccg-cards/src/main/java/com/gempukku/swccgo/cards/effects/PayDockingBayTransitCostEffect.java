package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that pays the cost of docking bay transit.
 */
public class PayDockingBayTransitCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _cardsToMove;
    private PhysicalCard _fromDockingBay;
    private PhysicalCard _toDockingBay;
    private float _changeInCost;

    /**
     * Creates an effect that pays the cost of docking bay transit.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param cardsToMove the cards to move
     * @param fromDockingBay the docking bay the cards are moving from
     * @param toDockingBay the docking bay the cards are moving to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PayDockingBayTransitCostEffect(Action action, String playerId, Collection<PhysicalCard> cardsToMove, PhysicalCard fromDockingBay, PhysicalCard toDockingBay, float changeInCost) {
        super(action);
        _playerId = playerId;
        _cardsToMove = cardsToMove;
        _fromDockingBay = fromDockingBay;
        _toDockingBay = toDockingBay;
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        float maxMoveCost = 0;
        for (PhysicalCard cardToMove : _cardsToMove) {
            float moveCost = game.getModifiersQuerying().getDockingBayTransitCost(game.getGameState(), cardToMove, _fromDockingBay, _toDockingBay, _changeInCost);
            maxMoveCost = Math.max(maxMoveCost, moveCost);
        }

        if (maxMoveCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _playerId, maxMoveCost));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
