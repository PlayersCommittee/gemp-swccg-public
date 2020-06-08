package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and play a card that is stacked on a specified card.
 */
public class PlayStackedCardEffect extends DeployStackedCardEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and play a card that is stacked
     * on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     */
    public PlayStackedCardEffect(Action action, PhysicalCard stackedOn) {
        super(action, stackedOn, Filters.any, true, true);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play the specified card
     * that is stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param cardToPlay the card to play
     */
    public PlayStackedCardEffect(Action action, PhysicalCard stackedOn, PhysicalCard cardToPlay) {
        super(action, stackedOn, cardToPlay, Filters.any, true);
    }
}
