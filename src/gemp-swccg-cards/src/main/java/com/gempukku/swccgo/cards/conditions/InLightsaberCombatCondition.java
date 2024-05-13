package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified card is in lightsaber combat.
 */
public class InLightsaberCombatCondition implements Condition {
    private Integer _permSourceCardId;
    private Integer _permCardId;
    private Filter _cardFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is in lightsaber combat.
     * @param card the card
     */
    public InLightsaberCombatCondition(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the specified cardFilter is in lightsaber combat.
     * @param source the card that is checking this condition
     * @param cardFilter the card filter
     */
    public InLightsaberCombatCondition(PhysicalCard source, Filterable cardFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _cardFilter = Filters.and(cardFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);

        if (card != null)
            return gameState.isParticipatingInLightsaberCombat(card);
        else {
            return Filters.canSpot(gameState.getGame(), source, Filters.and(_cardFilter, Filters.participatingInLightsaberCombat));
        }
    }
}
