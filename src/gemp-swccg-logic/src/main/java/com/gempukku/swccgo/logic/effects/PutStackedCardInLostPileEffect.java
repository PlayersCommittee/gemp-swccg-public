package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to put a stacked card into the Lost Pile.
 */
public class PutStackedCardInLostPileEffect extends PutStackedCardsInLostPileEffect {

    /**
     * Creates an effect that causes the player to put specified stacked cards in the Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCard the stacked card
     * @param hidden true if cards are not revealed when put in pile, otherwise false
     */
    public PutStackedCardInLostPileEffect(Action action, String playerId, PhysicalCard stackedCard, boolean hidden) {
        super(action, playerId, Collections.singletonList(stackedCard), hidden);
    }
}
