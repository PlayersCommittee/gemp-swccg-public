package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets politics to an unmodifiable value.
 */
public class ResetPoliticsModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets politics to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose politics is reset
     * @param resetValue the reset value
     */
    public ResetPoliticsModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        super(source, null, affectFilter, null, ModifierType.UNMODIFIABLE_POLITICS, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Politics = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getUnmodifiablePolitics(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
