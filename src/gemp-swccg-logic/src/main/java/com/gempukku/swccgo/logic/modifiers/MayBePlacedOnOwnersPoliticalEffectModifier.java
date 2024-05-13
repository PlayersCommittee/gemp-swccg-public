package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier which allows affected cards to be placed on owner's Political Effect (even if not a senator).
 */
public class MayBePlacedOnOwnersPoliticalEffectModifier extends AbstractModifier {

    /**
     * Creates a modifier which allows affected cards to be placed on owner's Political Effect (even if not a senator).
     * @param source the card that is the source of the modifier and allowed to be placed on owner's Political Effect
     */
    public MayBePlacedOnOwnersPoliticalEffectModifier(PhysicalCard source) {
        super(source, "May be placed on owner's Political Effect", source, ModifierType.MAY_BE_PLACED_ON_OWNERS_POLITICAL_EFFECT);
    }
}
