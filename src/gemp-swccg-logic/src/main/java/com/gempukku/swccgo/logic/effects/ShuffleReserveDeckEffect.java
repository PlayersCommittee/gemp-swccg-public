package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that shuffles the specified Reserve Deck
 */
public class ShuffleReserveDeckEffect extends ShufflePileEffect {

    /**
     * Creates an effect that shuffles the specified Reserve Deck.
     * @param action the action performing this effect
     */
    public ShuffleReserveDeckEffect(Action action) {
        this(action, action.getPerformingPlayer());
    }

    /**
     * Creates an effect that shuffles the specified Reserve Deck.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the Reserve Deck
     */
    public ShuffleReserveDeckEffect(Action action, String cardPileOwner) {
        super(action, action.getActionSource(), action.getPerformingPlayer(), cardPileOwner, Zone.RESERVE_DECK, false);
    }

    /**
     * Creates an effect that shuffles the specified Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player to do the shuffling
     * @param cardPileOwner the owner of the Reserve Deck
     */
    public ShuffleReserveDeckEffect(Action action, String playerId, String cardPileOwner) {
        super(action, action.getActionSource(), playerId, cardPileOwner, Zone.RESERVE_DECK, false);
    }
}
