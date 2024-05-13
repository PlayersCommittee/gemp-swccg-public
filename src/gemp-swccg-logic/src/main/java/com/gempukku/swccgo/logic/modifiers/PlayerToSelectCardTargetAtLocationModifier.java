package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that specifies which player chooses targets when a specified card is used to choose targets at a specified location.
 */
public class PlayerToSelectCardTargetAtLocationModifier extends AbstractModifier {
    private Filter _locationFilters;

    /**
     * Creates a modifier that specifies which player chooses targets when a specified card is used to choose targets at
     * a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     * @param locationFilter the location filter
     */
    public PlayerToSelectCardTargetAtLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.PLAYER_TO_SELECT_CARD_TARGET_AT_LOCATION, true);
        _playerId = playerId;
        _locationFilters = Filters.and(Filters.location, locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Player selecting card target modified";
    }

    @Override
    public String getPlayerToSelectCardTargetAtLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        if (_locationFilters.accepts(gameState, modifiersQuerying, location))
            return _playerId;
        return null;
    }
}
