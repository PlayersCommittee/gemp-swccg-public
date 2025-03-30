package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RelocatedDeviceOrWeaponBetweenCharactersResult;

public class RelocateDeviceOrWeaponBetweenCharactersEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _deviceOrWeapon;
    private PhysicalCard _fromCharacter;
    private PhysicalCard _toCharacter;

    /**
     * Creates an effect to relocate a weapon or device between characters
     *
     * @param action     the action performing this effect
     * @param cardToMove the weapon or device to move
     * @param moveFrom   the character the weapon or device is moving from
     * @param moveTo     the character the weapon or device is moving to
     */
    public RelocateDeviceOrWeaponBetweenCharactersEffect(Action action, PhysicalCard cardToMove, PhysicalCard moveFrom, PhysicalCard moveTo) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _deviceOrWeapon = cardToMove;
        _fromCharacter = moveFrom;
        _toCharacter = moveTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final PhysicalCard actionSource = _action.getActionSource();
        SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        gameState.sendMessage(_playerId + " relocates " + GameUtils.getCardLink(_deviceOrWeapon) + " to " + GameUtils.getCardLink(_toCharacter));
                        gameState.moveCardToAttached(_deviceOrWeapon, _toCharacter);
                        game.getActionsEnvironment().emitEffectResult(new RelocatedDeviceOrWeaponBetweenCharactersResult(_deviceOrWeapon, _playerId, _toCharacter));

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