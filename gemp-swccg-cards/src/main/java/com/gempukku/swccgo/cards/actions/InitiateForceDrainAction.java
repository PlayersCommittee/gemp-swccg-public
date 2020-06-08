package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayInitiateForceDrainCostEffect;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.ForceDrainEffect;
import com.gempukku.swccgo.logic.timing.Effect;


/**
 * The action to initiate a Force drain.
 */
public class InitiateForceDrainAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _useForceCostApplied;
    private boolean _forceDrainInitiated;

    /**
     * Creates an action to initiate a Force drain at the specified location.
     * @param playerId the player performing the Force drain
     * @param location the location
     */
    public InitiateForceDrainAction(String playerId, PhysicalCard location) {
        super(location, playerId);
        _location = location;
    }

    @Override
    public PhysicalCard getActionSource() {
        return null;
    }

    @Override
    public String getText() {
        return "Force drain";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;

                appendCost(new PayInitiateForceDrainCostEffect(this, _location, getPerformingPlayer()));
                return getNextCost();
            }

            if (!_forceDrainInitiated) {
                _forceDrainInitiated = true;

                return new ForceDrainEffect(this, _location);
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _forceDrainInitiated;
    }
}
