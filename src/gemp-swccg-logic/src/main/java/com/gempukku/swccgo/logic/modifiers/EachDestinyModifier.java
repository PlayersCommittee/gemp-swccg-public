package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to each destiny draw for a specified draw destiny.
 */
public class EachDestinyModifier extends AbstractModifier {
    private int _drawDestinyStateId;
    private Evaluator _evaluator;

    /**
     * Creates a modifier to each destiny draw for a specified draw destiny.
     * @param source the source of the modifier
     * @param drawDestinyStateId the id of the draw destiny state for the draw destiny to affect
     * @param modifierAmount the amount of the modifier
     */
    public EachDestinyModifier(PhysicalCard source, int drawDestinyStateId, int modifierAmount) {
        this(source, drawDestinyStateId, null, modifierAmount);
    }

    /**
     * Creates a modifier to each destiny draw for a specified draw destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param drawDestinyStateId the id of the draw destiny state for the draw destiny to affect
     * @param modifierAmount the amount of the modifier
     */
    public EachDestinyModifier(PhysicalCard source, int drawDestinyStateId, Condition condition, int modifierAmount) {
        this(source, drawDestinyStateId, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to each destiny draw for a specified draw destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param drawDestinyStateId the id of the draw destiny state for the draw destiny to affect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public EachDestinyModifier(PhysicalCard source, int drawDestinyStateId, Condition condition, Evaluator evaluator) {
        super(source, null, null, condition, ModifierType.EACH_DESTINY_DRAW, false);
        _drawDestinyStateId = drawDestinyStateId;
        _evaluator = evaluator;
    }

    /**
     * Determines if this modifier affects the current draw destiny effect.
     * @param gameState the game state
     * @return true or false
     */
    @Override
    public boolean isForTopDrawDestinyEffect(GameState gameState) {
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        return drawDestinyState != null && _drawDestinyStateId == drawDestinyState.getId();
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, target);
    }
}
