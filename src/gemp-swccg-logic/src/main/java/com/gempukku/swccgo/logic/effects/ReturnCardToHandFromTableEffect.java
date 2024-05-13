package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified card on table to be returned to hand.
 */
public class ReturnCardToHandFromTableEffect extends ReturnCardsToHandFromTableEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that causes the specified card on table to be returned to hand.
     * @param action the action performing this effect
     * @param card the card
     */
    public ReturnCardToHandFromTableEffect(Action action, PhysicalCard card) {
        this(action, card, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes the specified card on table to be returned to hand.
     * @param action the action performing this effect
     * @param card the card
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public ReturnCardToHandFromTableEffect(Action action, PhysicalCard card, Zone attachedCardsGoToZone) {
        this(action, card, attachedCardsGoToZone, attachedCardsGoToZone);
    }

    /**
     * Creates an effect that causes the specified card on table to be returned to hand.
     * @param action the action performing this effect
     * @param card the card
     * @param playersAttachedCardsGoToZone the zone that any of player's attached cards go to (instead of Lost Pile)
     * @param opponentsAttachedCardsGoToZone the zone that any of opponent's attached cards go to (instead of Lost Pile)
     */
    public ReturnCardToHandFromTableEffect(Action action, PhysicalCard card, Zone playersAttachedCardsGoToZone, Zone opponentsAttachedCardsGoToZone) {
        super(action, Collections.singleton(card), playersAttachedCardsGoToZone, opponentsAttachedCardsGoToZone);
        _card = card;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Return " + GameUtils.getFullName(_card) + " to hand";
    }
}
