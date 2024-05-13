package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used as the effect that is responded to when a weapon is being fired for purposes of canceling, re-targeting, etc.
 */
public abstract class RespondableWeaponFiringEffect extends RespondableEffect {

    /**
     * Creates an respondable weapon firing effect
     * @param action the action performing this effect
     */
    public RespondableWeaponFiringEffect(Action action) {
        super(action);
    }

    @Override
    public Type getType() {
        return Type.WEAPON_FIRING_EFFECT;
    }
}
