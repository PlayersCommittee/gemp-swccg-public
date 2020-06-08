package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the specified game text action is completed.
 */
public class AddUntilEndOfGameTextActionModifierEffect extends AddModifierWithDurationEffect {
    private GameTextAction _gameTextAction;

    /**
     * Creates an effect that adds a modifier until the playing of the specified Interrupt is completed.
     * @param action the action performing this effect
     * @param gameTextAction the game text action
     * @param modifier the modifier
     */
    public AddUntilEndOfGameTextActionModifierEffect(Action action, GameTextAction gameTextAction, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
        _gameTextAction = gameTextAction;
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg();
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfGameTextActionModifier(_modifier, _gameTextAction);
    }
}
