package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when all characters for the specified player on the specified system are accepted by
 * the specified filter.
 */
public class AllCharactersOnSystemCondition implements Condition {
    private int _permSourceCardId;
    private String _playerId;
    private String _system;
    private Filter _filters;

    /**
     * A condition that is fulfilled when all characters for the specified player on the specified system are accepted
     * by the specified filter.
     * @param source the card that is checking this condition
     * @param playerId the player
     * @param system the system name
     * @param filters the filter
     */
    public AllCharactersOnSystemCondition(PhysicalCard source, String playerId, String system, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _playerId = playerId;
        _system = system;
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), source, Filters.and(Filters.owner(_playerId), Filters.character, Filters.on(_system)));
        if (characters.isEmpty())
            return false;

        for (PhysicalCard character : characters) {
            if (!Filters.and(_filters).accepts(gameState, modifiersQuerying, character))
                return false;
        }

        return true;
    }
}

