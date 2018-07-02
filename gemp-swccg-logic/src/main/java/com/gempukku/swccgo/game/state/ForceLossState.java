package com.gempukku.swccgo.game.state;


import com.gempukku.swccgo.logic.effects.LoseForceEffect;

/**
 * This class contains the state information for a Force loss action.
 */
public class ForceLossState {
    private int _id;
    private LoseForceEffect _loseForceEffect;

    /**
     * Creates state information for a Force loss action.
     * @param id the unique id
     * @param loseForceEffect the lose Force effect
     */
    public ForceLossState(int id, LoseForceEffect loseForceEffect) {
        _id = id;
        _loseForceEffect = loseForceEffect;
    }

    /**
     * Gets the unique id.
     * @return the unique id
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Gets the lose Force effect.
     * @return the lose Force effect
     */
    public LoseForceEffect getLoseForceEffect() {
        return _loseForceEffect;
    }
}
