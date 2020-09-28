package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to define the initial printed maneuver of a card.
 * This is used when the printed armor of a card is defined by game text.
 */
public class DefinedByGameTextManeuverModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to define the initial printed maneuver of a card.
     *
     * @param source    the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefinedByGameTextManeuverModifier(PhysicalCard source, Evaluator evaluator) {
        super(source, null, source, null, ModifierType.PRINTED_MANEUVER, true);
        _evaluator = evaluator;
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
