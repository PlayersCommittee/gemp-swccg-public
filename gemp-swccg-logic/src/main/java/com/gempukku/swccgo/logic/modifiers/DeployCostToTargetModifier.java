package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A deploy cost modifier for when deploying to specified targets.
 */
public class DeployCostToTargetModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _cardToDeployFilter;
    private Filter _targetFilter;

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the card that is the source of the modifier and whose deploy cost is modified when deploying to specified targets
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostToTargetModifier(PhysicalCard source, int modifierAmount, Filterable targetFilter) {
        this(source, null, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the card that is the source of the modifier and whose deploy cost is modified when deploying to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostToTargetModifier(PhysicalCard source, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        super(source, null, source, condition, ModifierType.SELF_DEPLOY_COST_TO_TARGET, false);
        _evaluator = evaluator;
        _targetFilter = Filters.and(targetFilter);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable targetFilter) {
        this(source, affectFilter, null, modifierAmount, targetFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount, Filterable targetFilter, boolean cumulative) {
        this(source, affectFilter, null, modifierAmount, targetFilter, cumulative);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable targetFilter) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), targetFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int modifierAmount, Filterable targetFilter, boolean cumulative) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount), targetFilter, cumulative);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        this(source, affectFilter, condition, evaluator, targetFilter, false);
    }

    /**
     * Creates a deploy cost modifier for when deploying to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is modified when deploying to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public DeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator, Filterable targetFilter, boolean cumulative) {
        super(source, null, targetFilter, condition, ModifierType.DEPLOY_COST_TO_TARGET, cumulative);
        _evaluator = evaluator;
        _cardToDeployFilter = Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter)));
        if (source == affectFilter) {
            throw new UnsupportedOperationException("This constructor of DeployCostToTargetModifier should not be called as self modifier: " + GameUtils.getFullName(source));
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
