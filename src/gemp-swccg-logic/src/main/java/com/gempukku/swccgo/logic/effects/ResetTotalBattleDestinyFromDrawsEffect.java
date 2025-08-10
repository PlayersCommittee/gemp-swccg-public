package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that resets the specified player's total battle destiny after drawing battle destiny is complete.
 */
public class ResetTotalBattleDestinyFromDrawsEffect extends AbstractSuccessfulEffect {
    private String _playerAffected;
    private float _resetValue;

    /**
     * Creates an effect that resets the specified player's total battle destiny after drawing battle destiny is complete.
     * @param action the action performing this effect
     * @param playerId the player whose total battle destiny is reset
     * @param resetValue the reset value
     */
    public ResetTotalBattleDestinyFromDrawsEffect(Action action, String playerId, float resetValue) {
        super(action);
        _playerAffected = playerId;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String performingPlayerId = _action.getPerformingPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        BattleState battleState = gameState.getBattleState();
        if (battleState !=null) {

            if (modifiersQuerying.mayNotResetTotalBattleDestiny(gameState, _playerAffected, performingPlayerId)) {
                gameState.sendMessage(performingPlayerId + " may not reset " + _playerAffected + "'s total battle destiny");
                return;
            }

            battleState.setTotalBattleDestiny(game, _playerAffected, _resetValue);

            if (performingPlayerId != null)
                gameState.sendMessage(performingPlayerId + " resets " + _playerAffected + "'s total battle destiny to " + GuiUtils.formatAsString(_resetValue) + " using " + GameUtils.getCardLink(_action.getActionSource()));
            else
                gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " resets " + _playerAffected + "'s total battle destiny to " + GuiUtils.formatAsString(_resetValue));
        }
    }
}
