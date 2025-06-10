package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled if the specified player did not deploy an Objective.
 */
public class DidNotDeployObjectiveCondition implements Condition {
    private String _playerId;

    /**
     * Creates a condition that is fulfilled if the specified player did not deploy an Objective.
     * @param playerId the player
     */
    public DidNotDeployObjectiveCondition(String playerId) {
        _playerId = playerId;
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return gameState.getObjectivePlayed(_playerId) == null;
    }
}
