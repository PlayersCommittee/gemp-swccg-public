package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * An abstract effect that is always considered successful, so some of the methods are set to make this effect
 * do as little checking as possible in determining if the effect can be performed and if it was successful.
 */
public abstract class AbstractSuccessfulEffect implements StandardEffect {
    protected Action _action;

    /**
     * Creates an effect that is always considered successful.
     * @param action the action performing this effect
     */
    protected AbstractSuccessfulEffect(Action action) {
        _action = action;
    }

    @Override
    public final Action getAction() {
        return _action;
    }

    @Override
    public void setAction(Action action) {
        _action = action;
    }

    @Override
    public final boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    public final boolean wasCarriedOut() {
        return true;
    }

    @Override
    public final boolean isCanceled() {
        return false;
    }

    @Override
    public final PhysicalCard getCanceledByCard() {
        return null;
    }

    @Override
    public final void cancel(PhysicalCard canceledByCard) {
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
    public final void playEffect(SwccgGame game) {
        doPlayEffect(game);
    }

    /**
     * Performs the effect
     * @param game the game
     */
    protected abstract void doPlayEffect(SwccgGame game);
}
