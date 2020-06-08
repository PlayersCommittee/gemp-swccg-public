package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw cards into hand from the bottom of Force Pile.
 */
public class DrawCardsIntoHandFromBottomOfForcePileEffect extends DrawCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to draw cards into hand from the bottom of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to draw into hand
     */
    public DrawCardsIntoHandFromBottomOfForcePileEffect(Action action, String playerId, int amount) {
        super(action, playerId, amount, Zone.FORCE_PILE, true, false);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cards the cards drawn into hand
     */
    @Override
    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
    }
}
