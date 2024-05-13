package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to not be allowed to 'cloak'.
 */
public class MayNotCloakModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to not be allowed to 'cloak'.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotCloakModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affected cards to not be allowed to 'cloak'.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotCloakModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not 'cloak'", affectFilter, condition, ModifierType.MAY_NOT_CLOAK);
    }
}
