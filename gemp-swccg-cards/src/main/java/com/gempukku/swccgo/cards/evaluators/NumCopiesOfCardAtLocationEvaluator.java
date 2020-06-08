package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * An evaluator that returns the number of copies of a card at a location.
 */
public class NumCopiesOfCardAtLocationEvaluator extends BaseEvaluator {
    private int _permSourceCardId;

    /**
     * Creates an evaluator that returns the number of copies of a card at a location.
     * @param source the card that is creating this evaluator (Note: Not the card being checked for copies of at a location)
     */
    public NumCopiesOfCardAtLocationEvaluator(PhysicalCard source) {
        _permSourceCardId = source.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard targetCard) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        PhysicalCard location = modifiersQuerying.getLocationHere(gameState, targetCard);
        if (location == null)
            return 0;

        return Filters.countActive(gameState.getGame(), source, Filters.and(Filters.sameTitle(card), Filters.at(location)));
    }
}
