package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.ForceLossState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier for minimum amount to which Force loss (except battle damage) can be reduced.
 */
public class ForceLossMinimumModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private int _forceLossId;

    /**
     * Creates a modifier for minimum amount to which Force loss (except battle damage) can be reduced.
     * @param source the source of the modifier
     * @param minimum the amount of the modifier
     * @param playerId the player whose Force loss minimum is set
     * @param forceLossId the id of the Force loss to be modified
     */
    public ForceLossMinimumModifier(PhysicalCard source, int minimum, String playerId, int forceLossId) {
        this(source, null, new ConstantEvaluator(minimum), playerId, forceLossId);
    }

    /**
     * Creates a modifier for minimum amount to which Force loss (except battle damage) can be reduced.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force loss minimum is set
     * @param forceLossId the id of the Force loss to be modified
     */
    private ForceLossMinimumModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, int forceLossId) {
        super(source, null, null, condition, ModifierType.FORCE_LOSS_MINIMUM, true);
        _evaluator = evaluator;
        _playerId = playerId;
        _forceLossId = forceLossId;
    }

    @Override
    public float getForceLossMinimum(GameState gameState, ModifiersQuerying modifiersQuerying) {
        ForceLossState forceLossState = gameState.getTopForceLossState();
        if (forceLossState != null && forceLossState.getId() == _forceLossId)
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
