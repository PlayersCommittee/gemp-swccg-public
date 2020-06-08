package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that resets attrition against the specified player.
 */
public class ResetAttritionEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _resetValue;

    /**
     * Creates an effect that resets attrition against the specified player.
     * @param action the action performing this effect
     * @param playerId the player whose attrition is reset
     * @param resetValue the reset value
     */
    public ResetAttritionEffect(Action action, String playerId, float resetValue) {
        super(action);
        _playerId = playerId;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState !=null) {
            battleState.setAttritionTotal(_playerId, _resetValue);

            if (_action.getPerformingPlayer() != null)
                gameState.sendMessage(_action.getPerformingPlayer() + " resets attrition against " + _playerId + " to " + GuiUtils.formatAsString(_resetValue) + " using " + GameUtils.getCardLink(_action.getActionSource()));
            else
                gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " resets attrition against " + _playerId + " to " + GuiUtils.formatAsString(_resetValue));
        }
    }
}
