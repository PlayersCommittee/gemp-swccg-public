package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that pays the cost for starships to ship-dock.
 */
public class PayShipdockingCostEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _starship1;
    private PhysicalCard _starship2;
    private float _changeInCost;

    /**
     * Creates an effect that pays the cost for starships to ship-dock.
     * @param action the action performing this effect
     * @param playerId the player to pay the cost
     * @param starship1 a starship to ship-dock
     * @param starship2 a starship to ship-dock
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public PayShipdockingCostEffect(Action action, String playerId, PhysicalCard starship1, PhysicalCard starship2, float changeInCost) {
        super(action);
        _playerId = playerId;
        _starship1 = starship1;
        _starship2 = starship2;
        _changeInCost = changeInCost;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        float moveCost = game.getModifiersQuerying().getShipdockingCost(game.getGameState(), _starship1, _starship2, _changeInCost);
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
