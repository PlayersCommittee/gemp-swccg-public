package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A deploy cost modifier for when a card is deployed using dejarik rules.
 */
public class DeployCostUsingDejarikRulesModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostUsingDejarikRulesModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostUsingDejarikRulesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a deploy cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DeployCostUsingDejarikRulesModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.DEPLOY_COST_USING_DEJARIK_RULES, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Deploy cost +" + GuiUtils.formatAsString(value) + " when using dejarik rules";
        else
            return "Deploy cost " + GuiUtils.formatAsString(value) + " when using dejarik rules";
    }

    @Override
    public float getDeployCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
