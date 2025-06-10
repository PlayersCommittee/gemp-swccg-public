package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRetrieveForceResult;
import com.gempukku.swccgo.logic.timing.results.ForceRetrievalInitiatedResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the specified player to retrieve a specified amount of Force or cards.
 */
public class ForceRetrievalEffect extends AbstractSubActionEffect {
    private PhysicalCard _sourceCard;
    private String _playerToRetrieveForce;
    private Zone _retrieveToZone;
    private float _initialAmount;
    private float _amountToRetrieve;
    private boolean _randomly;
    private boolean _retrieveSpecificCards;
    private boolean _upToAmount;
    private boolean _topmost;
    private Filterable _filters;
    private PhysicalCard _currentCardToRetrieve;
    private boolean _emittedAboutToRetrieveForceResult;
    private Zone _retrieveCurrentCardToZone;
    private List<PhysicalCard> _cardsRetrieved = new LinkedList<PhysicalCard>();
    private int _numCountedAsRetrieved;
    private boolean _forceRetrievalComplete;
    private ForceRetrievalEffect _forceRetrievalEffect;

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force.
     *
     * @param action         the action performing this effect
     * @param playerId       the player to retrieve Force
     * @param retrieveToZone the zone to retrieve to
     * @param amount         the amount of Force to retrieve
     * @param randomly       true if cards are retrieved randomly, otherwise false
     */
    protected ForceRetrievalEffect(PhysicalCard sourceCard, Action action, String playerId, Zone retrieveToZone, float amount, boolean randomly) {
        this(sourceCard, action, playerId, retrieveToZone, amount, randomly, false, false, false, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force.
     *
     * @param action         the action performing this effect
     * @param playerId       the player to retrieve Force
     * @param retrieveToZone the zone to retrieve to
     * @param amount         the amount of Force to retrieve
     * @param randomly       true if cards are retrieved randomly, otherwise false
     */
    protected ForceRetrievalEffect(Action action, String playerId, Zone retrieveToZone, float amount, boolean randomly) {
        this(action, playerId, retrieveToZone, amount, randomly, false, false, false, Filters.any);
    }

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force or cards.
     *
     * @param action                the action performing this effect
     * @param playerId              the player to retrieve Force
     * @param retrieveToZone        the zone to retrieve to
     * @param amount                the amount of Force to retrieve
     * @param randomly              true if cards are retrieved randomly, otherwise false
     * @param retrieveSpecificCards true if specific cards are searched for and retrieved, otherwise false
     * @param upToAmount            true if retrieval is up to specified amount, otherwise false
     * @param topmost               true if only the topmost cards should be chosen from, otherwise false
     * @param filters               the filter
     */
    protected ForceRetrievalEffect(Action action, String playerId, Zone retrieveToZone, float amount, boolean randomly, boolean retrieveSpecificCards, boolean upToAmount, boolean topmost, Filterable filters) {
        super(action);
        _sourceCard = action.getActionSource();
        _playerToRetrieveForce = playerId;
        _retrieveToZone = retrieveToZone;
        _initialAmount = amount;
        _randomly = randomly;
        _retrieveSpecificCards = retrieveSpecificCards;
        _upToAmount = upToAmount;
        _topmost = topmost;
        _filters = filters;
        _forceRetrievalEffect = this;
    }

    /**
     * Creates an effect that causes the specified player to retrieve a specified amount of Force or cards.
     *
     * @param sourceCard            source of the retrieval
     * @param action                the action performing this effect
     * @param playerId              the player to retrieve Force
     * @param retrieveToZone        the zone to retrieve to
     * @param amount                the amount of Force to retrieve
     * @param randomly              true if cards are retrieved randomly, otherwise false
     * @param retrieveSpecificCards true if specific cards are searched for and retrieved, otherwise false
     * @param upToAmount            true if retrieval is up to specified amount, otherwise false
     * @param topmost               true if only the topmost cards should be chosen from, otherwise false
     * @param filters               the filter
     */
    protected ForceRetrievalEffect(PhysicalCard sourceCard, Action action, String playerId, Zone retrieveToZone, float amount, boolean randomly, boolean retrieveSpecificCards, boolean upToAmount, boolean topmost, Filterable filters) {
        super(action);
        _sourceCard = sourceCard;
        _playerToRetrieveForce = playerId;
        _retrieveToZone = retrieveToZone;
        _initialAmount = amount;
        _randomly = randomly;
        _retrieveSpecificCards = retrieveSpecificCards;
        _upToAmount = upToAmount;
        _topmost = topmost;
        _filters = filters;
        _forceRetrievalEffect = this;
    }

    @Override
    public String getText(SwccgGame game) {
        return _playerToRetrieveForce + " retrieves " + GuiUtils.formatAsString(_initialAmount) + " Force";
    }

    /**
     * Determines if Force retrieval is complete.
     *
     * @param game the game
     * @param checkAmountToRetrieve true if check amount to retrieve, otherwise false
     * @param checkEmptyLostPile true if check for empty Lost Pile, otherwise false
     * @return true if Force retrieval is complete, otherwise false
     */
    private boolean checkIfComplete(SwccgGame game, boolean checkAmountToRetrieve, boolean checkEmptyLostPile) {
        if (_forceRetrievalComplete)
            return true;

        if (!game.getGameState().getTopForceRetrievalState().canContinue()) {
            _forceRetrievalComplete = true;
            return true;
        }

        if (checkAmountToRetrieve && ((_amountToRetrieve - _numCountedAsRetrieved) <= 0)) {
            _forceRetrievalComplete = true;
            return true;
        }

        if (checkEmptyLostPile && game.getGameState().getLostPile(_playerToRetrieveForce).isEmpty()) {
            _forceRetrievalComplete = true;
            return true;
        }

        return _forceRetrievalComplete;
    }

    /**
     * Gets the player that is retrieving Force.
     * @return the zone
     */
    public String getPlayerToRetrieveForce() {
        return _playerToRetrieveForce;
    }

    /**
     * Determines if the retrieved cards may be taken into hand.
     * @return true or false
     */
    public boolean mayBeTakenIntoHand() {
        return false;
    }

    /**
     * Determines if the retrieval may not be canceled.
     * @return true or false
     */
    public boolean mayNotBeCanceled() {
        return false;
    }

    /**
     * Determines if the Force retrieval is due to initiating battle.
     * @return true or false
     */
    public boolean isDueToInitiatingBattle() {
        return false;
    }

    /**
     * Gets any additional cards involved in the Force retrieval (other than the action source card).
     * @return cards involved in Force retrieval
     */
    public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
        return null;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);
        if (_initialAmount > 0) {
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            // 1) Check if Force retrieval is for initiating battle
                            if (isDueToInitiatingBattle()
                                    && modifiersQuerying.mayNotRetrieveForceForInitiatingBattle(gameState, _playerToRetrieveForce)) {
                                gameState.sendMessage(_playerToRetrieveForce + " may not retrieve Force for initiating battle");
                                return;
                            }

                            // 2) Check if may not continue due to cards not allowed to contribute to Force retrieval
                            List<PhysicalCard> cardsContributing = new ArrayList<PhysicalCard>();
                            if (_action.getActionSource() != null) {
                                cardsContributing.add(_action.getActionSource());
                            }
                            Collection<PhysicalCard> additionalCards = getAdditionalCardsInvolvedInForceRetrieval();
                            if (additionalCards != null) {
                                cardsContributing.addAll(additionalCards);
                            }

                            if (Filters.canSpot(cardsContributing, game, Filters.not(Filters.mayContributeToForceRetrieval))) {
                                gameState.sendMessage("Force retrieval not allowed due to including cards not allowed to contribute to Force retrieval");
                                return;
                            }

                            // 3) Begin Force retrieval
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            gameState.beginForceRetrieval(_forceRetrievalEffect);
                                        }
                                    }
                            );

                            // 3) Automatic and optional responses to Force retrieval being initiated
                            subAction.appendEffect(
                                    new TriggeringResultEffect(subAction, new ForceRetrievalInitiatedResult(_playerToRetrieveForce)));

                            // 4) Set/choose initial amount to retrieve
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game, false, false))
                                                return;

                                            // If "retrieve up to X cards", then allow player to look at Lost Pile and then choose amount to retrieve (1 to X).
                                            final SubAction chooseValueForXSubAction = new SubAction(subAction);
                                            if (_upToAmount) {
                                                int maxAmount = (int) Math.floor(_initialAmount);
                                                chooseValueForXSubAction.appendEffect(
                                                        new SendMessageEffect(chooseValueForXSubAction, _playerToRetrieveForce + " must choose value for X to retrieve 'up to X'"));
                                                chooseValueForXSubAction.appendEffect(
                                                        new LookAtLostPileEffect(chooseValueForXSubAction, _playerToRetrieveForce, _playerToRetrieveForce) {
                                                            @Override
                                                            public String getShownText() {
                                                                return "Viewing Lost Pile prior to choosing X for retrieving 'up to X'";
                                                            }
                                                        });
                                                chooseValueForXSubAction.appendEffect(
                                                        new PlayoutDecisionEffect(chooseValueForXSubAction, _playerToRetrieveForce,
                                                                new IntegerAwaitingDecision("Choose value for X", 1, maxAmount, maxAmount) {
                                                                    @Override
                                                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                                                        chooseValueForXSubAction.setActionMsg(_playerToRetrieveForce + " chooses " + result + " as value for X");
                                                                        _amountToRetrieve = result;
                                                                    }
                                                                }
                                                        )
                                                );
                                            } else {
                                                chooseValueForXSubAction.appendEffect(
                                                        new PassthruEffect(chooseValueForXSubAction) {
                                                            @Override
                                                            protected void doPlayEffect(SwccgGame game) {
                                                                _amountToRetrieve = _initialAmount;
                                                            }
                                                        }
                                                );
                                            }
                                            subAction.stackSubAction(chooseValueForXSubAction);
                                        }
                                    }
                            );

                            // 5) Update amount to retrieve
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game, true, false))
                                                return;

                                            float updatedAmount = modifiersQuerying.getForceToRetrieve(gameState, _playerToRetrieveForce, _action.getActionSource(), _amountToRetrieve);
                                            if (updatedAmount > _amountToRetrieve) {
                                                gameState.sendMessage(_playerToRetrieveForce + "'s Force retrieval amount increased to " + GuiUtils.formatAsString(updatedAmount));
                                            }
                                            else if (updatedAmount < _amountToRetrieve) {
                                                gameState.sendMessage(_playerToRetrieveForce + "'s Force retrieval amount reduced to " + GuiUtils.formatAsString(updatedAmount));
                                            }
                                            else {
                                                gameState.sendMessage(_playerToRetrieveForce + "'s Force retrieval amount is " + GuiUtils.formatAsString(updatedAmount));
                                            }
                                            _amountToRetrieve = updatedAmount;
                                        }
                                    }
                            );

                            // 6) Retrieve each Force one at a time
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game, true, true))
                                                return;

                                            // Retrieve each Force
                                            final SubAction retrieveOneForceSubAction = new SubAction(subAction);
                                            retrieveOneForceSubAction.appendEffect(
                                                    new ForceRetrievalEffect.RetrieveEachForceEffect(retrieveOneForceSubAction));
                                            subAction.stackSubAction(retrieveOneForceSubAction);
                                        }
                                    }
                            );

                            // 7) End Force retrieval
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            gameState.endForceRetrieval();
                                            cardsRetrieved(_cardsRetrieved);
                                        }
                                    }
                            );
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
     * A callback method for the cards retrieved.
     * @param retrievedCards the cards retrieved
     */
    protected void cardsRetrieved(Collection<PhysicalCard> retrievedCards) {
    }

    /**
     * A private effect that causes the specified player to retrieve each Force in the retrieval.
     */
    private class RetrieveEachForceEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the specified player to retrieve each Force in the retrieval.
         *
         * @param subAction the action performing this effect
         */
        private RetrieveEachForceEffect(SubAction subAction) {
            super(subAction);
            _parentSubAction = subAction;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {

            final SubAction subAction = new SubAction(_parentSubAction);

            // 1) Retrieve a Force if there is still Force retrieval remaining
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (checkIfComplete(game, true, !_retrieveSpecificCards))
                                return;

                            if (_retrieveSpecificCards) {
                                subAction.appendEffect(
                                        new ChooseCardsFromLostPileEffect(subAction, _playerToRetrieveForce, 1, 1, (int) Math.floor(_amountToRetrieve), true, _topmost, _filters) {
                                            @Override
                                            protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                                                if (selectedCards.size() == 1) {
                                                    _currentCardToRetrieve = selectedCards.iterator().next();
                                                    _retrieveCurrentCardToZone = _retrieveToZone;
                                                }
                                                else {
                                                    _forceRetrievalComplete = true;
                                                    game.getGameState().sendMessage("No valid cards found to retrieve in " + _playerToRetrieveForce + "'s Lost Pile");
                                                }
                                            }
                                            @Override
                                            public String getChoiceText(int numCardsToChoose) {
                                                return "Choose card" + GameUtils.s(numCardsToChoose) + " to retrieve";
                                            }
                                        }
                                );
                            }
                            else {
                                subAction.appendEffect(
                                        new PassthruEffect(subAction) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                if (_randomly) {
                                                    game.getGameState().shufflePile(_playerToRetrieveForce, Zone.LOST_PILE);
                                                }
                                                _currentCardToRetrieve = game.getGameState().getTopOfLostPile(_playerToRetrieveForce);
                                                _retrieveCurrentCardToZone = _retrieveToZone;
                                            }
                                        }
                                );
                            }

                            // Automatic and optional responses to Force retrieval amount set and player about to retrieve Force
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game, false, false))
                                                return;

                                            if (!_emittedAboutToRetrieveForceResult) {
                                                _emittedAboutToRetrieveForceResult = true;
                                                game.getActionsEnvironment().emitEffectResult(new AboutToRetrieveForceResult(_sourceCard, _playerToRetrieveForce, _amountToRetrieve));
                                            }
                                        }
                                    }
                            );

                            // Retrieve the card
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            if (checkIfComplete(game, false, false))
                                                return;

                                            if (_currentCardToRetrieve != null) {
                                                subAction.appendEffect(
                                                        new RetrieveOneCardEffect(subAction));
                                            }
                                        }
                                    }
                            );
                        }
                    }
            );

            // 2) Check if there is no more Force retrieval remaining
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (checkIfComplete(game, true, true))
                                return;

                            // Schedule another Force to be retrieved
                            _parentSubAction.appendEffect(
                                    new ForceRetrievalEffect.RetrieveEachForceEffect(_parentSubAction));
                        }
                    }
            );

            return subAction;
        }
    }

    /**
     * A private effect that causes the player to retrieve a card.
     */
    private class RetrieveOneCardEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the player to retrieve a card.
         *
         * @param action the action performing this effect
         */
        private RetrieveOneCardEffect(SubAction action) {
            super(action);
            _parentSubAction = action;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {
            final GameState gameState = game.getGameState();

            final SubAction subAction = new SubAction(_parentSubAction);
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {
                            if (GameUtils.getZoneFromZoneTop(_currentCardToRetrieve.getZone()) != Zone.LOST_PILE) {
                                return;
                            }

                            // If the card may be taken into hand (and not already being retrieved into hand), ask player where to retrieve card
                            if (mayBeTakenIntoHand() && _retrieveCurrentCardToZone != Zone.HAND) {
                                subAction.appendEffect(
                                        new PlayoutDecisionEffect(subAction, _playerToRetrieveForce,
                                                new MultipleChoiceAwaitingDecision("Choose where to retrieve " + GameUtils.getCardLink(_currentCardToRetrieve), new String[]{_retrieveCurrentCardToZone.getHumanReadable(), "Hand"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 1) {
                                                            gameState.sendMessage(_playerToRetrieveForce + " chooses to take retrieved Force, " + GameUtils.getCardLink(_currentCardToRetrieve) + ", into hand");
                                                            _retrieveCurrentCardToZone = Zone.HAND;
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }

                            // Retrieve the card
                            subAction.appendEffect(
                                    new PassthruEffect(subAction) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            _cardsRetrieved.add(_currentCardToRetrieve);
                                            if (_retrieveSpecificCards) {
                                                _numCountedAsRetrieved += Filters.and(_filters).acceptsCount(game, _currentCardToRetrieve);
                                            }
                                            else {
                                                _numCountedAsRetrieved++;
                                            }
                                            String msgText = _playerToRetrieveForce + (_randomly ? " randomly" : "") + " retrieves " + GameUtils.getCardLink(_currentCardToRetrieve);
                                            if (_retrieveCurrentCardToZone != Zone.USED_PILE) {
                                                msgText = msgText + (_retrieveCurrentCardToZone == Zone.HAND ? " into hand " : " to " + _retrieveCurrentCardToZone.getHumanReadable());
                                            }
                                            msgText = msgText + " (" + _cardsRetrieved.size() + " Force retrieved)";
                                            gameState.sendMessage(msgText);
                                            gameState.removeCardFromZone(_currentCardToRetrieve);
                                            gameState.addCardToTopOfZone(_currentCardToRetrieve, _retrieveCurrentCardToZone, _playerToRetrieveForce);

                                            // Emit the result effect that can trigger other cards
                                            game.getActionsEnvironment().emitEffectResult(new RetrieveForceResult(_sourceCard, _playerToRetrieveForce, _cardsRetrieved.size(), _currentCardToRetrieve));
                                        }
                                    }
                            );
                        }
                    }
            );
            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }
    }
}