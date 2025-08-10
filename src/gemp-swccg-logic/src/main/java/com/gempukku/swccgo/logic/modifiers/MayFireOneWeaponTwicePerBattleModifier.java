package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows affected card to fire one weapon twice per battle.
 */
public class MayFireOneWeaponTwicePerBattleModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates a modifier that allows source card to fire one weapon twice per battle.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayFireOneWeaponTwicePerBattleModifier(PhysicalCard source, Condition condition, Filterable weaponFilter) {
        super(source, null, source, condition, ModifierType.MAY_FIRE_A_WEAPON_TWICE_PER_BATTLE, true);
        _weaponFilter = Filters.and(weaponFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, target);
    }
}
