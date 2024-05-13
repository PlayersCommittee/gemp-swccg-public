package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;

import java.util.Set;

/**
 * An effect for removing modifiers that expire when a specified condition is fulfilled.
 */
public class CheckForExpiredModifiersEffect extends AbstractSuccessfulEffect {
    private Set<EffectResult> _effectResults;

    public CheckForExpiredModifiersEffect(Action action, Set<EffectResult> effectResults) {
        super(action);
        _effectResults = effectResults;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        for (EffectResult effectResult : _effectResults) {
            if (TriggerConditions.isTableChanged(game, effectResult)) {
                game.getModifiersEnvironment().removeExpiredModifiers();
                break;
            }
        }
    }
}
