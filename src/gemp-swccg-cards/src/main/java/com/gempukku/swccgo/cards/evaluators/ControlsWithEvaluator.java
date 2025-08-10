package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * An evaluator that returns the number of locations accepted by the specified location filter that the specified player
 * controls with a card accepted by the specified filter.
 */
public class ControlsWithEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private String _playerId;
    private Filter _locationFilter;
    private Filter _withFilter;

    /**
     * Creates an evaluator that returns the number of locations accepted by the specified location filter that the specified
     * player controls with a card accepted by the specified filter.
     * @param playerId the player
     * @param locationFilter the location filter
     * @param withFilter the filter for cards to control with
     */
    public ControlsWithEvaluator(PhysicalCard source, String playerId, Filterable locationFilter, Filterable withFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
        _locationFilter = Filters.and(locationFilter);
        _withFilter = Filters.and(withFilter);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Filter filterToUse = Filters.or(_withFilter, Filters.hasPermanentAboard(_withFilter), Filters.hasPermanentWeapon(_withFilter));
        return Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(_locationFilter, Filters.controlsWith(_playerId, source, filterToUse)));
    }
}
