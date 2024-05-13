package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

public abstract class CompleteDestinyDrawEffect extends AbstractStandardEffect {

    public CompleteDestinyDrawEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        performEffect(game);
        return new FullEffectResult(false);
    }

    protected abstract void performEffect(SwccgGame game);
}
