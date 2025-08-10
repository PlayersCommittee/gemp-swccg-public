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
 * A move cost modifier when moving from specified locations to specified locations using hyperspeed.
 */
public class MoveCostFromLocationToLocationUsingHyperspeedModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _fromLocationFilter;
    private Filter _toLocationFilter;

    /**
     * Creates a move cost modifier when moving from specified locations to specified locations using hyperspeed.
     * @param source the card that is the source of the modifier and whose move cost is modified
     * @param modifierAmount the amount of the modifier
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MoveCostFromLocationToLocationUsingHyperspeedModifier(PhysicalCard source, int modifierAmount, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, source, null, modifierAmount, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations to specified locations using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param modifierAmount the amount of the modifier
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MoveCostFromLocationToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, affectFilter, null, modifierAmount, fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations to specified locations using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MoveCostFromLocationToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable fromLocationFilter, Filterable toLocationFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), fromLocationFilter, toLocationFilter);
    }

    /**
     * Creates a move cost modifier when moving from specified locations to specified locations using hyperspeed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose move cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param fromLocationFilter the from location filter
     * @param toLocationFilter the to location filter
     */
    public MoveCostFromLocationToLocationUsingHyperspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable fromLocationFilter, Filterable toLocationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, false);
        _evaluator = evaluator;
        _fromLocationFilter = Filters.and(Filters.location, fromLocationFilter);
        _toLocationFilter = Filters.and(Filters.location, toLocationFilter);
    }

    @Override
    public float getMoveCostFromLocationToLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard fromLocation, PhysicalCard toLocation) {
        if (Filters.and(_fromLocationFilter).accepts(gameState, modifiersQuerying, fromLocation)
                && Filters.and(_toLocationFilter).accepts(gameState, modifiersQuerying, toLocation)) {
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
        }
        return 0;
    }
}
