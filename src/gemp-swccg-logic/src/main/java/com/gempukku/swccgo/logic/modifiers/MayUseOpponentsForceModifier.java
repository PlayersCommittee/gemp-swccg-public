package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to the amount of opponent's Force a player may use.
 */
public class MayUseOpponentsForceModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private int _minForcePileSize;

    /**
     * Creates a modifier that allows a player to use any amount of opponent's Force.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may use opponent's Force
     */
    public MayUseOpponentsForceModifier(PhysicalCard source, Condition condition, String playerId) {
        this(source, condition, new ConstantEvaluator(Integer.MAX_VALUE), playerId);
    }

    /**
     * Creates a modifier that allows a player to use opponent's Force.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player that may use opponent's Force
     */
    public MayUseOpponentsForceModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        this(source, condition, evaluator, playerId, 1);
    }

    /**
     * Creates a modifier that allows a player to use opponent's Force.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player that may use opponent's Force
     * @param minForcePileSize the minimum required size of opponent's Force Pile
     */
    public MayUseOpponentsForceModifier(PhysicalCard source, Evaluator evaluator, String playerId, int minForcePileSize) {
        this(source, null, evaluator, playerId, minForcePileSize);
    }

    /**
     * Creates a modifier that allows a player to use opponent's Force.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player that may use opponent's Force
     * @param minForcePileSize the minimum required size of opponent's Force Pile
     */
    public MayUseOpponentsForceModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, int minForcePileSize) {
        super(source, null, null, condition, ModifierType.MAY_USE_OPPONENTS_FORCE, true);
        _evaluator = evaluator;
        _playerId = playerId;
        _minForcePileSize = minForcePileSize;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }

    @Override
    public int getMinForcePileSizeToUseOpponentsForce(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _minForcePileSize;
    }
}
