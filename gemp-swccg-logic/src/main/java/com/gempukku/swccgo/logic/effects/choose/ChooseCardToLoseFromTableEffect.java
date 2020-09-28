package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose a card on table to be lost.
 */
public class ChooseCardToLoseFromTableEffect extends ChooseCardsToLoseFromTableEffect {

    /**
     * Creates an effect that causes the specified player to choose a card on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     */
    public ChooseCardToLoseFromTableEffect(Action action, String playerId, Filterable cardFilter) {
        this(action, playerId, null, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to choose a card on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param allCardsSituation true if treated as an all cards situation, otherwise false
     * @param cardFilter the card filter
     */
    public ChooseCardToLoseFromTableEffect(Action action, String playerId, boolean allCardsSituation, Filterable cardFilter) {
        this(action, playerId, null, allCardsSituation, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to choose cards on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param additionalTargetingReason the additional targeting reason (in addition to "to be lost")
     * @param cardFilter the card filter
     */
    public ChooseCardToLoseFromTableEffect(Action action, String playerId, TargetingReason additionalTargetingReason, Filterable cardFilter) {
        this(action, playerId, additionalTargetingReason, false, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to choose cards on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param additionalTargetingReason the additional targeting reason (in addition to "to be lost")
     * @param allCardsSituation true if treated as an all cards situation, otherwise false
     * @param cardFilter the card filter
     */
    public ChooseCardToLoseFromTableEffect(Action action, String playerId, TargetingReason additionalTargetingReason, boolean allCardsSituation, Filterable cardFilter) {
        super(action, playerId, additionalTargetingReason, 1, 1, allCardsSituation, cardFilter);
    }
}
