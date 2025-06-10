package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets deploy cost to an unmodifiable value.
 */
public class ResetDeployCostModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value.
     * @param source the card that is the source of the reset and whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    public ResetDeployCostModifier(PhysicalCard source, Condition condition, float resetValue) {
        this(source, source, condition, resetValue);
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param resetValue the reset value
     */
    public ResetDeployCostModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     */
    private ResetDeployCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_DEPLOY_COST, true);
        _resetValue = resetValue;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploy cost = " + GuiUtils.formatAsString(_resetValue);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
