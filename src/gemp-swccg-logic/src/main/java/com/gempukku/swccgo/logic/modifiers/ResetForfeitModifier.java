package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets forfeit to an unmodifiable value.
 */
public class ResetForfeitModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param resetValue the reset value
     */
    public ResetForfeitModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * Creates a modifier that resets forfeit to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose forfeit value is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetForfeitModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_FORFEIT_VALUE, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Forfeit = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getUnmodifiableForfeit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
