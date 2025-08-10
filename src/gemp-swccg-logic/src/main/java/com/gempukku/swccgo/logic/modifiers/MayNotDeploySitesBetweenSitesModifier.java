package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents sites from deploying within the same location group between the specified sites.
 */
public class MayNotDeploySitesBetweenSitesModifier extends AbstractModifier {
    private Filter _site1Filter;
    private Filter _site2Filter;

    /**
     * Creates a modifier that prevents sites from deploying within the same location group between the specified sites.
     * @param source the card that is the source of the modifier
     * @param site1Filter filter for site on side of boundary
     * @param site2Filter filter for site on side of boundary
     */
    public MayNotDeploySitesBetweenSitesModifier(PhysicalCard source, Filterable site1Filter, Filterable site2Filter) {
        super(source, null, null, null, ModifierType.MAY_NOT_DEPLOY_SITES_BETWEEN_SITES);
        _site1Filter = Filters.and(site1Filter);
        _site2Filter = Filters.and(site2Filter);
    }

    /**
     * Determines if sites are prevented from deploying between the specified sites.
     * @param gameState the game state
     * @param site1 a site
     * @param site2 a site
     * @return true or false
     */
    @Override
    public boolean mayNotDeploySiteBetweenSites(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard site1, PhysicalCard site2) {
        return (_site1Filter.accepts(gameState, modifiersQuerying, site1) && _site2Filter.accepts(gameState, modifiersQuerying, site2))
                || (_site1Filter.accepts(gameState, modifiersQuerying, site2) && _site2Filter.accepts(gameState, modifiersQuerying, site1));
    }
}
