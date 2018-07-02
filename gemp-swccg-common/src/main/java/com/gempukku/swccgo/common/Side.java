package com.gempukku.swccgo.common;

/**
 * Represents the sides of the Force in SWCCG.
 */
public enum Side implements Filterable {
    LIGHT("Light"),
    DARK("Dark");

    private String _humanReadable;

    Side(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
