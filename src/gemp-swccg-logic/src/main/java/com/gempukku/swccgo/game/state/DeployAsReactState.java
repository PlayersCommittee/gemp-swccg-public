package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.logic.effects.RespondableDeployAsReactEffect;


/**
 * This class contains the state information for a deploy as 'react' action.
 */
public class DeployAsReactState {
    private RespondableDeployAsReactEffect _effect;

    /**
     * Creates state information for a deploy as 'react' action.
     * @param effect the deploy as 'react' effect
     */
    public DeployAsReactState(RespondableDeployAsReactEffect effect) {
        _effect = effect;
    }

    /**
     * Gets the respondable deploy as 'react' effect.
     * @return the effect
     */
    public RespondableDeployAsReactEffect getRespondableEffect() {
        return _effect;
    }
}
