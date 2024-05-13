package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card on table to be placed in Reserve Deck.
 */
public class PlaceCardOnReserveDeckFromTableEffect extends PlaceCardsOnReserveDeckFromTableEffect {

    /**
     * Creates an effect that causes the specified card on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param card the card
     */
    public PlaceCardOnReserveDeckFromTableEffect(Action action, PhysicalCard card) {
        this(action, card, false, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified card on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param card the card
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PlaceCardOnReserveDeckFromTableEffect(Action action, PhysicalCard card, boolean toBottomOfPile) {
        this(action, card, toBottomOfPile, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified card on table to be placed in Reserve Deck.
     * @param action the action performing this effect
     * @param card the card
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public PlaceCardOnReserveDeckFromTableEffect(Action action, PhysicalCard card, boolean toBottomOfPile, Zone attachedCardsGoToZone) {
        super(action, Collections.singleton(card), toBottomOfPile, attachedCardsGoToZone);
    }
}
