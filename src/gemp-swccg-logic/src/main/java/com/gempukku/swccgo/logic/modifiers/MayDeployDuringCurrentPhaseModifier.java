package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows specified cards to deploy during the current phase.
 */
public class MayDeployDuringCurrentPhaseModifier extends AbstractModifier {

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy during the current phase.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     */
    public MayDeployDuringCurrentPhaseModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy during the current phase.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployDuringCurrentPhaseModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_DURING_CURRENT_PHASE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy during " + gameState.getCurrentPhase().getHumanReadable().toLowerCase();
    }
}
