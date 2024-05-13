package com.gempukku.swccgo.game.state.actions;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.EpicEventState;

/**
 * This class contains the state information for an Epic Event action of Commence Primary Ignition (V).
 */
public class CommencePrimaryIgnitionV9State extends EpicEventState {
    private PhysicalCard _superlaser;
    private PhysicalCard _site;

    /**
     * Creates state information for an Epic Event action of Commence Primary Ignition (V).
     * @param epicEvent the Epic Event card
     */
    public CommencePrimaryIgnitionV9State(PhysicalCard epicEvent) {
        super(epicEvent, Type.COMMENCE_PRIMARY_IGNITION_V);
    }

    /**
     * Sets the Superlaser.
     * @param superlaser the superlaser
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
     * Sets the site.
     * @param site the site
     */
    public void setSite(PhysicalCard site) {
        _site = site;
    }

    /**
     * Gets the site.
     * @return the site
     */
    public PhysicalCard getSite() {
        return _site;
    }
}
