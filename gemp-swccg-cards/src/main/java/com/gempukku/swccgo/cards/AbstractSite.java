package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for sites.
 */
public abstract class AbstractSite extends AbstractLocation {
    private String _systemName;

    /**
     * Creates a blueprint for a site.
     * @param side the side of the Force
     * @param title the card title
     * @param systemName the system the site is related to, or null if a generic location or non-planet site
     */
    protected AbstractSite(Side side, String title, String systemName) {
        this(side, title, systemName, Uniqueness.UNIQUE);
    }

    /**
     * Creates a blueprint for a site.
     * @param side the side of the Force
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractSite(Side side, String title, Uniqueness uniqueness) {
        this(side, title, null, uniqueness);
    }

    /**
     * Creates a blueprint for a site.
     * @param side the side of the Force
     * @param title the card title
     * @param systemName the system the site is related to, or null if a generic location or non-planet site
     * @param uniqueness the uniqueness
     */
    protected AbstractSite(Side side, String title, String systemName, Uniqueness uniqueness) {
        super(side, title, uniqueness);
        _systemName = systemName;
        setCardSubtype(CardSubtype.SITE);
    }

    @Override
    public final String getSystemName() {
        return _systemName;
    }
}
