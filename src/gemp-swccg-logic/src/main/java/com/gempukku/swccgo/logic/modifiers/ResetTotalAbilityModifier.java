package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets total ability at a location to an unmodifiable value.
 */
public class ResetTotalAbilityModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets total ability at a location to an unmodifiable value.
     * @param source the source of the reset
     * @param locationFilter the filter for locations where the total ability is reset
     * @param resetValue the reset value
     * @param playerId the player whose total ability is reset
     */
    public ResetTotalAbilityModifier(PhysicalCard source, Filterable locationFilter, float resetValue, String playerId) {
        this(source, locationFilter, null, resetValue, playerId);
    }

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param locationFilter the filter for locations where the total ability is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param playerId the player whose total ability is reset
     */
    public ResetTotalAbilityModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float resetValue, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.UNMODIFIABLE_TOTAL_ABILITY_AT_LOCATION, true);
        _resetValue = resetValue;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        String sideText = (gameState.getSide(_playerId)== Side.DARK) ? "Dark side" : "Light side";
        return sideText + " total ability = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _resetValue;
    }
}
