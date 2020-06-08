package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Force loss (except battle damage).
 * See BattleDamageModifier for modifying Force loss due to battle damage.
 */
public class ForceLossModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private int _forceLossId;

    /**
     * Creates a modifier to Force loss (except battle damage).
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force loss is modified
     * @param forceLossId the id of the Force loss to be modified
     */
    public ForceLossModifier(PhysicalCard source, float modifierAmount, String playerId, int forceLossId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId, forceLossId);
    }

    /**
     * Creates a modifier to Force loss (except battle damage).
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force loss is modified
     * @param forceLossId the id of the Force loss to be modified
     */
    private ForceLossModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, int forceLossId) {
        super(source, null, null, condition, ModifierType.FORCE_LOSS, false);
        _evaluator = evaluator;
        _playerId = playerId;
        _forceLossId = forceLossId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return _playerId + " Force loss +" + GuiUtils.formatAsString(value);
        else
            return _playerId + " Force loss " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying) {
        ForceLossState forceLossState = gameState.getTopForceLossState();
        if (forceLossState != null && forceLossState.getId() == _forceLossId)
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
