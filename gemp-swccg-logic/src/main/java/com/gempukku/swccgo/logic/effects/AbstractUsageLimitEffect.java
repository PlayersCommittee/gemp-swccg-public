package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.UsageEffect;

/**
 * An abstract effect that can be added to an action as a usage cost.
 */
public abstract class AbstractUsageLimitEffect extends AbstractEffect implements UsageEffect {

    /**
     * Creates an effect that can be added to an action as a usage cost.
     * @param action the action performing this effect
     */
    protected AbstractUsageLimitEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }
}
