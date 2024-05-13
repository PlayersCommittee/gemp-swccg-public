package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A deploy cost modifier.
 */
public class DeployCostModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a deploy cost modifier.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, source, condition, evaluator);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DeployCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter))), condition, ModifierType.DEPLOY_COST, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Deploy cost +" + GuiUtils.formatAsString(value);
        else
            return "Deploy cost " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getDeployCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
