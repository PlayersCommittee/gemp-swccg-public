package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put the specified card from void in Reserve Deck.
 */
public class PutCardFromVoidInReserveDeckEffect extends PutOneCardFromVoidInCardPileEffect {

    /**
     * Creates an effect that causes the player to put the specified card from void in Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param card the card
     */
    public PutCardFromVoidInReserveDeckEffect(Action action, String playerId, PhysicalCard card) {
        super(action, card, Zone.RESERVE_DECK, false, ((playerId != null) ? playerId : card.getOwner()) + " puts " + GameUtils.getCardLink(card) + " in Reserve Deck");
    }
}