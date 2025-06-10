package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to define the initial printed defense value of a card.
 * This is used when the printed defense value of a card is defined by game text.
 */
public class DefinedByGameTextDefenseValueModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to define the initial printed defense value of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextDefenseValueModifier(PhysicalCard source, double modifierAmount) {
        this(source, new ConstantEvaluator((float) modifierAmount));
    }

    /**
     * Creates a modifier to define the initial printed defense value of a card.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private DefinedByGameTextDefenseValueModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, null, source, null, ModifierType.PRINTED_DEFENSE_VALUE, true);
        _evaluator = evaluator;
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
