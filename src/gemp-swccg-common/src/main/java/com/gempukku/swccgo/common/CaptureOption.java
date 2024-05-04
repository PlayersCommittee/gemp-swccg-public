package com.gempukku.swccgo.common;

/**
 * Represents the options for when a character is captured.
 */
public enum CaptureOption {
    ESCAPE("Escape"),
    IMPRISONMENT("Imprisonment"),
    SEIZE("Seize"),
    LEAVE_UNATTENDED("Leave Unattended");

    private String _humanReadable;

    CaptureOption(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
