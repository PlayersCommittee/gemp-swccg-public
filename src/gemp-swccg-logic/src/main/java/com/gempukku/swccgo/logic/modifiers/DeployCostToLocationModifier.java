package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A deploy cost modifier for when deploying to specified locations.
 */
public class DeployCostToLocationModifier extends DeployCostToTargetModifier {

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the card that is the source of the modifier and whose deploy cost is modified when deploying to specified locations
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, int modifierAmount, Filterable locationFilter) {
        super(source, modifierAmount, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the card that is the source of the modifier and whose deploy cost is modified when deploying to specified locations
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, Evaluator evaluator, Filterable locationFilter) {
        super(source, null, evaluator, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, null, modifierAmount, locationFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable locationFilter, boolean cumulative) {
        this(source, affectFilter, null, modifierAmount, locationFilter, cumulative);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, Filterable locationFilter) {
        this(source, affectFilter, null, evaluator, locationFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, Filterable locationFilter, boolean cumulative) {
        this(source, affectFilter, null, evaluator, locationFilter, cumulative);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable locationFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), locationFilter);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param locationFilter the location filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable locationFilter, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), locationFilter, cumulative);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable locationFilter) {
        this(source, affectFilter, condition, evaluator, locationFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param locationFilter the location filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable locationFilter, boolean cumulative) {
        super(source, affectFilter, condition, evaluator, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)), cumulative);
    }
}
