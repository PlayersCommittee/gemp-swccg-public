package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies total battle destiny for the specified player until the end of battle.
 */
public class ModifyTotalBattleDestinyEffect extends AbstractSuccessfulEffect {
    private String _playerAffected;
    private float _modifierAmount;
    private boolean _skipInBattleCheck;

    /**
     * Creates an effect that modifies total battle destiny for the specified player until the end of battle.
     * @param action the action
     * @param playerId the player whose total battle destiny is modified
     * @param modifierAmount the amount of total battle destiny to modify
     */
    public ModifyTotalBattleDestinyEffect(Action action, String playerId, float modifierAmount) {
        this(action, playerId, modifierAmount, false);
    }

    /**
     * Creates an effect that modifies total battle destiny for the specified player until the end of battle.
     * @param action the action
     * @param playerId the player whose total battle destiny is modified
     * @param modifierAmount the amount of total battle destiny to modify
     */
    public ModifyTotalBattleDestinyEffect(Action action, String playerId, float modifierAmount, boolean skipInBattleCheck) {
        super(action);
        _playerAffected = playerId;
        _modifierAmount = modifierAmount;
        _skipInBattleCheck = skipInBattleCheck;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String performingPlayerId = _action.getPerformingPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (modifiersQuerying.mayNotModifyTotalBattleDestiny(gameState, _playerAffected, performingPlayerId)) {
            gameState.sendMessage(performingPlayerId + " may not modify " + _playerAffected + "'s total battle destiny");
            return;
        }

        if (_modifierAmount > 0
                && modifiersQuerying.mayNotIncreaseTotalBattleDestiny(gameState, _playerAffected, performingPlayerId)) {
            gameState.sendMessage(performingPlayerId + " may not increase " + _playerAffected + "'s total battle destiny");
            return;
        }

        game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                new TotalBattleDestinyModifier(_action.getActionSource(), _modifierAmount, _playerAffected, _skipInBattleCheck));
        if (_modifierAmount > 0) {
            gameState.sendMessage(performingPlayerId + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to total battle destiny");
        } else if (_modifierAmount < 0) {
            gameState.sendMessage(performingPlayerId + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from total battle destiny");
        }
    }
}
