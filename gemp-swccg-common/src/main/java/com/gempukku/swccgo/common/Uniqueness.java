package com.gempukku.swccgo.common;

/**
 * Represents the uniqueness of a particular SWCCG card.
 */
public enum Uniqueness implements Filterable {
    UNRESTRICTED(Integer.MAX_VALUE, false, ""),
    UNIQUE(1, false, "•"),
    RESTRICTED_2(2, false, "••"),
    RESTRICTED_3(3, false, "•••"),
    DIAMOND_1(1, true, "◇"),
    DIAMOND_2(2, true, "◇◇"),
    DIAMOND_3(3, true, "◇◇◇");

    private int _value;
    private boolean _isPerSystem;
    private String _humanReadable;

    Uniqueness(int value, boolean isPerSystem, String humanReadable) {
        _value = value;
        _isPerSystem = isPerSystem;
        _humanReadable = humanReadable;
    }

    public int getValue() {
        return _value;
    }

    public boolean isPerSystem() {
        return _isPerSystem;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
