package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to put stacked cards onto the Reserve Deck.
 */
public class PutStackedCardsInReserveDeckEffect extends PutStackedCardsInCardPileEffect {

    /**
     * Creates an effect that causes the player to put specified stacked cards on the Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCards the stacked cards
     * @param hidden true if cards are not revealed when put in pile, otherwise false
     */
    public PutStackedCardsInReserveDeckEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, boolean hidden) {
        this(action, playerId, stackedCards, false, hidden);
    }

    /**
     * Creates an effect that causes the player to put specified stacked cards on the Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCards the stacked cards
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param hidden true if cards are not revealed when put in pile, otherwise false
     */
    protected PutStackedCardsInReserveDeckEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, boolean bottom, boolean hidden) {
        super(action, playerId, stackedCards, Zone.RESERVE_DECK, bottom, hidden);
    }
}
