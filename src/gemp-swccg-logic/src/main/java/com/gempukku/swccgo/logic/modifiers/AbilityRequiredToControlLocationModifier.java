package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that changes the amount of ability a player requires to control specified locations.
 */
public class AbilityRequiredToControlLocationModifier extends AbstractModifier {
    private float _value;

    /**
     * Creates a modifier that changes the amount of ability a player requires to control locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of ability required
     * @param playerId the player
     */
    public AbilityRequiredToControlLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        super(source, null, locationFilter, condition, ModifierType.ABILITY_REQUIRED_TO_CONTROL_LOCATION, true);
        _value = modifierAmount;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark side requires total ability of " + GuiUtils.formatAsString(_value) + " or more to control location";
        else
            return "Light side requires total ability of " + GuiUtils.formatAsString(_value) + " or more to control location";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _value;
    }
}
