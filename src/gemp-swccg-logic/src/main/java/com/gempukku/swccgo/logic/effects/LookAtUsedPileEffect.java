package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to look at the cards in the specified Used Pile.
 */
public class LookAtUsedPileEffect extends LookAtCardPileEffect {

    /**
     * Creates an effect that causes the specified player to look at the cards in the specified Used Pile.
     * @param action the action performing this effect
     * @param playerId the performing player
     * @param cardPileOwner the card pile owner
     */
    public LookAtUsedPileEffect(Action action, String playerId, String cardPileOwner) {
        super(action, playerId, cardPileOwner, Zone.USED_PILE);
    }
}
