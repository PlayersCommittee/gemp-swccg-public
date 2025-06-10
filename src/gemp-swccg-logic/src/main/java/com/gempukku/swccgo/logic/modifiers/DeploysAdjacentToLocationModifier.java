package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that requires a location to deploy adjacent to a specific location
 */
public class DeploysAdjacentToLocationModifier extends AbstractModifier {
    private Filter _adjacentToFilter;
    private boolean _onlyIfPossible;

    /**
     * Creates a modifier that requires a location to deploy adjacent to a specific location
     * @param source the card that is the source of the modifier
     * @param affectFilter
     * @param adjacentToFilter filter that the location must be adjacent to when deployed
     */
    public DeploysAdjacentToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable adjacentToFilter, boolean onlyIfPossible) {
        super(source, "Must deploy adjacent to specific location" + (onlyIfPossible?" (if possible)":""), affectFilter, null, ModifierType.DEPLOYS_ADJACENT_TO_SPECIFIC_LOCATION);
        _adjacentToFilter = Filters.and(adjacentToFilter);
        _onlyIfPossible = onlyIfPossible;
    }

    /**
     * Determines if site may deploy adjacent to the specified location
     * @return true or false
     */
    public boolean mayDeployAdjacentTo(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _adjacentToFilter.accepts(gameState.getGame(), location);
    }

    public Filter getAdjacentToFilter() {
        return _adjacentToFilter;
    }

    public boolean onlyIfPossible() {
        return _onlyIfPossible;
    }
}
