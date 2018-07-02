package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents the specified player from performing a 'react' from affected locations.
 */
public class MayNotReactFromLocationModifier extends AbstractModifier {

    /**
     * Creates modifier that prevents the specified player from performing a 'react' from locations accepted by the location filter.
     * @param source the location that is the source of the modifier and is affected by this modifier
     * @param playerId the player
     */
    public MayNotReactFromLocationModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates modifier that prevents the specified player from performing a 'react' from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public MayNotReactFromLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates modifier that prevents the specified player from performing a 'react' from locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotReactFromLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_REACT_FROM_LOCATION, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (gameState.getSide(_playerId)== Side.DARK)
            return "Dark side may not 'react' from location";
        else
            return "Light side may not 'react' from location";
    }
}
