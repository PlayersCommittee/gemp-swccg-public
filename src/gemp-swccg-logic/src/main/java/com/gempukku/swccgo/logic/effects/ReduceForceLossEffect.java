package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.BattleDamageModifier;
import com.gempukku.swccgo.logic.modifiers.ForceLossMinimumModifier;
import com.gempukku.swccgo.logic.modifiers.ForceLossModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;


/**
 * An effect that reduces Force loss.
 */
public class ReduceForceLossEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _amount;
    private int _toMinimum;
    private boolean _reduceToAmount;
    private ForceLossState _forceLossState;

    /**
     * Creates an effect that reduces Force loss.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced by
     */
    public ReduceForceLossEffect(Action action, String playerId, int amount) {
        this(action, playerId, amount, 0, false);
    }

    /**
     * Creates an effect that reduces Force loss.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced by
     */
    public ReduceForceLossEffect(Action action, String playerId, int amount, int toMinimum) {
        this(action, playerId, amount, toMinimum, false);
    }

    /**
     * Creates an effect that reduces Force loss.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced by
     * @param toMinimum the minimum the Force loss can be reduced to
     * @param reduceToAmount true to reduce Force loss to amount, instead of by amount
     */
    public ReduceForceLossEffect(Action action, String playerId, int amount, int toMinimum, boolean reduceToAmount) {
        this(action, playerId, amount, toMinimum, reduceToAmount, null);
    }

    /**
     * Creates an effect that reduces Force loss.
     * @param action the action performing this effect
     * @param playerId the player whose Force loss is reduced
     * @param amount the amount the Force loss is reduced by
     * @param toMinimum the minimum the Force loss can be reduced to
     * @param reduceToAmount true to reduce Force loss to amount, instead of by amount
     * @param forceLossState which force loss state to target (should almost never have to use this)
     */
    public ReduceForceLossEffect(Action action, String playerId, int amount, int toMinimum, boolean reduceToAmount, ForceLossState forceLossState) {
        super(action);
        _playerId = playerId;
        _amount = amount;
        _toMinimum = toMinimum;
        _reduceToAmount = reduceToAmount;
        _forceLossState = forceLossState;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_amount > 0 || _reduceToAmount) {
            GameState gameState = game.getGameState();
            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

            ForceLossState forceLossState = (_forceLossState == null ? gameState.getTopForceLossState() : _forceLossState);
            // Check if this is Force loss that is not from battle damage
            if (forceLossState != null) {

                if (forceLossState.getLoseForceEffect().isCannotBeReduced(game)) {
                    gameState.sendMessage(_playerId + "'s Force loss cannot be reduced");
                } else {
                    if (_reduceToAmount) {
                        _amount = Math.max(0, forceLossState.getLoseForceEffect().getForceLossRemaining(game) - _amount);
                    }
                    if (_amount > 0) {
                        float currentForceLoss = forceLossState.getLoseForceEffect().getForceLossRemaining(game);
                        float cannotReduceBelow = forceLossState.getLoseForceEffect().cannotBeReducedBelow();
                        if (currentForceLoss-_amount<cannotReduceBelow)
                            _toMinimum = (int) Math.max(_toMinimum, cannotReduceBelow);

                        if (_toMinimum > 0) {
                            gameState.sendMessage(_playerId + "'s Force loss is reduced by " + GuiUtils.formatAsString(_amount) + " (to a minimum of " + _toMinimum + ")");
                            modifiersEnvironment.addUntilEndOfForceLossModifier(
                                    new ForceLossMinimumModifier(_action.getActionSource(), _toMinimum, _playerId, forceLossState.getId()));
                        } else {
                            gameState.sendMessage(_playerId + "'s Force loss is reduced by " + GuiUtils.formatAsString(_amount));
                        }
                        modifiersEnvironment.addUntilEndOfForceLossModifier(
                                new ForceLossModifier(_action.getActionSource(), -_amount, _playerId, forceLossState.getId()));
                    }
                }
            } else {
                // Otherwise this is Force loss that is from battle damage
                BattleState battleState = gameState.getBattleState();
                if (battleState != null && battleState.isReachedDamageSegment()) {
                    if (_reduceToAmount) {
                        _amount = Math.max(0, battleState.getBattleDamageRemaining(game, _playerId) - _amount);
                    }
                    if (_amount > 0) {
                        if (_toMinimum > 0) {
                            throw new UnsupportedOperationException("Setting minimum Force loss for battle damage");
                        }

                        gameState.sendMessage(_playerId + "'s battle damage is reduced by " + GuiUtils.formatAsString(_amount));
                        modifiersEnvironment.addUntilEndOfBattleModifier(
                                new BattleDamageModifier(_action.getActionSource(), -_amount, _playerId));
                    }
                } else {
                    throw new UnsupportedOperationException("Reducing unknown Force loss");
                }
            }
        }
    }
}
