package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for allowing TIEs to land at exterior sites
 */
public class TIEsMayLandAtExteriorSiteModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier for allowing TIEs to land at exterior sites
     * @param source the card that is the source of the modifier and that may land specified locations
     * @param locationFilter the filter for locations that affected cards may not move to
     */
    public TIEsMayLandAtExteriorSiteModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier for allowing TIEs to land at exterior sites
     * @param source the source of the modifier
     * @param affectFilter the filter for TIEs that may land
     * @param locationFilter the filter for locations that affected cards may land at
     */
    public TIEsMayLandAtExteriorSiteModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for allowing TIEs to land at exterior sites
     * @param source the source of the modifier
     * @param affectFilter the filter for TIEs that may land
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the filter for locations that affected cards may land at
     */
    public TIEsMayLandAtExteriorSiteModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, "May land at certain exterior sites", Filters.and(affectFilter), condition, ModifierType.TIE_MAY_LAND_AT_EXTERIOR_SITE, true);
        _locationFilter = Filters.and(locationFilter);
    }

    public boolean allowedToLandAt(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard toLocation) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, toLocation);
    }
}
