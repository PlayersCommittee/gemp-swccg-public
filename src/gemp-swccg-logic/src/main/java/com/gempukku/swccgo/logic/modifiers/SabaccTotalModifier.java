package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A Sabacc total modifier.
 */
public class SabaccTotalModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a Sabacc total modifier.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Sabacc total is modified
     */
    public SabaccTotalModifier(PhysicalCard source, float modifierAmount, String playerId) {
        super(source, null, null, null, ModifierType.SABACC_TOTAL, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        StringBuilder sideText = new StringBuilder();
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText.append("Dark side");
        else
            sideText.append("Light side");

        if (value >= 0)
            return sideText.append(" Sabacc total +").append(GuiUtils.formatAsString(value)).toString();
        else
            return sideText.append(" Sabacc total ").append(GuiUtils.formatAsString(value)).toString();
    }

    @Override
    public float getSabaccTotalModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (playerId.equals(_playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
