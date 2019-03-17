package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.GuiUtils;


/**
 * A modifier that resets ability value to an unmodifiable value vs a specific card
 */
public class ResetAbilityVsCardModifier extends AbstractModifier {
    private float _resetValue;
    private Filterable _specificCard;

    /**
     * Creates a modifier that resets ability value to an unmodifiable value vs a specific card
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose defense value is reset
     * @param resetValue the reset value
     * @param specificCard  Specific card which we reset ability against
     */
    public ResetAbilityVsCardModifier(PhysicalCard source, Filterable affectFilter, float resetValue, Filterable specificCard) {
        super(source, null, affectFilter, null, ModifierType.UNMODIFIABLE_ABILITY_VS_CARD, true);
        _resetValue = resetValue;
        _specificCard = specificCard;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Ability value = " + GuiUtils.formatAsString(_resetValue) + " vs specific card";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}
