package com.gempukku.swccgo.ai.models.rando.strategy;

/**
 * A deployment plan with an associated score for comparison.
 *
 * Ported from Python plan scoring system.
 */
public class ScoredPlan implements Comparable<ScoredPlan> {
    public final DeploymentPlan plan;
    public final float score;
    public final String domain;  // "ground", "space", "combined", etc.

    public ScoredPlan(DeploymentPlan plan, float score, String domain) {
        this.plan = plan;
        this.score = score;
        this.domain = domain;
    }

    @Override
    public int compareTo(ScoredPlan other) {
        // Higher score is better
        return Float.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("ScoredPlan[%s: %.1f, %d cards]",
            domain, score, plan != null ? plan.getInstructions().size() : 0);
    }
}
