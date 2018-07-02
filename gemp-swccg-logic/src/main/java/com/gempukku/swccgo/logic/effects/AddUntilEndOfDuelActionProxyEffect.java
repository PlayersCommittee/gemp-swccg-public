package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that adds an action proxy until the end of current duel.
 */
public class AddUntilEndOfDuelActionProxyEffect extends PassthruEffect {
    private ActionProxy _actionProxy;

    /**
     * Creates an effect that adds an action proxy until the end of current duel.
     * @param action the action performing this effect
     * @param actionProxy the action proxy
     */
    public AddUntilEndOfDuelActionProxyEffect(Action action, ActionProxy actionProxy) {
        super(action);
        _actionProxy = actionProxy;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().addUntilEndOfDuelActionProxy(_actionProxy);
    }
}