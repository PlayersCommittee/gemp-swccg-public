package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

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
        super(source, "May not target to be captured", affectFilter, ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED);
    }
}
