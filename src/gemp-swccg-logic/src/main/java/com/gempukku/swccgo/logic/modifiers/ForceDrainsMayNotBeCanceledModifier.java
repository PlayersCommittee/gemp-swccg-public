package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents a specified player from canceling a specified player's Force drains at specified locations.
 */
public class ForceDrainsMayNotBeCanceledModifier extends AbstractModifier {
    private String _playerCanceling;
    private String _playerDraining;

    /**
     * Creates a modifier that prevents either player from canceling the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeCanceledModifier(PhysicalCard source, Filterable locationFilter, String playerDraining) {
        this(source, locationFilter, null, null, playerDraining);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerCanceling the player to cancel the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeCanceledModifier(PhysicalCard source, Filterable locationFilter, String playerCanceling, String playerDraining) {
        this(source, locationFilter, null, playerCanceling, playerDraining);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling the specified player's Force drains at locations
     * accepted by the location filter when a given condition is met.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ForceDrainsMayNotBeCanceledModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        this(source, locationFilter, condition, null, null);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling the specified player's Force drains at locations
     * accepted by the location filter when a given condition is met.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeCanceledModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerDraining) {
        this(source, locationFilter, condition, null, playerDraining);
    }

    /**
     * Creates a modifier that prevents the specified player from canceling the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerCanceling the player to cancel the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeCanceledModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerCanceling, String playerDraining) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_CANCEL_FORCE_DRAIN_AT_LOCATION, true);
        _playerCanceling = playerCanceling;
        _playerDraining = playerDraining;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerCanceling == null) {
            return (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? "Dark side's " : "Light side's ")) + "Force drains may not be canceled";
        }
        String sideCancelingText = (gameState.getSide(_playerCanceling)== Side.DARK) ? "Dark side " : "Light side ";
        String sideDrainingText = (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? " dark side's " : "light side's "));
        return sideCancelingText + "may not cancel " + sideDrainingText + "Force drain";
    }

    @Override
    public boolean cantCancelForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerCanceling, String playerDraining) {
        return (_playerCanceling == null || playerCanceling.equals(_playerCanceling)) && (_playerDraining == null || playerDraining.equals(_playerDraining));
    }
}
