package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from Reserve Deck simultaneously
 * with a specified card.
 */
public class DeployCardFromReserveDeckSimultaneouslyWithCardEffect extends DeployCardFromPileSimultaneouslyWithCardEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck simultaneously with the specified card.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, Filter cardFilter, boolean reshuffle) {
        super(action, cardToDeployWith, Zone.RESERVE_DECK, action.getPerformingPlayer(), cardFilter, false, 0, null, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck simultaneously with the specified card.
     * @param action the action performing this effect
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardFilter the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required for each
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromReserveDeckSimultaneouslyWithCardEffect(Action action, PhysicalCard cardToDeployWith, Filter cardFilter, float changeInCost, boolean reshuffle) {
        super(action, cardToDeployWith, Zone.RESERVE_DECK, action.getPerformingPlayer(), cardFilter, false, changeInCost, null, reshuffle);
    }
}
