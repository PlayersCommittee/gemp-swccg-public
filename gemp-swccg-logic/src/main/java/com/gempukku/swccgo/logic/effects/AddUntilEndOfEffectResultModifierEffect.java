package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect that adds a modifier until the specified effect result is completed.
 */
public class AddUntilEndOfEffectResultModifierEffect extends AddModifierWithDurationEffect {
    private EffectResult _effectResult;

    /**
     * Creates an effect that adds a modifier until the specified effect result is completed.
     * @param action the action performing this effect
     * @param effectResult the effect result
     * @param modifier the modifier
     */
    public AddUntilEndOfEffectResultModifierEffect(Action action, EffectResult effectResult, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
        _effectResult = effectResult;
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfEffectResultModifier(_modifier, _effectResult);
    }
}
