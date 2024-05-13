package com.gempukku.swccgo.common;

/**
 * Represents the zone in which a card can exist during a SWCCG game.
 */
public enum Zone implements Filterable {
    // Public knowledge and in play
    AT_LOCATION("play", true, true, true, false, false, false, false),
    SIDE_OF_TABLE("play", true, true, true, false, false, false, false),
    LOCATIONS("play", true, true, true, false, false, false, false),
    ATTACHED("play", true, true, true, false, false, false, false),

    // Public knowledge but not in play
    STACKED("stacked", true, true, false, false, false, false, false),
    STACKED_FACE_DOWN("stacked", true, true, false, true, false, false, false),
    OUT_OF_PLAY("out of play", true, true, false, false, false, false, false),
    TOP_OF_RESERVE_DECK("Reserve Deck", true, true, false, true, true, false, false),
    TOP_OF_FORCE_PILE("Force Pile", true, true, false, true, true, false, false),
    TOP_OF_USED_PILE("Used Pile", true, true, false, true, true, false, false),
    TOP_OF_LOST_PILE("Lost Pile", true, true, false, false, false, false, false),
    TOP_OF_UNRESOLVED_DESTINY_DRAW("unresolved destiny draw", true, true, false, false, true, true, true),
    REVEALED_SABACC_HAND("sabacc hand", true, true, false, false, true, false, false),
    SIDE_OF_TABLE_NOT_IN_PLAY("side of table", true, true, false, false, false, false, false),
    SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY("side of table", true, true, false, true, false, false, false),

    // Private knowledge
    HAND("hand", false, true, false, false, false, false, false),
    SABACC_HAND("sabacc hand", false, true, false, false, true, false, false),

    // Nobody sees
    VOID("void", false, false, false, false, false, false, false),
    RESERVE_DECK("Reserve Deck", false, false, false, true, true, false, true),
    FORCE_PILE("Force Pile", false, false, false, true, true, false, true),
    USED_PILE("Used Pile", false, false, false, true, true, false, true),
    LOST_PILE("Lost Pile", false, false, false, false, false, false, true),
    UNRESOLVED_DESTINY_DRAW("unresolved destiny draw", false, false, false, false, true, true, true),
    CONVERTED_LOCATIONS("play", false, false, false, false, false, false, false),
    OUTSIDE_OF_DECK("outside of deck", false, false, false, false, false, false, false);

    private String _humanReadable;
    private boolean _public;
    private boolean _visibleByOwner;
    private boolean _inPlay;
    private boolean _faceDown;
    private boolean _lifeForce;
    private boolean _isUnresolvedDestinyDraw;
    private boolean _isCardPile;

    Zone(String humanReadable, boolean isPublic, boolean visibleByOwner, boolean inPlay, boolean faceDown, boolean lifeForce, boolean unresolvedDestinyDraw, boolean cardPile) {
        _humanReadable = humanReadable;
        _public = isPublic;
        _visibleByOwner = visibleByOwner;
        _inPlay = inPlay;
        _faceDown = faceDown;
        _lifeForce = lifeForce;
        _isUnresolvedDestinyDraw = unresolvedDestinyDraw;
        _isCardPile = cardPile;
    }

    public String getHumanReadable() {
        return _humanReadable;
    }

    public boolean isInPlay() {
        return _inPlay;
    }

    public boolean isPublic() {
        return _public;
    }

    public boolean isVisibleByOwner() {
        return _visibleByOwner;
    }

    public boolean isFaceDown() {
        return _faceDown;
    }

    public boolean isLifeForce() {
        return _lifeForce;
    }

    public boolean isUnresolvedDestinyDraw() {
        return _isUnresolvedDestinyDraw;
    }

    public boolean isCardPile() {
        return _isCardPile;
    }
}
