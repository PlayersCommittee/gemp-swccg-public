package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that causes Operatives to only be deployed by a player once per turn.
 */
public class MayOnlyDeployOneOperativePerTurnRuleModifier extends AbstractModifier {

    /**
     * Creates a modifier Operatives to only be deployed by a player once per turn.
     * @param source the source of the modifier
     */
    public MayOnlyDeployOneOperativePerTurnRuleModifier(PhysicalCard source) {
        super(source, "Each player may only deploy one operative to a matching planet per turn", Filters.operative, null, ModifierType.MAY_ONLY_DEPLOY_ONE_OPERATIVE_PER_TURN_RULE);
    }
}
