package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * Triggers effect results to be responded to by cards or game rules. All effect results emitted are available to be
 * responded to at the same time.
 */
public class TriggeringResultsEffect extends AbstractSuccessfulEffect {
    private Collection<EffectResult> _effectResults;

    /**
     * Creates an effect that emits the specified effect results.
     * @param action the action performing this effect
     * @param effectResults the effect results to emit
     */
    public TriggeringResultsEffect(Action action, Collection<EffectResult> effectResults) {
        super(action);
        _effectResults = effectResults;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        for (EffectResult effectResult : _effectResults) {
            game.getActionsEnvironment().emitEffectResult(effectResult);
        }
    }
}
