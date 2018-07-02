package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to not be considered unique.
 */
public class NotUniqueModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to not be considered unique.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     */
    public NotUniqueModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Not unique", affectFilter, ModifierType.NOT_UNIQUE);
    }

    /**
     * Creates a modifier that causes the affected cards to not be considered unique.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public NotUniqueModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Not unique", affectFilter, condition, ModifierType.NOT_UNIQUE);
    }
}
