package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used as the effect that is responded when a top-level or response action occurs.
 * Setting a RespondableEffect via the allowResponses method of AbstractRespondableAction
 * is required if any targeting was done by the card. If there is nothing that would ever respond to that action, then
 * the child class UnrespondableEffect can set instead with the allowResponses method to avoid unnecessary triggers, etc.
 */
public abstract class RespondableEffect extends AbstractStandardEffect {
    private Action _targetingAction;

    /**
     * Creates a respondable effect
     * @param action the action performing this effect
     */
    protected RespondableEffect(Action action) {
        this(action, action);
    }

    /**
     * Creates a respondable effect
     * @param action the action performing this effect
     * @param targetingAction the action with the targeting information
     */
    protected RespondableEffect(Action action, Action targetingAction) {
        super(action);
        _targetingAction = targetingAction;
    }

    @Override
    public Type getType() {
        return Type.RESPONDABLE_EFFECT;
    }

    @Override
    public String getText(SwccgGame game) {
        return _targetingAction.getText();
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        performActionResults(_targetingAction);
        return new FullEffectResult(true);
    }

    /**
     * Gets the targeting action.
     * @return the targeting action
     */
    public Action getTargetingAction() {
        return _targetingAction;
    }

    /**
     * This method is called to perform the results part of the action if the action was not canceled.
     * @param targetingAction the action with the targeting information
     */
    protected abstract void performActionResults(Action targetingAction);
}
