package com.gempukku.swccgo.game.state;


import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;

/**
 * This class contains the state information for a draw destiny action.
 */
public class DrawDestinyState {
    private int _id;
    private DrawDestinyEffect _drawDestinyEffect;
    
    /**
     * Creates state information for a draw destiny action.
     * @param id the unique id
     * @param drawDestinyEffect the draw destiny effect
     */
    public DrawDestinyState(int id, DrawDestinyEffect drawDestinyEffect) {
        _id = id;
        _drawDestinyEffect = drawDestinyEffect;
    }

    /**
     * Gets the unique id.
     * @return the unique id
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Gets the draw destiny effect.
     * @return the draw destiny effect
     */
    public DrawDestinyEffect getDrawDestinyEffect() {
        return _drawDestinyEffect;
    }
}
