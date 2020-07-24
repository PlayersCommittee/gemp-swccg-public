package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to define the initial printed deploy cost when deploying to specified targets.
 * This is used when the printed deploy cost of a card is defined by game text.
 */
public class DefinedByGameTextDeployCostToTargetModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _targetFilter;
    private PlayCardOptionId _playCardOptionId;

    /**
     * Creates a modifier to define the initial printed deploy cost when deploying to specified targets.
     * @param source the card that is the source of the modifier and whose printed deploy cost is defined when deploying
     *               to specified targets
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DefinedByGameTextDeployCostToTargetModifier(PhysicalCard source, int modifierAmount, Filterable targetFilter) {
        this(source, source, modifierAmount, targetFilter);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost when deploying to specified targets.
     * @param source the card that is the source of the modifier and whose printed deploy cost is defined when deploying
     *               to specified targets
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param targetFilter the target filter
     */
    public DefinedByGameTextDeployCostToTargetModifier(PhysicalCard source, Evaluator evaluator, Filterable targetFilter) {
        this(source, source, null, evaluator, targetFilter);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost when deploying to specified targets.
     * @param source the source of the modifier
     * @param cardToDeployFilter the filter for cards whose printed deploy cost is defined when deploying to specified targets
     * @param modifierAmount the amount of the modifier
     * @param targetFilter the target filter
     */
    public DefinedByGameTextDeployCostToTargetModifier(PhysicalCard source, Filterable cardToDeployFilter, int modifierAmount, Filterable targetFilter) {
        this(source, cardToDeployFilter, null, new ConstantEvaluator(modifierAmount), targetFilter);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost when deploying to specified targets.
     *
     * @param source             the source of the modifier
     * @param cardToDeployFilter the filter for cards whose printed deploy cost is defined when deploying to specified targets
     * @param condition          condition that must be met
     * @param evaluator          the evaluator that calculates the amount of the modifier
     * @param targetFilter       the target filter
     */
    public DefinedByGameTextDeployCostToTargetModifier(PhysicalCard source, Filterable cardToDeployFilter, Condition condition, Evaluator evaluator, Filterable targetFilter) {
        super(source, null, cardToDeployFilter, condition, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, true);
        _evaluator = evaluator;
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public boolean isDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }

    @Override
    public float getDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, target);
    }
}
