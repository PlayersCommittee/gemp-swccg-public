package com.gempukku.swccgo.cards.conditions;

import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;


/**
 * A condition that is fulfilled during an attempt to 'blow away' Death Star II.
 */
public class DuringAttemptToBlowAwayDeathStarII implements Condition {

    @Override
    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        EpicEventState epicEventState = gameState.getEpicEventState();
        return epicEventState != null && epicEventState.getEpicEventType() == EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II;
    }
}
