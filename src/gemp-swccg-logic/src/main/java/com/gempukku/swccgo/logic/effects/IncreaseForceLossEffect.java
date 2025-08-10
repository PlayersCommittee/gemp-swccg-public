package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.BattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceLossModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that increases Force loss against the specified player.
 */
public class IncreaseForceLossEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private int _amount;

    /**
     * Creates an effect that increases Force loss against the specified player.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is increased
     * @param amount the amount the Force loss is increased by
     */
    public IncreaseForceLossEffect(Action action, String playerId, int amount) {
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
                // Check if this Force loss if from Force drain
                if (forceLossState.getLoseForceEffect().isForceDrain()) {
                    ForceDrainState forceDrainState = gameState.getForceDrainState();
                    gameState.sendMessage("Force drain is increased by " + _amount);
                    // Increase in Force loss is actually treated as a Force drain bonus
                    modifiersEnvironment.addUntilEndOfForceDrainModifier(
                            new ForceDrainModifier(_action.getActionSource(), forceDrainState.getLocation(), _amount, forceDrainState.getPlayerId()));
                }
                else {
                    gameState.sendMessage(_playerId + "'s Force loss is increased by " + _amount);
                    modifiersEnvironment.addUntilEndOfForceLossModifier(
                            new ForceLossModifier(_action.getActionSource(), _amount, _playerId, forceLossState.getId()));
                }
            }
            else {
                // Otherwise this is Force loss that is from battle damage
                BattleState battleState = gameState.getBattleState();
                if (battleState != null && battleState.isReachedDamageSegment()) {
                    gameState.sendMessage(_playerId + "'s battle damage is increased by " + _amount);
                    modifiersEnvironment.addUntilEndOfBattleModifier(
                            new BattleDamageModifier(_action.getActionSource(), _amount, _playerId));
                }
                else {
                    throw new UnsupportedOperationException("Increasing unknown Force loss");
                }
            }
        }
    }
}

