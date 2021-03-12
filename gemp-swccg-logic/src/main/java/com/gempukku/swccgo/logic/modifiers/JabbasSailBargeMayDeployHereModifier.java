package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows Jabba's Sail Barge to deploy at affected locations
 */
public class JabbasSailBargeMayDeployHereModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows Jabba's Sail Barge to deploy at affected locations
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public JabbasSailBargeMayDeployHereModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Jabba's Sail Barge may deploy here", affectFilter, ModifierType.JABBAS_SAIL_BARGE_MAY_DEPLOY_HERE);
    }

    /**
     * Creates a modifier that allows Jabba's Sail Barge to deploy at affected locations
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public JabbasSailBargeMayDeployHereModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Jabba's Sail Barge may deploy here", affectFilter, condition, ModifierType.JABBAS_SAIL_BARGE_MAY_DEPLOY_HERE);
    }
}
