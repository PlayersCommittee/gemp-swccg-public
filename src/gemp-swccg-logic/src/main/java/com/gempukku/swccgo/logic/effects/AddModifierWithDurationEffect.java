package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An abstract effect for adding a modifier for a specified duration.
 */
abstract class AddModifierWithDurationEffect extends AbstractSuccessfulEffect {
    protected Modifier _modifier;
    private String _actionMsg;

    /**
     * Creates an effect for adding a modifier for a specified duration.
     * @param action    the action adding the modifier
     * @param modifier  the modifier
     * @param actionMsg the action message
     */
    protected AddModifierWithDurationEffect(Action action, Modifier modifier, String actionMsg) {
        super(action);
        _modifier = modifier;
        _actionMsg = actionMsg;
    }

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

    protected final String getActionMsg() {
        if (_actionMsg == null)
            return null;

        return _actionMsg.length() > 2 ? (_actionMsg.substring(0, 2).toLowerCase() + _actionMsg.substring(2)) : _actionMsg.toLowerCase();
    }

    protected final void sendMsg(SwccgGame game) {
        String msgText = getMsgText(game);
        if (msgText != null) {
            game.getGameState().sendMessage(msgText);
        }
    }
}
