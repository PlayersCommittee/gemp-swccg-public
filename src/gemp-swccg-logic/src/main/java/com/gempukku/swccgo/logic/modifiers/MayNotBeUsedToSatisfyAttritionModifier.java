package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to not satisfy attrition when forfeited.
 */
public class MayNotBeUsedToSatisfyAttritionModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to not satisfy attrition when forfeited.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeUsedToSatisfyAttritionModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to not satisfy attrition when forfeited.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeUsedToSatisfyAttritionModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be used to satisfy attrition", affectFilter, condition, ModifierType.MAY_NOT_SATISFY_ATTRITION);
    }
}
