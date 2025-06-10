package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not being able to deploy to specified locations.
 */
public class MayNotDeployToLocationModifier extends MayNotDeployToTargetModifier {

    /**
     * Creates a modifier for not being able to deploy to specified locations.
     * @param source the card that is the source of the modifier and that may not deploy to specified locations
     * @param locationFilter the filter for locations that affected cards may not deploy to
     */
    public MayNotDeployToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier for not being able to deploy to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not deploy to specified locations
     * @param locationFilter the filter for locations that affected cards may not deploy to
     */
    public MayNotDeployToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for not being able to deploy to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not deploy to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the filter for locations that affected cards may not deploy to
     */
    public MayNotDeployToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, affectFilter, condition, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Valid deploy to locations affected";
    }
}
