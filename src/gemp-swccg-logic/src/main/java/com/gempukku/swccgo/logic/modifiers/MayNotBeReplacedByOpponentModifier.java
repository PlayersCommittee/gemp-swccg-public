package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents a character to be converted (replaced) by opponent.
 */
public class MayNotBeReplacedByOpponentModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents a character to be converted (replaced) by opponent.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeReplacedByOpponentModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be replaced by opponent", affectFilter, condition, ModifierType.MAY_NOT_BE_REPLACED_BY_OPPONENT);
    }
}
