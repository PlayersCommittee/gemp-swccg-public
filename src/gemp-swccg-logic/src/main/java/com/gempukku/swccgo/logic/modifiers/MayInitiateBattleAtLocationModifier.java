package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows battles to be initiated at specified locations.
 */
public class MayInitiateBattleAtLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows battles to be initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null, null);
    }

    /**
     * Creates a modifier that allows battles to be initiated at the source location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayInitiateBattleAtLocationModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition, null);
    }

    /**
     * Creates a modifier that allows battles to be initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null);
    }

    /**
     * Creates a modifier that allows battles to be initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player that may not initiate battle
     */
    public MayInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that allows battles to be initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not initiate battle
     */
    public MayInitiateBattleAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_INITIATE_BATTLE_AT_LOCATION, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId==null)
            return "Either player may initiate battle";
        else if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side may initiate battle";
        else
            return "Light side may initiate battle";
    }
}
