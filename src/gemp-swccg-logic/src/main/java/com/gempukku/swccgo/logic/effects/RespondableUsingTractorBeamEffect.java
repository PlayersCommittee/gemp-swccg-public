package com.gempukku.swccgo.logic.effects;


import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used as the effect that is responded to when a tractor beam is being used for purposes of canceling, re-targeting, etc.
 */
public abstract class RespondableUsingTractorBeamEffect extends RespondableEffect {

    /**
     * Creates an respondable tractor beam firing effect
     * @param action the action performing this effect
     */
    public RespondableUsingTractorBeamEffect(Action action) {
        super(action);
    }

    @Override
    public Type getType() {
        return Type.USING_TRACTOR_BEAM_EFFECT;
    }
}
