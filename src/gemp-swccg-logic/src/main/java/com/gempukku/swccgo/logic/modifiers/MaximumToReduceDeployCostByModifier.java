package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that defines the most a deploy cost can be modified (reduced) from printed value by modifiers.
 */
public class MaximumToReduceDeployCostByModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that defines the most a deploy cost can be modified (reduced) from printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public MaximumToReduceDeployCostByModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that defines the most a deploy cost can be modified (reduced) from printed value by modifiers.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private MaximumToReduceDeployCostByModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter))), condition, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY, true);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return "Deploy cost may be reduced by maximum of " + GuiUtils.formatAsString(_evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard));
    }

    @Override
    public float getMaximumToReduceDeployCostBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
