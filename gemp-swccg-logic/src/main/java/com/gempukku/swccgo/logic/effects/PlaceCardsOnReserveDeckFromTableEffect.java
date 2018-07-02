package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be placed in Reserve Deck.
 */
public class PlaceCardsOnReserveDeckFromTableEffect extends PlaceCardsInCardPileFromTableEffect {

    /**
     * Creates an effect that causes the specified cards on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public PlaceCardsOnReserveDeckFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        this(action, cards, false, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param cards the cards
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PlaceCardsOnReserveDeckFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean toBottomOfPile) {
        this(action, cards, toBottomOfPile, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified cards on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param cards the cards
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public PlaceCardsOnReserveDeckFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean toBottomOfPile, Zone attachedCardsGoToZone) {
        super(action, action.getPerformingPlayer(), cards, Zone.RESERVE_DECK, toBottomOfPile, attachedCardsGoToZone);
    }
}
