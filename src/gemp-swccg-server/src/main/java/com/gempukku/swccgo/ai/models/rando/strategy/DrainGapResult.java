package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.common.AiBoardAnalyzer;
import java.util.List;
import java.util.ArrayList;

/**
 * Result of force drain gap calculation.
 *
 * Ported from Python _calculate_force_drain_gap return value.
 */
public class DrainGapResult {
    /** Total icons opponent drains from us per turn */
    public final int theirDrain;

    /** Total icons we drain from them per turn */
    public final int ourDrain;

    /** Drain gap: ourDrain - theirDrain (negative = we're losing) */
    public final int drainGap;

    /** Locations where opponent drains us with low enemy power (contestable) */
    public final List<AiBoardAnalyzer.LocationAnalysis> bleedLocations;

    public DrainGapResult(int theirDrain, int ourDrain, int drainGap,
                          List<AiBoardAnalyzer.LocationAnalysis> bleedLocations) {
        this.theirDrain = theirDrain;
        this.ourDrain = ourDrain;
        this.drainGap = drainGap;
        this.bleedLocations = bleedLocations != null ? bleedLocations : new ArrayList<>();
    }

    public boolean isLosing() {
        return drainGap < 0;
    }

    public boolean hasBleedLocations() {
        return !bleedLocations.isEmpty();
    }
}
