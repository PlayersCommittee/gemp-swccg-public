package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a device or weapon is transferred.
 */
public class TransferredDeviceOrWeaponResult extends EffectResult {
    private PhysicalCard _deviceOrWeapon;
    private PhysicalCard _transferredFrom;
    private PhysicalCard _transferredTo;

    /**
     * Creates an effect result that is emitted when a device or weapon is transferred.
     * @param deviceOrWeapon the device or weapon
     * @param transferredTo the card the device or weapon was transferred to
     */
    public TransferredDeviceOrWeaponResult(String playerId, PhysicalCard deviceOrWeapon, PhysicalCard transferredFrom, PhysicalCard transferredTo) {
        super(Type.TRANSFERRED_DEVICE_OR_WEAPON, playerId);
        _deviceOrWeapon = deviceOrWeapon;
        _transferredFrom = transferredFrom;
        _transferredTo = transferredTo;
    }

    /**
     * Gets the device or weapon that was transferred.
     * @return the device or weapon
     */
    public PhysicalCard getDeviceOrWeapon() {
        return _deviceOrWeapon;
    }

    /**
     * Gets the card the device or weapon was transferred from.
     * @return the card the device or weapon was transferred from
     */
    public PhysicalCard getTransferredFrom() {
        return _transferredFrom;
    }

    /**
     * Gets the card the device or weapon was transferred to.
     * @return the card the device or weapon was transferred to
     */
    public PhysicalCard getTransferredTo() {
        return _transferredTo;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_deviceOrWeapon) + " transferred to " + GameUtils.getCardLink(_transferredTo);
    }
}
