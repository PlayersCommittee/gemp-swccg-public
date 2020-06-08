package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified card not on table (e.g. in a card pile, in hand, etc.) to be placed out of play.
 */
public class PlaceCardOutOfPlayFromOffTableEffect extends PlaceCardsOutOfPlayFromOffTableEffect {

    /**
     * Creates an effect that causes the specified card not on table (e.g. in a card pile, in hand, etc.) to be placed
     * out of play.
     * @param action the action performing this effect
     * @param card the card
     */
    public PlaceCardOutOfPlayFromOffTableEffect(Action action, PhysicalCard card) {
        super(action, Collections.singleton(card));
    }

    /**
     * A callback method for the cards placed out of play.
     * @param cards the cards placed out of play
     */
    @Override
    protected final void cardsPlacedOutOfPlay(List<PhysicalCard> cards) {
        if (cards.size() == 1) {
            cardPlacedOutOfPlay(cards.iterator().next());
        }
    }

    /**
     * A callback method for the card placed out of play.
     * @param card the card placed out of play
     */
    protected void cardPlacedOutOfPlay(PhysicalCard card) {
    }
}
