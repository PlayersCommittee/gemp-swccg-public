package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that pays the cost of initiating a battle.
 */
public class PayInitiateBattleCostEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;
    private String _playerId;

    /**
     * Creates an effect that pays the cost of initiating a battle.
     * @param action the action performing this effect
     * @param location the battle location
     * @param playerId the player initiating the battle
     */
    public PayInitiateBattleCostEffect(Action action, PhysicalCard location, String playerId) {
        super(action);
        _location = location;
        _playerId = playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {

        SubAction subAction = new SubAction(_action);
        float initiateBattleCostAsLoseForce = game.getModifiersQuerying().getInitiateBattleCostAsLoseForce(game.getGameState(), _location, _playerId);
        if (initiateBattleCostAsLoseForce > 0) {
            subAction.appendEffect(new LoseForceEffect(subAction, _playerId, initiateBattleCostAsLoseForce, true));
        }
        float initiateBattleCost = game.getModifiersQuerying().getInitiateBattleCost(game.getGameState(), _location, _playerId);
        if (initiateBattleCost > 0) {
            subAction.appendEffect(new UseForceEffect(subAction, _playerId, initiateBattleCost));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
