package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that transfers a device or weapon.
 */
public class TransferDeviceOrWeaponEffect extends AbstractSubActionEffect {
    private PhysicalCard _deviceOrWeapon;
    private PhysicalCard _transferTo;
    private boolean _forFree;

    /**
     * Creates an effect that transfers a device or weapon.
     * @param action the action performing this effect
     * @param deviceOrWeapon the device or weapon to be transferred
     * @param transferTo the card to transfer the device or weapon to
     * @param forFree true if the transfer is to be free, otherwise false
     */
    public TransferDeviceOrWeaponEffect(Action action, PhysicalCard deviceOrWeapon, PhysicalCard transferTo, boolean forFree) {
        super(action);
        _deviceOrWeapon = deviceOrWeapon;
        _transferTo = transferTo;
        _forFree = forFree;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Action transferDeviceOrWeaponAction = _deviceOrWeapon.getBlueprint().getTransferDeviceOrWeaponAction(_action.getPerformingPlayer(), game,
                                _deviceOrWeapon, _forFree, Filters.sameCardId(_transferTo));
                        if (transferDeviceOrWeaponAction != null) {
                            subAction.appendEffect(
                                    new StackActionEffect(subAction, transferDeviceOrWeaponAction));
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}