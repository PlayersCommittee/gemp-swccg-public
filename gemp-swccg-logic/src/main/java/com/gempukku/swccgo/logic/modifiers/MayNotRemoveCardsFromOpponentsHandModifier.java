package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from removing cards from opponent's hand using a specified card (or no card).
 */
public class MayNotRemoveCardsFromOpponentsHandModifier extends AbstractModifier {
    private Filter _sourceFilter;

    /**
     * Creates a modifier that prevents the specified player from removing cards from opponent's hand using a card accepted
     * by the source filter.
     * @param source the source of the modifier
     * @param playerId the player
     * @param sourceFilter the source filter
     */
    public MayNotRemoveCardsFromOpponentsHandModifier(PhysicalCard source, String playerId, Filterable sourceFilter) {
        this(source, playerId, null, sourceFilter);
    }

    /**
     * Creates a modifier that prevents the specified player from removing cards from opponent's hand using a card accepted
     * by the source filter.
     * @param source the source of the modifier
     * @param playerId the player
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param sourceFilter the source filter
     */
    public MayNotRemoveCardsFromOpponentsHandModifier(PhysicalCard source, String playerId, Condition condition, Filterable sourceFilter) {
        super(source, null, sourceFilter, condition, ModifierType.MAY_NOT_REMOVE_CARDS_FROM_OPPONENTS_HAND);
        _playerId = playerId;
        _sourceFilter = Filters.and(sourceFilter);
    }

    @Override
    public boolean isActionSource(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard actionSource) {
        return actionSource == null || Filters.and(_sourceFilter).accepts(gameState, modifiersQuerying, actionSource);
    }
}
