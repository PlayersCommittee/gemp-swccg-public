package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows the weapon or device to be used by a landed starship.
 */
public class MayBeUsedByLandedStarshipModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the weapon or device to be used by a landed starship.
     * @param source the source of the modifier
     */
    public MayBeUsedByLandedStarshipModifier(PhysicalCard source) {
        super(source, "May be used by landed starship", source, null, ModifierType.MAY_BE_USED_BY_LANDED_STARSHIP);
    }
}
