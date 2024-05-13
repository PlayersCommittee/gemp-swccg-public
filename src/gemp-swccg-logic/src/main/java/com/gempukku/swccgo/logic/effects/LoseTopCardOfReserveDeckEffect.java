package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to lose the top card of Reserve Deck.
 */
public class LoseTopCardOfReserveDeckEffect extends LoseTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the specified player to lose the top card of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public LoseTopCardOfReserveDeckEffect(Action action, String playerId) {
        super(action, playerId, Zone.RESERVE_DECK);
    }
}
