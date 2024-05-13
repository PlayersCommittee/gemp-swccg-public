package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.UsageEffect;


public class UseWeaponEffect extends AbstractStandardEffect implements UsageEffect {
    private PhysicalCard _user;
    private PhysicalCard _weapon;

    public UseWeaponEffect(Action action, PhysicalCard weapon) {
        this(action, (weapon.getAttachedTo() != null && weapon.getOwner().equals(weapon.getAttachedTo().getOwner())) ? weapon.getAttachedTo() : weapon, weapon);
    }

    public UseWeaponEffect(Action action, PhysicalCard user, PhysicalCard weapon) {
        super(action);
        _user = user;
        _weapon = weapon;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _weapon.getBlueprint().getCardCategory()== CardCategory.WEAPON
                && GameConditions.canUseWeapon(game, _user, _weapon);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            game.getModifiersQuerying().weaponUsedBy(_user, _weapon);
            return new FullEffectResult(true);
        }

        return new FullEffectResult(false);
    }
}
