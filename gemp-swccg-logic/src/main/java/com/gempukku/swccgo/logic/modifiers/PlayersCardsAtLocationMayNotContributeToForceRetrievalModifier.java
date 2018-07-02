package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prohibits affected the specified player's cards at affected locations from contributing to Force retrieval.
 */
public class PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents affected the specified player's cards at affected locations from contributing to Force retrieval.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents affected the specified player's cards at affected locations from contributing to Force retrieval.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    private PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, playerId + "'s cards here may not contribute to Force retrieval", Filters.and(Filters.location, locationFilter), condition, ModifierType.PLAYERS_CARDS_AT_LOCATION_MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL);
        _playerId = playerId;
    }
}
