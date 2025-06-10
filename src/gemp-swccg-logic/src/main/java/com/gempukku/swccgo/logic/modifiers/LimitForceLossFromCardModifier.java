package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that sets the maximum amount of Force that the specified player can lose from a card affected by the modifier.
 */
public class LimitForceLossFromCardModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a card affected
     * by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, String playerId) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a card affected
     * by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param condition the condition under which this modifier is in effect
     * @param modifierAmount the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a card affected
     * by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator that determine the amount of the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, String playerId) {
        this(source, affectFilter, null, evaluator, playerId);
    }

    /**
     * Creates a modifier that sets the maximum amount of Force that the specified player can lose from a card affected
     * by the modifier.
     * @param source the source card of this modifier
     * @param affectFilter the filter
     * @param condition the condition under which this modifier is in effect
     * @param evaluator the evaluator that determine the amount of the maximum Force loss limit
     * @param playerId the player whose maximum Force loss is limited
     */
    public LimitForceLossFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.LIMIT_FORCE_LOSS_FROM_CARD, true);
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

        if (value==0)
            return sideText + " loses no Force";
        else
            return sideText + " Force loss limited to " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getForceLossLimit(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
