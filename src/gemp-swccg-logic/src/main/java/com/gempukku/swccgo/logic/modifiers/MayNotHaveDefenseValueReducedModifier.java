package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have defense value reduced.
 */
public class MayNotHaveDefenseValueReducedModifier extends AbstractModifier {

    /**
     * Creates a modifier for the source card not being able to have power reduced.
     * @param source the source of the modifier
     * @param playerId the player not allowed to reduce power of affected cards
     */
    public MayNotHaveDefenseValueReducedModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates a modifier for not being able to have power reduced.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have power reduced
     * @param playerId the player not allowed to reduce power of affected cards
     */
    public MayNotHaveDefenseValueReducedModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier for not being able to have power reduced.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have power reduced
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player not allowed to reduce power of affected cards
     */
    public MayNotHaveDefenseValueReducedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not have defense value reduced", affectFilter, condition, ModifierType.MAY_NOT_HAVE_DEFENSE_VALUE_REDUCED, true);
        _playerId = playerId;
    }
}
