package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Force loss (due to battle damage).
 * See ForceLossModifier for modifying Force loss not due to battle damage.
 */
public class BattleDamageModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to Force loss (due to battle damage).
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force loss is modified
     */
    public BattleDamageModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, Filters.battleLocation, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to Force loss (due to battle damage).
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force loss is modified
     */
    private BattleDamageModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, locationFilter, condition, ModifierType.BATTLE_DAMAGE, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard battleLocation) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, battleLocation);

        String sideText;
        if (gameState.getSide(_playerId)== Side.DARK)
            sideText = "Dark side battle damage";
        else
            sideText = "Light side battle damage";

        if (value >= 0)
            return sideText + "+" + GuiUtils.formatAsString(value);
        else
            return sideText + " " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}
