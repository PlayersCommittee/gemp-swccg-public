package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows cards to be targeted by weapons like a starfighter.
 */
public class MayBeTargetedByWeaponsAsIfPresentModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the source card to be targeted by weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeTargetedByWeaponsAsIfPresentModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May be targeted by weapons as if present", affectFilter, condition, ModifierType.MAY_BE_TARGETED_BY_WEAPONS_AS_IF_PRESENT);
    }
}
