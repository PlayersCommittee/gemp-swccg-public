package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that pays the cost of initiating a Force drain.
 */
public class PayInitiateForceDrainCostEffect extends AbstractSubActionEffect {
    private PhysicalCard _location;
    private String _playerId;

    /**
     * Creates an effect that pays the cost of initiating a Force drain.
     * @param action the action performing this effect
     * @param location the Force drain location
     * @param playerId the player performing the Force drain
     */
    public PayInitiateForceDrainCostEffect(Action action, PhysicalCard location, String playerId) {
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

        float initiateForceDrainCost = game.getModifiersQuerying().getInitiateForceDrainCost(game.getGameState(), _location, _playerId);
        if (initiateForceDrainCost > 0) {
            subAction.appendEffect(
                    new UseForceEffect(subAction, _playerId, initiateForceDrainCost));
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
