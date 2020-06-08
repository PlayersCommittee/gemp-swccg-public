package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ChangeCapacitySlotResult;

/**
 * The effect to move a character between capacity slots.
 */
public class MoveBetweenCapacitySlotsEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _cardMoved;
    private PhysicalCard _cardAboard;

    /**
     * Create an effect to move the specified card between capacity slots.
     * @param action the action performing this effect
     * @param cardMoved the card to move
     */
    public MoveBetweenCapacitySlotsEffect(Action action, PhysicalCard cardMoved) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardMoved = cardMoved;
        _cardAboard = cardMoved.getAttachedTo();
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_cardMoved.isInCargoHoldAsStarfighterOrTIE()) {
            gameState.sendMessage(_playerId + " moves " + GameUtils.getCardLink(_cardMoved) + " from starfighter capacity slot to vehicle capacity slot in cargo hold of " + GameUtils.getCardLink(_cardAboard));
            gameState.moveCardToAttachedInVehicleCapacitySlot(_cardMoved, _cardAboard);
        }
        else if (_cardMoved.isInCargoHoldAsVehicle()) {
            gameState.sendMessage(_playerId + " moves " + GameUtils.getCardLink(_cardMoved) + " from vehicle capacity slot to starfighter capacity slot in cargo hold of " + GameUtils.getCardLink(_cardAboard));
            gameState.moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardMoved, _cardAboard);
        }
        else {

            String pilotSlotName = _cardAboard.getBlueprint().getCardSubtype() == CardSubtype.TRANSPORT ? "driver" : "pilot";

            if (_cardMoved.isPassengerOf()) {
                gameState.sendMessage(_playerId + " moves " + GameUtils.getCardLink(_cardMoved) + " from passenger capacity slot to " + pilotSlotName + " capacity slot of " + GameUtils.getCardLink(_cardAboard));
                gameState.moveCardToAttachedInPilotCapacitySlot(_cardMoved, _cardAboard);
            }
            else {
                gameState.sendMessage(_playerId + " moves " + GameUtils.getCardLink(_cardMoved) + " from " + pilotSlotName + " capacity slot to passenger capacity slot of " + GameUtils.getCardLink(_cardAboard));
                gameState.moveCardToAttachedInPassengerCapacitySlot(_cardMoved, _cardAboard);
            }
        }

        // Emit effect result
        game.getActionsEnvironment().emitEffectResult(new ChangeCapacitySlotResult(_cardMoved, _playerId));
    }
}
