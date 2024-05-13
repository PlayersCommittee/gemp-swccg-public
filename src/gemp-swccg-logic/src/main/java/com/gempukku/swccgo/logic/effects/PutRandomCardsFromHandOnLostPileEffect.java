package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put random cards from hand on Lost Pile.
 */
public class PutRandomCardsFromHandOnLostPileEffect extends PutRandomCardsFromHandInCardPileEffect {

    /**
     * Creates an effect that causes the player to put cards at random from hand on Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to put on card pile
     */
    public PutRandomCardsFromHandOnLostPileEffect(Action action, String playerId, int amount) {
        super(action, playerId, playerId, 0, amount, Zone.LOST_PILE, false);
    }

    /**
     * Creates an effect that causes the player to put cards at random from hand on Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     * @param downToSize the minimum number of cards the hand can get down to from this effect
     */
    public PutRandomCardsFromHandOnLostPileEffect(Action action, String playerId, String handOwner, int downToSize) {
        super(action, playerId, handOwner, downToSize, Integer.MAX_VALUE, Zone.LOST_PILE, false);
    }

    /**
     * Creates an effect that causes the player to put cards at random from hand on Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     * @param downToSize the minimum number of cards the hand can get down to from this effect
     * @param amount the number of cards to put on card pile (without going below downToSize)
     */
    protected PutRandomCardsFromHandOnLostPileEffect(Action action, String playerId, String handOwner, int downToSize, int amount) {
        super(action, playerId, handOwner, downToSize, amount, Zone.LOST_PILE, false);
    }
}
