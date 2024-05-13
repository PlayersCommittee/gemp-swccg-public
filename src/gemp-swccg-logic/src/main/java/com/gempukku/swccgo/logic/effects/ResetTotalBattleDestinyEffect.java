package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ResetTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that resets total battle destiny for the specified player until the end of battle.
 */
public class ResetTotalBattleDestinyEffect extends AbstractSuccessfulEffect {
    private String _playerAffected;
    private float _resetValue;

    /**
     * Creates an effect that reset total battle destiny for the specified player until the end of battle.
     * @param action the action
     * @param playerId the player whose total battle destiny is reset
     * @param resetValue the reset value
     */
    public ResetTotalBattleDestinyEffect(Action action, String playerId, float resetValue) {
        super(action);
        _playerAffected = playerId;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String performingPlayerId = _action.getPerformingPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (modifiersQuerying.mayNotResetTotalBattleDestiny(gameState, _playerAffected, performingPlayerId)) {
            gameState.sendMessage(performingPlayerId + " may not reset " + _playerAffected + "'s total battle destiny");
            return;
        }

        game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                new ResetTotalBattleDestinyModifier(_action.getActionSource(), _resetValue, _playerAffected));
        gameState.sendMessage(performingPlayerId + " resets " + _playerAffected + "'s total battle destiny to " + GuiUtils.formatAsString(_resetValue));
    }
}
