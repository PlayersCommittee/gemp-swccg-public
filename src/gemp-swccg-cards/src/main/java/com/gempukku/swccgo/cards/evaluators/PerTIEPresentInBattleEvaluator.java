package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of TIEs accepted by the specified filter that are present in battle.
 */
public class PerTIEPresentInBattleEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private Filter _filters;

    /**
     * Creates an evaluator that returns the number of TIEs accepted by the specified filter that are present in battle.
     * @param source the card that is creating this evaluator
     * @param filters the filter
     */
    public PerTIEPresentInBattleEvaluator(PhysicalCard source, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        return Filters.countActive(gameState.getGame(), source, true, Filters.and(Filters.TIE, _filters, Filters.presentInBattle));
    }
}
