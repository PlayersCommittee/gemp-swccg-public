package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy cards from Reserve Deck.
 */
public class DeployCardsFromReserveDeckEffect extends DeployCardsFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, boolean reshuffle) {
        this(action, cardFilter, minimum, maximum, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsFromReserveDeckEffect(Action action, Filter cardFilter, int minimum, int maximum, boolean forFree, boolean reshuffle) {
        this(action, cardFilter, null, minimum, maximum, forFree, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsFromReserveDeckEffect(Action action, Filter cardFilter, Filter specialLocationConditions, int minimum, int maximum, boolean reshuffle) {
        this(action, cardFilter, specialLocationConditions, minimum, maximum, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy cards accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param minimum the minimum number of cards to deploy
     * @param maximum the maximum number of cards to deploy
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardsFromReserveDeckEffect(Action action, Filter cardFilter, Filter specialLocationConditions, int minimum, int maximum, boolean forFree, boolean reshuffle) {
        super(action, Zone.RESERVE_DECK, cardFilter, minimum, maximum, null, specialLocationConditions, forFree, 0, reshuffle);
    }
}
