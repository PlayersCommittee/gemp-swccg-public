package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets maneuver to an unmodifiable value.
 */
public class ResetManeuverModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets maneuver to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose maneuver is reset
     * @param resetValue the reset value
     */
    public ResetManeuverModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * Creates a modifier that resets maneuver of the source card to an unmodifiable value.
     * @param source the source of the reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetManeuverModifier(PhysicalCard source, Condition condition, float resetValue) {
        this(source, source, condition, resetValue);
    }

    /**
     * Creates a modifier that resets maneuver to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose maneuver is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetManeuverModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_MANEUVER, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Maneuver = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
