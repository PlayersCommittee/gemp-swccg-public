package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to total power during battle.
 */
public class TotalPowerDuringBattleModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to total power during battle.
     *
     * @param source         the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId       the player whose total power is modified
     */
    public TotalPowerDuringBattleModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to total power during battle.
     *
     * @param source    the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId  the player whose total power is modified
     */
    public TotalPowerDuringBattleModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, null, evaluator, playerId);
    }

    /**
     * Creates a modifier to total power during battle.
     *
     * @param source         the location that is the source of the modifier and where total power is modified
     * @param condition      the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId       the player whose total power is modified
     */
    public TotalPowerDuringBattleModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to total power during battle.
     *
     * @param source    the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId  the player whose total power is modified
     */
    public TotalPowerDuringBattleModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, Filters.battleLocation), condition, ModifierType.TOTAL_POWER_DURING_BATTLE, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        String sideText = (gameState.getSide(_playerId) == Side.DARK) ? "Dark side" : "Light side";

        if (value >= 0)
            return sideText + " total power +" + GuiUtils.formatAsString(value);
        else
            return sideText + " total power " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getTotalPowerDuringBattleModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        else
            return 0;
    }
}
