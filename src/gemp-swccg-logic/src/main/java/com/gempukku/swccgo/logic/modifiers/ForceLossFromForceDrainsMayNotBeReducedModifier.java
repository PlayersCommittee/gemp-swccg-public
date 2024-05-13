package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

public class ForceLossFromForceDrainsMayNotBeReducedModifier extends AbstractModifier {

    private String _playerReducing;
    private String _playerDraining;


    /**
     * Creates a modifier that prevents the specified player from reducing the specified player's Force drains at locations
     * accepted by the location filter.
     *
     * @param source         the source of the modifier
     * @param locationFilter the location filter
     * @param playerReducing the player to reduce the Force drain
     * @param playerDraining the player Force draining, or null for either player
     */
    public ForceLossFromForceDrainsMayNotBeReducedModifier(PhysicalCard source, Filterable locationFilter, String playerReducing, String playerDraining) {
        super(source, null, Filters.and(Filters.location, locationFilter), ModifierType.MAY_NOT_REDUCE_FORCE_LOSS_FROM_FORCE_DRAIN_AT_LOCATION, true);
        _playerReducing = playerReducing;
        _playerDraining = playerDraining;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerReducing == null) {
            return (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining) == Side.DARK) ? "Dark side's " : "Light side's ")) + "Force Loss from Force drains may not be reduced";
        }
        String sideCancelingText = (gameState.getSide(_playerReducing) == Side.DARK) ? "Dark side " : "Light side ";
        String sideDrainingText = (_playerDraining == null ? "" : ((gameState.getSide(_playerDraining) == Side.DARK) ? " dark side's " : "light side's "));
        return sideCancelingText + "may not reduce " + sideDrainingText + " Force Loss from Force drain";
    }

    @Override
    public boolean cantModifyForceLossFromForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining) {
        return (_playerReducing == null || playerModifying.equals(_playerReducing)) && (_playerDraining == null || playerDraining.equals(_playerDraining));
    }
}