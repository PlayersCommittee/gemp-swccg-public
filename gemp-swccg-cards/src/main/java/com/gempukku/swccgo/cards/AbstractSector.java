package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
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
     */
    protected AbstractSector(Side side, String title, Uniqueness uniqueness) {
        this(side, title, null, uniqueness);
    }

    /**
     * Creates a blueprint for a sector.
     * @param side the side of the Force
     * @param title the card title
     * @param systemName the system the sector is related to, or null if a generic location
     */
    protected AbstractSector(Side side, String title, String systemName) {
        this(side, title, systemName, Uniqueness.UNIQUE);
    }

    /**
     * Creates a blueprint for a sector.
     * @param side the side of the Force
     * @param title the card title
     * @param systemName the system the sector is related to, or null if a generic location
     * @param uniqueness the uniqueness
     */
    protected AbstractSector(Side side, String title, String systemName, Uniqueness uniqueness) {
        super(side, title, uniqueness);
        _systemName = systemName;
        setCardSubtype(CardSubtype.SECTOR);
    }

    @Override
    public final String getSystemName() {
        return _systemName;
    }
}
