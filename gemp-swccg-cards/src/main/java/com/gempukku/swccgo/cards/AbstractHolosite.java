package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for holosites.
 */
public abstract class AbstractHolosite extends AbstractLocation {

    /**
     * Creates a blueprint for a holosite.
     * @param side the side of the Force
     * @param title the card title
     */
    protected AbstractHolosite(Side side, String title) {
        this(side, title, null, null);
    }

    /**
     * Creates a blueprint for a holosite.
     * @param side the side of the Force
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractHolosite(Side side, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, Uniqueness.UNIQUE, expansionSet, rarity);
        setCardSubtype(CardSubtype.SITE);
        addKeyword(Keyword.HOLOSITE);
    }

    @Override
    public String getSystemName() {
        return null;
    }
}
