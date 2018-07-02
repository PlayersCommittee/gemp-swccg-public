package com.gempukku.swccgo.logic.timing;


/**
 * An always successful effect that is just used as a passthru for when code needs to be run as an effect of an action.
 * The code to run is put in an overridden doPlayEffect method of this class.
 */
public abstract class PassthruEffect extends AbstractSuccessfulEffect implements UsageEffect, TargetingEffect {

    /**
     * Creates a passthru effect.
     * @param action the action performing this effect
     */
    public PassthruEffect(Action action) {
        super(action);
    }
}
