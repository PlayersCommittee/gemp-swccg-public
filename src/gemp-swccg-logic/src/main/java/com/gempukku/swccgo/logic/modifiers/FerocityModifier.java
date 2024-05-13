package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

public class FerocityModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public FerocityModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, null, evaluator, false);
    }

    public FerocityModifier(PhysicalCard source, Condition condition, int modifier) {
        this(source, source, condition, modifier, false);
    }

    public FerocityModifier(PhysicalCard source, Filterable affectFilter, int modifier) {
        this(source, affectFilter, null, modifier, false);
    }

    public FerocityModifier(PhysicalCard source, Filterable affectFilter, int modifier, boolean cumulative) {
        this(source, affectFilter, null, modifier, cumulative);
    }

    public FerocityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifier) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifier), false);
    }

    public FerocityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifier, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifier), cumulative);
    }

    public FerocityModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, affectFilter, condition, ModifierType.FEROCITY, cumulative);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Ferocity +" + GuiUtils.formatAsString(value);
        else
            return "Ferocity " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getFerocityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}
