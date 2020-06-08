package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for unique vehicle sites.
 */
public abstract class AbstractUniqueVehicleSite extends AbstractSite {
    private Persona _vehicle;

    /**
     * Creates a blueprint for a unique vehicle site.
     * @param side the side of the Force
     * @param title the card title
     * @param vehicle the persona of the vehicle
     */
    protected AbstractUniqueVehicleSite(Side side, String title, Persona vehicle) {
        super(side, title, null, Uniqueness.UNIQUE);
        _vehicle = vehicle;
        addIcons(Icon.VEHICLE_SITE);
    }

    @Override
    public final Persona getRelatedStarshipOrVehiclePersona() {
        return _vehicle;
    }
}
