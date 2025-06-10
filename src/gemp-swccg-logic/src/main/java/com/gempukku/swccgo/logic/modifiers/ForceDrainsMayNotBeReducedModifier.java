package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents a specified player from reducing a specified player's Force drains at specified locations.
 */
public class ForceDrainsMayNotBeReducedModifier extends AbstractModifier {
    private String _playerReducing;
    private String _playerDraining;

    /**
     * Creates a modifier that prevents either player from reducing the specified player's Force drains.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Condition condition, String playerDraining) {
        this(source, Filters.any, condition, null, playerDraining);
    }

    /**
     * Creates a modifier that prevents either player from reducing the specified player's Force drains.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerReducing the player to reduce the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Condition condition, String playerReducing, String playerDraining) {
        this(source, Filters.any, condition, playerReducing, playerDraining);
    }

    /**
     * Creates a modifier that prevents either player from reducing the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, String playerDraining) {
        this(source, locationFilter, null, null, playerDraining);
    }

    /**
     * Creates a modifier that prevents the specified player from reducing the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerReducing the player to reduce the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, String playerReducing, String playerDraining) {
        this(source, locationFilter, null, playerReducing, playerDraining);
    }

    /**
     * Creates a modifier that prevents either player from reducing the specified player's Force drains.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player from reducing the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerReducing the player to reduce the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerReducing, String playerDraining) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_REDUCE_FORCE_DRAIN_AT_LOCATION, true);
        _playerReducing = playerReducing;
        _playerDraining = playerDraining;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerReducing == null) {
            return (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? "Dark side's " : "Light side's ")) + "Force drains may not be reduced";
        }
        String sideCancelingText = (gameState.getSide(_playerReducing)== Side.DARK) ? "Dark side " : "Light side ";
        String sideDrainingText = (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? " dark side's " : "light side's "));
        return sideCancelingText + "may not reduce " + sideDrainingText + "Force drain";
    }

    @Override
    public boolean cantModifyForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining) {
        return (_playerReducing == null || playerModifying.equals(_playerReducing)) && (_playerDraining == null || playerDraining.equals(_playerDraining));
    }
}
