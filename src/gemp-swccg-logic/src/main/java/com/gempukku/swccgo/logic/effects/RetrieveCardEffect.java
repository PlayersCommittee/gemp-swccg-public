package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * An effect that causes the specified player to retrieve a specified card.
 */
public class RetrieveCardEffect extends RetrieveCardsEffect {

    /**
     * Creates an effect that causes the specified player to retrieve a card.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     */
    public RetrieveCardEffect(Action action, String playerId) {
        this(action, playerId, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a card accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param cardFilter the card filter
     */
    public RetrieveCardEffect(Action action, String playerId, Filterable cardFilter) {
        super(action, playerId, 1, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a card accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param cardFilter the card filter
     */
    public RetrieveCardEffect(Action action, String playerId, boolean topmost, Filterable cardFilter) {
        super(action, playerId, 1, false, topmost, cardFilter);
    }

    /**
     * A callback method for the cards retrieved.
     * @param retrievedCards the cards retrieved
     */
    @Override
    protected final void cardsRetrieved(Collection<PhysicalCard> retrievedCards) {
        if (retrievedCards.size() == 1) {
            cardRetrieved(retrievedCards.iterator().next());
        }
    }

    /**
     * A callback method for the card retrieved.
     * @param retrievedCard the card retrieved
     */
    protected void cardRetrieved(PhysicalCard retrievedCard) {
    }
}
