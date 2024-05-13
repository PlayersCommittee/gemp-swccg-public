package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be placed in Reserve Deck.
 */
public class PlaceCardsInReserveDeckFromOffTableEffect extends PlaceCardsInCardPileFromOffTableSimultaneouslyEffect {

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed in a
     * card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     */
    public PlaceCardsInReserveDeckFromOffTableEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile) {
        this(action, cardsToPlaceInCardPile, false);
    }

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed in a
     * card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param toBottomOfPile if the cards should go on the bottom of the pile
     */
    public PlaceCardsInReserveDeckFromOffTableEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, boolean toBottomOfPile) {
        super(action, cardsToPlaceInCardPile, Zone.RESERVE_DECK, null, toBottomOfPile);
    }
}
