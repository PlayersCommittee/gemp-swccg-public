package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from outside of the game simultaneously with
 * a specified card.
 */
public class DeployCardFromOutsideOfGameSimultaneouslyWithCardEffect extends DeployCardFromPileSimultaneouslyWithCardEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card from Lost Pile simultaneously
     * with a specified card.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardPileOwner the card pile owner
     * @param cardFilter the card filter
     */
    public DeployCardFromOutsideOfGameSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, String cardPileOwner, Filter cardFilter) {
        super(action, cardToDeployWith, Zone.OUTSIDE_OF_DECK, cardPileOwner, cardFilter, false, 0, null, false);
    }
}
