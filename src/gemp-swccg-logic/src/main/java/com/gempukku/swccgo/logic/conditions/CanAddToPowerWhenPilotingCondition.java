package com.gempukku.swccgo.logic.conditions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A condition that is fulfilled when the specified pilot is allowed to add power to a card that pilot is piloting.
 */
public class CanAddToPowerWhenPilotingCondition implements Condition {
    private int _permPilotCardId;

    /**
     * Creates a condition that is fulfilled when the specified pilot is allowed to add power to a card that pilot is piloting.
     * @param pilot the pilot
     */
    public CanAddToPowerWhenPilotingCondition(PhysicalCard pilot) {
        _permPilotCardId = pilot.getPermanentCardId();
    }

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        PhysicalCard pilot = gameState.findCardByPermanentId(_permPilotCardId);

        return !modifiersQuerying.cannotAddToPowerOfPilotedBy(gameState, pilot);
    }
}
