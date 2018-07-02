package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows a character to be converted (replaced) by opponent.
 */
public class MayBeReplacedByOpponentModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows a character to be converted (replaced) by opponent.
     * @param source the source of the modifier and the character that may be converted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeReplacedByOpponentModifier(PhysicalCard source, Condition condition) {
        super(source, "May be replaced by opponent", source, condition, ModifierType.MAY_BE_REPLACED_BY_OPPONENT);
    }
}
