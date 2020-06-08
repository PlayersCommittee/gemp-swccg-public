package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

public class AstromechCapacityModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public AstromechCapacityModifier(PhysicalCard source, Filterable affectFilter, int modifier) {
        this(source, affectFilter, null, modifier);
    }

    public AstromechCapacityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifier) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifier));
    }

    public AstromechCapacityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.ASTROMECH_CAPACITY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "(may add " + value + " astromech)";
        else
            return "(lose capacity for " + value + " astromech)";
    }

    @Override
    public int getAstromechCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return (int) _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
