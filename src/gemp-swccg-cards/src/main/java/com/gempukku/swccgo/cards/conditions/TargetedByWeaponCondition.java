package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when a specified card is targeted by a specified weapon.
 */
public class TargetedByWeaponCondition implements Condition {
    private Filter _targetFilter;
    private Filter _weaponFilter;

    /**
     * Creates a condition that is fulfilled when a card accepted by the target filter is targeted by a weapon accepted by the weapon filter.
     * @param targetFilter the target filter
     * @param weaponFilter the weapon filter
     */
    public TargetedByWeaponCondition(Filterable targetFilter, Filterable weaponFilter) {
        _targetFilter = Filters.and(targetFilter);
        _weaponFilter = Filters.and(weaponFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState == null)
            return false;

        Collection<PhysicalCard> targets = weaponFiringState.getTargets();
        PhysicalCard weaponCard = weaponFiringState.getCardFiring();
        SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();

        return Filters.canSpot(targets, gameState.getGame(), _targetFilter)
                && ((weaponCard != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, weaponCard))
                || (permanentWeapon != null && Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon)));
    }
}
