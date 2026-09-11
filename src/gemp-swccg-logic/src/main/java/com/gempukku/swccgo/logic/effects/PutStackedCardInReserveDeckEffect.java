package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to put a stacked card onto the Reserve Deck.
 */
public class PutStackedCardInReserveDeckEffect extends PutStackedCardsInReserveDeckEffect {

    /**
     * Creates an effect that causes the player to put the specified stacked card on the Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCard the stacked card
     * @param hidden true if card is not revealed when put in pile, otherwise false
     */
    public PutStackedCardInReserveDeckEffect(Action action, String playerId, PhysicalCard stackedCard, boolean hidden) {
        super(action, playerId, Collections.singletonList(stackedCard), hidden);
    }
}
