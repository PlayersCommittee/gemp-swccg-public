package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.UsageEffect;


public class UseDeviceEffect extends AbstractStandardEffect implements UsageEffect {
    private PhysicalCard _user;
    private PhysicalCard _device;

    public UseDeviceEffect(Action action, PhysicalCard device) {
        this(action, (device.getAttachedTo() != null && device.getOwner().equals(device.getAttachedTo().getOwner())) ? device.getAttachedTo() : device, device);
    }

    public UseDeviceEffect(Action action, PhysicalCard user, PhysicalCard device) {
        super(action);
        _user = user;
        _device = device;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return _device.getBlueprint().getCardCategory()== CardCategory.DEVICE
                && GameConditions.canUseDevice(game, _user, _device);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (isPlayableInFull(game)) {
            game.getModifiersQuerying().deviceUsedBy(_user, _device);
            return new FullEffectResult(true);
        }

        return new FullEffectResult(false);
    }
}
