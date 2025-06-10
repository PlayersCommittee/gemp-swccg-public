package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

public class ImmuneToAttritionOfExactlyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public ImmuneToAttritionOfExactlyModifier(PhysicalCard source, Filterable affectFilter, int immunityModifier) {
        this(source, affectFilter, null, immunityModifier);
    }

    public ImmuneToAttritionOfExactlyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int immunityModifier) {
        this(source, affectFilter, condition, new ConstantEvaluator(immunityModifier));
    }

    public ImmuneToAttritionOfExactlyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.IMMUNITY_TO_ATTRITION_OF_EXACTLY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Immune to attrition of exactly " + GuiUtils.formatAsString(value);
        else
            return null;
    }

    @Override
    public float getImmunityToAttritionOfExactlyModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersLogic, physicalCard);
    }
}
