package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that affects total training destiny.
 */
public class TotalTrainingDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, int modifierAmount) {
        this(source, Filters.any, null, modifierAmount);
    }

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, Filters.any, condition, modifierAmount);
    }

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param jediTestFilter the filter for Jedi Test whose training destiny is modified
     * @param modifierAmount the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, Filterable jediTestFilter, int modifierAmount) {
        this(source, jediTestFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param jediTestFilter the filter for Jedi Test whose training destiny is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, Filterable jediTestFilter, Condition condition, int modifierAmount) {
        this(source, jediTestFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param jediTestFilter the filter for Jedi Test whose training destiny is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, Filterable jediTestFilter, Evaluator evaluator) {
        this(source, jediTestFilter, null, evaluator);
    }

    /**
     * Creates a modifier that affects total training destiny.
     * @param source the source of the modifier
     * @param jediTestFilter the filter for Jedi Test whose training destiny is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public TotalTrainingDestinyModifier(PhysicalCard source, Filterable jediTestFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.Jedi_Test, jediTestFilter), condition, ModifierType.TOTAL_TRAINING_DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        if (value >= 0)
            return "Training destiny +" + GuiUtils.formatAsString(value);
        else
            return "Training destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard jediTest) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, jediTest);
    }
}
