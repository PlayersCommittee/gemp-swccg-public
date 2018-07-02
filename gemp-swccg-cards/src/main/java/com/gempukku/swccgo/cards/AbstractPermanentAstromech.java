package com.gempukku.swccgo.cards;


import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Defines the base implementation for a permanent astromech on a starship/vehicle.
 */
public abstract class AbstractPermanentAstromech extends AbstractPermanentAboard {

    /**
     * Not used.
     */
    private AbstractPermanentAstromech() {
        super(null, null, false, true, 0);
    }

    /**
     * Creates base implementation for a permanent astromech (with a specific persona) on a starship/vehicle.
     * @param persona the persona of the permanent astromech
     */
    public AbstractPermanentAstromech(Persona persona) {
        super(persona.getHumanReadable(), Uniqueness.UNIQUE, false, true, 0);
        addPersona(persona);
    }
}
