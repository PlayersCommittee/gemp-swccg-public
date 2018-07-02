package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from Lost Pile simultaneously with
 * a specified card.
 */
public class DeployCardFromLostPileSimultaneouslyWithCardEffect extends DeployCardFromPileSimultaneouslyWithCardEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card from Lost Pile simultaneously
     * with a specified card.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardPileOwner the card pile owner
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromLostPileSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, String cardPileOwner, Filter cardFilter, boolean reshuffle) {
        super(action, cardToDeployWith, Zone.LOST_PILE, cardPileOwner, cardFilter, false, 0, null, reshuffle);
    }
}
