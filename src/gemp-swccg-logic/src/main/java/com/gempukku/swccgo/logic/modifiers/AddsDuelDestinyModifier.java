package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier that adds duel destiny during a duel.
 * This is used to when the source card "Adds X duel destiny".
 */
public class AddsDuelDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that adds duel destiny during a duel.
     * @param source the source of the modifier
     * @param modifierAmount the number of destiny to add
     * @param playerId the player to add duel destiny
     */
    public AddsDuelDestinyModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that adds duel destiny during a duel.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny to add
     * @param playerId the player to add duel destiny
     */
    public AddsDuelDestinyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.NUM_DUEL_DESTINY_DRAWS, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark Side: Add " + value + " duel destiny";
        else
            return "Light Side: Add " + value + " duel destiny";
    }

    @Override
    public int getNumDuelDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}
