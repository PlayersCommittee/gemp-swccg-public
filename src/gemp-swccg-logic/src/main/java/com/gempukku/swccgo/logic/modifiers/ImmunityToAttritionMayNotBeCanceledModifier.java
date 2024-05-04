package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents immunity to attrition from being canceled.
 */
public class ImmunityToAttritionMayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents immunity to attrition of the source card from being canceled.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ImmunityToAttritionMayNotBeCanceledModifier(PhysicalCard source, Condition condition) {
        super(source, "Immunity to attrition may not be canceled", source, condition, ModifierType.IMMUNITY_TO_ATTRITION_MAY_NOT_BE_CANCELED);
    }
}
