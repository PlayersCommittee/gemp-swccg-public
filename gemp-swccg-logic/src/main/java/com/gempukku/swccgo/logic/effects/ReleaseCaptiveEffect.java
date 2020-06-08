package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect to release the specified captive.
 */
public class ReleaseCaptiveEffect extends ReleaseCaptivesEffect {

    /**
     * Creates an effect to release the specified captive.
     * @param action the action performing this effect
     * @param captive the captive to release
     */
    public ReleaseCaptiveEffect(Action action, PhysicalCard captive) {
        super(action, Collections.singletonList(captive));
    }
}
