package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes affected cards to not be allowed to 'react'.
 */
public class MayNotReactModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to not be allowed to 'react'.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotReactModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not 'react'", affectFilter, null, ModifierType.MAY_NOT_REACT);
    }
}
