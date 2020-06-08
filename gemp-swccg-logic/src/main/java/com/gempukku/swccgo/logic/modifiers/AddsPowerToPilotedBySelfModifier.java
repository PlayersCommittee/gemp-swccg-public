package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.CanAddToPowerWhenPilotingCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that adds power to cards the specified card is piloting.
 */
public class AddsPowerToPilotedBySelfModifier extends PowerModifier {

    /**
     * Creates a modifier that adds power to anything the source card is piloting.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public AddsPowerToPilotedBySelfModifier(PhysicalCard source, int modifierAmount) {
        this(source, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that adds power to anything the source card is piloting.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public AddsPowerToPilotedBySelfModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, Filters.hasPiloting(source), new CanAddToPowerWhenPilotingCondition(source), evaluator);
    }

    /**
     * Creates a modifier that adds power to cards accepted by the filter that the source card is piloting.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param filter the filter for cards piloted by source card whole power is modified
     */
    public AddsPowerToPilotedBySelfModifier(PhysicalCard source, int modifierAmount, Filterable filter) {
        super(source, Filters.and(filter, Filters.hasPiloting(source)), new CanAddToPowerWhenPilotingCondition(source), new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that adds power to cards accepted by the filter that the source card is piloting.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param filter the filter for cards piloted by source card whole power is modified
     */
    public AddsPowerToPilotedBySelfModifier(PhysicalCard source, Evaluator evaluator, Filterable filter) {
        super(source, Filters.and(filter, Filters.hasPiloting(source)), new CanAddToPowerWhenPilotingCondition(source), evaluator);
    }
}
