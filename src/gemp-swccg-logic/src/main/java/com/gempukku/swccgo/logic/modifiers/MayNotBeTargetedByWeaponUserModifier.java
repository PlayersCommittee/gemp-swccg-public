package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not being able to be targeted by weapons used by specified cards.
 */
public class MayNotBeTargetedByWeaponUserModifier extends AbstractModifier {
    private Filter _weaponUserFilter;

    /**
     * Creates a modifier for not being able to be targeted by weapons used by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param weaponUserFilter the filter for weapon user
     */
    public MayNotBeTargetedByWeaponUserModifier(PhysicalCard source, Filterable affectFilter, Filterable weaponUserFilter) {
        this(source, affectFilter, null, weaponUserFilter);
    }

    /**
     * Creates a modifier for not being able to be targeted by weapons used by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponUserFilter the filter for weapon user
     */
    private MayNotBeTargetedByWeaponUserModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable weaponUserFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_NOT_BE_TARGETED_BY_WEAPON_USER, true);
        _weaponUserFilter = Filters.and(weaponUserFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be targeted by weapon used by certain cards";
    }

    @Override
    public boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon) {
        return (targetedBy != null && Filters.and(_weaponUserFilter).accepts(gameState, modifiersQuerying, targetedBy));
    }
}
