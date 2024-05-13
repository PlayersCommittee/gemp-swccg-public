package com.gempukku.swccgo.game.state;


import com.gempukku.swccgo.logic.effects.ForceRetrievalEffect;

/**
 * This class contains the state information for a Force retrieval action.
 */
public class ForceRetrievalState {
    private int _id;
    private ForceRetrievalEffect _forceRetrievalEffect;
    private boolean _canceled;

    /**
     * Creates state information for a Force retrieval action.
     * @param id the unique id
     * @param forceRetrievalEffect the Force retrieval effect
     */
    public ForceRetrievalState(int id, ForceRetrievalEffect forceRetrievalEffect) {
        _id = id;
        _forceRetrievalEffect = forceRetrievalEffect;
    }

    /**
     * Gets the unique id.
     * @return the unique id
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Gets the Force retrieval effect.
     * @return the Force retrieval effect
     */
    public ForceRetrievalEffect getForceRetrievalEffect() {
        return _forceRetrievalEffect;
    }

    /**
     * Determines if the Force retrieval can continue.
     * @return true if Force retrieval can continue, otherwise false
     */
    public boolean canContinue() {
        return !_canceled;
    }

    /**
     * Sets Force retrieval as canceled.
     */
    public void cancel() {
        _canceled = true;
    }
}
