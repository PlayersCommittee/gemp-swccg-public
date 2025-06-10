package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows a specified artillery weapon to fire without a warrior present.
 */
public class MayFireArtilleryWeaponWithoutWarriorPresentModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows artillery weapons accepted by the filter to fire without a warrior present.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayFireArtilleryWeaponWithoutWarriorPresentModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, null, Filters.and(Filters.artillery_weapon, affectFilter), null, ModifierType.MAY_FIRE_ARTILLERY_WEAPON_WITHOUT_WARRIOR_PRESENT, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May fire without warrior present";
    }
}
