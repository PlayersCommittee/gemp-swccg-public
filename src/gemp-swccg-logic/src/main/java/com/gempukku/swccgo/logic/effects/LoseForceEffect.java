package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.CardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceNotFromBattleDamageResult;
import com.gempukku.swccgo.logic.timing.results.ForceLossInitiatedResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect that causes the specified player to lose a specified amount of Force.
 */
public class LoseForceEffect extends AbstractSubActionEffect {
    protected String _playerToLoseForce;
    protected float _initialAmount;
    private boolean _isCannotBeReduced;
    private ForceDrainState _forceDrainState;
    private boolean _isFromInsertCard;
    private boolean _fromHandOnly;
    private boolean _fromReserveDeckOnly;
    private boolean _fromForcePileOnly;
    private boolean _fromLifeForceOnly;
    private int _amountLostSoFar;
    private float _currentAmountLeft;
    private float _maxForceLossAllowed;
    private boolean _forceLossComplete;
    private PhysicalCard _stackOn;
    private boolean _stackFaceDown;
    private boolean _asLiberationCard;
    private float _cannotBeReducedBelow;
    private LoseForceEffect _loseForceEffect;

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     */
    public LoseForceEffect(Action action, String playerToLoseForce, float amount) {
        this(action, playerToLoseForce, amount, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     */
    public LoseForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced) {
        this(action, playerToLoseForce, amount, cannotBeReduced, null, false, false, false, false, null, false, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param cannotBeReducedBelow the amount below which the force loss cannot be reduced (if the initial amount is less than that then it will +)
     */
    public LoseForceEffect(Action action, String playerToLoseForce, float amount, float cannotBeReducedBelow) {
        this(action, playerToLoseForce, amount, false, null, false, false, false, false, null, false, false, false, cannotBeReducedBelow);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     *
     * @param action            the action performing this effect
     * @param playerToLoseForce the player
     * @param amount            the amount of Force to lose
     * @param cannotBeReduced   true if Force loss cannot be reduced, otherwise false
     * @param fromLifeForceOnly true if Force must be lost from Life Force, otherwise false
     */
    public LoseForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced, boolean fromLifeForceOnly) {
        this(action, playerToLoseForce, amount, cannotBeReduced, null, false, false, false, fromLifeForceOnly, null, false, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     * @param forceDrainState The active force drain state if this force loss is from a force drain, else null.
     * @param isFromInsertCard true if Force loss is from an 'insert' card, otherwise false
     * @param fromHandOnly true if Force must be lost from hand, otherwise false
     * @param fromReserveDeckOnly true if Force must be lost from Reserve Deck, otherwise false
     * @param fromLifeForceOnly true if Force must be lost from Life Force, otherwise false
     * @param stackOn card that lost Force is instead stacked on, otherwise null
     * @param stackFaceDown card that lost Force is stacked face down
     * @param asLiberationCard the card lost as Force is stacked as a liberation card
     */
    protected LoseForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced, ForceDrainState forceDrainState, boolean isFromInsertCard, boolean fromHandOnly, boolean fromReserveDeckOnly, boolean fromLifeForceOnly, PhysicalCard stackOn, boolean stackFaceDown, boolean asLiberationCard) {
        this(action, playerToLoseForce, amount, cannotBeReduced, forceDrainState, isFromInsertCard, fromHandOnly, fromReserveDeckOnly, fromLifeForceOnly, stackOn, stackFaceDown, asLiberationCard, false, Float.MIN_VALUE);
    }

    protected LoseForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced, ForceDrainState forceDrainState, boolean isFromInsertCard, boolean fromHandOnly, boolean fromReserveDeckOnly, boolean fromLifeForceOnly, PhysicalCard stackOn, boolean stackFaceDown, boolean asLiberationCard, boolean fromForcePileOnly, float cannotBeReducedBelow) {
        super(action);
        _playerToLoseForce = playerToLoseForce;
        _initialAmount = amount;
        _isCannotBeReduced = cannotBeReduced;
        _forceDrainState = forceDrainState;
        _isFromInsertCard = isFromInsertCard;
        _fromHandOnly = fromHandOnly;
        _fromReserveDeckOnly = fromReserveDeckOnly;
        _fromForcePileOnly = fromForcePileOnly;
        _fromLifeForceOnly = fromLifeForceOnly;
        _stackOn = stackOn;
        _stackFaceDown = stackFaceDown;
        _asLiberationCard = asLiberationCard;
        _loseForceEffect = this;
        _cannotBeReducedBelow = cannotBeReducedBelow;

        if(_forceDrainState != null) {
            _forceDrainState.updateTotal(_initialAmount);
        }
    }

    @Override
    public String getText(SwccgGame game) {
        return _playerToLoseForce + " loses " + GuiUtils.formatAsString(_initialAmount) + " Force";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        // Cannot lose Force before the 1st turn begins
        return game.getGameState().getCurrentPhase() != Phase.PLAY_STARTING_CARDS;
    }

    /**
     * Determines if the Force loss may not be reduced.
     * @return true or false
     */
    public boolean isCannotBeReduced(SwccgGame game) {
        return _isCannotBeReduced || (_stackOn != null) || (getForceLossRemaining(game) <= _cannotBeReducedBelow);
    }

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true or false
     */
    public boolean isForceDrain() {
        return _forceDrainState != null;
    }

    /**
     * Determines if the Force loss is from an insert card.
     * @return true or false
     */
    public boolean isFromInsertCard() {
        return _isFromInsertCard;
    }

    /**
     * Determines if a card lost as Force is shown if lost from hand.
     * @return true or false
     */
    public boolean isShownIfLostFromHand() {
        return false;
    }

    /**
     * Gets the amount of Force loss remaining.
     * @param game the game
     * @return the amount of Force loss remaining
     */
    public float getForceLossRemaining(SwccgGame game) {
        // If this is from a Force drain, need to recheck the Force drain amount,
        // since cards like It's Worse actually act as a Force drain bonus and
        // cause the Force drain amount to need to be recalculated.
        if (isForceDrain()) {
            float previousAmount = _initialAmount;
            ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
            _initialAmount = game.getModifiersQuerying().getForceDrainAmount(game.getGameState(), forceDrainState.getLocation(), forceDrainState.getPlayerId());
            // Update if amount changed
            if (previousAmount != _initialAmount) {
                game.getGameState().sendMessage(forceDrainState.getPlayerId() + " Force drains " + GuiUtils.formatAsString(_initialAmount) + " at " + GameUtils.getCardLink(forceDrainState.getLocation()));
                game.getModifiersQuerying().forceDrainPerformed(forceDrainState.getLocation(), _initialAmount);
                if(_forceDrainState != null) {
                    _forceDrainState.updateTotal(_initialAmount);
                }
            }
            _isCannotBeReduced = game.getModifiersQuerying().cantReduceForceLossFromForceDrainAtLocation(game.getGameState(), forceDrainState.getLocation(), _playerToLoseForce, forceDrainState.getPlayerId());
        }

        // Get "Force loss amount" since there may be modifiers for it
        _currentAmountLeft = Math.max(0, game.getModifiersQuerying().getForceToLose(game.getGameState(), _playerToLoseForce, _isCannotBeReduced, _initialAmount) - _amountLostSoFar);
        if(_forceDrainState != null) {
            _forceDrainState.updateRemaining(_currentAmountLeft);
        }
        return _currentAmountLeft;
    }

    /**
     * If the value is being reduced, it cannot be reduced to a number below the returned value unless it was already below it
     * @return minimum if being reduced
     */
    public float cannotBeReducedBelow() {
        return _cannotBeReducedBelow;
    }

    /**
     * Determines if Force loss is complete.
     * @param game the game
     * @return true if Force loss is complete, otherwise false
     */
    private boolean checkIfComplete(SwccgGame game) {
        if (_forceLossComplete)
            return true;

        if (getForceLossRemaining(game) == 0) {
            _forceLossComplete = true;
            return true;
        }

        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Get the maximum amount of Force that can be lost
        _maxForceLossAllowed = Integer.MAX_VALUE;
        if (isForceDrain()) {
            _maxForceLossAllowed = Math.min(_maxForceLossAllowed, modifiersQuerying.getForceToLoseFromForceDrainLimit(gameState, _playerToLoseForce, gameState.getForceDrainLocation()));
        }
        else if (_action.getActionAttachedToCard() != null) {
            _maxForceLossAllowed = Math.min(_maxForceLossAllowed, modifiersQuerying.getForceToLoseFromCardLimit(gameState, _playerToLoseForce, _action.getActionAttachedToCard()));
        }
        if (_isFromInsertCard) {
            _maxForceLossAllowed = Math.min(_maxForceLossAllowed, modifiersQuerying.getForceToLoseFromInsertCardLimit(gameState, _playerToLoseForce));
        }

        // If the amount of Force lost so far has reached the maximum Force to lose, then no Force needs to be lost
        if (_amountLostSoFar >= _maxForceLossAllowed) {
            StringBuilder msgText = new StringBuilder(_playerToLoseForce);
            if (_maxForceLossAllowed == 0) {
                msgText.append(" loses no Force");
            }
            else {
                msgText.append("'s Force loss limit of ");
                msgText.append(GuiUtils.formatAsString(_maxForceLossAllowed));
            }

            if (isForceDrain()) {
                msgText.append(" from Force drain at ").append(GameUtils.getCardLink(gameState.getForceDrainLocation()));
            }
            else if (_action.getActionAttachedToCard() != null) {
                msgText.append(" from ").append(GameUtils.getCardLink(_action.getActionAttachedToCard()));
            }

            if (_maxForceLossAllowed > 0) {
                msgText.append(" has been reached");
            }
            msgText.append(", remaining Force loss is canceled");
            gameState.sendMessage(msgText.toString());
            _forceLossComplete = true;
        }
        return _forceLossComplete;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final GameState gameState = game.getGameState();

        SubAction subAction = new SubAction(_action);
        if (isPlayableInFull(game)) {

            // 0) notify that force loss was initiated

            subAction.appendEffect(
                    new TriggeringResultEffect(subAction, new ForceLossInitiatedResult(subAction, _action.getActionSource(), _initialAmount)));
            if(_forceDrainState != null) {
                _forceDrainState.updateTotal(_initialAmount);
            }

            // 1) Begin Force loss
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            gameState.beginForceLoss(_loseForceEffect);
                        }
                    }
            );

            // 2) Perform loss of Force
            subAction.appendEffect(
                    new LoseEachForceEffect(subAction));

            // 3) End Force loss
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            gameState.endForceLoss();
                        }
                    }
            );
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _amountLostSoFar >= Math.min(_initialAmount, _maxForceLossAllowed);
    }

    /**
     * A private effect that causes the specified player to lose a Force.
     */
    private class LoseEachForceEffect extends AbstractSubActionEffect {
        private SubAction _parentSubAction;

        /**
         * Creates an effect that causes the specified player to lose a Force.
         * @param subAction the action performing this effect
         */
        private LoseEachForceEffect(SubAction subAction) {
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

            // 1) Check if the Force loss limit has been reached or if there is no more Force loss remaining
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            checkIfComplete(game);
                        }
                    }
            );

            // 2) Automatic and optional responses from "about to lose Force".
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (checkIfComplete(game))
                                return;

                            game.getActionsEnvironment().emitEffectResult(
                                    new AboutToLoseForceNotFromBattleDamageResult(subAction, _playerToLoseForce, _loseForceEffect));
                        }
                    }
            );

            // 3) Lose a Force if there is still Force loss remaining
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (checkIfComplete(game))
                                return;

                            // Send message of amount of Force lose remaining
                            StringBuilder msgText = new StringBuilder(_playerToLoseForce + " has " + GuiUtils.formatAsString(_currentAmountLeft) + " Force loss remaining to satisfy");
                            if (_maxForceLossAllowed != Integer.MAX_VALUE) {
                                msgText.append(" (with Force loss limited to ");
                                msgText.append(GuiUtils.formatAsString(_maxForceLossAllowed));
                                msgText.append(")");
                            }

                            if(_forceDrainState != null) {
                                _forceDrainState.updateRemaining(_currentAmountLeft);
                            }

                            game.getGameState().sendMessage(msgText.toString());

                            // Lose a Force
                            SubAction loseOneForceSubAction = new SubAction(subAction);
                            loseOneForceSubAction.appendEffect(
                                    new ChooseAndLoseNextForceEffect(loseOneForceSubAction));
                            subAction.stackSubAction(loseOneForceSubAction);
                        }
                    }
            );

            // 4) Check if the Force loss limit has been reached or if there is no more Force loss remaining
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            if (checkIfComplete(game))
                                return;

                            // Schedule another Force to be lost
                            _parentSubAction.insertEffect(
                                    new LoseEachForceEffect(_parentSubAction));
                        }
                    }
            );

            return subAction;
        }
    }

    /**
     * A private effect that causes the player to choose a Force to lose and then makes that Force lost.
     */
    private class ChooseAndLoseNextForceEffect extends AbstractSubActionEffect {
        private boolean _fromHand;
        private boolean _fromSabaccHand;
        private boolean _fromReserveDeck;
        private boolean _fromForcePile;
        private boolean _fromUsedPile;
        private boolean _fromDrawnDestiny;

        /**
         * Creates an effect that causes the player to choose a Force to lose and then makes that Force lost.
         * @param action the action performing this effect
         */
        private ChooseAndLoseNextForceEffect(SubAction action) {
            super(action);
            _fromHand = !_fromReserveDeckOnly && !_fromLifeForceOnly && !_fromForcePileOnly;
            _fromSabaccHand = !_fromHandOnly && !_fromReserveDeckOnly && !_fromForcePileOnly;
            _fromReserveDeck = !_fromHandOnly && !_fromForcePileOnly;
            _fromForcePile = !_fromHandOnly && !_fromReserveDeckOnly;
            _fromUsedPile = !_fromHandOnly && !_fromReserveDeckOnly && !_fromForcePileOnly;
            _fromDrawnDestiny = !_fromHandOnly && !_fromReserveDeckOnly && !_fromForcePileOnly;
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(SwccgGame game) {

            final SubAction subAction = new SubAction(_action);

            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(final SwccgGame game) {

                            // 1) The player chooses which card to lose as Force loss.
                            final List<PhysicalCard> selectableCards = new LinkedList<PhysicalCard>();

                            PhysicalCard topOfReserveDeck = game.getGameState().getTopOfReserveDeck(_playerToLoseForce);
                            PhysicalCard topOfForcePile = game.getGameState().getTopOfForcePile(_playerToLoseForce);

                            // check if all of the places that force loss "must come from" are empty
                            boolean allowForceLossFromAnywhereAvailable = !((_fromReserveDeckOnly && topOfReserveDeck != null) || (_fromForcePileOnly && topOfForcePile != null) || (_fromHandOnly && !game.getGameState().getHand(_playerToLoseForce).isEmpty()));

                            // Check if Force loss from Force drain must come certain places, if possible
                            if (isForceDrain()) {
                                boolean mustComeFromReserveDeck = topOfReserveDeck != null && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK, _playerToLoseForce);
                                boolean mustComeFromForcePile = topOfForcePile != null && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.FORCE_DRAIN_LOST_FROM_FORCE_PILE, _playerToLoseForce);
                                if (mustComeFromReserveDeck || mustComeFromForcePile) {
                                    _fromHand = _fromSabaccHand = _fromUsedPile = false;
                                    _fromReserveDeck = mustComeFromReserveDeck;
                                    _fromForcePile = mustComeFromForcePile;
                                    allowForceLossFromAnywhereAvailable = false;
                                }
                            }


                            if (topOfReserveDeck != null && (_fromReserveDeck || allowForceLossFromAnywhereAvailable))
                                selectableCards.add(topOfReserveDeck);

                            if (topOfForcePile != null && (_fromForcePile || allowForceLossFromAnywhereAvailable))
                                selectableCards.add(topOfForcePile);

                            PhysicalCard topOfUsedPile = game.getGameState().getTopOfUsedPile(_playerToLoseForce);
                            if (topOfUsedPile != null && (_fromUsedPile || allowForceLossFromAnywhereAvailable))
                                selectableCards.add(topOfUsedPile);

                            PhysicalCard topOfUnresolvedDestinyDraws = game.getGameState().getTopOfUnresolvedDestinyDraws(_playerToLoseForce);
                            if (topOfUnresolvedDestinyDraws != null && (_fromDrawnDestiny || allowForceLossFromAnywhereAvailable))
                                selectableCards.add(topOfUnresolvedDestinyDraws);

                            if (_fromHand || allowForceLossFromAnywhereAvailable)
                                selectableCards.addAll(game.getGameState().getHand(_playerToLoseForce));

                            if (_fromSabaccHand || allowForceLossFromAnywhereAvailable)
                                selectableCards.addAll(game.getGameState().getSabaccHand(_playerToLoseForce));

                            // If an action is losing Force as part of the cost, and that action is from a card, exclude that card from being an eligible card to be lost.
                            Collection<PhysicalCard> cardsToLose = ((_isCannotBeReduced && subAction.getActionAttachedToCard() != null) ? Filters.filter(selectableCards, game, Filters.not(subAction.getActionAttachedToCard())) : selectableCards);

                            game.getUserFeedback().sendAwaitingDecision(_playerToLoseForce,
                                    new CardsSelectionDecision("Choose Force to lose", cardsToLose, 1, 1) {
                                        @Override
                                        public void decisionMade(String result) throws DecisionResultInvalidException {
                                            List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                                            if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.DROIDS_SATISFY_FORCE_LOSS_UP_TO_THEIR_FORFEIT_VALUE, _playerToLoseForce)
                                                    && cards.getFirst().getBlueprint().hasIcon(Icon.DROID) && (_fromHand || _fromUsedPile || _fromForcePile || _fromReserveDeck)) {
                                                //get modifiers to droid forfeit (especially modifiers that set the forfeit value like Septoid droid)
                                                Float droidForfeit = game.getModifiersQuerying().getForfeit(game.getGameState(), cards.getFirst());

                                                //if it still isn't defined, treat it as 0
                                                int forceLossToApply = (int)Math.min(getForceLossRemaining(game), droidForfeit.intValue());

                                                _amountLostSoFar = _amountLostSoFar + forceLossToApply;
                                            } else {
                                                _amountLostSoFar++;
                                            }
                                            if(_forceDrainState != null) {
                                                _forceDrainState.updatePaid(_amountLostSoFar);
                                            }
                                            subAction.appendEffect(
                                                    new LoseOneForceEffect(subAction, cards.getFirst(), _amountLostSoFar, false, isForceDrain(), _stackOn, _stackFaceDown, _asLiberationCard) {
                                                        @Override
                                                        public boolean isShownIfLostFromHand() {
                                                            return _loseForceEffect.isShownIfLostFromHand();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
            );

            return subAction;
        }

        @Override
        public boolean wasActionCarriedOut() {
            return true;
        }
    }
}
