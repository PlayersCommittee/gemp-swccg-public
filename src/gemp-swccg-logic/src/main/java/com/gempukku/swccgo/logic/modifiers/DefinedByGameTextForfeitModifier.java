package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to define the initial printed forfeit value of a card.
 * This is used when the printed forfeit value of a card is defined by game text.
 */
public class DefinedByGameTextForfeitModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to define the initial printed forfeit value of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextForfeitModifier(PhysicalCard source, float modifierAmount) {
        this(source, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to define the initial printed forfeit value of a card.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextForfeitModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to define the initial printed forfeit value of a card.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefinedByGameTextForfeitModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, source, evaluator);
    }

    /**
     * Creates a modifier to define the initial printed forfeit value of a card.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    private DefinedByGameTextForfeitModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        super(source, null, affectFilter, null, ModifierType.PRINTED_FORFEIT_VALUE, true);
        _evaluator = evaluator;
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
