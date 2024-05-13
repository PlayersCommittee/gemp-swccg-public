package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to lose the specific card from the top of Reserve Deck.
 */
public class LoseCardFromTopOfReserveDeckEffect extends LoseTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the specified player to lose the specific card from the top of Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardToLose the card to lose
     */
    public LoseCardFromTopOfReserveDeckEffect(Action action, String playerId, PhysicalCard cardToLose) {
        super(action, playerId, Zone.RESERVE_DECK, cardToLose);
    }
}
