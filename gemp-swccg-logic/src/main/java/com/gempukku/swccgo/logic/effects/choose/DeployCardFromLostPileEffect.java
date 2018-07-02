package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from Lost Pile.
 */
public class DeployCardFromLostPileEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromLostPileEffect(Action action, Filter cardFilter, boolean reshuffle) {
        this(action, cardFilter, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Lost Pile.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromLostPileEffect(Action action, Filter cardFilter, boolean forFree, boolean reshuffle) {
        super(action, Zone.LOST_PILE, cardFilter, null, null, forFree, 0, null, false, reshuffle);
    }

    /**
     * Creates an effect that causes the player performing the action to deploy a specific card from Lost Pile.
     * @param action the action performing this effect
     * @param card the card
     * @param forFree true if deploying for free, otherwise false
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public DeployCardFromLostPileEffect(Action action, PhysicalCard card, boolean forFree, boolean reshuffle) {
        super(action, Zone.LOST_PILE, card, null, forFree, 0, false, reshuffle);
    }
}
