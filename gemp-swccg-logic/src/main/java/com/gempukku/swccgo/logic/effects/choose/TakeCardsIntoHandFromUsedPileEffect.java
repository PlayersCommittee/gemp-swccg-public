package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the player to search Used Pile for cards and take them into hand (or take specific cards from
 * Used Pile into hand).
 */
public class TakeCardsIntoHandFromUsedPileEffect extends TakeCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to search Used Pile for cards and take them into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardsIntoHandFromUsedPileEffect(Action action, String playerId, int minimum, int maximum, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, playerId, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Used Pile for cards and take them into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param cardPileOwner the card pile owner
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardsIntoHandFromUsedPileEffect(Action action, String playerId, int minimum, int maximum, String cardPileOwner, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, cardPileOwner, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Used Pile for cards accepted by the specified filter and take
     * them into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public TakeCardsIntoHandFromUsedPileEffect(Action action, String playerId, int minimum, int maximum, Filter filters, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, playerId, false, filters, reshuffle);
    }

    /**
     * Creates an effect that causes the player to search Used Pile for cards accepted by the specified filter and take
     * them into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardsIntoHandFromUsedPileEffect(Action action, String playerId, int minimum, int maximum, String cardPileOwner, Filter filters, boolean reshuffle) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, cardPileOwner, false, filters, reshuffle);
    }

    /**
     * Creates an effect that causes the player to take the specific cards into hand from Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cards the cards
     * @param hidden true if cards are not revealed, otherwise false
     */
    protected TakeCardsIntoHandFromUsedPileEffect(Action action, String playerId, Collection<PhysicalCard> cards, boolean hidden) {
        super(action, playerId, Zone.USED_PILE, playerId, cards, hidden, false);
    }

    /**
     * A callback method for the cards taken into hand.
     * @param cards the cards taken into hand
     */
    @Override
    protected void cardsTakenIntoHand(Collection<PhysicalCard> cards) {
    }
}
