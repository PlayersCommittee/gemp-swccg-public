package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.timing.Effect;

/**
 * Legacy stub for firing one weapon multiple times as a combined firing.
 *
 * Targeting Computer (1_39) fire-twice combined is implemented with SeparatelyOrCombinedFiringState,
 * not this class. Combined Attack (1_75) is multi-weapon and uses CombinedAttackFiringState plus
 * FireWeaponsCombinedAction. Do not treat Combined Attack as TC fire-twice.
 *
 * Left in place so the original filename remains; body stays unused.
 */
public class FireWeaponAsCombinedAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _weaponToFire;

    public FireWeaponAsCombinedAction(final SwccgGame game, PhysicalCard weaponToFire, PhysicalCard cardFiringWeapon, Filter filter, int timesToFire) {
        super(weaponToFire, weaponToFire.getOwner());
        _weaponToFire = weaponToFire;
        setPerformingPlayer(weaponToFire.getOwner());
    }

    @Override
    public PhysicalCard getActionSource() {
        return _weaponToFire;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public Effect nextEffect(final SwccgGame game) {
        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return false;
    }
}
