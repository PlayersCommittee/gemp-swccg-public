package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy cards from Reserve Deck to a specified target.
 */
public class DeployCardsToTargetFromReserveDeckEffect extends DeployCardsFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to targets accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToTargetFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, Filter targetFilter, boolean reshuffle) {
        this(action, cardFilter, minimum, maximum, targetFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to targets accepted by the target filter.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToTargetFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, action.getPerformingPlayer(), Zone.RESERVE_DECK, cardFilter, minimum, maximum, targetFilter, null, null, forFree, 0, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck to targets accepted by the target filter.
     * @param action the action performing this effect
     * @param performingPlayerId the player to deploy cards
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param targetFilter the target filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsToTargetFromReserveDeckEffect(Action action, String performingPlayerId, Filter cardFilter, int minimum, int maximum, Filter targetFilter, boolean forFree, boolean reshuffle) {
        super(action, performingPlayerId, Zone.RESERVE_DECK, cardFilter, minimum, maximum, targetFilter, null, null, forFree, 0, reshuffle);
    }
}
