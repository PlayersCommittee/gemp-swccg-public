package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that specifies "Draws X race destiny and choose Y".
 */
public class DrawsRaceDestinyAndChooseModifier extends AbstractModifier {
    private Evaluator _numToDrawEvaluator;
    private Evaluator _numToChooseEvaluator;

    /**
     * Creates a modifier that specifies "Draws X race destiny and choose Y".
     * @param source the card that is the source of the modifier and that can draw race destiny
     * @param numToDraw the number of destiny to draw
     * @param numToChoose the number of destiny to choose
     */
    public DrawsRaceDestinyAndChooseModifier(PhysicalCard source, int numToDraw, int numToChoose) {
        this(source, source, null, numToDraw, numToChoose);
    }

    /**
     * Creates a modifier that specifies "Draws X race destiny and choose Y".
     * @param source the source of the modifier
     * @param affectedFilter the filter for Podracers that can draw race destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param numToDraw the number of destiny to draw
     * @param numToChoose the number of destiny to choose
     */
    private DrawsRaceDestinyAndChooseModifier(PhysicalCard source, Filterable affectedFilter, Condition condition, int numToDraw, int numToChoose) {
        super(source, null, Filters.and(Filters.Podracer, affectedFilter), condition, ModifierType.NUM_RACE_DESTINY_DRAW_AND_CHOOSE, true);
        _numToDrawEvaluator = new ConstantEvaluator(numToDraw);
        _numToChooseEvaluator = new ConstantEvaluator(numToChoose);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int numToDraw = (int) _numToDrawEvaluator.evaluateExpression(gameState, modifiersQuerying, self);
        final int numToChoose = (int) _numToChooseEvaluator.evaluateExpression(gameState, modifiersQuerying, self);
        return "Draws " + numToDraw + " and chooses " + numToChoose + " race destiny";
    }

    @Override
    public int getNumToDraw(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return (int) _numToChooseEvaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }

    @Override
    public int getNumToChoose(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return (int) _numToChooseEvaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
