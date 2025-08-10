package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes battles to be initiated for free.
 */
public class InitiateBattlesForFreeModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the specified player to initiate battles for free.
     * @param source the source of the modifier
     * @param playerId the player
     */
    public InitiateBattlesForFreeModifier(PhysicalCard source, String playerId) {
        this(source, Filters.any, null, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to initiate battles for free.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public InitiateBattlesForFreeModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes the specified player to initiate battles for free.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public InitiateBattlesForFreeModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.INITIATE_BATTLE_FOR_FREE, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side initiates battle for free";
        else
            return "Light side initiates battle for free";
    }
}
