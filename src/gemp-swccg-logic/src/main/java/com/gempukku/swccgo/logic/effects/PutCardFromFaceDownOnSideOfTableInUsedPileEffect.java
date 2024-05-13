package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put the specified card from face down on side of table in Lost Pile.
 */
public class PutCardFromFaceDownOnSideOfTableInUsedPileEffect extends PutOneCardFromFaceDownOnSideOfTableInCardPileEffect {

    /**
     * Creates an effect that causes the player to put the specified card from face down on side of table in Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param card the card
     */
    public PutCardFromFaceDownOnSideOfTableInUsedPileEffect(Action action, String playerId, PhysicalCard card) {
        super(action, card, Zone.USED_PILE, false, ((playerId != null) ? playerId : card.getOwner()) + " puts a card in Used Pile");
    }
}