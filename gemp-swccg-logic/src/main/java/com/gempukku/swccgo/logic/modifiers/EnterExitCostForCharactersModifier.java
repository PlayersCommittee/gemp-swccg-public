package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;


/**
 * A modifier that defines the enter/exit cost for characters to/from a starship/vehicle site.
 * This is used when the the enter/exit cost for characters to/from a starship/vehicle site is defined by game text.
 */
public class EnterExitCostForCharactersModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that defines the enter/exit cost for characters to/from a starship/vehicle site for the specified player.
     * @param source the source of the modifier
     * @param printedValue the printed docking bay transit from cost
     * @param playerId the player
     */
    public EnterExitCostForCharactersModifier(PhysicalCard source, int printedValue, String playerId) {
        this(source, source, null, new ConstantEvaluator(printedValue), playerId);
    }

    /**
     * Creates a modifier that defines the the enter/exit cost for characters to/from a starship/vehicle site accepted by
     * the filter for the specified player.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player
     */
    private EnterExitCostForCharactersModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.ENTER_EXIT_COST, true);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getSide(_playerId)== Side.DARK)
            sideText = "Dark side";
        else
            sideText = "Light side";

        return sideText + " enter/exit cost = " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
