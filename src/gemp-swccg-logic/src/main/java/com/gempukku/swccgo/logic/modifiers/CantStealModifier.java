package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to not be allowed to steal cards.
 */
public class CantStealModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to not be allowed to steal cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public CantStealModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Can't 'steal'", affectFilter, ModifierType.CANT_STEAL);
    }

    /**
     * Creates a modifier that causes affected cards to not be allowed to steal cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CantStealModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Can't 'steal'", affectFilter, condition, ModifierType.CANT_STEAL);
    }
}
