package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets landspeed to an unmodifiable value.
 */
public class ResetLandspeedModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets landspeed to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose landspeed is reset
     * @param resetValue the reset value
     */
    public ResetLandspeedModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * Creates a modifier that resets landspeed to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose landspeed is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetLandspeedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_LANDSPEED, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Landspeed = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
