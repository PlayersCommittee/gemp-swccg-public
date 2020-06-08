package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A deploy cost modifier for when deploying aboard specified starships or vehicles.
 */
public class DeployCostAboardModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _cardToDeployFilter;
    private Filter _targetFilter;

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, int modifierAmount, PhysicalCard starshipOrVehicle) {
        this(source, (Condition) null, new ConstantEvaluator(modifierAmount), starshipOrVehicle);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, int modifierAmount, Filter starshipOrVehicleFilter) {
        this(source, (Condition) null, new ConstantEvaluator(modifierAmount), starshipOrVehicleFilter);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeployCostAboardModifier(PhysicalCard source, int modifierAmount, Persona starshipOrVehiclePersona) {
        this(source, (Condition) null, new ConstantEvaluator(modifierAmount), starshipOrVehiclePersona);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, Condition condition, Evaluator evaluator, PhysicalCard starshipOrVehicle) {
        super(source, null, source, condition, ModifierType.SELF_DEPLOY_COST_TO_TARGET, false);
        _evaluator = evaluator;
        _targetFilter = Filters.or(starshipOrVehicle, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicle)));
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, Condition condition, Evaluator evaluator, Filter starshipOrVehicleFilter) {
        super(source, null, source, condition, ModifierType.SELF_DEPLOY_COST_TO_TARGET, false);
        _evaluator = evaluator;
        _targetFilter = Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter)));
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the card that is the source of the modifier and whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeployCostAboardModifier(PhysicalCard source, Condition condition, Evaluator evaluator, Persona starshipOrVehiclePersona) {
        super(source, null, source, condition, ModifierType.SELF_DEPLOY_COST_TO_TARGET, false);
        _evaluator = evaluator;
        _targetFilter = Filters.or(starshipOrVehiclePersona, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehiclePersona, false)));
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, PhysicalCard starshipOrVehicle) {
        this(source, affectFilter, null, modifierAmount, starshipOrVehicle);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filter starshipOrVehicleFilter) {
        this(source, affectFilter, null, modifierAmount, starshipOrVehicleFilter);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Persona starshipOrVehiclePersona) {
        this(source, affectFilter, null, modifierAmount, starshipOrVehiclePersona);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, PhysicalCard starshipOrVehicle) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), starshipOrVehicle, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filter starshipOrVehicleFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), starshipOrVehicleFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Persona starshipOrVehiclePersona) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), starshipOrVehiclePersona, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, PhysicalCard starshipOrVehicle) {
        this(source, affectFilter, null, evaluator, starshipOrVehicle, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, Filter starshipOrVehicleFilter) {
        this(source, affectFilter, null, evaluator, starshipOrVehicleFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator, Persona starshipOrVehiclePersona) {
        this(source, affectFilter, null, evaluator, starshipOrVehiclePersona, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, PhysicalCard starshipOrVehicle, boolean cumulative) {
        super(source, null, Filters.or(starshipOrVehicle, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicle))),
                condition, ModifierType.DEPLOY_COST_TO_TARGET, cumulative);
        _cardToDeployFilter = Filters.and(affectFilter);
        _evaluator = evaluator;
        if (source == affectFilter) {
            throw new UnsupportedOperationException("This constructor of DeployCostAboardModifier should not be called as self modifier: " + GameUtils.getFullName(source));
        }
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard specified starships or vehicles accepted by the
     * starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filter starshipOrVehicleFilter, boolean cumulative) {
        super(source, null, Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter))),
                condition, ModifierType.DEPLOY_COST_TO_TARGET, cumulative);
        _cardToDeployFilter = Filters.and(affectFilter);
        _evaluator = evaluator;
        if (source == affectFilter) {
            throw new UnsupportedOperationException("This constructor of DeployCostAboardModifier should not be called as self modifier: " + GameUtils.getFullName(source));
        }
    }

    /**
     * Creates a deploy cost modifier for when deploying aboard a starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param starshipOrVehiclePersona the starship/vehicle persona
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Persona starshipOrVehiclePersona, boolean cumulative) {
        super(source, null, Filters.or(starshipOrVehiclePersona, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehiclePersona, false))),
                condition, ModifierType.DEPLOY_COST_TO_TARGET, cumulative);
        _cardToDeployFilter = Filters.and(affectFilter);
        _evaluator = evaluator;
        if (source == affectFilter) {
            throw new UnsupportedOperationException("This constructor of DeployCostAboardModifier should not be called as self modifier: " + GameUtils.getFullName(source));
        }
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
        if (getModifierType() == ModifierType.SELF_DEPLOY_COST_TO_TARGET) {

            if (Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target)) {
                return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy);
            }
            // Check if self deployment modifier is applied at any location
            if (modifiersQuerying.appliesOwnDeploymentModifiersAtAnyLocation(gameState, cardToDeploy)) {
                return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy);
            }
        }
        else {
            if (Filters.and(_cardToDeployFilter).accepts(gameState, modifiersQuerying, cardToDeploy))
                return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy);
        }
        return 0;
    }
}
