package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows characters aboard the vehicle to "jump off" (disembark) when vehicle about to be lost.
 */
public class CharactersAboardMayJumpOffModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the source card to initiate a Force drain.
     * @param source the card that is the source of the modifier and that is allowed to initiate a Force drain
     */
    public CharactersAboardMayJumpOffModifier(PhysicalCard source) {
        super(source, "Characters aboard may 'jump off'", source, null, ModifierType.CHARACTERS_ABOARD_MAY_JUMP_OFF);
    }
}
