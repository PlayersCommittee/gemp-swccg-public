package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.Collection;

/**
 * A condition that is fulfilled when the specified card (or a card accepted by a specified filter) is escorting a captive
 * (or a captive accepted by a specified filter).
 */
public class EscortingCaptiveCondition implements Condition {
    private int _permSourceCardId;
    private Integer _permEscortCardId;
    private Filter _escortFilter;
    private Filter _captiveFilter;

    /**
     * Creates a condition that is fulfilled when the specified card is escorting a captive.
     * @param card the card
     */
    public EscortingCaptiveCondition(PhysicalCard card) {
        this(card, Filters.any);
    }

    /**
     * Creates a condition that is fulfilled when the specified card is escorting a captive accepted by the captive filter.
     * @param card the card
     * @param captiveFilter the captive filter
     */
    public EscortingCaptiveCondition(PhysicalCard card, Filterable captiveFilter) {
        _permSourceCardId = card.getPermanentCardId();
        _permEscortCardId = card.getPermanentCardId();
        _captiveFilter = Filters.and(captiveFilter);
    }

    /**
     * Creates a condition that is fulfilled when a card accepted by the escort filter is escorting a captive accepted by the captive filter.
     * @param source the card checking this condition
     * @param escortFilter the escort filter
     * @param captiveFilter the captive filter
     */
    public EscortingCaptiveCondition(PhysicalCard source, Filterable escortFilter, Filterable captiveFilter) {
        _permSourceCardId = source.getPermanentCardId();
        _escortFilter = Filters.and(escortFilter);
        _captiveFilter = Filters.and(captiveFilter);
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        PhysicalCard escort = gameState.findCardByPermanentId(_permEscortCardId);

        if (escort != null) {
            Collection<PhysicalCard> captives = gameState.getCaptivesOfEscort(escort);
            return !Filters.filterCount(captives, gameState.getGame(), 1, _captiveFilter).isEmpty();
        }
        return Filters.canSpot(gameState.getGame(), source, Filters.and(_escortFilter, Filters.escorting(_captiveFilter)));
    }
}
