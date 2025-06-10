package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents battle from being initiated at specified locations.
 */
public class MayNotInitiateBattleAtLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents battle from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayNotInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null, null);
    }

    /**
     * Creates a modifier that prevents battle from being initiated at the source location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotInitiateBattleAtLocationModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition, null);
    }

    /**
     * Creates a modifier that prevents battle from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null);
    }

    /**
     * Creates a modifier that prevents battle from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player that may not initiate battle
     */
    public MayNotInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents battle from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not initiate battle
     */
    public MayNotInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_INITIATE_BATTLE_AT_LOCATION, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId==null)
            return "Neither player may initiate battle";
        else if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side may not initiate battle";
        else
            return "Light side may not initiate battle";
    }
}
