package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be 'communing'
 */
public class CommuningModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to be 'communing'
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public CommuningModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Is 'communing'", affectFilter, ModifierType.COMMUNING);
    }

    /**
     * Creates a modifier that causes affected cards to be 'communing'
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CommuningModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Is 'communing'", affectFilter, condition, ModifierType.COMMUNING);
    }
}
