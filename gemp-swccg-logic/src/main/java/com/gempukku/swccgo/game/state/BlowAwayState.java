package com.gempukku.swccgo.game.state;


import com.gempukku.swccgo.logic.effects.BlowAwayEffect;

/**
 * This class contains the state information for a blow away action.
 */
public class BlowAwayState {
    private int _id;
    private BlowAwayEffect _blowAwayEffect;

    /**
     * Creates state information for a blow away action.
     * @param id the unique id
     * @param blowAwayEffect the blow away effect
     */
    public BlowAwayState(int id, BlowAwayEffect blowAwayEffect) {
        _id = id;
        _blowAwayEffect = blowAwayEffect;
    }

    /**
     * Gets the unique id.
     * @return the unique id
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Gets the blow away effect.
     * @return the blow away effect
     */
    public BlowAwayEffect getBlowAwayEffect() {
        return _blowAwayEffect;
    }
}
