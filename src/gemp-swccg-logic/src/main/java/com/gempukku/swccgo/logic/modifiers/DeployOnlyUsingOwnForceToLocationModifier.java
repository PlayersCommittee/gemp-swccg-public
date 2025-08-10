package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to deploy using only own Force to specific locations.
 */
public class DeployOnlyUsingOwnForceToLocationModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that causes affected cards to deploy using only own Force to locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param locationFilter the location filter
     */
    public DeployOnlyUsingOwnForceToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), null, ModifierType.DEPLOY_ONLY_USING_OWN_FORCE_TO_TARGET, true);
        _targetFilter = Filters.locationAndCardsAtLocation(Filters.and(locationFilter));
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploys only using own Force to specific locations";
    }
}
