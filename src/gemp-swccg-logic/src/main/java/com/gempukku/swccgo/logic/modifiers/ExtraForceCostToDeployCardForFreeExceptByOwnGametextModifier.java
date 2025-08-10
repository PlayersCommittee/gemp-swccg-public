package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to require extra Force cost to deploy cards that deploy for free (except by own game text).
 */
public class ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter that deploy for free (except by own game text).
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param modifierAmount the amount of the modifier
     */
    public ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(PhysicalCard source, Filterable affectFilter, int modifierAmount) {
        this(source, affectFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter that deploy for free (except by own game text).
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier that requires extra Force cost to deploy cards accepted by the filter that deploy for free (except by own game text).
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_FOR_FREE_EXCEPT_BY_OWN_GAME_TEXT, false);
        _evaluator = evaluator;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, cardToDeploy);
    }
}
