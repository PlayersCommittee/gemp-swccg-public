package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Death Star weapons.
 */
public abstract class AbstractDeathStarWeapon extends AbstractWeapon {

    /**
     * Creates a blueprint for a Death Star weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDeathStarWeapon(Side side, float destiny, String title, Uniqueness uniqueness) {
        this(side, destiny, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a Death Star weapon.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the ExpansionSet
     * @param rarity the rarity
     */
    protected AbstractDeathStarWeapon(Side side, float destiny, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.ATTACHED, null, title, uniqueness, expansionSet, rarity);
        setCardSubtype(CardSubtype.DEATH_STAR);
    }
}
