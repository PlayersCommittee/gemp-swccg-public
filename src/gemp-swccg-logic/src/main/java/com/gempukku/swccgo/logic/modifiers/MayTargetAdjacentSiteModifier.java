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
 * An modifier that allowed specified cards to target cards at adjacent site.
 */
public class MayTargetAdjacentSiteModifier extends AbstractModifier {
    private Filter _weaponFilter;

    /**
     * Creates a modifier that allows the source card to target cards at adjacent sites.
     * @param source the source of the modifier and card affected by the modifier
     */
    public MayTargetAdjacentSiteModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows cards accepted by filter to target cards at adjacent sites.
     * @param source the source of the modifier
     * @param weaponFilter the filter
     */
    public MayTargetAdjacentSiteModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by filter to target cards at adjacent sites.
     * @param source the source of the modifier
     * @param weaponFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayTargetAdjacentSiteModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "May target at adjacent site", null, condition, ModifierType.TARGET_ADJACENT_SITE, true);
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
