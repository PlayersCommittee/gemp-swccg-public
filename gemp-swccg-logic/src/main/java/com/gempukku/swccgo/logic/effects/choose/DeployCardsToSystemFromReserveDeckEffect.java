package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy cards to a system from Reserve Deck.
 */
public class DeployCardsToSystemFromReserveDeckEffect extends DeployCardsFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * to a system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param system the system name
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToSystemFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, String system, boolean reshuffle) {
        this(action, cardFilter, minimum, maximum, system, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * to a system from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param system the system name
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToSystemFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, String system, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, minimum, maximum, null, system, null, forFree, 0, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * to a system from Reserve Deck.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param system the system name
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToSystemFromReserveDeckEffect(Action action, String performingPlayerId, Filter cardFilter, int minimum, int maximum, String system, Filter specialLocationConditions, boolean forFree, boolean reshuffle) {
        super(action, performingPlayerId, Zone.RESERVE_DECK, cardFilter, minimum, maximum, null, system, specialLocationConditions, forFree, 0, reshuffle);
    }
}
