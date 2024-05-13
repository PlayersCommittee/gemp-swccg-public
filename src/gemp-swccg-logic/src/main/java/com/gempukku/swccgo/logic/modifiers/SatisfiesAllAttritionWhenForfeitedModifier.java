package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to satisfy all of owner's attrition when forfeited.
 */
public class SatisfiesAllAttritionWhenForfeitedModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to satisfy all of owner's attrition when forfeited.
     * @param source the card that is the source of the modifier that satisfies all attrition when forfeited
     */
    public SatisfiesAllAttritionWhenForfeitedModifier(PhysicalCard source) {
        super(source, "Satisfies all attrition when forfeited", source, ModifierType.SATISFIES_ALL_ATTRITION_WHEN_FORFEITED);
    }

    /**
     * Creates a modifier that causes the affected cards to satisfy all of owner's attrition when forfeited.
     * @param source the card that is the source of the modifier that satisfies all attrition when forfeited
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public SatisfiesAllAttritionWhenForfeitedModifier(PhysicalCard source, Condition condition) {
        super(source, "Satisfies all attrition when forfeited", source, condition, ModifierType.SATISFIES_ALL_ATTRITION_WHEN_FORFEITED);
    }
}
