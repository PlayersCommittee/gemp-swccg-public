package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allowed affected cards to fire any number of weapons.
 */
public class MayFireAnyNumberOfWeaponsModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to fire any number of weapons.
     * @param source the card that is the source card and that is affected by this modifier.
     */
    public MayFireAnyNumberOfWeaponsModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows source card to fire any number of weapons.
     * @param source the card that is the source card and that is affected by this modifier.
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayFireAnyNumberOfWeaponsModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to fire any number of weapons.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayFireAnyNumberOfWeaponsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to fire any number of weapons.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayFireAnyNumberOfWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May fire any number of weapons", affectFilter, condition, ModifierType.MAY_FIRE_ANY_NUMBER_OF_WEAPONS, true);
    }
}
