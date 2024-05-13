package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

public class PilotCapacityModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public PilotCapacityModifier(PhysicalCard source, Filterable affectFilter, int modifier) {
        this(source, affectFilter, null, modifier);
    }

    public PilotCapacityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifier) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifier));
    }

    public PilotCapacityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.PILOT_CAPACITY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "(may add " + value + " pilot)";
        else
            return "(lose capacity for " + value + " pilot)";
    }

    @Override
    public int getPilotCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return (int) _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
