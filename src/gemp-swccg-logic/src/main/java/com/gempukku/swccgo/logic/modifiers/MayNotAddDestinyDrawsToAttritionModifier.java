package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to add destiny draws to attrition.
 */
public class MayNotAddDestinyDrawsToAttritionModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to add destiny draws to attrition.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotAddDestinyDrawsToAttritionModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, null, null, condition, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_ATTRITION, true);
        _playerId = playerId;
    }
}
