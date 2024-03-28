package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being targeted to be captured.
 */
public class MayNotTargetToBeCapturedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents affected cards from being targeted to be captured.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotTargetToBeCapturedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents affected cards from being targeted to be captured.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotTargetToBeCapturedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not target to be captured", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED);
    }
}
