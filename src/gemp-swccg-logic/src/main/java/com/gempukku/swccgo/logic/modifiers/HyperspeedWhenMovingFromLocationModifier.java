package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A hyperspeed modifier for when affected cards moving from specified locations.
 */
public class HyperspeedWhenMovingFromLocationModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _locationFilter;

    /**
     * Creates a hyperspeed modifier for when moving from a location accepted by the location filter.
     * @param source the card that is the source of the modifier and whose hyperspeed is modified
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public HyperspeedWhenMovingFromLocationModifier(PhysicalCard source, int modifierAmount, Filterable locationFilter) {
        this(source, source, null, modifierAmount, locationFilter);
    }

    /**
     * Creates a hyperspeed modifier for when moving from a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public HyperspeedWhenMovingFromLocationModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, null, modifierAmount, locationFilter);
    }

    /**
     * Creates a hyperspeed modifier for when moving from a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public HyperspeedWhenMovingFromLocationModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, Filterable locationFilter) {
        this(source, affectFilter, null, evaluator, locationFilter);
    }

    /**
     * Creates a hyperspeed modifier for when moving from a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public HyperspeedWhenMovingFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), locationFilter);
    }

    /**
     * Creates a hyperspeed modifier for when moving from a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose hyperspeed is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public HyperspeedWhenMovingFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.HYPERSPEED_WHEN_MOVING_FROM_LOCATION, false);
        _evaluator = evaluator;
        _locationFilter = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard locationCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, locationCard);
    }

    @Override
    public float getHyperspeedModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
