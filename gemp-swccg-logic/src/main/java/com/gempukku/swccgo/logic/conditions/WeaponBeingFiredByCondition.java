package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when a weapon is being fired by a specified card.
 */
public class WeaponBeingFiredByCondition implements Condition {
    private Filter _weaponFilter;
    private Filter _weaponUserFilter;

    /**
     * Creates a condition that is fulfilled when the specified weapon is being fired by a card accepted by the specified
     * filter.
     * @param weapon the weapon filter
     * @param weaponUserFilter the weapon user filter
     */
    public WeaponBeingFiredByCondition(Filterable weapon, Filterable weaponUserFilter) {
        _weaponFilter = Filters.and(weapon);
        _weaponUserFilter = Filters.and(weaponUserFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            PhysicalCard weapon = weaponFiringState.getCardFiring();
            SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
            PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

            return ((weapon != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, weapon))
                    || permanentWeapon != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon))
                    && (cardFiringWeapon != null && Filters.and(_weaponUserFilter).accepts(gameState, modifiersQuerying, cardFiringWeapon));
        }
        return false;
    }
}
