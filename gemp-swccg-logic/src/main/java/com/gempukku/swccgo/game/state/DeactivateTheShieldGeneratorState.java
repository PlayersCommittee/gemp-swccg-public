package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for an Epic Event action of Deactivate The Shield Generator.
 */
public class DeactivateTheShieldGeneratorState extends EpicEventState {

    /**
     * Creates state information for an Epic Event action of Deactivate The Shield Generator.
     * @param epicEvent the Epic Event card
     */
    public DeactivateTheShieldGeneratorState(PhysicalCard epicEvent) {
        super(epicEvent, Type.DEACTIVATE_THE_SHIELD_GENERATOR);
    }
}
