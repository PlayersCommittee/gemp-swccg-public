package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that specifies "Draws X race destiny instead of 1".
 */
public class DrawsRaceDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that specifies "Draws X race destiny instead of 1".
     * @param source the card that is the source of the modifier and that can draw race destiny
     * @param modifierAmount the number of destiny
     */
    public DrawsRaceDestinyModifier(PhysicalCard source, int modifierAmount) {
        this(source, source, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that specifies "Draws X race destiny instead of 1".
     * @param source the source of the modifier
     * @param affectedFilter the filter for Podracers that can draw race destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny
     */
    private DrawsRaceDestinyModifier(PhysicalCard source, Filterable affectedFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.Podracer, affectedFilter), condition, ModifierType.NUM_RACE_DESTINY_DRAWS, true);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        return "Draws " + value + " race destiny";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
