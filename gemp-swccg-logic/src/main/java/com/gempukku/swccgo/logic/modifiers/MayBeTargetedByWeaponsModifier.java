package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that allows cards to be targeted by weapons
 */
public class MayBeTargetedByWeaponsModifier extends AbstractModifier {
    private Filter _weaponFilter;
    private Filter _mayBeTargetedFilter;

    /**
     * Creates a modifier that allows the source card to be targeted by weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     */
    public MayBeTargetedByWeaponsModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, source);
    }

    /**
     * Creates a modifier that allows cards accepted by the affect Filter to be targeted by weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     */
    public MayBeTargetedByWeaponsModifier(PhysicalCard source, Filterable weaponFilter, Filterable affectFilter) {
        super(source, null, affectFilter, null, ModifierType.MAY_BE_TARGETED_BY_WEAPONS);
        _weaponFilter = Filters.and(Filters.weapon, weaponFilter);
        _mayBeTargetedFilter = Filters.and(affectFilter);
    }

    @Override
    public boolean grantedToBeTargetedByCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardTargeting) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, cardTargeting);
    }

    public Filter getMayBeTargetedFilter() {
        return _mayBeTargetedFilter;
    }
}
