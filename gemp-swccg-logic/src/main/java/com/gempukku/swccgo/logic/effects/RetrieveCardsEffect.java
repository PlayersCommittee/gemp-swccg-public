package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to retrieve specified cards.
 */
public class RetrieveCardsEffect extends ForceRetrievalEffect {

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     */
    public RetrieveCardsEffect(Action action, String playerId, float amount) {
        this(action, playerId, amount, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param cardFilter the card filter
     */
    public RetrieveCardsEffect(Action action, String playerId, float amount, Filterable cardFilter) {
        this(action, playerId, amount, false, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param upToAmount true if retrieval is up to specified amount, otherwise false
     * @param cardFilter the card filter
     */
    public RetrieveCardsEffect(Action action, String playerId, float amount, boolean upToAmount, Filterable cardFilter) {
        super(action, playerId, Zone.USED_PILE, amount, false, true, upToAmount, false, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param amount the amount of Force to retrieve
     * @param upToAmount true if retrieval is up to specified amount, otherwise false
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param cardFilter the card filter
     */
    protected RetrieveCardsEffect(Action action, String playerId, float amount, boolean upToAmount, boolean topmost, Filterable cardFilter) {
        super(action, playerId, Zone.USED_PILE, amount, false, true, upToAmount, topmost, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to retrieve specified number of cards accepted by the card filter.
     * @param action the action performing this effect
     * @param playerId the player to retrieve Force
     * @param retrieveToZone the zone to retrieve to
     * @param amount the amount of Force to retrieve
     * @param cardFilter the card filter
     */
    protected RetrieveCardsEffect(Action action, String playerId, Zone retrieveToZone, float amount, Filterable cardFilter) {
        super(action, playerId, retrieveToZone, amount, false, true, false, false, cardFilter);
    }
}
