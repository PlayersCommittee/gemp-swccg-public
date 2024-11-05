package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being targeted to be lost.
 */
public class MayNotTargetToBeLostModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents affected cards from being targeted to be lost.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotTargetToBeLostModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not target to be lost", affectFilter, ModifierType.MAY_NOT_TARGET_TO_BE_LOST);
    }

    /**
     * Creates a modifier that prevents affected cards from being targeted to be lost.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition for the modifier to take effect
     */
    public MayNotTargetToBeLostModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not target to be lost", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_LOST);
    }
}
