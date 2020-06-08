package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Keyword;
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
        super(side, title, Uniqueness.UNIQUE);
        setCardSubtype(CardSubtype.SITE);
        addKeyword(Keyword.HOLOSITE);
    }

    @Override
    public String getSystemName() {
        return null;
    }
}
