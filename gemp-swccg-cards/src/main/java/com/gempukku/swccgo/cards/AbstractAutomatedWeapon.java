package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for automated weapons.
 */
public abstract class AbstractAutomatedWeapon extends AbstractWeapon {

    /**
     * Creates a blueprint for an automated weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     */
    protected AbstractAutomatedWeapon(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title) {
        this(side, destiny, playCardZoneOption, title, null);
    }

    /**
     * Creates a blueprint for an automated weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractAutomatedWeapon(Side side, float destiny, PlayCardZoneOption playCardZoneOption, String title, Uniqueness uniqueness) {
        super(side, destiny, playCardZoneOption, null, title, uniqueness);
        setCardSubtype(CardSubtype.AUTOMATED);
    }
}
