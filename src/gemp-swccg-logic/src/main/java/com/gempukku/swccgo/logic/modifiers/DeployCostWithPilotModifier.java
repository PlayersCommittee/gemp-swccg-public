package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to the deploy cost of a starship/vehicle when deployed simultaneously with a specified pilot.
 */
public class DeployCostWithPilotModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _pilotFilter;

    /**
     * Creates a modifier to the deploy cost of the starship/vehicle when deployed simultaneously with a pilot accepted
     * by the pilot filter.
     * @param source the card that is the source of the modifier and affected by this modifier
     * @param modifierAmount the amount of the modifier
     * @param pilotFilter the pilot filter
     */
    public DeployCostWithPilotModifier(PhysicalCard source, int modifierAmount, Filterable pilotFilter) {
        this(source, source, modifierAmount, pilotFilter);
    }

    /**
     * Creates a modifier to the deploy cost of starships/vehicles accepted by the filter when deployed simultaneously with
     * a pilot accepted by the pilot filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for starships/vehicles affected by the modifier
     * @param modifierAmount the amount of the modifier
     * @param pilotFilter the pilot filter
     */
    public DeployCostWithPilotModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable pilotFilter) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount), pilotFilter);
    }

    /**
     * Creates a modifier to the deploy cost of starships/vehicles accepted by the filter when deployed simultaneously with
     * a pilot accepted by the pilot filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for starships/vehicles affected by the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param pilotFilter the pilot filter
     */
    private DeployCostWithPilotModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable pilotFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.DEPLOY_COST_WITH_PILOT, false);
        _evaluator = evaluator;
        _pilotFilter = Filters.and(pilotFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploy cost affected when deployed with pilot";
    }

    @Override
    public float getDeployCostWithPilotModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot) {
        if (pilot!=null && Filters.and(_pilotFilter).accepts(gameState, modifiersQuerying, pilot))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, pilot);

        return 0;
    }
}
