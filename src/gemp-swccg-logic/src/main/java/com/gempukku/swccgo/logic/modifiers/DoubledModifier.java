package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to be "doubled".
 */
public class DoubledModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to be "doubled".
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     */
    public DoubledModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Doubled", affectFilter, ModifierType.IS_DOUBLED);
    }

    /**
     * Creates a modifier that causes the affected cards to be "doubled".
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public DoubledModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Doubled", affectFilter, condition, ModifierType.IS_DOUBLED);
    }
}
