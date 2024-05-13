package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to Force loss from lightsaber combat.
 */
public class LightsaberCombatForceLossModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filter _winningCharacterFilter;

    /**
     * Creates a Force loss from lightsaber combat modifier.
     * @param source the card that is the source of the modifier and that applies the modifier if the character winning
     *               the lightsaber combat
     * @param modifierAmount the amount of the modifier
     */
    public LightsaberCombatForceLossModifier(PhysicalCard source, float modifierAmount) {
        this(source, new ConstantEvaluator(modifierAmount), source);
    }

    /**
     * Creates a Force loss from lightsaber combat modifier.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param winningCharacterFilter the filter for the character winning the lightsaber combat that would cause this modifier
     *                               to be applied
     */
    public LightsaberCombatForceLossModifier(PhysicalCard source, Evaluator evaluator, Filterable winningCharacterFilter) {
        super(source, null, null, null, ModifierType.LIGHTSABER_COMBAT_FORCE_LOSS, false);
        _evaluator = evaluator;
        _winningCharacterFilter = Filters.and(winningCharacterFilter);
    }

    @Override
    public float getLightsaberCombatForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard winningCharacter) {
        if (Filters.and(_winningCharacterFilter).accepts(gameState, modifiersQuerying, winningCharacter))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, winningCharacter);
        else
            return 0;
    }
}
