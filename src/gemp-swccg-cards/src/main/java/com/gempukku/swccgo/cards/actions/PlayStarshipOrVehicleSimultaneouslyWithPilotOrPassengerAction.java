package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PaySimultaneousDeployCostEffect;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DeployMultipleCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardsSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * The action to deploy a starship or vehicle simultaneously with a specified pilot.
 */
public class PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction extends AbstractPlayCardAction {
    private Action _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private PhysicalCard _starshipOrVehicle;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private float _changeInCost;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _deployInVehicleSlot;
    private boolean _characterMustBePilot;
    private boolean _capacitySlotForCharacterChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotForCharacterEffect;
    private boolean _playCharacterAsPilot;
    private PhysicalCard _character;
    private boolean _characterForFree;
    private float _characterChangeInCost;
    private PhysicalCard _target;
    private boolean _forceCostApplied;
    private boolean _cardPlayed;
    private Effect _playCardEffect;

    /**
     * Creates an action to deploy the specified starship/vehicle.
     * @param game the game
     * @param sourceCard the card to initiate the deployment
     * @param starshipOrVehicle the starship/vehicle to deploy
     * @param forFree true if deploying starship/vehicle for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deployWithCharacterTargetFilter the filter for where the starship/vehicle can be deployed with a simultaneously deployed character from hand
     * @param characterToDeployWith the pilot, driver, or passenger to deploy simultaneously with, or null
     * @param characterToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param characterToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     */
    public PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction(final SwccgGame game, final PhysicalCard sourceCard, final PhysicalCard starshipOrVehicle, boolean forFree, float changeInCost, final Filter deployWithCharacterTargetFilter,
                                                                         final PhysicalCard characterToDeployWith, final boolean characterToDeployWithForFree, final float characterToDeployWithChangeInCost, final DeploymentRestrictionsOption deploymentRestrictionsOption) {
        super(starshipOrVehicle, sourceCard);
        setPerformingPlayer(starshipOrVehicle.getOwner());
        _that = this;
        _starshipOrVehicle = starshipOrVehicle;
        _forFree = forFree;
        _changeInCost = changeInCost;
        _characterMustBePilot = !Filters.piloted.accepts(game, starshipOrVehicle);
        _character = characterToDeployWith;
        _characterForFree = characterToDeployWithForFree;
        _characterChangeInCost = characterToDeployWithChangeInCost;
        _text = "Deploy";

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        appendTargeting(
                new PassthruEffect(_that) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        // Update the card owner in case cards are being stolen as part of being deployed
                        starshipOrVehicle.setOwner(getPerformingPlayer());
                        characterToDeployWith.setOwner(getPerformingPlayer());

                        // Filter to use with this starship/vehicle and pilot/driver/passenger
                        Filter deployWithCharacterFilter = Filters.and(_starshipOrVehicle.getBlueprint().getValidDeployTargetWithPilotOrPassengerFilter(getPerformingPlayer(), game, _starshipOrVehicle, _that.getActionSource(), _forFree, _changeInCost, _character, _characterForFree, _characterChangeInCost, deploymentRestrictionsOption, null), deployWithCharacterTargetFilter);

                        // Choose where to deploy starship/vehicle and pilot/driver/passenger
                        appendTargeting(
                                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(_starshipOrVehicle) + " and " + GameUtils.getCardLink(_character) + " simultaneously", deployWithCharacterFilter) {
                                    @Override
                                    protected void cardSelected(PhysicalCard selectedCard) {
                                        _target = selectedCard;

                                        // If deploying at location, deploy starship/vehicle.
                                        if (Filters.location.accepts(game.getGameState(), game.getModifiersQuerying(), _target)) {
                                            _capacitySlotChosen = true;

                                            boolean canBePilot = Filters.hasAvailablePilotCapacity(_character).accepts(game, _starshipOrVehicle);
                                            boolean canBePassenger = !_characterMustBePilot && Filters.hasAvailablePassengerCapacity(_character).accepts(game, _starshipOrVehicle);

                                            if (canBePilot && canBePassenger) {
                                                String[] seatChoices;
                                                if (Filters.transport_vehicle.accepts(game, _starshipOrVehicle))
                                                    seatChoices = new String[]{"Driver", "Passenger"};
                                                else
                                                    seatChoices = new String[]{"Pilot", "Passenger"};

                                                // Ask player to choose pilot/driver or passenger capacity slot
                                                _chooseCapacitySlotForCharacterEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_character) + " aboard " + GameUtils.getCardLink(_starshipOrVehicle), seatChoices) {
                                                            @Override
                                                            protected void validDecisionMade(int index, String result) {
                                                                _capacitySlotForCharacterChosen = true;
                                                                _playCharacterAsPilot = (index == 0);

                                                                // Deploy starship/vehicle and character.
                                                                _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _character, _playCharacterAsPilot, _target, false, null, _reshuffle);
                                                            }
                                                        });
                                            }
                                            else {
                                                _capacitySlotForCharacterChosen = true;
                                                _playCharacterAsPilot = canBePilot;

                                                // Deploy starship/vehicle and character.
                                                _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _character, _playCharacterAsPilot, _target, false, null, _reshuffle);
                                            }
                                        }
                                        // Otherwise deploy starship/vehicle as attached.
                                        else {
                                            // Need to determine capacity slot for starship/vehicle
                                            boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_starshipOrVehicle).accepts(gameState, modifiersQuerying, _target);
                                            boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_starshipOrVehicle).accepts(gameState, modifiersQuerying, _target);

                                            if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                                // Ask player to choose vehicle or starship capacity slot
                                                _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_starshipOrVehicle) + " in cargo bay of " + GameUtils.getCardLink(_starshipOrVehicle), new String[]{"Vehicle", "Starship"}) {
                                                            @Override
                                                            protected void validDecisionMade(int index, String result) {
                                                                _capacitySlotChosen = true;
                                                                _deployInVehicleSlot = (index == 0);

                                                                boolean canBePilot = Filters.hasAvailablePilotCapacity(_character).accepts(game, _starshipOrVehicle);
                                                                boolean canBePassenger = !_characterMustBePilot && Filters.hasAvailablePassengerCapacity(_character).accepts(game, _starshipOrVehicle);

                                                                if (canBePilot && canBePassenger) {
                                                                    String[] seatChoices;
                                                                    if (Filters.transport_vehicle.accepts(game, _starshipOrVehicle))
                                                                        seatChoices = new String[]{"Driver", "Passenger"};
                                                                    else
                                                                        seatChoices = new String[]{"Pilot", "Passenger"};

                                                                    // Ask player to choose pilot/driver or passenger capacity slot
                                                                    _chooseCapacitySlotForCharacterEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_character) + " aboard " + GameUtils.getCardLink(_starshipOrVehicle), seatChoices) {
                                                                                @Override
                                                                                protected void validDecisionMade(int index, String result) {
                                                                                    _capacitySlotForCharacterChosen = true;
                                                                                    _playCharacterAsPilot = (index == 0);

                                                                                    // Deploy starship/vehicle and character.
                                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _character, _playCharacterAsPilot, _deployInVehicleSlot, null, _reshuffle);
                                                                                }
                                                                            });
                                                                }
                                                                else {
                                                                    _capacitySlotForCharacterChosen = true;
                                                                    _playCharacterAsPilot = canBePilot;

                                                                    // Deploy starship/vehicle and character.
                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _character, _playCharacterAsPilot, _deployInVehicleSlot, null, _reshuffle);
                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                _capacitySlotChosen = true;
                                                _deployInVehicleSlot = canGoInVehicleSlot;

                                                boolean canBePilot = Filters.hasAvailablePilotCapacity(_character).accepts(game, _starshipOrVehicle);
                                                boolean canBePassenger = !_characterMustBePilot && Filters.hasAvailablePassengerCapacity(_character).accepts(game, _starshipOrVehicle);

                                                if (canBePilot && canBePassenger) {
                                                    String[] seatChoices;
                                                    if (Filters.transport_vehicle.accepts(game, _starshipOrVehicle))
                                                        seatChoices = new String[]{"Driver", "Passenger"};
                                                    else
                                                        seatChoices = new String[]{"Pilot", "Passenger"};

                                                    // Ask player to choose pilot/driver or passenger capacity slot
                                                    _chooseCapacitySlotForCharacterEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_character) + " aboard " + GameUtils.getCardLink(_starshipOrVehicle), seatChoices) {
                                                                @Override
                                                                protected void validDecisionMade(int index, String result) {
                                                                    _capacitySlotForCharacterChosen = true;
                                                                    _playCharacterAsPilot = (index == 0);

                                                                    // Deploy starship/vehicle and character.
                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _character, _playCharacterAsPilot, _deployInVehicleSlot, null, _reshuffle);
                                                                }
                                                            });
                                                }
                                                else {
                                                    _capacitySlotForCharacterChosen = true;
                                                    _playCharacterAsPilot = canBePilot;

                                                    // Deploy starship/vehicle and character.
                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _character, _playCharacterAsPilot, _deployInVehicleSlot, null, _reshuffle);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
        );
    }

    /**
     * Gets the other played card in cases that two cards are deployed simultaneously.
     * @return the other played card
     */
    @Override
    public PhysicalCard getOtherPlayedCard() {
        return _character;
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (!_actionInitiated) {
            _actionInitiated = true;
            gameState.beginPlayCard(this);
        }

        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                appendTargeting(_chooseCapacitySlotEffect);
                return getNextCost();
            }

            if (!_capacitySlotForCharacterChosen) {
                _capacitySlotForCharacterChosen = true;
                appendTargeting(_chooseCapacitySlotForCharacterEffect);
                return getNextCost();
            }

            // Add any extra cost to deploy
            if (!_extraCostAdded) {
                _extraCostAdded = true;
                appendBeforeCost(new PayExtraCostToDeployCardsSimultaneouslyEffect(this, _starshipOrVehicle, _character, _target, _forFree));
                return getNextCost();
            }

            if (!_forceCostApplied) {
                _forceCostApplied = true;

                appendCost(new PaySimultaneousDeployCostEffect(_that, _starshipOrVehicle, _forFree, _changeInCost, _character, _characterForFree, _characterChangeInCost, _target, null));
                return getNextCost();
            }

            // Play the card
            if (!_cardPlayed) {
                _cardPlayed = true;

                return _playCardEffect;
            }
        }

        if (!_actionComplete) {
            _actionComplete = true;
            gameState.endPlayCard();
        }

        return null;
    }

    @Override
    public boolean wasCarriedOut() {
        return _cardPlayed && _playCardEffect.wasCarriedOut();
    }
}
