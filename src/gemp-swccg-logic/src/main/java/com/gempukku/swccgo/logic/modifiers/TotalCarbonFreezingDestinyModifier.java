package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that affects total Carbon-Freezing destiny.
 */
public class TotalCarbonFreezingDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects total Carbon-Freezing destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public TotalCarbonFreezingDestinyModifier(PhysicalCard source, float modifierAmount) {
        this(source, null, modifierAmount, null);
    }

    /**
     * Creates a modifier that affects total Carbon-Freezing destiny for the specified player.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose total Carbon-Freezing destiny is modified
     */
    public TotalCarbonFreezingDestinyModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier that affects total Carbon-Freezing destiny for the specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose total Carbon-Freezing destiny is modified
     */
    public TotalCarbonFreezingDestinyModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that affects total Carbon-Freezing destiny for the specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose total Carbon-Freezing destiny is modified
     */
    private TotalCarbonFreezingDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.TOTAL_CARBON_FREEZING_DESTINY, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        if (value >= 0)
            return "Total Carbon-Freezing destiny +" + GuiUtils.formatAsString(value);
        else
            return "Total Carbon-Freezing destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
