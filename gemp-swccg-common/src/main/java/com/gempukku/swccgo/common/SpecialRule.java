package com.gempukku.swccgo.common;

/**
 * Represents special rules in effect.
 */
public enum SpecialRule {
    ASTEROID_RULES("'Asteroid Rules'"),
    BLUFF_RULES("'Bluff Rules'"),
    CAVE_RULES("'Cave Rules'"),
    DEJARIK_RULES("'Dejarik Rules'"),
    TRENCH_RULES("'Trench Rules'");

    private String _humanReadable;

    SpecialRule(String humanReadable) {
        _humanReadable = humanReadable;
    }

    @Override
    public String toString() {
        return _humanReadable;
    }
}
