package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * An evaluator that returns the number of cards accepted by the specified filter that are in battle.
 */
public class InBattleOrStackedInBattleEvaluator extends BaseEvaluator {
    private int _permSourceCardId;
    private Filter _inBattleFilter;
    private Filter _stackedInBattleFilter;

    /**
     * Creates an evaluator that returns the number of cards accepted by the specified filter that are in battle.
     * @param source the card that is creating this evaluator
     * @param inBattleFilter the filter
     */
    public InBattleOrStackedInBattleEvaluator(PhysicalCard source, Filterable inBattleFilter, Filterable stackedInBattleFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _inBattleFilter = Filters.and(inBattleFilter);
        _stackedInBattleFilter = Filters.and(stackedInBattleFilter);
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {

        // Make sure battle is even happening
        PhysicalCard battleLocation = gameState.getBattleLocation();
        if (battleLocation == null) {
            return 0;
        }

        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        // Find the cards in-battle matching the filter (using same logic as "InBattleEvaluator")
        Filter filterToUse = Filters.or(_inBattleFilter, Filters.hasPermanentAboard(_inBattleFilter), Filters.hasPermanentWeapon(_inBattleFilter));
        int cardsInBattle = Filters.countActive(gameState.getGame(), source, Filters.and(filterToUse, Filters.participatingInBattle));

        // Check for stacked cards there too
        Filter cardsAtBattleLocation = Filters.here(battleLocation);
        Collection<PhysicalCard> matchingCardsStackedAtLocation = Filters.filterStacked(gameState.getGame(), Filters.and(_stackedInBattleFilter, Filters.stackedOn(self, cardsAtBattleLocation)));

        return cardsInBattle + matchingCardsStackedAtLocation.size();
    }
}
