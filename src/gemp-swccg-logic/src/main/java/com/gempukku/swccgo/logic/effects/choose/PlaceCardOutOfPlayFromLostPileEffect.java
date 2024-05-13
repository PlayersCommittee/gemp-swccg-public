package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the player to search Lost Pile for a card and place it out of play.
 */
public class PlaceCardOutOfPlayFromLostPileEffect extends PlaceCardsOutOfPlayFromLostPileEffect {

    /**
     * Creates an effect that causes the player to search Lost Pile for a card and place it out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceCardOutOfPlayFromLostPileEffect(Action action, String playerId, String cardPileOwner, boolean reshuffle) {
        super(action, playerId, cardPileOwner, 1, 1, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Lost Pile for a card accepted by the specified filter and place
     * it out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceCardOutOfPlayFromLostPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters, boolean reshuffle) {
        super(action, playerId, cardPileOwner, 1, 1, filters, reshuffle);
    }

    /**
     * A callback method for the cards placed out of play.
     * @param cards the cards placed out of play
     */
    @Override
    protected final void cardsPlacedOutOfPlay(Collection<PhysicalCard> cards) {
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
