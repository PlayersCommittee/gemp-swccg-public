package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents a specified player from modifying a specified player's Force drains at specified locations.
 */
public class ForceDrainsMayNotBeModifiedModifier extends AbstractModifier {
    private String _playerModifying;
    private String _playerDraining;

    /**
     * Creates a modifier that prevents the specified player from modifying the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param playerModifying the player to modify the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeModifiedModifier(PhysicalCard source, Filterable locationFilter, String playerModifying, String playerDraining) {
        this(source, locationFilter, null, playerModifying, playerDraining);
    }

    /**
     * Creates a modifier that prevents the specified player from modifying the specified player's Force drains at locations
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerModifying the player to modify the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceDrainsMayNotBeModifiedModifier(PhysicalCard source, Filterable locationFilter, Condition condition, String playerModifying, String playerDraining) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_MODIFY_FORCE_DRAIN_AT_LOCATION, true);
        _playerModifying = playerModifying;
        _playerDraining = playerDraining;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerModifying == null) {
            return (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? "Dark side's " : "Light side's ")) + "Force drains may not be modified";
        }
        String sideCancelingText = (gameState.getSide(_playerModifying)== Side.DARK) ? "Dark side " : "Light side ";
        String sideDrainingText = (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining)== Side.DARK) ? " dark side's " : "light side's "));
        return sideCancelingText + "may not modify " + sideDrainingText + "Force drain";
    }

    @Override
    public boolean cantModifyForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining) {
        return (_playerModifying == null || playerModifying.equals(_playerModifying)) && (_playerDraining == null || playerDraining.equals(_playerDraining));
    }
}
