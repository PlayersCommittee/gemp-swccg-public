package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to deploy free to a specified system (and locations on that system).
 */
public class DeploysFreeToSystemModifier extends DeploysFreeToLocationModifier {

    /**
     * Creates a modifier that causes the source card to deploy free to specified system (and locations on that system).
     * @param source the card that is the source of the modifier and deploys free to specified locations
     * @param system the system
     */
    public DeploysFreeToSystemModifier(PhysicalCard source, String system) {
        this(source, source, null, system);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified system (and locations on that system).
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param system the system
     */
    public DeploysFreeToSystemModifier(PhysicalCard source, Filterable affectFilter, String system) {
        this(source, affectFilter, null, system);
    }

    /**
     * Creates a modifier that causes the source card to deploy free to specified system (and locations on that system).
     * @param source the card that is the source of the modifier and deploys free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param system the system
     */
    public DeploysFreeToSystemModifier(PhysicalCard source, Condition condition, String system) {
        this(source, source, condition, system);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified system (and locations on that system).
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param system the system
     */
    public DeploysFreeToSystemModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String system) {
        super(source, affectFilter, condition, Filters.partOfSystem(system));
    }
}
