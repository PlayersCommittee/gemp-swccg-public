package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the end of the turn.
 */
public class AddUntilEndOfTurnModifierEffect extends AddModifierWithDurationEffect {

    /**
     * Creates an effect that adds a modifier until the end of the turn.
     * @param action the action adding the modifier
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public AddUntilEndOfTurnModifierEffect(Action action, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg() + " until end of the turn";
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " until end of the turn using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfTurnModifier(_modifier);
    }
}
