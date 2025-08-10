package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.BattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that reduces battle damage.
 */
public class ReduceBattleDamageEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private int _amount;

    /**
     * Creates an effect that reduces battle damage.
     * @param action the action performing this effect
     * @param playerId the player whose battle damage is reduced
     * @param amount the amount the battle damage is reduced by
     */
    public ReduceBattleDamageEffect(Action action, String playerId, int amount) {
        super(action);
        _playerId = playerId;
        _amount = amount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_amount > 0) {
            GameState gameState = game.getGameState();
            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

            ForceLossState forceLossState = gameState.getTopForceLossState();
            // Check if this is Force loss that is not from battle damage
            if (forceLossState != null) {
                throw new UnsupportedOperationException("Reducing Force loss that is not battle damage");
            }
            else {
                // Otherwise this is Force loss that is from battle damage
                BattleState battleState = gameState.getBattleState();
                if (battleState != null && battleState.isReachedDamageSegment()) {
                    gameState.sendMessage(_playerId + "'s battle damage is reduced by " + _amount);
                    modifiersEnvironment.addUntilEndOfBattleModifier(
                            new BattleDamageModifier(_action.getActionSource(), -_amount, _playerId));
                }
                else {
                    throw new UnsupportedOperationException("Reducing unknown Force loss");
                }
            }
        }
    }
}
