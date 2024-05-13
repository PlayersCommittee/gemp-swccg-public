package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

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
}
