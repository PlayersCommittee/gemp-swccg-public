package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ShuttledResult;
import com.gempukku.swccgo.logic.timing.results.ShuttlingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An effect to shuttle characters to a starship at the related system using a shuttle vehicle at an exterior site.
 */
public class ShuttleUpUsingShuttleVehicleEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _shuttleVehicle;
    private PhysicalCard _starship;
    private Collection<PhysicalCard> _remainingCards;
    private List<PhysicalCard> _cardsMoved = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect to shuttle characters to a starship at the related system using a shuttle vehicle at an exterior site.
     * @param action the action performing this effect
     * @param shuttleVehicle the shuttle vehicle
     * @param starship the starship
     */
    public ShuttleUpUsingShuttleVehicleEffect(Action action, PhysicalCard shuttleVehicle, PhysicalCard starship) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _shuttleVehicle = shuttleVehicle;
        _starship = starship;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        SubAction subAction = new SubAction(_action);

        // Get the passengers of the shuttle vehicle that can shuttle to the starship
        _remainingCards = Filters.filterActive(game, null, Filters.canShuttleUpUsingShuttleVehicle(_shuttleVehicle, Filters.sameCardId(_starship)));

        if (!_remainingCards.isEmpty()) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            gameState.sendMessage(_playerId + " targets to shuttle characters to " + GameUtils.getCardLink(_starship) + " using " + GameUtils.getCardLink(_shuttleVehicle));
                            gameState.activatedCard(_playerId, _shuttleVehicle);
                            gameState.cardAffectsCard(_playerId, _shuttleVehicle, _starship);
                        }
                    }
            );
            subAction.appendEffect(
                    new ChooseNextCardToShuttle(subAction, game, _remainingCards));
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            // Record that regular move was performed and emit effect result
                            modifiersQuerying.regularMovePerformed(_shuttleVehicle);
                            for (PhysicalCard cardMoved : _cardsMoved) {
                                modifiersQuerying.regularMovePerformed(cardMoved);
                            }
                            game.getActionsEnvironment().emitEffectResult(new ShuttledResult(_cardsMoved, _playerId, _shuttleVehicle, _starship));
                        }
                    }
            );
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to be shuttled using the shuttle vehicle.
     */
    private class ChooseNextCardToShuttle extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;

        /**
         * Creates an effect for choosing the next card to be shuttled using the shuttle vehicle.
         * @param subAction the action
         * @param remainingCards the remaining cards to choose from to be shuttled
         */
        public ChooseNextCardToShuttle(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, _playerId, "Choose character to shuttle to " + GameUtils.getFullName(_starship), _cardsMoved.isEmpty() ? 1 : 0, 1, remainingCards);
            _subAction = subAction;
            _game = game;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            if (!selectedCards.isEmpty()) {
                final PhysicalCard selectedCard = selectedCards.iterator().next();

                final GameState gameState = _game.getGameState();
                final ModifiersQuerying modifiersQuerying = _game.getModifiersQuerying();

                // Emit effect result that card is beginning to move
                _game.getActionsEnvironment().emitEffectResult(new ShuttlingResult(selectedCard, _playerId, _shuttleVehicle, _starship));

                _subAction.insertEffect(
                        new PassthruEffect(_subAction) {
                            @Override
                            protected void doPlayEffect(final SwccgGame game) {

                                _cardsMoved.add(selectedCard);

                                // Check that card is in play and may still move
                                if (Filters.in_play.accepts(gameState, modifiersQuerying, selectedCard)
                                        && !modifiersQuerying.mayNotMove(gameState, selectedCard)) {

                                    // Need to determine capacity slot for character
                                    boolean canBePilot = Filters.hasAvailablePilotCapacity(selectedCard).accepts(gameState, modifiersQuerying, _starship);
                                    boolean canBePassenger = Filters.hasAvailablePassengerCapacity(selectedCard).accepts(gameState, modifiersQuerying, _starship);

                                    if (canBePilot && canBePassenger) {
                                        // Ask player to choose pilot or passenger capacity slot
                                        _subAction.insertEffect(new PlayoutDecisionEffect(_subAction, _subAction.getPerformingPlayer(),
                                                new MultipleChoiceAwaitingDecision("Choose capacity slot for  " + GameUtils.getCardLink(selectedCard) + " aboard " + GameUtils.getCardLink(_starship), new String[]{"Pilot", "Passenger"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        // Capacity slot chosen, move character.
                                                        if (index == 0) {
                                                            gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(selectedCard) + " from " + GameUtils.getCardLink(_shuttleVehicle) + " to " + GameUtils.getCardLink(_starship) + " as pilot");
                                                            gameState.moveCardToAttachedInPilotCapacitySlot(selectedCard, _starship);
                                                        } else {
                                                            gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(selectedCard) + " from " + GameUtils.getCardLink(_shuttleVehicle) + " to " + GameUtils.getCardLink(_starship) + " as passenger");
                                                            gameState.moveCardToAttachedInPassengerCapacitySlot(selectedCard, _starship);
                                                        }

                                                        // Get the passengers of the shuttle vehicle that can shuttle to the starship
                                                        _remainingCards = Filters.filterActive(game, null, Filters.canShuttleUpUsingShuttleVehicle(_shuttleVehicle, Filters.sameCardId(_starship)));

                                                        if (!_remainingCards.isEmpty()) {
                                                            _subAction.insertEffect(
                                                                    new ChooseNextCardToShuttle(_subAction, _game, _remainingCards));
                                                        }
                                                    }
                                                }));
                                    } else {
                                        // If both capacity slots were not available, move character to available slot.
                                        if (canBePilot) {
                                            gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(selectedCard) + " from " + GameUtils.getCardLink(_shuttleVehicle) + " to " + GameUtils.getCardLink(_starship) + " as pilot");
                                            gameState.moveCardToAttachedInPilotCapacitySlot(selectedCard, _starship);
                                        } else {
                                            gameState.sendMessage(_playerId + " shuttles " + GameUtils.getCardLink(selectedCard) + " from " + GameUtils.getCardLink(_shuttleVehicle) + " to " + GameUtils.getCardLink(_starship) + " as passenger");
                                            gameState.moveCardToAttachedInPassengerCapacitySlot(selectedCard, _starship);
                                        }

                                        // Get the passengers of the shuttle vehicle that can shuttle to the starship
                                        _remainingCards = Filters.filterActive(game, null, Filters.canShuttleUpUsingShuttleVehicle(_shuttleVehicle, Filters.sameCardId(_starship)));

                                        if (!_remainingCards.isEmpty()) {
                                            _subAction.insertEffect(
                                                    new ChooseNextCardToShuttle(_subAction, _game, _remainingCards));
                                        }
                                    }
                                } else {
                                    // Get the passengers of the shuttle vehicle that can shuttle to the starship
                                    _remainingCards = Filters.filterActive(game, null, Filters.canShuttleUpUsingShuttleVehicle(_shuttleVehicle, Filters.sameCardId(_starship)));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.insertEffect(
                                                new ChooseNextCardToShuttle(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                        }
                );
            }
        }
    }
}
