package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets Force retrieval from specified cards to an unmodifiable value.
 */
public class ResetForceRetrievalFromCardModifier extends AbstractModifier {
    private float _resetValue;

    /**
     * Creates a modifier that resets Force retrieval from a card accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param resetValue the reset value
     * @param playerId the player whose Force loss is modified
     */
    public ResetForceRetrievalFromCardModifier(PhysicalCard source, Filterable affectFilter, float resetValue, String playerId) {
        this(source, affectFilter, null, resetValue, playerId);
    }

    /**
     * Creates a modifier to Force retrieval.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param playerId the player whose Force retrieval is modified
     */
    public ResetForceRetrievalFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_FORCE_RETRIEVAL, true);
        _resetValue = resetValue;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _resetValue;
        String sideText = _playerId.equals(gameState.getDarkPlayer()) ? "Dark Side" : "Light Side";

        if (value == 0)
            return sideText + " retrieves no Force";
        else
            return sideText + "'s Force retrieval = " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _resetValue;
    }
}
