package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to total Force generation.
 */
public class TotalForceGenerationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to total Force generation.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public TotalForceGenerationModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to total Force generation.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public TotalForceGenerationModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, null, evaluator, playerId);
    }

    /**
     * Creates a modifier to total Force generation.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public TotalForceGenerationModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to total Force generation.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public TotalForceGenerationModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.TOTAL_FORCE_GENERATION, false);
        _evaluator = evaluator;
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

        if (value >= 0)
            return sideText + " total Force generation +" + GuiUtils.formatAsString(value);
        else
            return sideText + " total Force generation " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getTotalForceGenerationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (playerId.equals(_playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
