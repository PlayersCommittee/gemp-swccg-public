package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to the deploy cost of a pilot simultaneously deploying with a specified card to a specified target.
 */
public class DeployCostForSimultaneouslyDeployingPilotModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _pilotFilter;
    private Filter _targetFilter;

    /**
     * Creates a modifier to the deploy cost of a pilot accepted by the pilot filter that is simultaneously deploying
     * with the source card.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param modifierAmount the amount of the modifier
     */
    public DeployCostForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, int modifierAmount) {
        this(source, pilotFilter, null, new ConstantEvaluator(modifierAmount), source, Filters.any);
    }

    /**
     * Creates a modifier to the deploy cost of a pilot accepted by the pilot filter that is simultaneously deploying
     * with the source card to a target accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, int modifierAmount, Filterable targetFilter) {
        this(source, pilotFilter, null, new ConstantEvaluator(modifierAmount), source, targetFilter);
    }

    /**
     * Creates a modifier to the deploy cost of a pilot accepted by the pilot filter that is simultaneously deploying
     * with a card accepted by the starshipOrVehicleFilter to a target accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param targetFilter the target filter
     */
    public DeployCostForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, int modifierAmount, Filterable starshipOrVehicleFilter, Filterable targetFilter) {
        this(source, pilotFilter, null, new ConstantEvaluator(modifierAmount), starshipOrVehicleFilter, targetFilter);
    }

    /**
     * Creates a modifier to the deploy cost of a pilot accepted by the pilot filter that is simultaneously deploying
     * with a card accepted by the starshipOrVehicleFilter to a target accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param targetFilter the target filter
     */
    public DeployCostForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, Condition condition, int modifierAmount, Filterable starshipOrVehicleFilter, Filterable targetFilter) {
        this(source, pilotFilter, condition, new ConstantEvaluator(modifierAmount), starshipOrVehicleFilter, targetFilter);
    }

    /**
     * Creates a modifier to the deploy cost of a pilot accepted by the pilot filter that is simultaneously deploying
     * with a card accepted by the starshipOrVehicleFilter to a target accepted by the target filter.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     * @param targetFilter the target filter
     */
    private DeployCostForSimultaneouslyDeployingPilotModifier(PhysicalCard source, Filterable pilotFilter, Condition condition, Evaluator evaluator, Filterable starshipOrVehicleFilter, Filterable targetFilter) {
        super(source, null, starshipOrVehicleFilter, condition, ModifierType.SIMULTANEOUS_PILOT_DEPLOY_COST, false);
        _evaluator = evaluator;
        _pilotFilter = Filters.and(pilotFilter);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedPilot(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot) {
        return Filters.and(_pilotFilter).accepts(gameState, modifiersQuerying, pilot);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot, PhysicalCard target) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, target);
    }
}
