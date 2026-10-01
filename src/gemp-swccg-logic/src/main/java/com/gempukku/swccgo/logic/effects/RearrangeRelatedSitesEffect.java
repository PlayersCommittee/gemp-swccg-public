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
 * system), not a Death Star-only rule. An empty order is a no-op. For the click-sites-in-order UI,
 * use ChooseAndRearrangeRelatedSitesEffect.
 */
public class RearrangeRelatedSitesEffect extends AbstractSuccessfulEffect {
    private String _systemName;
    private Filter _filter;
    private List<PhysicalCard> _newOrder;
    private List<Integer> _permutation;

    /**
     * Rearranges sites accepted by filter into the given left-to-right order.
     * @param action the action performing this effect
     * @param filter location filter for the row, typically interior sites of a system
     * @param newOrder requested left-to-right order of matching top locations
     */
    public RearrangeRelatedSitesEffect(Action action, Filter filter, List<? extends PhysicalCard> newOrder) {
        super(action);
        _filter = filter;
        _permutation = Collections.emptyList();
        if (newOrder == null) {
            _newOrder = Collections.emptyList();
        }
        else {
            _newOrder = new ArrayList<PhysicalCard>(newOrder);
        }
    }

    /**
     * Rearranges the matching system's site row using a permutation of current
     * stack indexes. An empty permutation is a no-op.
     * @param action the action performing this effect
     * @param systemName the system title, for example Title.Death_Star
     * @param filter location filter for the row
     * @param permutation new left-to-right stack indexes
     */
    public RearrangeRelatedSitesEffect(Action action, String systemName, Filter filter, List<Integer> permutation) {
        super(action);
        _systemName = systemName;
        _filter = filter;
        _newOrder = Collections.emptyList();
        if (permutation == null) {
            _permutation = Collections.emptyList();
        }
        else {
            _permutation = new ArrayList<Integer>(permutation);
        }
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        boolean done;
        if (!_permutation.isEmpty()) {
            done = game.getGameState().reorderTopLocationsInGroupByPermutation(_systemName, _filter, _permutation);
        }
        else if (_newOrder.isEmpty()) {
            return;
        }
        else {
            done = game.getGameState().reorderTopLocationsInGroup(_filter, _newOrder);
        }
        if (done && _action.getPerformingPlayer() != null) {
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " rearranges sites");
        }
    }
}
