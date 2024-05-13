package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that adds power to cards the specified card is driving.
 */
public class AddsPowerToDrivenBySelfModifier extends PowerModifier {

    /**
     * Creates a modifier that adds power to anything the source card is driving.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AddsPowerToDrivenBySelfModifier(PhysicalCard source, int modifierAmount) {
        this(source, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that adds power to anything the source card is driving.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public AddsPowerToDrivenBySelfModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, Filters.hasDriving(source), null, evaluator);
    }

    /**
     * Creates a modifier that adds power to cards accepted by the filter that the source card is driving.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param filter the filter for cards driven by source card whose power is modified
     */
    public AddsPowerToDrivenBySelfModifier(PhysicalCard source, int modifierAmount, Filterable filter) {
        super(source, Filters.and(filter, Filters.hasDriving(source)), null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that adds power to cards accepted by the filter that the source card is driving.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param filter the filter for cards driven by source card whose power is modified
     */
    public AddsPowerToDrivenBySelfModifier(PhysicalCard source, Evaluator evaluator, Filterable filter) {
        super(source, Filters.and(filter, Filters.hasDriving(source)), null, evaluator);
    }
}
