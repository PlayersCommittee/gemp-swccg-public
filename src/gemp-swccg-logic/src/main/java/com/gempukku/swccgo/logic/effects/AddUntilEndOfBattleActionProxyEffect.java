package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds an action proxy until the end of battle.
 */
public class AddUntilEndOfBattleActionProxyEffect extends AbstractSuccessfulEffect {
    private ActionProxy _actionProxy;

    /**
     * Creates an effect that adds an action proxy until the end of battle.
     * @param action the action performing this effect
     * @param actionProxy the action proxy
     */
    public AddUntilEndOfBattleActionProxyEffect(Action action, ActionProxy actionProxy) {
        super(action);
        _actionProxy = actionProxy;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().addUntilEndOfBattleActionProxy(_actionProxy);
    }
}