package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect to take a combination of cards into hand from the specified card pile.
 */
abstract class TakeCardCombinationIntoHandFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private String _cardPileOwner;
    private boolean _reshuffle;
    private boolean _hidden;
    private List<PhysicalCard> _cardsToTakeIntoHand = new LinkedList<PhysicalCard>();
    private List<PhysicalCard> _cardsTakenIntoHand = new LinkedList<PhysicalCard>();
    private TakeCardCombinationIntoHandFromPileEffect _that;

    /**
     * Creates an effect that causes the player to search the specified card pile and take a combination of cards into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardCombinationIntoHandFromPileEffect(Action action, String playerId, Zone cardPile, String cardPileOwner, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
        _cardPileOwner = cardPileOwner;
        _reshuffle = reshuffle;
        _hidden = false;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Gets a filter for cards that would be valid to add to the current selection to either become a valid combination
     * (or eventually become a valid after additional cards are added).
     * @param game the game
     * @param cardsSelected the current selection of cards
     * @return the filter
     */
    public abstract Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected);

    /**
     * Determines if the current selection of cards is a valid combination.
     * @param game the game
     * @param cardsSelected the current selection of cards
     * @return true if valid selection of cards is valid, otherwise false
     */
    public abstract boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected);

    public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
        return "Choose cards to take into hand";
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_cardPileOwner, _cardPile)
                    || (gameState.getCardPile(_cardPileOwner, _cardPile).size() > 1);
        }

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseCardCombinationEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        subAction.appendEffect(getTakeCardsIntoHandEffect(subAction));

                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Shuffle the card pile
                                        if (_reshuffle) {
                                            subAction.insertEffect(
                                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _playerId, _cardPileOwner, _cardPile, true));
                                        }
                                        else if (!_cardsTakenIntoHand.isEmpty()) {
                                            actionsEnvironment.emitEffectResult(
                                                    new RemovedFromCardPileResult(subAction));
                                        }
                                    }
                                }
                        );
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Only callback with the cards still in the player's hand
                                        cardsTakenIntoHand(Filters.filter(_cardsTakenIntoHand, game, Filters.inHand(_playerId)));
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    /**
     * Gets an effect that takes the cards into hand.
     * @param subAction the sub-action
     * @return the effect
     */
    private StandardEffect getTakeCardsIntoHandEffect(final SubAction subAction) {
        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (_cardsToTakeIntoHand.isEmpty())
                    return;

                final GameState gameState = game.getGameState();
                String cardInfo = _hidden ? GameUtils.numCards(_cardsToTakeIntoHand) : GameUtils.getAppendedNames(_cardsToTakeIntoHand);
                String msgText = _playerId + " takes " + cardInfo + " into hand from " + (_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ")) + _cardPile.getHumanReadable();

                gameState.removeCardsFromZone(_cardsToTakeIntoHand);
                for (PhysicalCard card : _cardsToTakeIntoHand) {
                    card.setOwner(_playerId);
                    gameState.addCardToZone(card, Zone.HAND, _playerId);
                    _cardsTakenIntoHand.add(card);
                }
                gameState.sendMessage(msgText);
            }
        };
    }

    /**
     * A private effect that performs the steps for choosing a valid combination of cards.
     */
    private class ChooseCardCombinationEffect extends AbstractSubActionEffect {

        /**
         * Creates an effect that performs the steps for choosing a valid combination of cards.
         * @param action the action performing this effect
         */
        public ChooseCardCombinationEffect(Action action) {
            super(action);
        }

        @Override
        public boolean isPlayableInFull(SwccgGame game) {
            return true;
        }

        @Override
        protected SubAction getSubAction(final SwccgGame game) {
            GameState gameState = game.getGameState();
            Collection<PhysicalCard> cardsInCardPile = gameState.getCardPile(_cardPileOwner, _cardPile);

            final SubAction subAction = new SubAction(_action);

            // Check if no valid combination can be found in the card pile
            if (!isValidCombinationPossible(game, cardsInCardPile)) {
                subAction.appendEffect(
                        new ChooseCardsFromPileEffect(subAction, _playerId, _cardPile, _cardPileOwner, 1, 1, 1, false, false, Filters.none) {
                            @Override
                            public String getChoiceText(int numCardsToChoose) {
                                return _that.getChoiceText(game, Collections.<PhysicalCard>emptyList());
                            }

                            @Override
                            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                            }
                        }
                );
            }
            else {
                // Select cards for the card combination until a valid combination is selected.
                subAction.appendEffect(
                        getSelectCardEffect(subAction, cardsInCardPile));
                // Check if player looked at cards in own card pile
                if (_cardPileOwner.equals(_playerId)) {
                    subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_playerId, _cardPile)));
                }
            }

            return subAction;
        }

        @Override
        protected boolean wasActionCarriedOut() {
            return true;
        }

        /**
         * Gets an effect that causes the player to select the next card to add to the card combination.
         * @param subAction the sub-action
         * @param cardsInPile the cards in the card pile
         * @return the effect
         */
        private StandardEffect getSelectCardEffect(final SubAction subAction, final Collection<PhysicalCard> cardsInPile) {
            return new PassthruEffect(subAction) {
                @Override
                protected void doPlayEffect(final SwccgGame game) {
                    boolean isCurrentCombinationValid = isSelectionValid(game, _cardsToTakeIntoHand);
                    Filter selectionFilter = getValidToSelectFilter(game, _cardsToTakeIntoHand);
                    final Collection<PhysicalCard> prevSelection = new LinkedList<PhysicalCard>(_cardsToTakeIntoHand);
                    Collection<PhysicalCard> selectableCards = Filters.filter(cardsInPile, game, Filters.or(selectionFilter, Filters.in(prevSelection)));
                    int max = Math.min(prevSelection.size() + 1, selectableCards.size());
                    int min = isCurrentCombinationValid ? prevSelection.size() : max;

                    game.getUserFeedback().sendAwaitingDecision(_playerId,
                            new ArbitraryCardsSelectionDecision(_that.getChoiceText(game, _cardsToTakeIntoHand), cardsInPile, prevSelection, selectableCards, min, max, true, null) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    _cardsToTakeIntoHand = getSelectedCardsByResponse(result);
                                    boolean isCurrentCombinationValid = isSelectionValid(game, _cardsToTakeIntoHand);
                                    Filter selectionFilter = getValidToSelectFilter(game, _cardsToTakeIntoHand);
                                    if (!isCurrentCombinationValid || !Filters.filterCount(cardsInPile, game, 1, Filters.and(selectionFilter, Filters.not(Filters.in(_cardsToTakeIntoHand)))).isEmpty()) {
                                        subAction.appendEffect(
                                                getSelectCardEffect(subAction, cardsInPile));
                                    }
                                }
                            });
                }
            };
        }
    }

    /**
     * Determines if any valid combination of cards is possible.
     * @param game the game
     * @param cardsInPile the cards in the card pile
     * @return true or false
     */
    private boolean isValidCombinationPossible(SwccgGame game, Collection<PhysicalCard> cardsInPile) {
        List<PhysicalCard> currentCombination = new LinkedList<PhysicalCard>();
        Collection<PhysicalCard> selectableCards = Filters.filter(cardsInPile, game, getValidToSelectFilter(game, Collections.<PhysicalCard>emptyList()));
        for (PhysicalCard selectableCard : selectableCards) {
            currentCombination.add(selectableCard);
            if (isValidCombinationPossibleInner(game, cardsInPile, currentCombination)) {
                return true;
            }
            currentCombination.remove(selectableCard);
        }
        return false;
    }

    /**
     * Determines if any valid combination of cards is possible within the current combination.
     * @param game the game
     * @param cardsInPile the cards in the card pile
     * @param currentCombination the current combination
     * @return true or false
     */
    private boolean isValidCombinationPossibleInner(SwccgGame game, Collection<PhysicalCard> cardsInPile, List<PhysicalCard> currentCombination) {
        if (isSelectionValid(game, currentCombination)) {
            return true;
        }

        Collection<PhysicalCard> selectableCards = Filters.filter(cardsInPile, game, Filters.and(getValidToSelectFilter(game, currentCombination), Filters.not(Filters.in(currentCombination))));
        for (PhysicalCard selectableCard : selectableCards) {
            currentCombination.add(selectableCard);
            if (isValidCombinationPossibleInner(game, cardsInPile, currentCombination)) {
                return true;
            }
            currentCombination.remove(selectableCard);
        }

        return false;
    }

    /**
     * Determines if the card selections are the same.
     * @param selection1 a selection
     * @param selection2 a selection
     * @return true or false
     */
    private boolean isSameSelections(Collection<PhysicalCard> selection1, Collection<PhysicalCard> selection2) {
        if (selection1.size() != selection2.size()) {
            return false;
        }

        for (PhysicalCard card1 : selection1) {
            boolean foundMatch = false;
            for (PhysicalCard card2 : selection2) {
                if (card1.getCardId() == card2.getCardId()) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }
        for (PhysicalCard card2 : selection2) {
            boolean foundMatch = false;
            for (PhysicalCard card1 : selection1) {
                if (card1.getCardId() == card2.getCardId()) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return !_cardsTakenIntoHand.isEmpty();
    }

    /**
     * A callback method for the cards taken into hand.
     * @param cardsTakenIntoHand the cards taken into hand
     */
    protected void cardsTakenIntoHand(Collection<PhysicalCard> cardsTakenIntoHand) {
    }
}
