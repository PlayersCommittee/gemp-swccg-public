package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected Artillery Weapons to not require a power source.
 */
public class DoesNotRequirePowerSourceModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the source Artillery Weapon to not require a power source.
     * @param source the source of the modifier
     */
    public DoesNotRequirePowerSourceModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that causes Artillery Weapons accepted by the filter to not require a power source.
     * @param source the source of the modifier
     * @param affectFilter the affected cards filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private DoesNotRequirePowerSourceModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Does not require power source", affectFilter, condition, ModifierType.DOES_NOT_REQUIRE_POWER_SOURCE);
    }
}
