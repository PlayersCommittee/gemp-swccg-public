package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to deploy free to specified locations (and cards at those locations).
 */
public class DeploysFreeToLocationModifier extends DeploysFreeToTargetModifier {

    /**
     * Creates a modifier that causes the source card to deploy free to specified locations.
     * @param source the card that is the source of the modifier and deploys free to specified locations
     * @param locationFilter the location filter
     */
    public DeploysFreeToLocationModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, source, null, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free to specified locations
     * @param locationFilter the location filter
     */
    public DeploysFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes the source card to deploy free to specified locations.
     * @param source the card that is the source of the modifier and deploys free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public DeploysFreeToLocationModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        this(source, source, condition, locationFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified locations.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free to specified locations
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public DeploysFreeToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, affectFilter, condition, Filters.locationAndCardsAtLocation(Filters.and(locationFilter)));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploys for free to specific locations";
    }
}
