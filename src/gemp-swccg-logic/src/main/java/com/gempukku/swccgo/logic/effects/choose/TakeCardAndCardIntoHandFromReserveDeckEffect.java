package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * An effect to take a card (and another card) into hand from Reserve Deck.
 */
public class TakeCardAndCardIntoHandFromReserveDeckEffect extends TakeCardCombinationIntoHandFromReserveDeckEffect {
    private Filter _cardFilter1;
    private Filter _cardFilter2;
    private Filter _cardFilter3;

    /**
     * Creates an effect that causes the player to search Reserve Deck and take both a card accepted by cardFilter1 and
     * a card accepted by cardFilter2 into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter1 the filter for a card
     * @param cardFilter2 the filter for a card
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardAndCardIntoHandFromReserveDeckEffect(Action action, String playerId, Filter cardFilter1, Filter cardFilter2, boolean reshuffle) {
        this(action, playerId, cardFilter1, cardFilter2, null, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Reserve Deck and take a card accepted by cardFilter1, a card
     * accepted by cardFilter2, and a card accepted by cardFilter3 into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter1 the filter for a card
     * @param cardFilter2 the filter for a card
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardAndCardIntoHandFromReserveDeckEffect(Action action, String playerId, Filter cardFilter1, Filter cardFilter2, Filter cardFilter3, boolean reshuffle) {
        super(action, playerId, reshuffle);
        _cardFilter1 = cardFilter1;
        _cardFilter2 = cardFilter2;
        _cardFilter3 = cardFilter3;
    }

    @Override
    public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
        Filter filter = Filters.none;
        if (Filters.filterCount(cardsSelected, game, 1, _cardFilter1).isEmpty()) {
            filter = Filters.or(_cardFilter1, filter);
        }
        if (Filters.filterCount(cardsSelected, game, 1, _cardFilter2).isEmpty()) {
            filter = Filters.or(_cardFilter2, filter);
        }
        if (_cardFilter3 != null && Filters.filterCount(cardsSelected, game, 1, _cardFilter3).isEmpty()) {
            filter = Filters.or(_cardFilter3, filter);
        }
        return filter;
    }

    @Override
    public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
        return Filters.filter(cardsSelected, game, _cardFilter1).size() == 1
                && Filters.filter(cardsSelected, game, _cardFilter2).size() == 1
                && (_cardFilter3 == null || Filters.filter(cardsSelected, game, _cardFilter3).size() == 1);
    }
}
