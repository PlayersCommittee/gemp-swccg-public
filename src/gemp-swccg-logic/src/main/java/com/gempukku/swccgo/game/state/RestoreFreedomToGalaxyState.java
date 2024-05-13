package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for an Epic Event action of Restore Freedom To The Galaxy.
 */
public class RestoreFreedomToGalaxyState extends EpicEventState {

    /**
     * Creates state information for an Epic Event action of Deactivate The Shield Generator.
     * @param epicEvent the Epic Event card
     */
    public RestoreFreedomToGalaxyState(PhysicalCard epicEvent) {
        super(epicEvent, Type.RESTORE_FREEDOM_TO_THE_GALAXY);
    }
}
