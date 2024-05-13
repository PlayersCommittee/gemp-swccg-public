package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified weapons or devices from being used.
 */
public class MayNotBeUsedModifier extends AbstractModifier {
    private Filter _weaponOrDeviceFilter;

    /**
     * Creates a modifier that prevents weapons or devices accepted by the filter from being used.
     * @param source the source of the modifier
     * @param weaponOrDeviceFilter the filter
     */
    public MayNotBeUsedModifier(PhysicalCard source, Filterable weaponOrDeviceFilter) {
        this(source, weaponOrDeviceFilter, null);
    }

    /**
     * Creates a modifier that prevents weapons or devices accepted by the filter from being fired.
     * @param source the source of the modifier
     * @param weaponOrDeviceFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeUsedModifier(PhysicalCard source, Filterable weaponOrDeviceFilter, Condition condition) {
        super(source, "May not be used", null, condition, ModifierType.MAY_NOT_BE_USED);
        _weaponOrDeviceFilter = Filters.and(weaponOrDeviceFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_weaponOrDeviceFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint targetPermanentWeapon) {
        return Filters.and(_weaponOrDeviceFilter).accepts(gameState, modifiersQuerying, targetPermanentWeapon);
    }
}
