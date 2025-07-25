package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that allows cards with that can only deploy with replacement deployment for an unmodifiable value.
 */
public class MayDeployWithoutReplacementModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * A modifier that allows cards with that can only deploy with replacement deployment for an unmodifiable value.
     *
     * @param source       the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param resetValue   the reset value
     */
    public MayDeployWithoutReplacementModifier(PhysicalCard source, Filterable affectFilter, float resetValue) {
        this(source, affectFilter, null, resetValue);
    }

    /**
     * A modifier that allows cards with that can only deploy with replacement deployment for an unmodifiable value.
     *
     * @param source       the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue   the reset value
     */
    public MayDeployWithoutReplacementModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_WITHOUT_REPLACEMENT, true);
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
