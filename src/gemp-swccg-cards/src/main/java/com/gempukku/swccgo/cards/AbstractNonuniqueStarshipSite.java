package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
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
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractNonuniqueStarshipSite(Side side, String title, Filter starshipFilter, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, title, null, uniqueness, expansionSet, rarity);
        _starshipFilter = starshipFilter;
        addIcons(Icon.STARSHIP_SITE);
    }

    @Override
    public final Filter getRelatedStarshipOrVehicleFilter() {
        return _starshipFilter;
    }
}
