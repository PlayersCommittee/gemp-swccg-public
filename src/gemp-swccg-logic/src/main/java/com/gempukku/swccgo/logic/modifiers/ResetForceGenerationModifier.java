package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets Force generation at specified locations to an unmodifiable value.
 */
public class ResetForceGenerationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that resets Force generation for the specified player at locations accepted by the location filter
     * to an unmodifiable value.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param resetValue the reset value
     * @param playerId the player
     */
    public ResetForceGenerationModifier(PhysicalCard source, Filterable locationFilter, int resetValue, String playerId) {
        this(source, locationFilter, null, resetValue, playerId);
    }

    /**
     * Creates a modifier that resets Force generation for the specified player at locations accepted by the location filter
     * to an unmodifiable value.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param playerId the player
     */
    protected ResetForceGenerationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int resetValue, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.UNMODIFIABLE_FORCE_GENERATION_AT_LOCATION, true);
        _evaluator = new ConstantEvaluator(resetValue);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side";
        else
            sideText = "Light side";

        return sideText + " Force generation = " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
