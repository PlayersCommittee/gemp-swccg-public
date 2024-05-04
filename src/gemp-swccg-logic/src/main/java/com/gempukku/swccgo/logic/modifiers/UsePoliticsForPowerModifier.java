package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to use politics instead to determine power.
 */
public class UsePoliticsForPowerModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to use politics instead to determine power.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public UsePoliticsForPowerModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to use politics instead to determine power.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public UsePoliticsForPowerModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Power is equal to politics", affectFilter, condition, ModifierType.USE_POLITICS_FOR_POWER, true);
    }
}
