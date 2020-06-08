package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that cancels immunity to attrition.
 */
public class CancelImmunityToAttritionModifier extends AbstractModifier {

    /**
     * Creates a modifier that cancels immunity to attrition.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose immunity to attrition is canceled
     */
    public CancelImmunityToAttritionModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Immunity to attrition is canceled", affectFilter, ModifierType.LOSE_IMMUNITY_TO_ATTRITION);
    }

    /**
     * Creates a modifier that cancels immunity to attrition.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose immunity to attrition is canceled
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CancelImmunityToAttritionModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Immunity to attrition is canceled", affectFilter, condition, ModifierType.LOSE_IMMUNITY_TO_ATTRITION);
    }
}
