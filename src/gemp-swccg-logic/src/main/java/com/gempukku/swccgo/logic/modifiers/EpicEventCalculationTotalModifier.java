package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;


public class EpicEventCalculationTotalModifier extends AbstractModifier {
    protected Evaluator _evaluator;

    public EpicEventCalculationTotalModifier(PhysicalCard source, Filterable affectFilter, int calculationTotalModifier) {
        this(source, affectFilter, null, calculationTotalModifier);
    }

    public EpicEventCalculationTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int calculationTotalModifier) {
        this(source, affectFilter, condition, new ConstantEvaluator(calculationTotalModifier), false);
    }

    public EpicEventCalculationTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        this(source, affectFilter, condition, evaluator, false);
    }

    public EpicEventCalculationTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int calculationTotalModifier, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(calculationTotalModifier), cumulative);
    }

    public EpicEventCalculationTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.EPIC_EVENT_CALCULATION_TOTAL, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Epic event calculation total +" + GuiUtils.formatAsString(value);
        else
            return "Epic event calculation total " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getEpicEventCalculationTotalModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
