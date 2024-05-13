package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to put stacked cards into the Lost Pile.
 */
public class PutStackedCardsInLostPileEffect extends PutStackedCardsInCardPileEffect {

    /**
     * Creates an effect that causes the player to put cards stacked on the specified card in the Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put in card pile
     * @param maximum the maximum number of cards to put in card pile
     * @param stackedOn the card that the stacked cards are stacked on
     */
    public PutStackedCardsInLostPileEffect(Action action, String playerId, int minimum, int maximum, PhysicalCard stackedOn) {
        super(action, playerId, minimum, maximum, Zone.LOST_PILE, false, stackedOn);
    }

    /**
     * Creates an effect that causes the player to put specified stacked cards in the Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedCards the stacked cards
     * @param hidden true if cards are not revealed when put in pile, otherwise false
     */
    public PutStackedCardsInLostPileEffect(Action action, String playerId, Collection<PhysicalCard> stackedCards, boolean hidden) {
        super(action, playerId, stackedCards, Zone.LOST_PILE, false, hidden);
    }
}
