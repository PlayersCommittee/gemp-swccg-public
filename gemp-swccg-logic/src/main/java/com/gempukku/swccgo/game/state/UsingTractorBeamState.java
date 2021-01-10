package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.RespondableEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


// This class contains the state information for an
// action using a tractor beam within a game of Gemp-Swccg.
//
public class UsingTractorBeamState {
    private SwccgGame _game;
    private PhysicalCard _tractorBeam;
    private Collection<PhysicalCard> _targets = new LinkedList<PhysicalCard>();
    private RespondableEffect _tractorBeamEffect;

    public UsingTractorBeamState(SwccgGame game, PhysicalCard tractorBeam) {
        _game = game;
        _tractorBeam = tractorBeam;
    }


    public PhysicalCard getTractorBeam() {
        return _tractorBeam;
    }

    public void setTarget(PhysicalCard target) {
        setTargets(Collections.singletonList(target));
    }

    public void replaceTarget(PhysicalCard oldTarget, PhysicalCard newTarget) {
        _targets.remove(oldTarget);
        if (!_targets.contains(newTarget)) {
            _targets.add(newTarget);
        }
    }

    public void setTargets(Collection<PhysicalCard> targets) {
        _targets = new LinkedList<PhysicalCard>(targets);
    }

    public Collection<PhysicalCard> getTargets() {
        return _targets;
    }

    /**
     * Sets the tractor beam effect.
     * @param tractorBeamEffect the tractor beam effect
     */
    public void setWeaponFiringEffect(RespondableEffect tractorBeamEffect) {
        _tractorBeamEffect = tractorBeamEffect;
    }

    /**
     * Gets the tractor beam effect.
     * @return the tractor beam effect
     */
    public RespondableEffect getWeaponFiringEffect() {
        return _tractorBeamEffect;
    }
}
