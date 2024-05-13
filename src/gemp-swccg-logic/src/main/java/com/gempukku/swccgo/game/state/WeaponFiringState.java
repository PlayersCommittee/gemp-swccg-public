package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.RespondableEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;


// This class contains the state information for a
// weapon firing action within a game of Gemp-Swccg.
//
public class WeaponFiringState {
    private SwccgGame _game;
    private PhysicalCard _weaponFiring;
    private SwccgBuiltInCardBlueprint _permanentWeaponFiring;
    private Collection<PhysicalCard> _targets = new LinkedList<PhysicalCard>();
    private PhysicalCard _cardFiringWeapon;
    private RespondableEffect _weaponFiringEffect;

    public WeaponFiringState(SwccgGame game, PhysicalCard weaponFiring, SwccgBuiltInCardBlueprint permanentWeapon) {
        _game = game;
        _weaponFiring = weaponFiring;
        _permanentWeaponFiring = permanentWeapon;
    }


    public PhysicalCard getCardFiring() {
        return _weaponFiring;
    }
    public SwccgBuiltInCardBlueprint getPermanentWeaponFiring() {
        return _permanentWeaponFiring;
    }

    public boolean isCardFiring(PhysicalCard card) {
        return _weaponFiring != null && card.getCardId() == _weaponFiring.getCardId();
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
        for (PhysicalCard target : targets) {
            if (_permanentWeaponFiring != null)
                _game.getModifiersQuerying().targetedByPermanentWeapon(target, _permanentWeaponFiring);
            else
                _game.getModifiersQuerying().targetedByWeapon(target, _weaponFiring);
        }
    }

    public Collection<PhysicalCard> getTargets() {
        return _targets;
    }

    public void setCardFiringWeapon(PhysicalCard cardFiringWeapon) {
        _cardFiringWeapon = cardFiringWeapon;
    }

    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * Sets the weapon firing effect.
     * @param weaponFiringEffect the weapon firing effect
     */
    public void setWeaponFiringEffect(RespondableEffect weaponFiringEffect) {
        _weaponFiringEffect = weaponFiringEffect;
    }

    /**
     * Gets the weapon firing effect.
     * @return the weapon firing effect
     */
    public RespondableEffect getWeaponFiringEffect() {
        return _weaponFiringEffect;
    }
}
