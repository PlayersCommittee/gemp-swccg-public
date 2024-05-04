package com.gempukku.swccgo.common;

/**
 * Represents the reasons that the game ended for a player.
 */
public enum GameEndReason {
    LOSS__CONCEDED("Conceded"),
    LOSS__DECISION_TIMEOUT("Decision timeout"),
    LOSS__FORCE_DEPLETED("Life Force depleted"),
    LOSS__GAME_TIMEOUT("Ran out of time"),
    WIN__OPPONENT_CONCEDED("Opponent conceded"),
    WIN__OPPONENT_DECISION_TIMEOUT("Opponent decision timeout"),
    WIN__OPPONENT_FORCE_DEPLETED("Depleted opponent's Life Force"),
    WIN__OPPONENT_GAME_TIMEOUT("Opponent ran out of time"),
    UNKNOWN("Unknown");

    private String _humanReadable;

    GameEndReason(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public static GameEndReason getOpponentsReason(GameEndReason reason) {
        if (reason == LOSS__CONCEDED)
            return WIN__OPPONENT_CONCEDED;
        if (reason == LOSS__DECISION_TIMEOUT)
            return WIN__OPPONENT_DECISION_TIMEOUT;
        if (reason == LOSS__FORCE_DEPLETED)
            return WIN__OPPONENT_FORCE_DEPLETED;
        if (reason == LOSS__GAME_TIMEOUT)
            return WIN__OPPONENT_GAME_TIMEOUT;
        if (reason == WIN__OPPONENT_CONCEDED)
            return LOSS__CONCEDED;
        if (reason == WIN__OPPONENT_DECISION_TIMEOUT)
            return LOSS__DECISION_TIMEOUT;
        if (reason == WIN__OPPONENT_FORCE_DEPLETED)
            return LOSS__FORCE_DEPLETED;
        if (reason == WIN__OPPONENT_GAME_TIMEOUT)
            return LOSS__GAME_TIMEOUT;
        else
            return UNKNOWN;
    }
}
