package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * An "Immune to attrition" modifier.
 */
public class ImmuneToAttritionModifier extends ImmuneToAttritionLessThanModifier {

    /**
     * Creates an "Immune to attrition" modifier.
     * @param source the card that is the source of the modifier and that is given immunity
     */
    public ImmuneToAttritionModifier(PhysicalCard source) {
        super(source, source, null, Float.MAX_VALUE);
    }

    /**
     * Creates an "Immune to attrition" modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose immunity to attrition is modified
     */
    public ImmuneToAttritionModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, affectFilter, null, Float.MAX_VALUE);
    }

    /**
     * Creates an "Immune to attrition" modifier.
     * @param source the card that is the source of the modifier and that is given immunity
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ImmuneToAttritionModifier(PhysicalCard source, Condition condition) {
        super(source, source, condition, Float.MAX_VALUE);
    }

    /**
     * Creates an "Immune to attrition" modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose immunity to attrition is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ImmuneToAttritionModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, affectFilter, condition, Float.MAX_VALUE);
    }
}
