package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the end of the next turn.
 */
public class AddUntilEndOfNextTurnModifierEffect extends AddModifierWithDurationEffect {

    /**
     * Creates an effect that adds a modifier until the end of the next turn.
     * @param action the action adding the modifier
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public AddUntilEndOfNextTurnModifierEffect(Action action, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg() + " until end of next turn";
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " until end of next turn using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public final void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        String playerWithNextTurn = game.getOpponent(game.getGameState().getCurrentPlayerId());
        game.getModifiersEnvironment().addUntilEndOfPlayersNextTurnModifier(_modifier, playerWithNextTurn);
    }
}
