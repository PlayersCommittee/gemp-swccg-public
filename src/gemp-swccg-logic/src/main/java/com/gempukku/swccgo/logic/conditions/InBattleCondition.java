package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when a specified card (or a card accepted by the specified filter) is participating
 * in battle.
 */
public class InBattleCondition implements Condition {
    private Integer _permCardId;
    private Integer _permSourceCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when a specified card is participating in battle.
     * @param card the card
     */
    public InBattleCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when a card fulfilled by the specified filter is participating in battle.
     * @param source the card that is checking this condition
     * @param filters the filter
     */
    public InBattleCondition(PhysicalCard source, Filterable filters) {
        _permSourceCardId = source.getPermanentCardId();
        _filters = Filters.and(filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        if (card != null) {
            return Filters.participatingInBattle.accepts(gameState, modifiersQuerying, card);
        }
        else {
            Filter filterToUse = Filters.and(Filters.or(_filters, Filters.hasPermanentAboard(_filters), Filters.hasPermanentWeapon(_filters)), Filters.participatingInBattle);
            return Filters.canSpot(gameState.getGame(), source, filterToUse);
        }
    }
}
