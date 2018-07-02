package com.gempukku.swccgo.common;

/**
 * Represents the types of targeting used to target cards.
 */
public enum TargetingType {
    TARGET_CARDS("Target cards"),
    TARGET_CARDS_AT_SAME_LOCATION("Target cards at same location");

    private String _humanReadable;

    TargetingType(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
