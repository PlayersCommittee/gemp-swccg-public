package com.gempukku.swccgo.league;

/**
 * Defines the deck-lock behaviors a league can enforce. A null value means
 * the league has no deck-locking rules.
 */
public enum LockedDeckType {

    WHEN_FIRST_PLAYED("when_first_played", "When First Played"),
    ;

    public static LockedDeckType getLockedDeckType(String code) {
        if (code == null || code.isEmpty())
            return null;
        for (LockedDeckType type : LockedDeckType.values()) {
            if (type.getCode().equals(code))
                return type;
        }
        return null;
    }

    private final String _code;
    private final String _humanReadable;

    LockedDeckType(String code, String humanReadable) {
        _code = code;
        _humanReadable = humanReadable;
    }

    public String getCode() {
        return _code;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }
}
