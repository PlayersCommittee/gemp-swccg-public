package com.gempukku.swccgo.ai.models.rando.strategy;

/**
 * High-level deployment strategy for a deploy phase.
 *
 * Ported from Python deploy_planner.py DeployStrategy enum.
 */
public enum DeployStrategy {
    /** Don't deploy - save resources for later */
    HOLD_BACK("hold_back"),

    /** Deploy to new location to establish control */
    ESTABLISH("establish"),

    /** Strengthen a weak position */
    REINFORCE("reinforce"),

    /** Crush opponent at a location */
    OVERWHELM("overwhelm"),

    /** Deploy location cards first */
    DEPLOY_LOCATIONS("locations"),

    /** Comprehensive plan with locations and characters */
    COMPREHENSIVE("comprehensive");

    private final String value;

    DeployStrategy(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
