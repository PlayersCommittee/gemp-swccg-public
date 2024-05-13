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
 * A move using landspeed cost modifier.
 */
public class MoveCostUsingLandspeedModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a move using landspeed cost modifier.
     * @param source the card that is the source of the modifier and whose move using landspeed cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public MoveCostUsingLandspeedModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a move using landspeed cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move using landspeed cost is modified
     * @param modifierAmount the amount of the modifier
     */
    public MoveCostUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a move using landspeed cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move using landspeed cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public MoveCostUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a move using landspeed cost modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move using landspeed cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public MoveCostUsingLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVE_COST_USING_LANDSPEED, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Move using landspeed cost +" + GuiUtils.formatAsString(value);
        else
            return "Move using landspeed cost " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getMoveCostModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
