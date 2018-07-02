package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified weapons from being fired.
 */
public class MayNotBeFiredModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates a modifier that prevents weapons accepted by the filter from being fired.
     * @param source the source of the modifier
     * @param weaponFilter the filter
     */
    public MayNotBeFiredModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, null);
    }

    /**
     * Creates a modifier that prevents weapons accepted by the filter from being fired.
     * @param source the source of the modifier
     * @param weaponFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeFiredModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "May not be fired", null, condition, ModifierType.MAY_NOT_BE_FIRED, true);
        _weaponFilter = Filters.and(weaponFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint targetPermanentWeapon) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, targetPermanentWeapon);
    }
}
