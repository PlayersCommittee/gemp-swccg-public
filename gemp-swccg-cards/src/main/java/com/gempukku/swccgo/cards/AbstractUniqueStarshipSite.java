package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * The abstract class providing the common implementation for unique starship sites.
 */
public abstract class AbstractUniqueStarshipSite extends AbstractSite {
    private Persona _starship;

    /**
     * Creates a blueprint for a unique starship site.
     * @param side the side of the Force
     * @param title the card title
     * @param starship the persona of the starship
     */
    protected AbstractUniqueStarshipSite(Side side, String title, Persona starship) {
        super(side, title, null, Uniqueness.UNIQUE);
        _starship = starship;
        addIcons(Icon.STARSHIP_SITE);
    }

    @Override
    public final Persona getRelatedStarshipOrVehiclePersona() {
        return _starship;
    }
}
