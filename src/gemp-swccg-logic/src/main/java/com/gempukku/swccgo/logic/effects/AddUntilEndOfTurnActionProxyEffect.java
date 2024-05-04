package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that adds an action proxy until the end of the turn.
 */
public class AddUntilEndOfTurnActionProxyEffect extends PassthruEffect {
    private ActionProxy _actionProxy;

    /**
     * Creates an effect that adds an action proxy until the end of the turn.
     * @param action the action performing this effect
     * @param actionProxy the action proxy
     */
    public AddUntilEndOfTurnActionProxyEffect(Action action, ActionProxy actionProxy) {
        super(action);
        _actionProxy = actionProxy;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().addUntilEndOfTurnActionProxy(_actionProxy);
    }
}