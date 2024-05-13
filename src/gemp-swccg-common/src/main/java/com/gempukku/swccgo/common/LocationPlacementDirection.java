package com.gempukku.swccgo.common;

/**
 * Represents the possible choices for placement of a location relative to another location.
 */
public enum LocationPlacementDirection {

    LEFT_OF(true, false, false),
    REPLACE(false, true, false),
    RIGHT_OF(false, false, true),
    LEFT_OF_OR_REPLACE(true, true, false),
    LEFT_OR_RIGHT_OF(true, false, true),
    RIGHT_OF_OR_REPLACE(false, true, true),
    LEFT_OR_RIGHT_OF_OR_REPLACE(true, true, true);

    private boolean _leftOf;
    private boolean _replace;
    private boolean _rightOf;

    LocationPlacementDirection(boolean leftOf, boolean replace, boolean rightOf) {
        _leftOf = leftOf;
        _replace = replace;
        _rightOf = rightOf;
    }

    /**
     * Determines if placing location to "left of" another card is a valid choice.
     * @return true or false
     */
    public boolean isLeftOf() {
        return _leftOf;
    }

    /**
     * Determines if "replacing" another card is a valid choice.
     * @return true or false
     */
    public boolean isReplace() {
        return _replace;
    }

    /**
     * Determines if placing location to "right of" another card is a valid choice.
     * @return true or false
     */
    public boolean isRightOf() {
        return _rightOf;
    }

    /**
     * Determines if only one direction.
     * @return true or false
     */
    public boolean isOneDirection() {
        int validChoices = 0;
        if (isLeftOf()) validChoices++;
        if (isReplace()) validChoices++;
        if (isRightOf()) validChoices++;
        return validChoices == 1;
    }

    /**
     * Gets the location placement direction that represents the values.
     * @param isLeftOf true if left of is included, otherwise false
     * @param isReplace true if convert is included, otherwise false
     * @param isRightOf true if right of is included, otherwise false
     * @return the location placement direction, or null if no match
     */
    public static LocationPlacementDirection getDirectionFromValues(boolean isLeftOf, boolean isReplace, boolean isRightOf) {
        for (LocationPlacementDirection direction : values()) {
            if (direction.isLeftOf() == isLeftOf && direction.isReplace() == isReplace && direction.isRightOf() == isRightOf)
                return direction;
        }
        return null;
    }

    /**
     * Gets the location placement direction that represents the union of the specified location placement directions.
     * @param direction1 direction 1
     * @param direction2 direction 2
     * @return the location placement direction, or null if no value union
     */
    public static LocationPlacementDirection getUnion(LocationPlacementDirection direction1, LocationPlacementDirection direction2) {
        for (LocationPlacementDirection direction : values()) {
            if ((direction1.isLeftOf() || direction2.isLeftOf())  == direction.isLeftOf()
                    && (direction1.isReplace() || direction2.isReplace()) == direction.isReplace()
                    && (direction1.isRightOf() || direction2.isRightOf()) == direction.isRightOf())
                return direction;
        }
        return null;
    }

}
