package com.gempukku.swccgo.common;

/**
 * Represents the phases of a turn in a SWCCG game.
 */
public enum Phase {
    PLAY_STARTING_CARDS("Play starting cards"),
    ACTIVATE("Activate"),
    CONTROL("Control"),
    DEPLOY("Deploy"),
    BATTLE("Battle"),
    MOVE("Move"),
    DRAW("Draw"),
    END_OF_TURN("End of turn"),
    BETWEEN_TURNS("Between turns");

    private String _humanReadable;

    Phase(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
