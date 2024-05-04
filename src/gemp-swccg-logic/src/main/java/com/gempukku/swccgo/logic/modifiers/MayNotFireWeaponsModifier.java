package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prohibits affected cards from firing any weapons.
 */
public class MayNotFireWeaponsModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits affected cards from firing any weapons.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to fire weapons
     */
    public MayNotFireWeaponsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents the source card from being deployed.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to fire weapons
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotFireWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not fire weapons", affectFilter, condition, ModifierType.MAY_NOT_FIRE_WEAPONS);
    }
}
