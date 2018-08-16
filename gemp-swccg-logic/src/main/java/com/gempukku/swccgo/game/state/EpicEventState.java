package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;

/**
 * This class contains the state information for an Epic Event action.
 */
public abstract class EpicEventState {
    private PhysicalCard _epicEvent;
    private Type _epicEventType;

    public enum Type {
        ATTACK_RUN,
        ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II,
        COMMENCE_PRIMARY_IGNITION,
        COMMENCE_PRIMARY_IGNITION_V,
        DEACTIVATE_THE_SHIELD_GENERATOR,
        RESTORE_FREEDOM_TO_THE_GALAXY,
        TARGET_THE_MAIN_GENERATOR
    }

    /**
     * Creates state information for an Epic Event action;
     * @param epicEvent the Epic Event card
     * @param epicEventType the Epic Event type
     */
    public EpicEventState(PhysicalCard epicEvent, Type epicEventType) {
        _epicEvent = epicEvent;
        _epicEventType = epicEventType;
    }

    /**
     * Gets the Epic Event card
     * @return the card
     */
    public PhysicalCard getEpicEvent() {
        return _epicEvent;
    }

    /**
     * Gets the Epic Event type
     * @return the type
     */
    public Type getEpicEventType() {
        return _epicEventType;
    }
}
