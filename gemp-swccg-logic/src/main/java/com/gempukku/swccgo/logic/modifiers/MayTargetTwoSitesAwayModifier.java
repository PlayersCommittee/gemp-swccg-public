package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayTargetTwoSitesAwayModifier extends AbstractModifier {
    private Filter _weaponFilter;

    public MayTargetTwoSitesAwayModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, null, weaponFilter);
    }

    public MayTargetTwoSitesAwayModifier(PhysicalCard source, Condition condition, Filterable weaponFilter) {
        super(source, "May target two sites away", null, condition, ModifierType.TARGET_TWO_SITE_AWAY);
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
