package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A move cost modifier when moving from specified locations.
 */
public class MoveCostFromLocationModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _movingCardFilter;

    /**
     * Creates a move cost modifier when moving from specified locations.
     * @param source the card that is the source of the modifier and whose move cost is modified
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public MoveCostFromLocationModifier(PhysicalCard source, int modifierAmount, Filterable locationFilter) {
        this(source, source, null, modifierAmount, locationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public MoveCostFromLocationModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, null, modifierAmount, locationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public MoveCostFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), locationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public MoveCostFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MOVE_COST_FROM_LOCATION, false);
        _evaluator = evaluator;
        _movingCardFilter = Filters.and(Filters.in_play, affectFilter);
    }

    @Override
    public float getMoveCostFromLocationModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard, PhysicalCard fromLocation) {
        if (Filters.and(_movingCardFilter).accepts(gameState, modifiersLogic, physicalCard)) {
            return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
        }
        return 0;
    }
}
