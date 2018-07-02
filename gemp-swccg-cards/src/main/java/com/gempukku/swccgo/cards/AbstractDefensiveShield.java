package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.*;

/**
 * The abstract class providing the common implementation for Defensive Shields.
 */
public abstract class AbstractDefensiveShield extends AbstractNonLocationPlaysToTable {

    /**
     * Creates a blueprint for a Defensive Shield card.
     * @param side the side of the Force
     * @param title the card title
     */
    protected AbstractDefensiveShield(Side side, String title) {
        this(side, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, title);
    }

    /**
     * Creates a blueprint for a Defensive Shield card.
     * @param side the side of the Force
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractDefensiveShield(Side side, PlayCardZoneOption playCardZoneOption, String title) {
        super(side, 0f, playCardZoneOption, 0f, title, Uniqueness.UNIQUE);
        setCardCategory(CardCategory.DEFENSIVE_SHIELD);
        addCardType(CardType.DEFENSIVE_SHIELD);
        addIcon(Icon.DEFENSIVE_SHIELD);
    }

    /**
     * Determines if the card type, subtype, etc. always plays for free.
     * @return true if card type card type, subtype, etc. always plays for free, otherwise false
     */
    @Override
    protected final boolean isCardTypeAlwaysPlayedForFree() {
        return true;
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
        return false;
    }
}
