package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to put stacked cards into the Force Pile.
 */
public class PutStackedCardsInForcePileEffect extends PutStackedCardsInCardPileEffect {

    /**
     * Creates an effect that causes the player to put specified stacked cards in the specified card pile.
     *
     * @param action       the action performing this effect
     * @param playerId     the player
     * @param stackedCards the stacked cards
     * @param hidden       true if cards are not revealed when put in pile, otherwise false
     */
    public PutStackedCardsInForcePileEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, boolean hidden) {
        this(action, playerId, stackedCards, false, hidden);
    }

    /**
     * Creates an effect that causes the player to put specified stacked cards in the specified card pile.
     *
     * @param action       the action performing this effect
     * @param playerId     the player
     * @param stackedCards the stacked cards
     * @param bottom       true if cards are to be put on the bottom of the card pile, otherwise false
     * @param hidden       true if cards are not revealed when put in pile, otherwise false
     */
    protected PutStackedCardsInForcePileEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, boolean bottom, boolean hidden) {
        super(action, playerId, stackedCards, Zone.FORCE_PILE, bottom, hidden);
    }
}
