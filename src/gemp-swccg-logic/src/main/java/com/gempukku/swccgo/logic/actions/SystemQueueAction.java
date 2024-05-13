package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * An action that does not have a source card. This is used for game actions, such as performing the phases
 * of a turn, performing the steps of a Force drain, or performing the steps of a battle.
 */
public class SystemQueueAction extends AbstractAction {

    @Override
    public Type getType() {
        return Type.GAME_PROCESS;
    }

    @Override
    public PhysicalCard getActionSource() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }
        return null;
    }
}
