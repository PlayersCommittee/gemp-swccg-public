package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to put cards from hand on Used Pile.
 */
public class PutCardsFromHandOnUsedPileEffect extends PutCardsFromHandInCardPileEffect {

    /**
     * Creates an effect that causes the player to put all cards from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    protected PutCardsFromHandOnUsedPileEffect(Action action, String playerId) {
        super(action, playerId, Zone.USED_PILE, false);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, Filterable filters, boolean hidden) {
        super(action, playerId, Zone.USED_PILE, false, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on the specified
     * player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters, boolean hidden) {
        super(action, playerId, Zone.USED_PILE, cardPileOwner, false, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put specified cards from hand on the specified player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param cards the cards
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, String cardPileOwner, Collection<PhysicalCard> cards, boolean hidden) {
        super(action, playerId, Zone.USED_PILE, cardPileOwner, false, cards, hidden);
    }

    /**
     * Creates an effect that causes the player to put cards from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, int minimum, int maximum) {
       super(action, playerId, minimum, maximum, Zone.USED_PILE, playerId, false);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, int minimum, int maximum, Filterable filters, boolean hidden) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, playerId, false, filters, hidden);
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from hand on the specified
     * player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardsFromHandOnUsedPileEffect(Action action, String playerId, String cardPileOwner, int minimum, int maximum, Filterable filters, boolean hidden) {
        super(action, playerId, minimum, maximum, Zone.USED_PILE, cardPileOwner, false, filters, hidden);
    }
}
