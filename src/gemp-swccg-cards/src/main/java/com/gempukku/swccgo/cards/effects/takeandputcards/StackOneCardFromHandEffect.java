package com.gempukku.swccgo.cards.effects.takeandputcards;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put the specified card from hand in the specified card pile.
 */
abstract class StackOneCardFromHandEffect extends StackOneCardEffect {

    /**
     * Creates an effect that causes the specified card from hand to be stacked on the specified card.
     * @param action the action performing this effect
     * @param card the card
     * @param stackOn the card to stack on
     * @param faceDown true if card is to be stacked face down, otherwise false
     * @param msgText the message to send
     */
    protected StackOneCardFromHandEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown, String msgText) {
        super(action, card, stackOn, faceDown, msgText);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _card.getZone() == Zone.HAND;
    }
}
