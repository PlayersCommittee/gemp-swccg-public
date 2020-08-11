package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to put stacked cards into the Used Pile.
 */
public class PutStackedCardInForcePileEffect extends PutStackedCardsInForcePileEffect {

    /**
     * Creates an effect that causes the player to put specified stacked cards in the specified card pile.
     *
     * @param action   the action performing this effect
     * @param playerId the player
     * @param hidden   true if cards are not revealed when put in pile, otherwise false
     */
    public PutStackedCardInForcePileEffect(Action action, String playerId, PhysicalCard stackedCard, boolean hidden) {
        super(action, playerId, Collections.singletonList(stackedCard), hidden);
    }
}
