package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to require extra Force cost to deploy specified cards to locations.
 */
public class ExtraForceCostToDeployCardToLocationModifier extends ExtraForceCostToDeployCardToTargetModifier {

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ExtraForceCostToDeployCardToLocationModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator, Filters.location);
    }

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location Filter
     */
    private ExtraForceCostToDeployCardToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable locationFilter) {
        super(source, affectFilter, condition, evaluator, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }
}
