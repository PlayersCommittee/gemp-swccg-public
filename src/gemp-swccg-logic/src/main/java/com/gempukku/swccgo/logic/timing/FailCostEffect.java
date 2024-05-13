package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;

/**
 * An effect that always fails. This can be used to fail out during the initiation step of an action.
 */
public class FailCostEffect extends AbstractStandardEffect implements UsageEffect, TargetingEffect {

    /**
     * Creates a passthru effect.
     * @param action the action performing this effect
     */
    public FailCostEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        return new FullEffectResult(false);
    }
}
