package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to each duel destiny.
 */
public class EachDuelDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to each duel destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose each duel destiny is modified
     */
    public EachDuelDestinyModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to each duel destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose each duel destiny is modified
     */
    public EachDuelDestinyModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId) {
        this(source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier to each duel destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose each duel destiny is modified
     */
    private EachDuelDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.EACH_DUEL_DESTINY, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
