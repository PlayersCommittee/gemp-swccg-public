package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.MoveToRelatedStarshipOrVehicleEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

/**
 * An action to move to a starship or vehicle from a related starship or vehicle site.
 */
public class MoveToRelatedStarshipOrVehicleAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _cardToMove;
    private boolean _starshipOrVehicleChosen;
    private TargetingEffect _chooseStarshipOrVehicleEffect;
    private PhysicalCard _starshipOrVehicle;
    private boolean _capacitySlotChosen;
    private PlayoutDecisionEffect _chooseCapacitySlotEffect;
    private boolean _moveAsPilot;
    private boolean _moveAsVehicle;
    private Effect _moveToStarshipOrVehicleEffect;
    private boolean _cardMoved;
    private Action _that;

    /**
     * Creates an action to move to a starship or vehicle from a related starship or vehicle site.
     * @param playerId the player
     * @param game the game
     * @param card the card to move
     * @param moveTargetFilter the filter for where the card can be move
     */
    public MoveToRelatedStarshipOrVehicleAction(final String playerId, SwccgGame game, final PhysicalCard card, final Filter moveTargetFilter) {
        super(card, playerId);
        _cardToMove = card;
        _that = this;

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Choose starship/vehicle to move to
        _chooseStarshipOrVehicleEffect =
                new ChooseCardOnTableEffect(_that, playerId, "Choose where to move " + GameUtils.getCardLink(card), moveTargetFilter) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        _starshipOrVehicle = selectedCard;

                        if (_cardToMove.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                            // Need to determine capacity slot for character
                            boolean canBePilot = Filters.hasAvailablePilotCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _starshipOrVehicle);
                            boolean canBePassenger = Filters.hasAvailablePassengerCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _starshipOrVehicle);

                            if (canBePilot && canBePassenger) {
                                String[] seatChoices;
                                if (Filters.transport_vehicle.accepts(gameState, modifiersQuerying, _starshipOrVehicle))
                                    seatChoices = new String[]{"Driver", "Passenger"};
                                else
                                    seatChoices = new String[]{"Pilot", "Passenger"};

                                // Ask player to choose pilot/driver or passenger capacity slot
                                _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " aboard " + GameUtils.getCardLink(_starshipOrVehicle), seatChoices) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                _capacitySlotChosen = true;
                                                _moveAsPilot = (index == 0);

                                                // Capacity slot chosen, move character.
                                                _moveToStarshipOrVehicleEffect = new MoveToRelatedStarshipOrVehicleEffect(_that, _cardToMove, _starshipOrVehicle, _moveAsPilot, false);
                                            }
                                        });
                            } else {
                                _capacitySlotChosen = true;
                                _moveAsPilot = canBePilot;

                                // If both capacity slots were not available, move character to available slot.
                                _moveToStarshipOrVehicleEffect = new MoveToRelatedStarshipOrVehicleEffect(_that, _cardToMove, _starshipOrVehicle, _moveAsPilot, false);
                            }
                        } else if (_cardToMove.getBlueprint().isMovesLikeCharacter()) {
                            _capacitySlotChosen = true;

                            // If both capacity slots were not available, move character to available slot.
                            _moveToStarshipOrVehicleEffect = new MoveToRelatedStarshipOrVehicleEffect(_that, _cardToMove, _starshipOrVehicle, false, false);
                        } else {
                            // Need to determine capacity slot for starship/vehicle
                            boolean canGoInVehicleSlot = Filters.hasAvailableVehicleCapacity(_cardToMove).accepts(gameState, modifiersQuerying, _starshipOrVehicle);
                            boolean canGoInStarshipSlot = Filters.hasAvailableStarfighterOrTIECapacity(_cardToMove).accepts(gameState, modifiersQuerying, _starshipOrVehicle);

                            if (canGoInVehicleSlot && canGoInStarshipSlot) {
                                // Ask player to choose vehicle or  capacity slot
                                _chooseCapacitySlotEffect = new PlayoutDecisionEffect(_that, getPerformingPlayer(),
                                        new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(_cardToMove) + " in cargo bay of " + GameUtils.getCardLink(_starshipOrVehicle), new String[]{"Vehicle", "Starship"}) {
                                            @Override
                                            protected void validDecisionMade(int index, String result) {
                                                _capacitySlotChosen = true;
                                                _moveAsVehicle = (index == 0);

                                                // Capacity slot chosen, move starship/vehicle.
                                                _moveToStarshipOrVehicleEffect = new MoveToRelatedStarshipOrVehicleEffect(_that, _cardToMove, _starshipOrVehicle, false, _moveAsVehicle);
                                            }
                                        });
                            } else {
                                _capacitySlotChosen = true;
                                _moveAsVehicle = canGoInVehicleSlot;

                                // If both capacity slots were not available, move starship/vehicle to available slot.
                                _moveToStarshipOrVehicleEffect = new MoveToRelatedStarshipOrVehicleEffect(_that, _cardToMove, _starshipOrVehicle, false, _moveAsVehicle);
                            }
                        }
                    }
                };
    }

    @Override
    public String getText() {
        return "Move to related starship or vehicle";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        // Verify no costs have failed
        if (!isAnyCostFailed()) {

            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_starshipOrVehicleChosen) {
                _starshipOrVehicleChosen = true;
                appendTargeting(_chooseStarshipOrVehicleEffect);
                return getNextCost();
            }

            if (!_capacitySlotChosen) {
                _capacitySlotChosen = true;
                appendTargeting(_chooseCapacitySlotEffect);
                return getNextCost();
            }

            if (!_cardMoved) {
                _cardMoved = true;
                return _moveToStarshipOrVehicleEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }

    @Override
    public boolean wasActionCarriedOut() {
        return _cardMoved && _moveToStarshipOrVehicleEffect.wasCarriedOut();
    }
}
