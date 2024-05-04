package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows affected cards to deploy instead of a starfighter using Combat Response.
 */
public class MayDeployInsteadOfStarfighterUsingCombatResponseModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to deploy instead of a starfighter using Combat Response.
     * @param source the source of the modifier
     */
    public MayDeployInsteadOfStarfighterUsingCombatResponseModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy instead of a starfighter using Combat Response.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    private MayDeployInsteadOfStarfighterUsingCombatResponseModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May deploy instead of starfighter using Combat Response", affectFilter, null, ModifierType.MAY_DEPLOY_INSTEAD_OF_STARFIGHTER_USING_COMBAT_RESPONSE, true);
    }
}
