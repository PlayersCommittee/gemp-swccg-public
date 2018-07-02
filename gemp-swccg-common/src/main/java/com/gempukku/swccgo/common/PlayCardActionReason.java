package com.gempukku.swccgo.common;

/**
 * Represents the reasons a card can be played. In SWCCG, cards can be canceled
 * if they are played for a specified reasons (that does not involve explicitly
 * targeting a card for a specific reason first). This enum is to identify those
 * reasons that other cards can look for.
 */
public enum PlayCardActionReason {
    ATTEMPTING_TO_PLACE_A_CARD_OUT_OF_PLAY("attempting to place a card out of play");

    private String _humanReadable;

    PlayCardActionReason(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
