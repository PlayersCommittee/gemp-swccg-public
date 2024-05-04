package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;

/**
 * An abstract effect that gets a sub-action and puts it on the action stack.
 * In order to determine if this effect was fully carried out, the sub-action is
 * checked to see if it was fully carried out.
 */
public abstract class AbstractSubActionEffect implements StandardEffect {
    protected Action _action;
    private SubAction _subAction;
    private boolean _canceled;
    private PhysicalCard _canceledByCard;

    /**
     * Creates an effect that gets a sub-action and puts it on the action stack.
     * @param action the action performing this effect
     */
    protected AbstractSubActionEffect(Action action) {
        _action = action;
    }

    @Override
    public Action getAction() {
        return _action;
    }

    @Override
    public void setAction(Action action) {
        _action = action;
    }

    @Override
    public final boolean wasCarriedOut() {
        return _subAction != null && !_canceled && _subAction.wasCarriedOut() && wasActionCarriedOut();
    }

    @Override
    public boolean isCanceled() {
        return _canceled;
    }

    @Override
    public PhysicalCard getCanceledByCard() {
        return _canceledByCard;
    }

    @Override
    public void cancel(PhysicalCard canceledByCard) {
        if (!isCanceled()) {
            _canceled = true;
            _canceledByCard = canceledByCard;
        }
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String getText(SwccgGame game) {
        return null;
    }

    @Override
    public void playEffect(SwccgGame game) {
        if (!isCanceled()) {
            _subAction = getSubAction(game);
            game.getActionsEnvironment().addActionToStack(_subAction);
        }
    }

    /**
     * Gets the sub-action to perform.
     * @param game the game
     * @return the sub-action to perform.
     */
    protected abstract SubAction getSubAction(SwccgGame game);

    /**
     * Determines if the action process was fully carried out.
     * The class that implements the getSubAction method should implement this method to do any additional checking as to
     * if the action was fully carried out.
     * @return true if the action was fully carried out, otherwise false
     */
    protected abstract boolean wasActionCarriedOut();
}
