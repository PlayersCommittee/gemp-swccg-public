package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class FireWeaponFiredByForFreeModifier extends AbstractModifier {
    private Filter _weaponFilter;

    public FireWeaponFiredByForFreeModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, source, null, weaponFilter);
    }

    public FireWeaponFiredByForFreeModifier(PhysicalCard source, Condition condition, Filterable weaponFilter) {
        this(source, source, condition, weaponFilter);
    }

    public FireWeaponFiredByForFreeModifier(PhysicalCard source, Filterable affectFilter, Filterable weaponFilter) {
        this(source, affectFilter, null, weaponFilter);
    }

    public FireWeaponFiredByForFreeModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable weaponFilter) {
        super(source, null, affectFilter, condition, ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE, true);
        _weaponFilter = Filters.and(weaponFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Fires specified weapons for free";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, physicalCard);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint permanentWeapon) {
        return Filters.and(_weaponFilter).accepts(gameState, modifiersQuerying, permanentWeapon);
    }
}
