package com.gempukku.swccgo.common;

/**
 * Represents the movement direction of a Mobile Effect
 */
public enum MovementDirection {
    LEFT("Left"),
    NONE("None"),
    RIGHT("Right");

    private String _humanReadable;

    MovementDirection(String humanReadable) {
        _humanReadable = humanReadable;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public MovementDirection getReversedDirection() {
        if (this == LEFT)
            return RIGHT;
        else if (this == RIGHT)
            return LEFT;
        else
            return NONE;
    }
}
