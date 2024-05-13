package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.TransferredBetweenDockedStarshipsResult;

/**
 * An effect to transfer a card between docked starships.
 */
public class TransferBetweenStarshipsEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _cardTransferred;
    private PhysicalCard _transferredFrom;
    private PhysicalCard _transferredTo;
    private boolean _transferAsPilot;
    private boolean _transferAsVehicle;

    /**
     * Creates an effect to transfer a card between docked starships.
     * @param action the action performing this effect
     * @param cardTransferred the card to transfer
     * @param siteToTransferTo the starship site to transfer to
     */
    public TransferBetweenStarshipsEffect(Action action, PhysicalCard cardTransferred, PhysicalCard siteToTransferTo) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardTransferred = cardTransferred;
        _transferredFrom = cardTransferred.getAttachedTo() != null ? cardTransferred.getAttachedTo() : cardTransferred.getAtLocation();
        _transferredTo = siteToTransferTo;
    }

    /**
     * Creates an effect to transfer a card between docked starships.
     * @param action the action performing this effect
     * @param cardTransferred the card to transfer
     * @param starshipToTransferTo the starship to transfer to
     * @param transferAsPilot true if transferring to pilot capacity slot, otherwise false
     * @param transferAsVehicle true if transferring to vehicle capacity slot, otherwise false
     */
    public TransferBetweenStarshipsEffect(Action action, PhysicalCard cardTransferred, PhysicalCard starshipToTransferTo, boolean transferAsPilot, boolean transferAsVehicle) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardTransferred = cardTransferred;
        _transferredFrom = cardTransferred.getAttachedTo() != null ? cardTransferred.getAttachedTo() : cardTransferred.getAtLocation();
        _transferredTo = starshipToTransferTo;
        _transferAsPilot = transferAsPilot;
        _transferAsVehicle = transferAsVehicle;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_transferredTo.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            gameState.sendMessage(_playerId + " transfers " + GameUtils.getCardLink(_cardTransferred) + " from " + GameUtils.getCardLink(_transferredFrom) + " to " + GameUtils.getCardLink(_transferredTo));
            gameState.moveCardToLocation(_cardTransferred, _transferredTo);
        }
        else if (_cardTransferred.getBlueprint().getCardCategory() == CardCategory.CHARACTER || _cardTransferred.getBlueprint().isMovesLikeCharacter()) {
            if (_transferAsPilot) {
                if (_transferredTo.getBlueprint().getCardSubtype() == CardSubtype.TRANSPORT)
                    gameState.sendMessage(_playerId + " transfers " + GameUtils.getCardLink(_cardTransferred) + " from " + GameUtils.getCardLink(_transferredFrom) + " to " + GameUtils.getCardLink(_transferredTo) + " as driver");
                else
                    gameState.sendMessage(_playerId + " transfers " + GameUtils.getCardLink(_cardTransferred) + " from " + GameUtils.getCardLink(_transferredFrom) + " to " + GameUtils.getCardLink(_transferredTo) + " as pilot");
                gameState.moveCardToAttachedInPilotCapacitySlot(_cardTransferred, _transferredTo);
            }
            else {
                gameState.sendMessage(_playerId + " transfers " + GameUtils.getCardLink(_cardTransferred) + " from " + GameUtils.getCardLink(_transferredFrom) + " to " + GameUtils.getCardLink(_transferredTo) + " as passenger");
                gameState.moveCardToAttachedInPassengerCapacitySlot(_cardTransferred, _transferredTo);
            }
        }
        else {
            gameState.sendMessage(_playerId + " transfers " + GameUtils.getCardLink(_cardTransferred) + " from " + GameUtils.getCardLink(_transferredFrom) + " into cargo hold of " + GameUtils.getCardLink(_transferredTo));
            if (_transferAsVehicle)
                gameState.moveCardToAttachedInVehicleCapacitySlot(_cardTransferred, _transferredTo);
            else if (Filters.capital_starship.accepts(game, _cardTransferred))
                gameState.moveCardToAttachedInCapitalStarshipCapacitySlot(_cardTransferred, _transferredTo);
            else
                gameState.moveCardToAttachedInStarfighterOrTIECapacitySlot(_cardTransferred, _transferredTo);
        }

        // Emit effect result
        game.getActionsEnvironment().emitEffectResult(new TransferredBetweenDockedStarshipsResult(_cardTransferred, _playerId));
    }
}
