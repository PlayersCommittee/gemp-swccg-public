package com.gempukku.swccgo.game.state.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.EpicEventState;

/**
 * This class contains the state information for an Epic Event action of Commence Primary Ignition.
 */
public class CommencePrimaryIgnitionState extends EpicEventState {
    private PhysicalCard _superlaser;
    private PhysicalCard _planetSystem;

    /**
     * Creates state information for an Epic Event action of Commence Primary Ignition.
     * @param epicEvent the Epic Event card
     */
    public CommencePrimaryIgnitionState(PhysicalCard epicEvent) {
        super(epicEvent, Type.COMMENCE_PRIMARY_IGNITION);
    }

    /**
     * Sets the Superlaser.
     * @param superlaser the AT-AT Cannon
     */
    public void setSuperlaser(PhysicalCard superlaser) {
        _superlaser = superlaser;
    }

    /**
     * Gets the Superlaser.
     * @return the Superlaser
     */
    public PhysicalCard getSuperlaser() {
        return _superlaser;
    }

    /**
     * Sets the planet system.
     * @param planetSystem the planet system
     */
    public void setPlanetSystem(PhysicalCard planetSystem) {
        _planetSystem = planetSystem;
    }

    /**
     * Gets the planet system.
     * @return the planet system
     */
    public PhysicalCard getPlanetSystem() {
        return _planetSystem;
    }
}
