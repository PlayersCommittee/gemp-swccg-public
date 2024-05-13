package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An abstract effect that can be added to an action as a targeting cost.
 */
public abstract class AbstractTargetCardsEffect extends AbstractEffect implements TargetingEffect {

    /**
     * Creates an effect that can be added to an action as a targeting cost.
     * @param action the action performing this effect
     */
    protected AbstractTargetCardsEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }
}
