package com.gempukku.swccgo.cards;


import com.gempukku.swccgo.common.Uniqueness;

/**
 * Defines the base implementation of a permanent (pilot or astromech) aboard a starship/vehicle.
 */
public abstract class AbstractPermanentAboard extends AbstractPermanent {
    private boolean _isPilot;
    private boolean _isAstromech;
    private float _ability;

    /**
     * Creates the base implementation of a permanent (pilot or astromech) aboard a starship/vehicle.
     * @param title the title
     * @param uniqueness the uniqueness
     * @param isPilot true if pilot, otherwise false
     * @param isAstromech true if astromech, otherwise false
     * @param ability the ability provided by permanent
     */
    protected AbstractPermanentAboard(String title, Uniqueness uniqueness, boolean isPilot, boolean isAstromech, float ability) {
        super(title, title, uniqueness);
        _isPilot = isPilot;
        _isAstromech = isAstromech;
        _ability = ability;
    }

    /**
     * Determines if the built-in is a permanent pilot.
     * @return true or false
     */
    @Override
    public final boolean isPilot() {
        return _isPilot;
    }

    /**
     * Determines if the built-in is a permanent astromech.
     * @return true or false
     */
    @Override
    public final boolean isAstromech() {
        return _isAstromech;
    }

    /**
     * Gets the ability of the built-in.
     * @return the ability
     */
    @Override
    public final float getAbility() {
        return _ability;
    }
}
