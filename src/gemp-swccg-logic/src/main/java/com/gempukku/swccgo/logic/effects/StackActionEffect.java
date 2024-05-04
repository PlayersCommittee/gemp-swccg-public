package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An effect that stacks an action on the action stack. The effect is considered to be fully carried out if the action
 * that was stack was fully carried out.
 */
public class StackActionEffect extends AbstractStandardEffect implements TargetingEffect {
    private Action _actionToStack;

    /**
     * Stacks an action on the action stack.
     * @param action the action performing this effect
     * @param actionToStack the action to stack
     */
    public StackActionEffect(Action action, Action actionToStack) {
        super(action);
        _actionToStack = actionToStack;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        game.getActionsEnvironment().addActionToStack(_actionToStack);
        return new FullEffectResult(true);
    }

    @Override
    public boolean wasCarriedOut() {
        return super.wasCarriedOut() && _actionToStack.wasCarriedOut();
    }
}
