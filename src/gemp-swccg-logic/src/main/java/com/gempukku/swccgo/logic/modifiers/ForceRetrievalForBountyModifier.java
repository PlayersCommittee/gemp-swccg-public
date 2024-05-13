package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to Force retrieval for collecting a bounty.
 */
public class ForceRetrievalForBountyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to Force retrieval amount when a bounty hunter accepted by the filter collects the bounty.
     * @param source the source of the modifier
     * @param bountyHunterFilter the bounty hunter filter
     * @param modifierAmount the amount of the modifier
     */
    public ForceRetrievalForBountyModifier(PhysicalCard source, Filterable bountyHunterFilter, float modifierAmount) {
        super(source, null, Filters.and(Filters.bounty_hunter, bountyHunterFilter), null, ModifierType.FORCE_RETRIEVAL_FOR_BOUNTY, false);
        _evaluator = new ConstantEvaluator(modifierAmount);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        if (value >= 0)
            return "Force retrieval for collecting bounty +" + GuiUtils.formatAsString(value);
        else
            return "Force retrieval for collecting bounty " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}
