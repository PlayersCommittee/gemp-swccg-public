package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDeployCostEffect;
import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractPlayCardAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.DeploySingleCardEffect;
import com.gempukku.swccgo.logic.effects.PayExtraCostToDeployCardEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.CaptureCharacterResult;

/**
 * The action to deploy a character.
 */
public class PlayCharacterAction extends AbstractPlayCardAction {
    private Action _that;
    private boolean _actionInitiated;
    private boolean _actionComplete;
    private PhysicalCard _character;
    private boolean _extraCostAdded;
    private boolean _forFree;
    private float _changeInCost;
    private ReactActionOption _reactActionOption;
    private boolean _chosenIfUndercover;
    private Effect _chooseIfUndercoverEffect;
    private String _zoneOwnerToDeployTo;
    private PhysicalCard _target;
    private boolean _capacitySlotChosen;
    private boolean _playAsPilot;
    private Effect _chooseCapacitySlotEffect;
    private boolean _forceCostApplied;
    private Effect _playCardEffect;
    private boolean _cardPlayed;

    /**
     * Creates an action to deploy the specified character.
     * @param game the game
     * @param sourceCard the card to initiate the deployment
     * @param character the character to deploy
     * @param forFree true if deploying character for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param deployTargetFilter the filter for where the character can be deployed
     */
    public PlayCharacterAction(final SwccgGame game, PhysicalCard sourceCard, final PhysicalCard character, boolean forFree, float changeInCost, final DeployAsCaptiveOption deployAsCaptiveOption, final ReactActionOption reactActionOption, final Filter deployTargetFilter) {
        super(character, sourceCard);
        setPerformingPlayer(character.getOwner());
        _that = this;
        _character = character;
        _forFree = forFree || (reactActionOption != null && (reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(game, character))));
        _changeInCost = changeInCost;
        _reactActionOption = reactActionOption;
        _text = reactActionOption != null ? "Deploy as 'react'" : "Deploy";
        if (character.getBlueprint().isOnlyDeploysAsEscortedCaptive(game, character)) {
            _text = "Deploy as escorted captive";
        }

        appendTargeting(
                new ChooseCardOnTableEffect(_that, getPerformingPlayer(), "Choose where to deploy " + GameUtils.getCardLink(character), deployTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard target) {
                        _target = target;

                        // If deploying at location, deploy character.
                        if (Filters.location.accepts(game, _target)) {
                            _capacitySlotChosen = true;

                            // Determine if deploying characters as an Undercover spy
                            if (_character.getBlueprint().mayDeployAsUndercoverSpy(game, _character)) {

                                // Ask player to choose whether to deploy character as Undercover spy
                                _chooseIfUndercoverEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                        new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(_character) + " as an Undercover spy?") {
                                            @Override
                                            protected void yes() {
                                                _chosenIfUndercover = true;
                                                _zoneOwnerToDeployTo = game.getOpponent(character.getOwner());
                                                _playCardEffect = new DeploySingleCardEffect(_that, _character, _target, !_character.getOwner().equals(_zoneOwnerToDeployTo), deployAsCaptiveOption, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                            }
                                            @Override
                                            protected void no() {
                                                _chosenIfUndercover = true;
                                                _zoneOwnerToDeployTo = character.getOwner();
                                                _playCardEffect = new DeploySingleCardEffect(_that, _character, _target, !_character.getOwner().equals(_zoneOwnerToDeployTo), deployAsCaptiveOption, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                            }
                                        });
                            }
                            else {
                                _chosenIfUndercover = true;
                                _zoneOwnerToDeployTo = _character.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, _character) ? game.getOpponent(character.getOwner()) : character.getOwner();
                                _playCardEffect = new DeploySingleCardEffect(_that, _character, _target, !_character.getOwner().equals(_zoneOwnerToDeployTo), deployAsCaptiveOption, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                            }
                        }
                        // If attaching to a card that is not a starship or vehicle, deploy character.
                        else if (!Filters.or(Filters.starship, Filters.vehicle).accepts(game, _target)) {
                            _chosenIfUndercover = true;
                            _capacitySlotChosen = true;
                            if (character.getBlueprint().isOnlyDeploysAsEscortedCaptive(game, character)) {
                                DeployAsCaptiveOption option = new DeployAsCaptiveOption();
                                option.setCaptureOption(CaptureOption.SEIZE);
                                _playCardEffect = new DeploySingleCardEffect(_that, _character, false, _target, option, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                GameState gameState = game.getGameState();
                                gameState.seizeCharacter(game, _character, _target);
                                game.getActionsEnvironment().emitEffectResult(new CaptureCharacterResult(_character.getOwner(), _character, _target, _character, false, false, CaptureOption.SEIZE));
                            }
                            else {
                                _playCardEffect = new DeploySingleCardEffect(_that, _character, false, _target, deployAsCaptiveOption, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                            }
                        }
                        // Otherwise, determine if character should deploy to pilot/driver or passenger capacity slot.
                        else {
                            _chosenIfUndercover = true;
                            boolean canBePilot = Filters.hasAvailablePilotCapacity(_character).accepts(game, _target);
                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_character).accepts(game, _target);

                            if (canBePilot && canBePassenger) {
                                String[] seatChoices;
                                if (Filters.transport_vehicle.accepts(game, _target))
                                    seatChoices = new String[]{"Driver", "Passenger"};
                                else
                                    seatChoices = new String[]{"Pilot", "Passenger"};

                                // Ask player to choose pilot/driver or passenger capacity slot
                                _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_character) + " aboard " + GameUtils.getCardLink(_target), seatChoices) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                _capacitySlotChosen = true;
                                                _playAsPilot = (index == 0);

                                                // Capacity slot chosen, deploy character.
                                                _playCardEffect = new DeploySingleCardEffect(_that, _character, _target, _playAsPilot, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                                            }
                                        });
                            } else {
                                _capacitySlotChosen = true;
                                _playAsPilot = canBePilot;

                                // If both capacity slots were not available, deploy character to available slot.
                                _playCardEffect = new DeploySingleCardEffect(_that, _character, _target, _playAsPilot, _reactActionOption, PlayCardOptionId.PLAY_CARD_OPTION_1, _reshuffle);
                            }
                        }
                    }
                }
        );
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

            // Perform any costs in the queue
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            // Choose if deploying as Undercover
            if (!_chosenIfUndercover) {
                _chosenIfUndercover = true;
                return _chooseIfUndercoverEffect;
            }

            // Choose the capacity slot
            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                return _chooseCapacitySlotEffect;
            }

            // Add any extra cost to deploy
            if (!_extraCostAdded) {
                _extraCostAdded = true;
                appendBeforeCost(new PayExtraCostToDeployCardEffect(this, _cardToPlay, _target, _forFree));
                return getNextCost();
            }

            // Pay the deploy cost(s)
            if (!_forceCostApplied) {
                _forceCostApplied = true;

                if (!_forFree) {
                    // Check if card has special cost (e.g. lose Force to play)
                    StandardEffect specialDeployCostEffect = _cardToPlay.getBlueprint().getSpecialDeployCostEffect(_that, getPerformingPlayer(), game, _cardToPlay, _target, null);
                    if (specialDeployCostEffect != null)
                        appendCost(specialDeployCostEffect);
                    else
                        appendCost(new PayDeployCostEffect(this, _character, _target, null, _changeInCost, _reactActionOption));
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
