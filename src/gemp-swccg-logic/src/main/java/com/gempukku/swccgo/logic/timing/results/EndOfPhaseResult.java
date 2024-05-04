package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * The effect result that is emitted at the end of a phase.
 */
public class EndOfPhaseResult extends EffectResult {
    private Phase _phase;

    /**
     * Needed to generate snapshot.
     */
    public EndOfPhaseResult() {
    }

    public void generateSnapshot(EffectResult selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        EndOfPhaseResult snapshot = (EndOfPhaseResult) selfSnapshot;

        // Set each field
        snapshot._phase = _phase;
    }

    /**
     * Creates an effect result that is emitted at the start of a phase.
     * @param phase the phase
     */
    public EndOfPhaseResult(Phase phase) {
        super(Type.END_OF_PHASE, null);
        _phase = phase;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "End of " + _phase.getHumanReadable() + " phase";
    }
}
