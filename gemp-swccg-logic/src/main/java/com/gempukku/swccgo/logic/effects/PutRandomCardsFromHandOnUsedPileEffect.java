package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put random cards from hand on Used Pile.
 */
public class PutRandomCardsFromHandOnUsedPileEffect extends PutRandomCardsFromHandInCardPileEffect {

    /**
     * Creates an effect that causes the player to put cards at random from specified player's hand on that player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     * @param downToSize the minimum number of cards the hand can get down to from this effect
     */
    public PutRandomCardsFromHandOnUsedPileEffect(Action action, String playerId, String handOwner, int downToSize) {
        super(action, playerId, handOwner, downToSize, Integer.MAX_VALUE, Zone.USED_PILE, false);
    }

    /**
     * Creates an effect that causes the player to put cards at random from specified player's hand on that player's Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param handOwner the owner of the hand
     * @param downToSize the minimum number of cards the hand can get down to from this effect
     * @param amount the number of cards to put on card pile (without going below downToSize)
     */
    public PutRandomCardsFromHandOnUsedPileEffect(Action action, String playerId, String handOwner, int downToSize, int amount) {
        super(action, playerId, handOwner, downToSize, amount, Zone.USED_PILE, false);
    }
}
