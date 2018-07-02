package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to lose the specific card from the top of Used Pile.
 */
public class LoseCardFromTopOfUsedPileEffect extends LoseTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the specified player to lose the specific card from the top of Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardToLose the card to lose
     */
    public LoseCardFromTopOfUsedPileEffect(Action action, String playerId, PhysicalCard cardToLose) {
        super(action, playerId, Zone.USED_PILE, cardToLose);
    }
}
