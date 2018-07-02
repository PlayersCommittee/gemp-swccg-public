package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.TransferBetweenStarshipsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * An action to transfer a card between docked starships.
 */
public class TransferBetweenDockedStarshipsAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToTransfer;
    private PhysicalCard _otherStarship;
    private List<PhysicalCard> _possibleDestinations = new ArrayList<PhysicalCard>();
    private boolean _destinationChosen;
    private StandardEffect _chooseDestinationEffect;
    private PhysicalCard _destination;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _transferAsPilot;
    private boolean _transferAsVehicle;
    private Effect _transferBetweenStarshipsEffect;
    private boolean _cardTransferred;
    private Action _that;

    /**
     * Creates an action to transfer a card between docked starships.
     * @param playerId the player
     * @param game the game
     * @param card the card to transfer
     */
    public TransferBetweenDockedStarshipsAction(final String playerId, SwccgGame game, final PhysicalCard card) {
        super(card, playerId);
        _cardToTransfer = card;
        PhysicalCard starship = card.getAttachedTo();
        PhysicalCard atLocation = card.getAtLocation();
        if (atLocation != null) {
            starship = Filters.findFirstFromAllOnTable(game, Filters.relatedStarshipOrVehicle(atLocation));
        }
        if (starship != null) {
            _otherStarship = starship.getShipdockedWith();
        }
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Determine the possible cards this card can transfer to
        _possibleDestinations.addAll(Filters.filterTopLocationsOnTable(game,
                Filters.and(Filters.siteOfStarshipOrVehicle(_otherStarship),
                        Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(playerId),
                        Filters.notProhibitedFromTarget(card))));

        if (_cardToTransfer.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
            boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
            if (canBePilot || canBePassenger) {
                _possibleDestinations.add(_otherStarship);
            }
        }
        else if (_cardToTransfer.getBlueprint().isMovesLikeCharacter()) {
            _possibleDestinations.add(_otherStarship);
        }
        else {
            boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
            boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
            if (canGoInVehicleSlot || canGoInStarshipSlot) {
                _possibleDestinations.add(_otherStarship);
            }
        }

        // Determine if destination needs to be chosen
        if (_possibleDestinations.size() == 1) {

            _destinationChosen = true;
            _destination = _possibleDestinations.get(0);

            if (_destination.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                _capacitySlotChosen = true;

                // Transfer to location
                _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _destination);
            }
            else if (_cardToTransfer.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                // Need to determine capacity slot for character
                boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
                boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);

                if (canBePilot && canBePassenger) {
                    // Ask player to choose pilot/driver or passenger capacity slot
                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToTransfer) + " aboard " + GameUtils.getCardLink(_otherStarship), new String[]{"Pilot", "Passenger"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    _capacitySlotChosen = true;
                                    _transferAsPilot = (index == 0);

                                    // Capacity slot chosen, transfer character.
                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, _transferAsPilot, false);
                                }
                            });
                }
                else {
                    _capacitySlotChosen = true;
                    _transferAsPilot = canBePilot;

                    // If both capacity slots were not available, transfer character to available slot.
                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, _transferAsPilot, false);
                }
            }
            else if (_cardToTransfer.getBlueprint().isMovesLikeCharacter()) {
                _capacitySlotChosen = true;

                // If both capacity slots were not available, transfer character to available slot.
                _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, false);
            }
            else {
                // Need to determine capacity slot for starship/vehicle
                boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
                boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);

                if (canGoInVehicleSlot && canGoInStarshipSlot) {
                    // Ask player to choose vehicle or  capacity slot
                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToTransfer) + " in cargo bay of " + GameUtils.getCardLink(_otherStarship), new String[]{"Vehicle", "Starship"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    _capacitySlotChosen = true;
                                    _transferAsVehicle = (index == 0);

                                    // Capacity slot chosen, transfer starship/vehicle.
                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, _transferAsVehicle);
                                }
                            });
                }
                else {
                    _capacitySlotChosen = true;
                    _transferAsVehicle = canGoInVehicleSlot;

                    // If both capacity slots were not available, transfer starship/vehicle to available slot.
                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, _transferAsVehicle);
                }
            }
        }
        else {
            // Choose destination
            _chooseDestinationEffect =
                    new ChooseCardOnTableEffect(_that, playerId, "Transfer " + GameUtils.getCardLink(card) + ". Choose where to transfer", _possibleDestinations) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            _destinationChosen = true;
                            _destination = selectedCard;

                            if (_destination.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                _capacitySlotChosen = true;

                                // Transfer to location
                                _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _destination);
                            }
                            else if (_cardToTransfer.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                // Need to determine capacity slot for character
                                boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
                                boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);

                                if (canBePilot && canBePassenger) {
                                    // Ask player to choose pilot/driver or passenger capacity slot
                                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToTransfer) + " aboard " + GameUtils.getCardLink(_otherStarship), new String[]{"Pilot", "Passenger"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    _capacitySlotChosen = true;
                                                    _transferAsPilot = (index == 0);

                                                    // Capacity slot chosen, transfer character.
                                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, _transferAsPilot, false);
                                                }
                                            });
                                }
                                else {
                                    _capacitySlotChosen = true;
                                    _transferAsPilot = canBePilot;

                                    // If both capacity slots were not available, transfer character to available slot.
                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, _transferAsPilot, false);
                                }
                            }
                            else if (_cardToTransfer.getBlueprint().isMovesLikeCharacter()) {
                                _capacitySlotChosen = true;

                                // If both capacity slots were not available, transfer character to available slot.
                                _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, false);
                            }
                            else {
                                // Need to determine capacity slot for starship/vehicle
                                boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);
                                boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToTransfer).accepts(gameState, modifiersQuerying, _otherStarship);

                                if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                    // Ask player to choose vehicle or  capacity slot
                                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToTransfer) + " in cargo bay of " + GameUtils.getCardLink(_otherStarship), new String[]{"Vehicle", "Starship"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    _capacitySlotChosen = true;
                                                    _transferAsVehicle = (index == 0);

                                                    // Capacity slot chosen, transfer starship/vehicle.
                                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, _transferAsVehicle);
                                                }
                                            });
                                }
                                else {
                                    _capacitySlotChosen = true;
                                    _transferAsVehicle = canGoInVehicleSlot;

                                    // If both capacity slots were not available, transfer starship/vehicle to available slot.
                                    _transferBetweenStarshipsEffect = new TransferBetweenStarshipsEffect(_that, _cardToTransfer, _otherStarship, false, _transferAsVehicle);
                                }
                            }
                        }
                    };
        }
    }

    @Override
    public String getText() {
        return "Transfer to other starship";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_destinationChosen) {
                _destinationChosen = true;
                appendCost(_chooseDestinationEffect);
                return getNextCost();
            }

            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                appendCost(_chooseCapacitySlotEffect);
                return getNextCost();
            }

            if (!_cardTransferred) {
                _cardTransferred = true;
                return _transferBetweenStarshipsEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardTransferred && _transferBetweenStarshipsEffect.wasCarriedOut();
    }
}
