package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An armor modifier.
 */
public class ArmorModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * An armor modifier.
     * @param source the card that is the source of the modifier and whose armor is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ArmorModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator);
    }

    /**
     * An armor modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose armor is modified
     * @param modifierAmount the amount of the modifier
     */
    public ArmorModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * An armor modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose armor is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ArmorModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * An armor modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose armor is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ArmorModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.ARMOR, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Armor +" + GuiUtils.formatAsString(value);
        else
            return "Armor " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getArmorModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
