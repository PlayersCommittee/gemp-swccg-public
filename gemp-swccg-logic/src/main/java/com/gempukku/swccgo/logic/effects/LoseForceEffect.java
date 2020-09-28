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
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceNotFromBattleDamageResult;

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
    private boolean _isFromForceDrain;
    private boolean _isFromInsertCard;
    private boolean _fromHandOnly;
    private boolean _fromReserveDeckOnly;
    private boolean _fromLifeForceOnly;
    private int _amountLostSoFar;
    private float _currentAmountLeft;
    private float _maxForceLossAllowed;
    private boolean _forceLossComplete;
    private PhysicalCard _stackFaceDownOn;
    private boolean _asLiberationCard;
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
        this(action, playerToLoseForce, amount, cannotBeReduced, false, false, false, false, false, null, false);
    }

    /**
     * Creates an effect that causes the specified player to lose a specified amount of Force.
     * @param action the action performing this effect
     * @param playerToLoseForce the player
     * @param amount the amount of Force to lose
     * @param cannotBeReduced true if Force loss cannot be reduced, otherwise false
     * @param isFromForceDrain true if Force loss is from a Force drain, otherwise false
     * @param isFromForceDrain true if Force loss is from an 'insert' card, otherwise false
     * @param fromHandOnly true if Force must be lost from hand, otherwise false
     * @param fromReserveDeckOnly true if Force must be lost from Reserve Deck, otherwise false
     * @param fromLifeForceOnly true if Force must be lost from Life Force, otherwise false
     * @param stackFaceDownOn card that lost Force is instead stacked face down on, otherwise null
     * @param asLiberationCard the card lost as Force is stacked as a liberation card
     */
    protected LoseForceEffect(Action action, String playerToLoseForce, float amount, boolean cannotBeReduced, boolean isFromForceDrain, boolean isFromInsertCard, boolean fromHandOnly, boolean fromReserveDeckOnly, boolean fromLifeForceOnly, PhysicalCard stackFaceDownOn, boolean asLiberationCard) {
        super(action);
        _playerToLoseForce = playerToLoseForce;
        _initialAmount = amount;
        _isCannotBeReduced = cannotBeReduced;
        _isFromForceDrain = isFromForceDrain;
        _isFromInsertCard = isFromInsertCard;
        _fromHandOnly = fromHandOnly;
        _fromReserveDeckOnly = fromReserveDeckOnly;
        _fromLifeForceOnly = fromLifeForceOnly;
        _stackFaceDownOn = stackFaceDownOn;
        _asLiberationCard = asLiberationCard;
        _loseForceEffect = this;
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
    public boolean isCannotBeReduced() {
        return _isCannotBeReduced || (_stackFaceDownOn != null);
    }

    /**
     * Determines if the Force loss is from a Force drain.
     * @return true or false
     */
    public boolean isForceDrain() {
        return _isFromForceDrain;
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
        if (_isFromForceDrain) {
            float previousAmount = _initialAmount;
            ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
            _initialAmount = game.getModifiersQuerying().getForceDrainAmount(game.getGameState(), forceDrainState.getLocation(), forceDrainState.getPlayerId());
            // Update if amount changed
            if (previousAmount != _initialAmount) {
                game.getGameState().sendMessage(forceDrainState.getPlayerId() + " Force drains " + GuiUtils.formatAsString(_initialAmount) + " at " + GameUtils.getCardLink(forceDrainState.getLocation()));
                game.getModifiersQuerying().forceDrainPerformed(forceDrainState.getLocation(), _initialAmount);
            }
            _isCannotBeReduced = game.getModifiersQuerying().cantReduceForceLossFromForceDrainAtLocation(game.getGameState(), forceDrainState.getLocation(), _playerToLoseForce, forceDrainState.getPlayerId());
        }

        // Get "Force loss amount" since there may be modifiers for it
        _currentAmountLeft = Math.max(0, game.getModifiersQuerying().getForceToLose(game.getGameState(), _playerToLoseForce, _isCannotBeReduced, _initialAmount) - _amountLostSoFar);
        return _currentAmountLeft;
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
        if (_isFromForceDrain) {
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

            if (_isFromForceDrain) {
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
            _fromHand = !_fromReserveDeckOnly && !_fromLifeForceOnly;
            _fromSabaccHand = !_fromHandOnly && !_fromReserveDeckOnly;
            _fromReserveDeck = !_fromHandOnly;
            _fromForcePile = !_fromHandOnly && !_fromReserveDeckOnly;
            _fromUsedPile = !_fromHandOnly && !_fromReserveDeckOnly;
            _fromDrawnDestiny = !_fromHandOnly && !_fromReserveDeckOnly;
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

                            // Check if Force loss from Force drain must come certain places, if possible
                            if (_isFromForceDrain) {
                                boolean mustComeFromReserveDeck = topOfReserveDeck != null && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK, _playerToLoseForce);
                                boolean mustComeFromForcePile = topOfForcePile != null && game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.FORCE_DRAIN_LOST_FROM_FORCE_PILE, _playerToLoseForce);
                                if (mustComeFromReserveDeck || mustComeFromForcePile) {
                                    _fromHand = _fromSabaccHand = _fromUsedPile = false;
                                    _fromReserveDeck = mustComeFromReserveDeck;
                                    _fromForcePile = mustComeFromForcePile;
                                }
                            }

                            if (topOfReserveDeck != null && _fromReserveDeck)
                                selectableCards.add(topOfReserveDeck);

                            if (topOfForcePile != null && _fromForcePile)
                                selectableCards.add(topOfForcePile);

                            PhysicalCard topOfUsedPile = game.getGameState().getTopOfUsedPile(_playerToLoseForce);
                            if (topOfUsedPile != null && _fromUsedPile)
                                selectableCards.add(topOfUsedPile);

                            PhysicalCard topOfUnresolvedDestinyDraws = game.getGameState().getTopOfUnresolvedDestinyDraws(_playerToLoseForce);
                            if (topOfUnresolvedDestinyDraws != null && _fromDrawnDestiny)
                                selectableCards.add(topOfUnresolvedDestinyDraws);

                            if (_fromHand)
                                selectableCards.addAll(game.getGameState().getHand(_playerToLoseForce));

                            if (_fromSabaccHand)
                                selectableCards.addAll(game.getGameState().getSabaccHand(_playerToLoseForce));

                            // If an action is losing Force as part of the cost, and that action is from a card, exclude that card from being an eligible card to be lost.
                            Collection<PhysicalCard> cardsToLose = ((_isCannotBeReduced && subAction.getActionAttachedToCard() != null) ? Filters.filter(selectableCards, game, Filters.not(subAction.getActionAttachedToCard())) : selectableCards);

                            game.getUserFeedback().sendAwaitingDecision(_playerToLoseForce,
                                    new CardsSelectionDecision("Choose Force to lose", cardsToLose, 1, 1) {
                                        @Override
                                        public void decisionMade(String result) throws DecisionResultInvalidException {
                                            List<PhysicalCard> cards = getSelectedCardsByResponse(result);
                                            if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.DROIDS_SATISFY_FORCE_LOSS_UP_TO_THEIR_FORFEIT_VALUE, _playerToLoseForce)
                                                    && cards.get(0).getBlueprint().hasIcon(Icon.DROID) && (_fromHand || _fromUsedPile || _fromForcePile || _fromReserveDeck)) {
                                            	int forceLossToApply = (int)Math.min(getForceLossRemaining(game),cards.get(0).getBlueprint().getForfeit().intValue());
                                                _amountLostSoFar = _amountLostSoFar + forceLossToApply;
                                            } else {
                                                _amountLostSoFar++;
                                            }
                                            subAction.appendEffect(
                                                    new LoseOneForceEffect(subAction, cards.get(0), _amountLostSoFar, false, _isFromForceDrain, _stackFaceDownOn, _asLiberationCard) {
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
