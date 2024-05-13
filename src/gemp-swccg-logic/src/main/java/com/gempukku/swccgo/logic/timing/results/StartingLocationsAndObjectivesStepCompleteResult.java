package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when the play starting locations and Objectives step is complete.
 */
public class StartingLocationsAndObjectivesStepCompleteResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when the play starting locations and Objectives step is complete.
     */
    public StartingLocationsAndObjectivesStepCompleteResult() {
        super(Type.STARTING_LOCATIONS_AND_OBJECTIVES_STEP_COMPLETE, null);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Deployment of starting locations and Objectives complete";
    }
}
