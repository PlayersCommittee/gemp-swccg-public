package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;

/**
 * The abstract class providing the common implementation for non-unique starship sites.
 */
public abstract class AbstractNonuniqueStarshipSite extends AbstractSite {
    private Filter _starshipFilter;

    /**
     * Creates a blueprint for a non-unique starship site.
     * @param side the side of the Force
     * @param title the card title
     * @param starshipFilter the filter for a starship this site can be related to
     */
    protected AbstractNonuniqueStarshipSite(Side side, String title, Filter starshipFilter, Uniqueness uniqueness) {
        super(side, title, null, uniqueness);
        _starshipFilter = starshipFilter;
        addIcons(Icon.STARSHIP_SITE);
    }

    @Override
    public final Filter getRelatedStarshipOrVehicleFilter() {
        return _starshipFilter;
    }
}
