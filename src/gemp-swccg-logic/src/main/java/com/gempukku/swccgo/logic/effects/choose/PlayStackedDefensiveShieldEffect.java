package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and play a Defensive Shield that is stacked on a specified card.
 */
public class PlayStackedDefensiveShieldEffect extends DeployStackedCardEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and play a Defensive Shield that is stacked
     * on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     */
    public PlayStackedDefensiveShieldEffect(Action action, PhysicalCard stackedOn) {
        super(action, stackedOn, Filters.Defensive_Shield, true, true);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and play the specified Defensive Shield
     * that is stacked on the specified card.
     * @param action the action performing this effect
     * @param stackedOn the card that the card to deploy is stacked on
     * @param defensiveShield the defensive shield to play
     */
    public PlayStackedDefensiveShieldEffect(Action action, PhysicalCard stackedOn, PhysicalCard defensiveShield) {
        super(action, stackedOn, defensiveShield, Filters.any, true);
    }
}
