package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier to allow card to make a Kessel Run even when not a smuggler.
 */
public class MayMakeKesselRunInsteadOfSmugglerModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows card to make a Kessel Run even when not a smuggler.
     * @param source the card that is the source of the modifier and is allowed to make a Kessel Run
     */
    public MayMakeKesselRunInsteadOfSmugglerModifier(PhysicalCard source) {
        super(source, "May make Kessel Run", source, ModifierType.MAY_MAKE_KESSEL_RUN_WHEN_NOT_SMUGGLER);
    }
}
