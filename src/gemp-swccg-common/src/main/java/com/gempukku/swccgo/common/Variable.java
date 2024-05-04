package com.gempukku.swccgo.common;

/**
 * Represents the X, Y, or Z variables used in a calculation on a SWCCG card.
 */
public enum Variable {
    X("X"),
    Y("Y"),
    Z("Z");

    private String _humanReadable;

    Variable(String humanReadable) {
        _humanReadable = humanReadable;
    }

    @Override
    public String toString() {
        return _humanReadable;
    }
}
