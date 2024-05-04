package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that adds a modifier until the end of the specified player's next turn.
 */
public class AddUntilEndOfPlayersNextTurnModifierEffect extends AddModifierWithDurationEffect {
    private String _playerId;

    /**
     * Creates an effect that adds a modifier until the end of the specified player's next turn.
     * @param action the action adding the modifier
     * @param playerId the player
     * @param modifier the modifier
     * @param actionMsg the action message
     */
    public AddUntilEndOfPlayersNextTurnModifierEffect(Action action, String playerId, Modifier modifier, String actionMsg) {
        super(action, modifier, actionMsg);
        _playerId = playerId;
    }

    @Override
    protected String getMsgText(SwccgGame game) {
        if (getActionMsg() == null)
            return null;

        if (_action.getPerformingPlayer() == null) {
            return GameUtils.getCardLink(_action.getActionSource()) + " " + getActionMsg() + " until end of " +
                    _playerId + "'s next turn";
        }
        else {
            return _action.getPerformingPlayer() + " " + getActionMsg() + " until end of " +
                    _playerId + "'s next turn using " + GameUtils.getCardLink(_action.getActionSource());
        }
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        sendMsg(game);
        game.getModifiersEnvironment().addUntilEndOfPlayersNextTurnModifier(_modifier, _playerId);
    }
}
