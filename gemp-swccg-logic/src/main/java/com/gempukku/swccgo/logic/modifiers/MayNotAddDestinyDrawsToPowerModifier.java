package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to add destiny draws to power.
 */
public class MayNotAddDestinyDrawsToPowerModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to add destiny draws to power.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotAddDestinyDrawsToPowerModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, null, null, condition, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_POWER, true);
        _playerId = playerId;
    }
}
