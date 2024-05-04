package com.gempukku.swccgo.cards;


import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Defines the base implementation for a permanent pilot on a starship/vehicle.
 */
public abstract class AbstractPermanentPilot extends AbstractPermanentAboard {

    /**
     * Creates base implementation for a generic permanent pilot on a starship/vehicle.
     */
    public AbstractPermanentPilot() {
        this(0);
    }

    /**
     * Creates base implementation for a generic permanent pilot on a starship/vehicle.
     * @param ability the ability provided by permanent pilot
     */
    public AbstractPermanentPilot(int ability) {
        super("Permanent Pilot", null, true, false, ability);
    }

    /**
     * Creates base implementation for a permanent pilot (with a specific persona) on a starship/vehicle.
     * @param persona the persona of the permanent pilot
     * @param ability the ability provided by permanent pilot
     */
    public AbstractPermanentPilot(Persona persona, float ability) {
        super(persona.getHumanReadable(), Uniqueness.UNIQUE, true, false, ability);
        addPersona(persona);
    }
}
