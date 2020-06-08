package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;

/**
 * The abstract class providing the common implementation for non-unique vehicle sites.
 */
public abstract class AbstractNonuniqueVehicleSite extends AbstractSite {
    private Filter _vehicleFilter;

    /**
     * Creates a blueprint for a non-unique vehicle site.
     * @param side the side of the Force
     * @param title the card title
     * @param vehicleFilter the filter for a vehicle this site can be related to
     */
    protected AbstractNonuniqueVehicleSite(Side side, String title, Filter vehicleFilter, Uniqueness uniqueness) {
        super(side, title, null, uniqueness);
        _vehicleFilter = vehicleFilter;
        addIcons(Icon.VEHICLE_SITE);
    }

    @Override
    public final Filter getRelatedStarshipOrVehicleFilter() {
        return _vehicleFilter;
    }
}
