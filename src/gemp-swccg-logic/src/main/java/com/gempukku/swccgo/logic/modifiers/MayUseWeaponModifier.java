package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows a card to use specified weapons.
 */
public class MayUseWeaponModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates modifier that allows the source card to use weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     */
    public MayUseWeaponModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, source, null, weaponFilter);
    }

    /**
     * Creates modifier that allows the cards accepted by the filter to use weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param weaponFilter the weapon filter
     */
    public MayUseWeaponModifier(PhysicalCard source, Filterable affectFilter, Filterable weaponFilter) {
        this(source, affectFilter, null, weaponFilter);
    }

    /**
     * Creates modifier that allows the cards accepted by the filter to use weapons accepted by the weapon filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponFilter the weapon filter
     */
    private MayUseWeaponModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable weaponFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_USE_WEAPON, true);
        _weaponFilter = Filters.and(Filters.weapon, weaponFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May use specified weapons";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, target);
    }
}
