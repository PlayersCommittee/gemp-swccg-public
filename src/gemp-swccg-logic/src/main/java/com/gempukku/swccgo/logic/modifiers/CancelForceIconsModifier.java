package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that cancels Force icons for a player at specified locations.
 */
public class CancelForceIconsModifier extends AbstractModifier {

    /**
     * Creates a modifier that cancels Force icons for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public CancelForceIconsModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that cancels Force icons for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public CancelForceIconsModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.CANCEL_FORCE_ICONS);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _playerId.equals(gameState.getDarkPlayer()) ? "Dark Side Force icons canceled" : "Light Side Force icons canceled";
    }
}
