package com.gempukku.swccgo.logic.timing;


import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * An abstract effect that provides the base implementation for an effect that needs to be checked whether it
 * can be performed, checked whether it was canceled, and checked if it was fully carried out.
 * AbstractSuccessfulEffect can be used instead if none of this checking is needed for an effect.
 */
public abstract class AbstractEffect implements Effect {
    protected Action _action;
    private Boolean _carriedOut;
    private boolean _canceled;
    private PhysicalCard _canceledByCard;

    /**
     * Creates an effect that needs to be checked whether or not it was fully carried out.
     * @param action the action performing the effect
     */
    protected AbstractEffect(Action action) {
        _action = action;
    }

    protected abstract FullEffectResult playEffectReturningResult(SwccgGame game);

    @Override
    public Action getAction() {
        return _action;
    }

    @Override
    public void setAction(Action action) {
        _action = action;
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
        if (!isCanceled()) {
            FullEffectResult fullEffectResult = playEffectReturningResult(game);
            _carriedOut = fullEffectResult.isCarriedOut();
        }
        else {
            _carriedOut = false;
        }
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
    public boolean wasCarriedOut() {
        if (_carriedOut == null)
            throw new IllegalStateException("Effect has to be played first");
        return _carriedOut;
    }

    /**
     * A class that represents whether or not an effect was fully carried out.
     */
    protected static class FullEffectResult {
        private boolean _carriedOut;

        public FullEffectResult(boolean carriedOut) {
            _carriedOut = carriedOut;
        }

        public boolean isCarriedOut() {
            return _carriedOut;
        }
    }
}