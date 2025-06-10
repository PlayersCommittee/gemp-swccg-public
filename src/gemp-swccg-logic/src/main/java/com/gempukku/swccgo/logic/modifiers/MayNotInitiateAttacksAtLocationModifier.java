package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents attacks from being initiated at specified locations.
 */
public class MayNotInitiateAttacksAtLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents attacks from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayNotInitiateAttacksAtLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null, null);
    }

    /**
     * Creates a modifier that prevents attacks from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotInitiateAttacksAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null);
    }

    /**
     * Creates a modifier that prevents attacks from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player that may not initiate attacks
     */
    public MayNotInitiateAttacksAtLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents attacks from being initiated at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not initiate attacks
     */
    public MayNotInitiateAttacksAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_INITIATE_ATTACKS_AT_LOCATION, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId==null)
            return "Neither player may initiate attacks";
        else if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side may not initiate attacks";
        else
            return "Light side may not initiate attacks";
    }
}
