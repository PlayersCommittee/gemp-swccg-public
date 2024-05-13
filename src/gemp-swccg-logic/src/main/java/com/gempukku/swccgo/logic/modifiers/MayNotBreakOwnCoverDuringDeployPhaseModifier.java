package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.PhaseCondition;

/**
 * A modifier that prevents an Undercover spy from using normal Undercover spy rule of voluntarily 'breaking cover'
 * during deploy phase.
 */
public class MayNotBreakOwnCoverDuringDeployPhaseModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents an Undercover spy from using normal Undercover spy rule of voluntarily 'breaking cover'
     * during deploy phase.
     * @param source the card that is the source of the modifier and that is affected by this modifier
     */
    public MayNotBreakOwnCoverDuringDeployPhaseModifier(PhysicalCard source) {
        super(source, "May not voluntarily 'break cover'", source, new PhaseCondition(Phase.DEPLOY, source.getOwner()), ModifierType.MAY_NOT_BREAK_OWN_COVER_DURING_DEPLOY_PHASE);
    }
}
