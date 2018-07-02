package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A defense value modifier.
 */
public class DefenseValueModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a defense value modifier.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator);
    }

    /**
     * Creates a defense value modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose defense value is modified
     * @param modifierAmount the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a defense value modifier.
     * @param source the card that is the source of the modifier and whose defense value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Condition condition, float modifierAmount) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a defense value modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose defense value is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a defense value modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose defense value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a defense value modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose defense value is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefenseValueModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.DEFENSE_VALUE, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Defense value +" + GuiUtils.formatAsString(value);
        else
            return "Defense value " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getDefenseValueModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
