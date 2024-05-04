package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the end of attack.
 */
public class AddUntilEndOfAttackModifierEffect extends AddModifierWithDurationEffect {

    /**
     * Creates an effect that adds a modifier until the end of attack.
     * @param action the action adding the modifier
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public AddUntilEndOfAttackModifierEffect(Action action, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfAttackModifier(_modifier);
    }
}
