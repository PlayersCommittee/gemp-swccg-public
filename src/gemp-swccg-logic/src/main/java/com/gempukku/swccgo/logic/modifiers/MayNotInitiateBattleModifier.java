package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents battle from being initiated.
 */
public class MayNotInitiateBattleModifier extends MayNotInitiateBattleAtLocationModifier {

    /**
     * Creates a modifier that prevents the specified player from initiated battle.
     * @param source the source of the modifier
     * @param playerId the player that may not initiate battle
     */
    public MayNotInitiateBattleModifier(PhysicalCard source, String playerId) {
        this(source, null, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from initiated battle.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not initiate battle
     */
    public MayNotInitiateBattleModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, Filters.any, condition, playerId);
        _playerId = playerId;
    }
}
