package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier which allows specified cards to deploy without presence or Force icons to specified locations.
 */
public class MayDeployToLocationWithoutPresenceOrForceIconsModifier extends MayDeployToTargetWithoutPresenceOrForceIconsModifier {

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to locations accepted
     * by the location filter.
     * @param source the card that is the source of the modifier and that is allowed to deploy without presence or Force icons
     * @param locationFilter the location filter
     */
    public MayDeployToLocationWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to locations accepted
     * by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     * @param locationFilter the location filter
     */
    public MayDeployToLocationWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier which allows specified cards to deploy without presence or Force icons to locations accepted
     * by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are allowed to deploy without presence or Force icons
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployToLocationWithoutPresenceOrForceIconsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, affectFilter, condition, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy to certain locations without presence or Force icons";
    }
}
