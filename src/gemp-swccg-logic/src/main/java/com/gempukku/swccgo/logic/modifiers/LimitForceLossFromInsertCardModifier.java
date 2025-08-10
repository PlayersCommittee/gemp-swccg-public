package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that sets the maximum amount of Force that the specified player can lose from an 'insert' card.
 */
public class LimitForceLossFromInsertCardModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from an 'insert' card.
     * @param source the source card of this modifier
     * @param condition the condition under which this modifier is in effect
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromInsertCardModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose an 'insert' card.
     * @param source the source card of this modifier
     * @param condition the condition under which this modifier is in effect
     * @param evaluator the evaluator that determine the amount of the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromInsertCardModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.LIMIT_FORCE_LOSS_FROM_INSERT_CARD, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public float getForceLossLimit(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
