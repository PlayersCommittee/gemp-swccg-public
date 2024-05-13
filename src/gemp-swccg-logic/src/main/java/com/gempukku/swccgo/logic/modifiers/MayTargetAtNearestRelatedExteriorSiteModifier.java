package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayTargetAtNearestRelatedExteriorSiteModifier extends AbstractModifier {
    private Filter _weaponFilter;

    public MayTargetAtNearestRelatedExteriorSiteModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, null);
    }

    public MayTargetAtNearestRelatedExteriorSiteModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "May target at nearest related exterior site", null, condition, ModifierType.MAY_TARGET_AT_NEAREST_RELATED_EXTERIOR_SITE);
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
