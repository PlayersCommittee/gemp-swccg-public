package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.cards.effects.PaySimultaneousDeployCostEffect;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collection;

/**
 * The action to deploy a starship or vehicle.
 */
public class PlayStarshipOrVehicleAction extends AbstractPlayCardAction {
    private Action _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private PhysicalCard _starshipOrVehicle;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private float _changeInCost;
    private ReactActionOption _reactActionOption;
    private String _pilotOrDriverText;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _deployInVehicleSlot;
    private PhysicalCard _pilot;
    private boolean _optionalPilotChosen;
    private PlayoutDecisionEffect _optionalPilotEffect;
    private boolean _pilotChosen;
    private ChooseCardsFromHandEffect _choosePilotEffect;
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
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param deployWithoutPilotTargetFilter the filter for where the starship/vehicle can be deployed without a simultaneously deployed pilot from hand
     * @param deployWithPilotTargetFilter the filter for where the starship/vehicle can be deployed with a simultaneously deployed pilot from hand
     * @param validPilotsFromHand the pilots/drivers from hand that can be deployed simultaneously with starship/vehicle
     */
    public PlayStarshipOrVehicleAction(final SwccgGame game, final PhysicalCard sourceCard, final PhysicalCard starshipOrVehicle, boolean forFree, float changeInCost, final DeploymentRestrictionsOption deploymentRestrictionsOption,
                                       final ReactActionOption reactActionOption, final Filter deployWithoutPilotTargetFilter, final Filter deployWithPilotTargetFilter, final Collection<PhysicalCard> validPilotsFromHand) {
        super(starshipOrVehicle, sourceCard);
        setPerformingPlayer(starshipOrVehicle.getOwner());
        _that = this;
        _starshipOrVehicle = starshipOrVehicle;
        _forFree = forFree || (reactActionOption != null && (reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(game, starshipOrVehicle))));
        _changeInCost = changeInCost;
        _reactActionOption = reactActionOption;
        _text = _reactActionOption != null ? "Deploy as 'react'" : "Deploy";
        _pilotOrDriverText = Filters.transport_vehicle.accepts(game, starshipOrVehicle) ? "driver" : "pilot";

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // If there are no pilots in hand to deploy simultaneously, then just deploy the starship/vehicle (since the only targets available
        // will be places the starship can deploy empty).
        if (validPilotsFromHand.isEmpty()) {
            _optionalPilotChosen = true;
            _pilotChosen = true;

            // Choose where to deploy starship/vehicle
            appendTargeting(
                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(_starshipOrVehicle), deployWithoutPilotTargetFilter) {
                        @Override
                        protected void cardSelected(PhysicalCard target) {
                            _target = target;

                            // If deploying at location, deploy starship/vehicle.
                            if (Filters.location.accepts(game, _target)) {
                                _capacitySlotChosen = true;
                                _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _target, false, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                            }
                            // Otherwise deploy starship/vehicle as attached.
                            else {
                                // Need to determine capacity slot for starship/vehicle
                                boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_starshipOrVehicle).accepts(game, _target);
                                boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_starshipOrVehicle).accepts(game, _target);

                                if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                    // Ask player to choose vehicle or starship capacity slot
                                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_starshipOrVehicle) + " in cargo bay of " + GameUtils.getCardLink(_starshipOrVehicle), new String[]{"Vehicle", "Starship"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    _capacitySlotChosen = true;
                                                    _deployInVehicleSlot = (index == 0);

                                                    // Capacity slot chosen, deploy starship/vehicle.
                                                    _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _deployInVehicleSlot, _target, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                                }
                                            });
                                } else {
                                    _capacitySlotChosen = true;
                                    _deployInVehicleSlot = canGoInVehicleSlot;

                                    // If both capacity slots were not available, deploy starship/vehicle to available slot.
                                    _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _deployInVehicleSlot, _target, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                }
                            }
                        }
                    });
            return;
        }

        // Check if there are any places the starship/vehicle can deploy without a separate pilot.
        boolean canDeployWithoutPilot = Filters.canSpot(game, _starshipOrVehicle, deployWithoutPilotTargetFilter);

        if (canDeployWithoutPilot) {
            // Both options are available, ask player if they want to simultaneously deploy a pilot/driver from hand
            _optionalPilotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                    new YesNoDecision("Do you want to simultaneously deploy a " + _pilotOrDriverText + " from hand aboard  " + GameUtils.getCardLink(_starshipOrVehicle)) {
                        @Override
                        protected void yes() {
                            _optionalPilotChosen = true;
                            _choosePilotEffect =
                                    new ChooseCardFromHandEffect(_that, getPerformingPlayer(), Filters.in(validPilotsFromHand), true) {
                                        @Override
                                        public String getChoiceText(int numCardsToChoose) {
                                            return "Choose a " + _pilotOrDriverText + " from hand to simultaneously deploy aboard " + GameUtils.getCardLink(_starshipOrVehicle);
                                        }

                                        @Override
                                        protected void cardSelected(final SwccgGame game, PhysicalCard selectedCard) {
                                            _pilotChosen = true;
                                            _pilot = selectedCard;

                                            // Filter to use with this starship/vehicle and pilot/driver
                                            Filter deployWithPilotFilter = Filters.and(_starshipOrVehicle.getBlueprint().getValidDeployTargetWithPilotOrPassengerFilter(getPerformingPlayer(), game, _starshipOrVehicle, sourceCard, _forFree, _changeInCost, _pilot, false, 0, deploymentRestrictionsOption, _reactActionOption), deployWithPilotTargetFilter);

                                            // Choose where to deploy starship/vehicle and pilot/driver
                                            appendTargeting(
                                                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(_starshipOrVehicle) + " and " + GameUtils.getCardLink(_pilot) + " simultaneously", deployWithPilotFilter) {
                                                        @Override
                                                        protected void cardSelected(PhysicalCard selectedCard) {
                                                            _target = selectedCard;

                                                            // If deploying at location, deploy starship/vehicle.
                                                            if (Filters.location.accepts(game.getGameState(), game.getModifiersQuerying(), _target)) {
                                                                _capacitySlotChosen = true;
                                                                _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _pilot, true, _target, false, reactActionOption, _reshuffle);
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

                                                                                    // Capacity slot chosen, deploy starship/vehicle.
                                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _pilot, true, _deployInVehicleSlot, reactActionOption, _reshuffle);
                                                                                }
                                                                            });
                                                                } else {
                                                                    _capacitySlotChosen = true;
                                                                    _deployInVehicleSlot = canGoInVehicleSlot;

                                                                    // If both capacity slots were not available, deploy starship/vehicle to available slot.
                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _pilot, true, _deployInVehicleSlot, reactActionOption, _reshuffle);
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    };
                        }

                        @Override
                        protected void no() {
                            _optionalPilotChosen = true;
                            _pilotChosen = true;

                            // Choose where to deploy starship/vehicle
                            appendTargeting(
                                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(_starshipOrVehicle), deployWithoutPilotTargetFilter) {
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            _target = selectedCard;

                                            // If deploying at location, deploy starship/vehicle.
                                            if (Filters.location.accepts(game.getGameState(), game.getModifiersQuerying(), _target)) {
                                                _capacitySlotChosen = true;
                                                _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _target, false, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
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

                                                                    // Capacity slot chosen, deploy starship/vehicle.
                                                                    _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _deployInVehicleSlot, _target, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                                                }
                                                            });
                                                } else {
                                                    _capacitySlotChosen = true;
                                                    _deployInVehicleSlot = canGoInVehicleSlot;

                                                    // If both capacity slots were not available, deploy starship/vehicle to available slot.
                                                    _playCardEffect = new DeploySingleCardEffect(_that, _starshipOrVehicle, _deployInVehicleSlot, _target, null, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                                }
                                            }
                                        }
                                    });
                        }
                    });
        } else {
            _optionalPilotChosen = true;
            _choosePilotEffect =
                    new ChooseCardFromHandEffect(_that, getPerformingPlayer(), Filters.in(validPilotsFromHand), true) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose a " + _pilotOrDriverText + " from hand to simultaneously deploy aboard " + GameUtils.getCardLink(_starshipOrVehicle);
                        }

                        @Override
                        protected void cardSelected(final SwccgGame game, PhysicalCard selectedCard) {
                            _pilotChosen = true;
                            _pilot = selectedCard;

                            // Filter to use with this starship/vehicle and pilot/driver
                            Filter deployWithPilotFilter = Filters.and(_starshipOrVehicle.getBlueprint().getValidDeployTargetWithPilotOrPassengerFilter(getPerformingPlayer(), game, _starshipOrVehicle, sourceCard, _forFree, _changeInCost, _pilot, false, 0, deploymentRestrictionsOption, _reactActionOption), deployWithPilotTargetFilter);

                            // Choose where to deploy starship/vehicle and pilot/driver
                            appendTargeting(
                                    new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(_starshipOrVehicle) + " and " + GameUtils.getCardLink(_pilot) + " simultaneously", deployWithPilotFilter) {
                                        @Override
                                        protected void cardSelected(PhysicalCard selectedCard) {
                                            _target = selectedCard;

                                            // If deploying at location, deploy starship/vehicle.
                                            if (Filters.location.accepts(game.getGameState(), game.getModifiersQuerying(), _target)) {
                                                _capacitySlotChosen = true;
                                                _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _pilot, true, _target, false, reactActionOption, _reshuffle);
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

                                                                    // Capacity slot chosen, deploy starship/vehicle.
                                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _pilot, true, _deployInVehicleSlot, reactActionOption, _reshuffle);
                                                                }
                                                            });
                                                } else {
                                                    _capacitySlotChosen = true;
                                                    _deployInVehicleSlot = canGoInVehicleSlot;

                                                    // If both capacity slots were not available, deploy starship/vehicle to available slot.
                                                    _playCardEffect = new DeployMultipleCardsSimultaneouslyEffect(_that, _starshipOrVehicle, _target, _pilot, true, _deployInVehicleSlot, reactActionOption, _reshuffle);
                                                }
                                            }
                                        }
                                    });
                        }
                    };
        }
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

            if (!_optionalPilotChosen) {
                _optionalPilotChosen = true;
                appendTargeting(_optionalPilotEffect);
                return getNextCost();
            }

            if (!_pilotChosen) {
                _pilotChosen = true;
                appendTargeting(_choosePilotEffect);
                return getNextCost();
            }

            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                appendTargeting(_chooseCapacitySlotEffect);
                return getNextCost();
            }

            // Add any extra cost to deploy
            if (!_extraCostAdded) {
                _extraCostAdded = true;

                if (_pilot != null) {
                    appendBeforeCost(new PayExtraCostToDeployCardsSimultaneouslyEffect(this, _cardToPlay, _pilot, _target, _forFree));
                    return getNextCost();
                }
                else {
                    appendBeforeCost(new PayExtraCostToDeployCardEffect(this, _cardToPlay, _target, _forFree));
                    return getNextCost();
                }
            }

            if (!_forceCostApplied) {
                _forceCostApplied = true;

                if (_pilot != null) {
                    appendCost(new PaySimultaneousDeployCostEffect(_that, _starshipOrVehicle, _forFree, _changeInCost, _pilot, false, 0, _target, _reactActionOption));
                    return getNextCost();
                }
                else if (!_forFree) {
                    appendCost(new PayDeployCostEffect(_that, _starshipOrVehicle, _target, null, _changeInCost, _reactActionOption));
                    return getNextCost();
                }
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
