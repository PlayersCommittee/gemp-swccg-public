package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to look at the cards in the specified Lost Pile.
 */
public class LookAtLostPileEffect extends LookAtCardPileEffect {

    /**
     * Creates an effect that causes the specified player to look at the cards in the specified Lost Pile.
     * @param action the action performing this effect
     * @param playerId the performing player
     * @param cardPileOwner the card pile owner
     */
    public LookAtLostPileEffect(Action action, String playerId, String cardPileOwner) {
        super(action, playerId, cardPileOwner, Zone.LOST_PILE);
    }
}
