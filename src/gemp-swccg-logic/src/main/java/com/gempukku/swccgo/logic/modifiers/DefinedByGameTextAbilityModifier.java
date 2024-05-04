package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to define the initial printed ability of a card.
 * This is used when the printed ability of a card is defined by game text.
 */
public class DefinedByGameTextAbilityModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to define the initial printed ability of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextAbilityModifier(PhysicalCard source, double modifierAmount) {
        this(source, source, modifierAmount);
    }

    /**
     * Creates a modifier to define the initial printed ability of a card.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextAbilityModifier(PhysicalCard source, Filterable affectFilter, double modifierAmount) {
        super(source, null, affectFilter, null, ModifierType.PRINTED_ABILITY, true);
        _evaluator = new ConstantEvaluator((float) modifierAmount);
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}
