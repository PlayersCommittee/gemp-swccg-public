package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A maneuver modifier.
 */
public class ManeuverModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a maneuver modifier.
     * @param source the card that is the source of the modifier and whose maneuver is modified
     * @param modifierAmount the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, float modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the card that is the source of the modifier and whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Condition condition, float modifierAmount) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param modifierAmount the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the card that is the source of the modifier and whose maneuver is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the card that is the source of the modifier and whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, source, condition, evaluator);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param cumulative true if the effect should cumulatively stack with other modifiers from the same source, false otherwise
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), cumulative);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        this(source, affectFilter, condition, evaluator, false);
    }

    /**
     * Creates a maneuver modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose maneuver is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param cumulative true if the effect should cumulatively stack with other modifiers from the same source, false otherwise
     */
    public ManeuverModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.MANEUVER, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Maneuver +" + GuiUtils.formatAsString(value);
        else
            return "Maneuver " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getManeuverModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
