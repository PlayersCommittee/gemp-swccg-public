package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.TransferredDeviceOrWeaponResult;

/**
 * The effect to transfer a device or weapon to another card.
 */
public class TransferDeviceOrWeaponEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _deviceOrWeapon;
    private PhysicalCard _transferTo;
    private PlayCardOptionId _playCardOptionId;

    /**
     * Create an effect to transfer a weapon or device to a new holder.
     * @param action the action performing this effect
     * @param deviceOrWeapon the device or weapon
     * @param transferTo the card to transfer the device or weapon to
     * @param playCardOptionId the play card option id
     */
    public TransferDeviceOrWeaponEffect(Action action, PhysicalCard deviceOrWeapon, PhysicalCard transferTo, PlayCardOptionId playCardOptionId) {
        super(action);
        _deviceOrWeapon = deviceOrWeapon;
        _transferTo = transferTo;
        _playCardOptionId = playCardOptionId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        String performingPlayerId = _action.getPerformingPlayer();

        gameState.sendMessage(performingPlayerId + " transfers " + GameUtils.getCardLink(_deviceOrWeapon) + " from " + GameUtils.getCardLink(_deviceOrWeapon.getAttachedTo()) + " to " + GameUtils.getCardLink(_transferTo));
        var transferFrom = _deviceOrWeapon.getAttachedTo();
        gameState.moveCardToAttached(_deviceOrWeapon, _transferTo);
        _deviceOrWeapon.setPlayCardOptionId(_playCardOptionId);

        // Emit the result effect that can trigger other cards
        game.getActionsEnvironment().emitEffectResult(new TransferredDeviceOrWeaponResult(performingPlayerId, _deviceOrWeapon, transferFrom, _transferTo));
    }
}
