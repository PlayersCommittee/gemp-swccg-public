package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

public class AddUntilEndOfGameActionProxyEffect extends PassthruEffect {
    private ActionProxy _actionProxy;

    public AddUntilEndOfGameActionProxyEffect(Action action, ActionProxy actionProxy) {
        super(action);
        _actionProxy = actionProxy;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().addUntilEndOfGameActionProxy(_actionProxy);
    }
}