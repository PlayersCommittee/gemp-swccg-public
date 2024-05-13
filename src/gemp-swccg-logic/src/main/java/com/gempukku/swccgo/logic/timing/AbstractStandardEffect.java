package com.gempukku.swccgo.logic.timing;


/**
 * An abstract effect that can be added to an action as a standard cost or an effect.
 */
public abstract class AbstractStandardEffect extends AbstractEffect implements StandardEffect {

    /**
     * Creates an effect that can be added to an action as a standard cost or an effect.
     * @param action the action performing this effect
     */
    protected AbstractStandardEffect(Action action) {
        super(action);
    }
}