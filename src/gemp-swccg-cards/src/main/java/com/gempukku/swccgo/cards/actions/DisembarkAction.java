package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.cards.effects.PayDisembarkingCostEffect;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.DisembarkEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

/**
 * An action to disembark from a card.
 */
public class DisembarkAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private boolean _forFree;
    private boolean _destinationChosen;
    private StandardEffect _chooseDestinationEffect;
    private PhysicalCard _destination;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _moveAsPilot;
    private boolean _moveAsVehicle;
    private boolean _useForceCostApplied;
    private Effect _disembarkEffect;
    private boolean _cardMoved;
    private Action _that;

    /**
     * Creates an action to disembark from a card.
     * @param playerId the player
     * @param card the card to move
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     */
    public DisembarkAction(final String playerId, SwccgGame game, final PhysicalCard card, boolean forFree, final Filter moveTargetFilter) {
        super(card, playerId);
        _cardToMove = card;
        _forFree = forFree;
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Choose card to disembark to
        _chooseDestinationEffect =
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to disembark " + GameUtils.getCardLink(card), moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _destination = selectedCard;

                        // Check if disembarking to location
                        if (_destination.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                            _capacitySlotChosen = true;
                            _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, false, false);
                        } else {
                            if (_cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                // Need to determine capacity slot for character
                                boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);
                                boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);

                                if (canBePilot && canBePassenger) {
                                    String[] seatChoices;
                                    if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, _destination))
                                        seatChoices = new String[]{"Driver", "Passenger"};
                                    else
                                        seatChoices = new String[]{"Pilot", "Passenger"};

                                    // Ask player to choose pilot/driver or passenger capacity slot
                                    _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                            new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " aboard " + GameUtils.getCardLink(_destination), seatChoices) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    _capacitySlotChosen = true;
                                                    _moveAsPilot = (index == 0);

                                                    // Capacity slot chosen, move character.
                                                    _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, _moveAsPilot, false);
                                                }
                                            });
                                } else {
                                    _capacitySlotChosen = true;
                                    _moveAsPilot = canBePilot;

                                    // If both capacity slots were not available, move character to available slot.
                                    _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, _moveAsPilot, false);
                                }
                            } else if (_cardToMove.getBlueprint().isMovesLikeCharacter()) {
                                _capacitySlotChosen = true;
                                _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, false, false);
                            } else {
                                // Check if goes in capital starship capacity slot
                                boolean canGoInCapitalStarshipSlot = Filters.hasAvailableCapitalStarshipCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);
                                if (canGoInCapitalStarshipSlot) {
                                    _capacitySlotChosen = true;
                                    _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, false, false);
                                }
                                else {
                                    // Need to determine capacity slot for starship/vehicle
                                    boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);
                                    boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToMove).accepts(gameState, modifiersQuerying, _destination);

                                    if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                        // Ask player to choose vehicle or starship capacity slot
                                        _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " in cargo bay of " + GameUtils.getCardLink(_destination), new String[]{"Vehicle", "Starship"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        _capacitySlotChosen = true;
                                                        _moveAsVehicle = (index == 0);

                                                        // Capacity slot chosen, move starship/vehicle.
                                                        _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, false, _moveAsVehicle);
                                                    }
                                                });
                                    } else {
                                        _capacitySlotChosen = true;
                                        _moveAsVehicle = canGoInVehicleSlot;

                                        // If both capacity slots were not available, move starship/vehicle to available slot.
                                        _disembarkEffect = new DisembarkEffect(_that, _cardToMove, _destination, false, _moveAsVehicle);
                                    }
                                }
                            }
                        }
                    }
                };
    }

    @Override
    public String getText() {
        return "Disembark";
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

            if (!_useForceCostApplied) {
                _useForceCostApplied = true;
                if (!_forFree) {
                    appendCost(new PayDisembarkingCostEffect(_that, getPerformingPlayer(), _cardToMove, _destination, 0));
                    return getNextCost();
                }
            }

            if (!_cardMoved) {
                _cardMoved = true;
                return _disembarkEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardMoved && _disembarkEffect.wasCarriedOut();
    }
}
