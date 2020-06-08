package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.BlowAwayState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to blown away Force loss.
 */
public class BlownAwayForceLossModifier extends AbstractModifier {
    private int _blowAwayStateId;
    private Evaluator _evaluator;

    /**
     * Creates a modifier to blown away Force loss.
     * @param source the source of the modifier
     * @param blowAwayStateId the id of the blow away state for the blow away to affect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose blown away Force loss is modified
     */
    public BlownAwayForceLossModifier(PhysicalCard source, int blowAwayStateId, float modifierAmount, String playerId) {
        this(source, blowAwayStateId, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to blown away Force loss.
     * @param source the source of the modifier
     * @param blowAwayStateId the id of the blow away state for the blow away to affect
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose blown away Force loss is modified
     */
    private BlownAwayForceLossModifier(PhysicalCard source, int blowAwayStateId, Condition condition, float modifierAmount, String playerId) {
        this(source, blowAwayStateId, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to blown away Force loss.
     * @param source the source of the modifier
     * @param blowAwayStateId the id of the blow away state for the blow away to affect
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose blown away Force loss is modified
     */
    private BlownAwayForceLossModifier(PhysicalCard source, int blowAwayStateId, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.BLOWN_AWAY_FORCE_LOSS, false);
        _blowAwayStateId = blowAwayStateId;
        _evaluator = evaluator;
        _playerId = playerId;
    }

    /**
     * Determines if this modifier affects the current draw destiny effect.
     * @param gameState the game state
     * @return true or false
     */
    @Override
    public boolean isForTopBlowAwayEffect(GameState gameState) {
        BlowAwayState blowAwayState = gameState.getTopBlowAwayState();
        return blowAwayState != null && _blowAwayStateId == blowAwayState.getId();
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
