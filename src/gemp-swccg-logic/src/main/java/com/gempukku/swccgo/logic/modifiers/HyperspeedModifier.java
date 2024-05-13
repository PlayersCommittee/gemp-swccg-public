package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A hyperspeed modifier.
 */
public class HyperspeedModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a hyperspeed modifier.
     * @param source the card that is the source of the modifier and whose hyperspeed is modified
     * @param modifierAmount the amount of the modifier
     */
    public HyperspeedModifier(PhysicalCard source, float modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param modifierAmount the amount of the modifier
     */
    public HyperspeedModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public HyperspeedModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount, boolean cumulative) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), cumulative);
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public HyperspeedModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public HyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public HyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        this(source, affectFilter, condition, evaluator, false);
    }

    /**
     * Creates a hyperspeed modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    private HyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.HYPERSPEED, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Hyperspeed +" + GuiUtils.formatAsString(value);
        else
            return "Hyperspeed " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getHyperspeedModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
