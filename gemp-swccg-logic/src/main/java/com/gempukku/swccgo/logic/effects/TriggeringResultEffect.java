package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect to emit the specified effect result.
 */
public class TriggeringResultEffect extends AbstractSuccessfulEffect {
    private EffectResult _effectResult;

    /**
     * Creates an effect to emit the specified effect result.
     * @param action the action performing this
     * @param effectResult the effect result to emit
     */
    public TriggeringResultEffect(Action action, EffectResult effectResult) {
        super(action);
        _effectResult = effectResult;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().emitEffectResult(_effectResult);
    }
}
