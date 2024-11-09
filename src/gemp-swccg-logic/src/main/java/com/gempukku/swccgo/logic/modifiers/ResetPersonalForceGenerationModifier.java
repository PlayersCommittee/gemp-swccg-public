package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to reset a player's personal Force generation.
 */
public class ResetPersonalForceGenerationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to a player's personal Force generation.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose personal Force generation is reset
     */
    public ResetPersonalForceGenerationModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to a player's personal Force generation.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose personal Force generation is reset
     */
    public ResetPersonalForceGenerationModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, null, evaluator, playerId);
    }

    /**
     * Creates a modifier to a player's personal Force generation.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose personal Force generation is reset
     */
    public ResetPersonalForceGenerationModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to a player's personal Force generation.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose personal Force generation is reset
     */
    public ResetPersonalForceGenerationModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.PERSONAL_FORCE_GENERATION, false);
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

        return sideText + " personal Force generation = " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getTotalForceGenerationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (playerId.equals(_playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
