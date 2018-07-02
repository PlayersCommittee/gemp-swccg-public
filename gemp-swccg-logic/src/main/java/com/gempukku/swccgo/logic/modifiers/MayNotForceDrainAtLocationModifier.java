package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents players from Force draining at specified locations.
 */
public class MayNotForceDrainAtLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents either player from Force draining at the source location.
     * @param source the source of the modifier
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source) {
        this(source, source, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player from Force draining at the source location.
     * @param source the source of the modifier
     * @param playerId the player
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates a modifier that prevents either player from Force draining at locations accepted by the filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null, null);
    }

    /**
     * Creates a modifier that prevents either player from Force draining at the source location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player from Force draining at locations accepted by the filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerId the player
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Filterable locationFilter, String playerId) {
        this(source, locationFilter, null, playerId);
    }

    /**
     * Creates a modifier that prevents either player from Force draining at locations accepted by the filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null);
    }

    /**
     * Creates a modifier that prevents the specified player from Force draining at the source location.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, source, condition, playerId);
    }

    /**
     * Creates a modifier that prevents the specified player from Force draining at locations accepted by the filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public MayNotForceDrainAtLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_FORCE_DRAIN_AT_LOCATION, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId==null)
            return "Neither player may Force drain";
        else if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side may not Force drain";
        else
            return "Light side may not Force drain";
    }
}
