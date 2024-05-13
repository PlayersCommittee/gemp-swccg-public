package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to define the initial printed destiny of a card.
 * This is used when the printed destiny of a card is defined by game text.
 */
public class DefinedByGameTextDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to define the initial printed destiny of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextDestinyModifier(PhysicalCard source, int modifierAmount) {
        super(source, null, source, null, ModifierType.PRINTED_DESTINY, true);
        _evaluator = new ConstantEvaluator(modifierAmount);
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
