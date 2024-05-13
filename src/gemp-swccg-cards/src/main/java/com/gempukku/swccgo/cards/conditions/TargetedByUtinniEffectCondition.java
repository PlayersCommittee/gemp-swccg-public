package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is targeted by an Utinni Effect accepted by the specified filter.
 */
public class TargetedByUtinniEffectCondition implements Condition {
    private int _permCardId;
    private Filter _filters;

    /**
     * Creates a condition that is fulfilled when the specified card is targeted by an Utinni Effect accepted by the specified filter.
     * @param card the card
     * @param filters the filter
     */
    public TargetedByUtinniEffectCondition(PhysicalCard card, Filterable filters) {
        _permCardId = card.getPermanentCardId();
        _filters = Filters.and(Filters.Utinni_Effect, filters);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        SwccgGame game = gameState.getGame();
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);

        for (PhysicalCard utinniEffect : Filters.filterActive(game, card, _filters)) {
            // Check if attached to card
            if (Filters.hasAttached(utinniEffect).accepts(game, card)) {
                return true;
            }

            // Check if explicitly targeting this card
            if (utinniEffect.getTargetedCards(gameState).values().contains(card)) {
                return true;
            }
        }

        return false;
    }
}
