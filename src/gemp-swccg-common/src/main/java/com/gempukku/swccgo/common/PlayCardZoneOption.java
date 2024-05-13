package com.gempukku.swccgo.common;

/**
 * Used to identify the different choices of zones for a card to play to using a specific play option.
 */
public enum PlayCardZoneOption {
    ATTACHED(Zone.ATTACHED, null, 0, false, false, false),

    YOUR_SIDE_OF_LOCATION(Zone.AT_LOCATION, null, 0, true, false, false),
    OPPONENTS_SIDE_OF_LOCATION(Zone.AT_LOCATION, null, 0, false, true, false),
    EITHER_SIDE_OF_LOCATION(Zone.AT_LOCATION, null, 0, true, true, false),

    YOUR_SIDE_OF_TABLE(Zone.SIDE_OF_TABLE, null, 0, true, false, false),
    OPPONENTS_SIDE_OF_TABLE(Zone.SIDE_OF_TABLE, null, 0, false, true, false),
    EITHER_SIDE_OF_TABLE(Zone.SIDE_OF_TABLE, null, 0, true, true, false),

    NEXT_TO_EITHER_LOST_PILE(Zone.SIDE_OF_TABLE, Zone.LOST_PILE, 1, true, true, false),
    OPPONENTS_RESERVE_DECK(Zone.RESERVE_DECK, Zone.RESERVE_DECK, 2, false, true, true),
    OPPONENTS_FORCE_PILE(Zone.FORCE_PILE, Zone.FORCE_PILE, 1, false, true, false);


    private Zone _zone;
    private Zone _zoneThatCannotBeEmpty;
    private int _minCardsInZone;
    private boolean _isYourZoneAnOption;
    private boolean _isOpponentsZoneAnOption;
    private boolean _isAsInsertCard;

    /**
     * Creates a play card zone option
     * @param zone the zone to play to
     * @param zoneThatCannotBeEmpty the zone (may be a different zone) that cannot be empty to be able to play to the zone
     *                              indicated by the zone parameter
     * @param minCardsInZone the minimum number of cards that must be in zoneThatCannotBeEmpty to be able to play to the
     *                       zone indicated by the zone parameter
     * @param isYourZoneAnOption true if owner of zone being played to can be same as owner of card being played, otherwise false
     * @param isOpponentsZoneAnOption true if of owner of zone being played to can be opponent of owner of card being played, otherwise false
     * @param isAsInsertCard true if is as 'insert' card, otherwise false
     */
    PlayCardZoneOption(Zone zone, Zone zoneThatCannotBeEmpty, int minCardsInZone, boolean isYourZoneAnOption, boolean isOpponentsZoneAnOption, boolean isAsInsertCard) {
        _zone = zone;
        _zoneThatCannotBeEmpty = zoneThatCannotBeEmpty;
        _minCardsInZone = minCardsInZone;
        _isYourZoneAnOption = isYourZoneAnOption;
        _isOpponentsZoneAnOption = isOpponentsZoneAnOption;
        _isAsInsertCard = isAsInsertCard;
    }

    public Zone getZone() {
        return _zone;
    }

    public Zone getZoneThatCannotBeEmpty() {
        return _zoneThatCannotBeEmpty;
    }

    public int getMinimumCardsInZone() {
        return _minCardsInZone;
    }

    public boolean isYourZoneAnOption() {
        return _isYourZoneAnOption;
    }

    public boolean isOpponentsZoneAnOption() {
        return _isOpponentsZoneAnOption;
    }

    public boolean isAsInsertCard() {
        return _isAsInsertCard;
    }
}
