package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for Podracers.
 */
public abstract class AbstractPodracer extends AbstractDeployable {

    /**
     * Creates a blueprint for an Effect.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractPodracer(Side side, float destiny, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, PlayCardZoneOption.YOUR_SIDE_OF_LOCATION, null, title, Uniqueness.UNIQUE, expansionSet, rarity);
        setCardCategory(CardCategory.PODRACER);
        addCardType(CardType.PODRACER);
        addIcon(Icon.PODRACER);
    }
}
