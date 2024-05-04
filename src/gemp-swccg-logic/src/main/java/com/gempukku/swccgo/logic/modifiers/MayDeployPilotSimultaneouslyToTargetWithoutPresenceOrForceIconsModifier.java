package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows specified cards to deploy without presence or Force icons to specified targets.
 */
public class MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to targets accepted
     * by the target filter.
     * @param source the card that is the source of the modifier and that is allowed to deploy without presence or Force icons
     * @param targetFilter the target filter
     */
    public MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, null, targetFilter);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to targets accepted
     * by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     * @param targetFilter the target filter
     */
    public MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        this(source, affectFilter, null, targetFilter);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to targets accepted
     * by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    public MayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.MAY_DEPLOY_PILOT_SIMULTANEOUSLY_WITHOUT_PRESENCE_OR_FORCE_ICONS, true);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy simultaneously with a pilot to certain targets without presence or Force icons";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}
