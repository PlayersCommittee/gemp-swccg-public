package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

public class AddUntilEndOfDrawDestinyModifierEffect extends PassthruEffect {
    private Modifier _modifier;

    public AddUntilEndOfDrawDestinyModifierEffect(Action action, Modifier modifier) {
        super(action);
        _modifier = modifier;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getModifiersEnvironment().addUntilEndOfDrawDestinyModifier(_modifier);
    }
}
