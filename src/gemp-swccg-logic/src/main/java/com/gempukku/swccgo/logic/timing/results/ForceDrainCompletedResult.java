package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a Force drain when the Force drain is completed.
 */
public class ForceDrainCompletedResult extends EffectResult {
    private PhysicalCard _location;

    /**
     * Creates an effect result that is triggered during a Force drain when the Force drain is completed.
     * @param action the action performing this effect result
     * @param location the Force drain location
     */
    public ForceDrainCompletedResult(Action action, PhysicalCard location) {
        super(Type.FORCE_DRAIN_COMPLETED, action.getPerformingPlayer());
        _location = location;
    }

    /**
     * Gets the Force drain location.
     * @return the location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just Force drained at " + GameUtils.getCardLink(_location);
    }
}
