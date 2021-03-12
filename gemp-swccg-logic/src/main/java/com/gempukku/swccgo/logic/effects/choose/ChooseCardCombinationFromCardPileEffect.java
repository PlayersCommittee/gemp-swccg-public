package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect to choose a combination of cards from a specified pile.
 */
public abstract class ChooseCardCombinationFromCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private List<PhysicalCard> _cardsChosen = new LinkedList<PhysicalCard>();
    private ChooseCardCombinationFromCardPileEffect _that;

    /**
     * Creates an effect that causes the player to choose a combination of cards from a specified pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to take cards from
     */
    public ChooseCardCombinationFromCardPileEffect(Action action, String playerId, Zone cardPile) {
        super(action);
        _playerId = playerId;
        _cardPile = cardPile;
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
        return "Choose cards from " + _cardPile.getHumanReadable();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new ChooseCardCombinationEffect(subAction));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_cardsChosen.isEmpty()) {
                            cardsChosen(_cardsChosen);
                        }
                    }
                }
        );
        return subAction;
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
            Collection<PhysicalCard> cardsToChooseFrom = new LinkedList<PhysicalCard>();
            cardsToChooseFrom.addAll(gameState.getCardPile(_playerId, _cardPile));

            final SubAction subAction = new SubAction(_action);

            // Check if no valid combination can be found
            if (!isValidCombinationPossible(game, cardsToChooseFrom)) {
                subAction.appendEffect(
                        new ChooseCardsFromPileEffect(subAction, _playerId, _cardPile, _playerId, 1, 1, 1, false, false, Filters.none) {
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
                        getSelectCardEffect(subAction, cardsToChooseFrom));
                // Check if player looked at cards in own card pile
                subAction.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_playerId, _cardPile)));
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
                    boolean isCurrentCombinationValid = isSelectionValid(game, _cardsChosen);
                    Filter selectionFilter = getValidToSelectFilter(game, _cardsChosen);
                    final Collection<PhysicalCard> prevSelection = new LinkedList<PhysicalCard>(_cardsChosen);
                    Collection<PhysicalCard> selectableCards = Filters.filter(cardsInPile, game, Filters.or(selectionFilter, Filters.in(prevSelection)));
                    int max = Math.min(prevSelection.size() + 1, selectableCards.size());
                    int min = isCurrentCombinationValid ? prevSelection.size() : max;

                    game.getUserFeedback().sendAwaitingDecision(_playerId,
                            new ArbitraryCardsSelectionDecision(_that.getChoiceText(game, _cardsChosen), cardsInPile, prevSelection, selectableCards, min, max, true, null) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    _cardsChosen = getSelectedCardsByResponse(result);
                                    boolean isCurrentCombinationValid = isSelectionValid(game, _cardsChosen);
                                    Filter selectionFilter = getValidToSelectFilter(game, _cardsChosen);
                                    if (!isCurrentCombinationValid || !Filters.filterCount(cardsInPile, game, 1, Filters.and(selectionFilter, Filters.not(Filters.in(_cardsChosen)))).isEmpty()) {
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
        return !_cardsChosen.isEmpty();
    }

    /**
     * A callback method for the cards chosen.
     * @param cardsChosen the cards chosen in order
     */
    protected void cardsChosen(List<PhysicalCard> cardsChosen) {
    }
}
