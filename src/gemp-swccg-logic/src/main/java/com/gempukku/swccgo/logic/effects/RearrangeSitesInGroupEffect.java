package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Applies a left-to-right reorder of top locations that already sit in one
 * LocationGroup. Converted stacks stay together. Cards at those sites are not
 * moved. An empty order is a no-op. This does not add client UI.
 */
public class RearrangeSitesInGroupEffect extends AbstractSuccessfulEffect {
    private String _systemName;
    private Filter _filter;
    private List<PhysicalCard> _newOrder;

    /**
     * Reorders the LocationGroup that already contains the given top locations.
     * @param action the action performing this effect
     * @param newOrder requested left-to-right order of top location cards
     */
    public RearrangeSitesInGroupEffect(Action action, List<? extends PhysicalCard> newOrder) {
        this(action, null, null, newOrder);
    }

    /**
     * Reorders sites accepted by filter that already sit in the same LocationGroup.
     * @param action the action performing this effect
     * @param filter location filter for the row, or null
     * @param newOrder requested left-to-right order of matching top locations
     */
    public RearrangeSitesInGroupEffect(Action action, Filter filter, List<? extends PhysicalCard> newOrder) {
        this(action, null, filter, newOrder);
    }

    /**
     * Reorders sites in the LocationGroup for systemName whose tops match siteFilter.
     * Death Star and Bespin share this path; the system is a parameter.
     * @param action the action performing this effect
     * @param systemName the system title
     * @param siteFilter filter for the row, typically interior-only sites of that system
     * @param newOrder requested left-to-right order of matching top locations
     */
    public RearrangeSitesInGroupEffect(Action action, String systemName, Filter siteFilter, List<? extends PhysicalCard> newOrder) {
        super(action);
        _systemName = systemName;
        _filter = siteFilter;
        if (newOrder == null) {
            _newOrder = Collections.emptyList();
        }
        else {
            _newOrder = new ArrayList<PhysicalCard>(newOrder);
        }
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_newOrder.isEmpty()) {
            return;
        }
        boolean done;
        if (_systemName != null) {
            done = game.getGameState().reorderTopLocationsInGroup(_systemName, _filter, _newOrder);
        }
        else {
            done = game.getGameState().reorderTopLocationsInGroup(_filter, _newOrder);
        }
        if (done && _action.getPerformingPlayer() != null) {
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " rearranges sites");
        }
    }
}
