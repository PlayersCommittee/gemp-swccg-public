package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RelocatingBetweenLocationsResult;

/**
 * An effect that relocates a card from a location to a starship or vehicle.
 */
public class RelocateFromLocationToStarshipOrVehicle extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private PhysicalCard _starshipOrVehicle;
    private boolean _asPilot;
    private PhysicalCard _source;

    /**
     * Creates an effect that relocates a card from a location to a starship or vehicle.
     * @param action the action performing this effect
     * @param card the card to relocate to starship or vehicle
     * @param starshipOrVehicle the starship or vehicle
     * @param asPilot true if relocating to pilot capacity slot, otherwise false
     */
    public RelocateFromLocationToStarshipOrVehicle(Action action, PhysicalCard card, PhysicalCard starshipOrVehicle, boolean asPilot, PhysicalCard source) {
        super(action);
        _card = card;
        _starshipOrVehicle = starshipOrVehicle;
        _asPilot = asPilot;
        _source = source;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        PhysicalCard locationRelocatingFrom = _card.getAtLocation();
        PhysicalCard locationRelocatingTo = modifiersQuerying.getLocationThatCardIsAt(gameState, _starshipOrVehicle);
        if (_asPilot) {
            if (_starshipOrVehicle.getBlueprint().getCardSubtype()== CardSubtype.TRANSPORT)
                gameState.sendMessage(GameUtils.getCardLink(_card) + " is relocated from " + GameUtils.getCardLink(locationRelocatingFrom) + " to " + GameUtils.getCardLink(_starshipOrVehicle) + " as driver");
            else
                gameState.sendMessage(GameUtils.getCardLink(_card) + " is relocated from " + GameUtils.getCardLink(locationRelocatingFrom) + " to " + GameUtils.getCardLink(_starshipOrVehicle) + " as pilot");
            gameState.moveCardToAttachedInPilotCapacitySlot(_card, _starshipOrVehicle);
        }
        else {
            gameState.sendMessage(GameUtils.getCardLink(_card) + " is relocated from " + GameUtils.getCardLink(locationRelocatingFrom) + " to " + GameUtils.getCardLink(_starshipOrVehicle) + " as passenger");
            gameState.moveCardToAttachedInPassengerCapacitySlot(_card, _starshipOrVehicle);
        }
        game.getActionsEnvironment().emitEffectResult(new RelocatingBetweenLocationsResult(_card, _source, _source.getOwner(), locationRelocatingFrom, locationRelocatingTo));
    }
}
