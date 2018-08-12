package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to retrieve specified cards into hand.
 */
public class RetrieveCardsIntoHandEffect extends ForceRetrievalEffect {

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards into hand.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     */
    public RetrieveCardsIntoHandEffect(Action action, String playerId, float amount) {
        this(action, playerId, amount, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve retrieve specified number of cards into hand.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param retrieveSpecificCards true if specific cards are searched for and retrieved, otherwise false
     */
    public RetrieveCardsIntoHandEffect(Action action, String playerId, float amount, boolean retrieveSpecificCards) {
        super(action, playerId, Zone.HAND, amount, false, false, false, true, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter
     * into hand.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param cardFilter the card filter
     */
    public RetrieveCardsIntoHandEffect(Action action, String playerId, float amount, Filterable cardFilter) {
        super(action, playerId, Zone.HAND, amount, false, true, false, false, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter
     * into hand.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param cardFilter the card filter
     */
    public RetrieveCardsIntoHandEffect(Action action, String playerId, float amount, boolean upToAmount, boolean topmost, Filterable cardFilter) {
        super(action, playerId, Zone.HAND, amount, false, true, upToAmount, topmost, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter
     * into hand.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param cardFilter the card filter
     */
    protected RetrieveCardsIntoHandEffect(Action action, String playerId, float amount, boolean topmost, Filterable cardFilter) {
        super(action, playerId, Zone.HAND, amount, false, true, false, topmost, cardFilter);
    }
}
