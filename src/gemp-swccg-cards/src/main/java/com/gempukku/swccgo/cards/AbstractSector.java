package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for sectors.
 */
public abstract class AbstractSector extends AbstractLocation {
    private String _systemName;

    /**
     * Creates a blueprint for a sector.
     * @param side the side of the Force
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractSector(Side side, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        this(side, title, null, uniqueness, expansionSet, rarity);
    }

    /**
     * Creates a blueprint for a sector.
     * @param side the side of the Force
     * @param title the card title
     * @param systemName the system the sector is related to, or null if a generic location
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractSector(Side side, String title, String systemName, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, uniqueness, expansionSet, rarity);
        _systemName = systemName;
        setCardSubtype(CardSubtype.SECTOR);
    }

    @Override
    public final String getSystemName() {
        return _systemName;
    }
}
