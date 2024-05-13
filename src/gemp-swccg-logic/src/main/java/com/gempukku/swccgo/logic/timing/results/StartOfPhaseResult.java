package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted at the start of a phase.
 */
public class StartOfPhaseResult extends EffectResult {
    private Phase _phase;

    /**
     * Creates an effect result that is emitted at the start of a phase.
     * @param phase the phase
     */
    public StartOfPhaseResult(Phase phase) {
        super(Type.START_OF_PHASE, null);
        _phase = phase;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Start of " + _phase.getHumanReadable() + " phase";
    }
}
