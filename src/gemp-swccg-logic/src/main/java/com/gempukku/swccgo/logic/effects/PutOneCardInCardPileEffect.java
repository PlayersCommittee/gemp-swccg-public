package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put the specified card in the specified card pile.
 */
abstract class PutOneCardInCardPileEffect extends AbstractSuccessfulEffect {
    protected PhysicalCard _card;
    protected Zone _zone;
    protected String _cardPileOwner;
    protected boolean _bottom;
    protected String _msgText;

    /**
     * Creates an effect that causes the specified card to be put in the specified card pile.
     * @param action the action performing this effect
     * @param card the card
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param msgText the message to send
     */
    protected PutOneCardInCardPileEffect(Action action, PhysicalCard card, Zone cardPile, String cardPileOwner, boolean bottom, String msgText) {
        super(action);
        _card = card;
        _zone = cardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _msgText = msgText;
    }

    protected abstract void afterCardPutInCardPile();

    protected abstract void scheduleNextStep();
}
