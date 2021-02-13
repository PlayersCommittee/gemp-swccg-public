package com.gempukku.swccgo.common;

/**
 * Represents the options for when a character is released.
 */
public enum ReleaseOption implements Filterable {
    ESCAPE("Escape (to Used Pile)"),
    RALLY("Rally (to same site)"),
    LAUNCH("Launch (to appropriate system or sector)");

    private String _humanReadable;

    ReleaseOption(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
