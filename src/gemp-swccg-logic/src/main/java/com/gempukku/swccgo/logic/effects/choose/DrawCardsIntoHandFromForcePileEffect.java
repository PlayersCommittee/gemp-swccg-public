package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect to draw the top cards into hand from Force Pile.
 */
public class DrawCardsIntoHandFromForcePileEffect extends DrawCardsIntoHandFromPileEffect {

    /**
     * Creates an effect that causes the player to draw the top cards into hand from Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to draw into hand
     */
    public DrawCardsIntoHandFromForcePileEffect(Action action, String playerId, int amount) {
        this(action, playerId, amount, false);
    }

    /**
     * Creates an effect that causes the player to draw the top cards into hand from Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param amount the number of cards to draw into hand
     * @param isTakeIntoHand true if take into hand, false if draw into hand
     */
    protected DrawCardsIntoHandFromForcePileEffect(Action action, String playerId, int amount, boolean isTakeIntoHand) {
        super(action, playerId, amount, Zone.FORCE_PILE, false, isTakeIntoHand);
    }

    /**
     * A callback method for the cards drawn into hand.
     * @param cards the cards drawn into hand
     */
    @Override
    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
    }
}
