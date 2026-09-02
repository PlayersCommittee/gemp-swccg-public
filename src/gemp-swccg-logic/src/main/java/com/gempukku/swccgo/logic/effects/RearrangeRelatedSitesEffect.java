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
 * An effect that rearranges related sites matching a location filter.
 * The filter is the row to rearrange (for example interior sites of a given
 * system), not a Death Star-only rule. An empty order is a no-op. This does
 * not add client UI for clicking sites.
 */
public class RearrangeRelatedSitesEffect extends AbstractSuccessfulEffect {
    private Filter _filter;
    private List<PhysicalCard> _newOrder;

    /**
     * Rearranges sites accepted by filter into the given left-to-right order.
     * @param action the action performing this effect
     * @param filter location filter for the row, typically interior sites of a system
     * @param newOrder requested left-to-right order of matching top locations
     */
    public RearrangeRelatedSitesEffect(Action action, Filter filter, List<? extends PhysicalCard> newOrder) {
        super(action);
        _filter = filter;
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
        boolean done = game.getGameState().reorderTopLocationsInGroup(_filter, _newOrder);
        if (done && _action.getPerformingPlayer() != null) {
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " rearranges sites");
        }
    }
}