package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that allows cards to be targeted by weapons like a starfighter.
 */
public class MayBeTargetedByWeaponsModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates a modifier that allows the source card to be targeted by weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     */
    public MayBeTargetedByWeaponsModifier(PhysicalCard source, Filterable weaponFilter) {
        super(source, null, source, null, ModifierType.MAY_BE_TARGETED_BY_WEAPONS);
        _weaponFilter = Filters.and(Filters.weapon, weaponFilter);
    }

    @Override
    public boolean grantedToBeTargetedByCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardTargeting) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, cardTargeting);
    }
}
