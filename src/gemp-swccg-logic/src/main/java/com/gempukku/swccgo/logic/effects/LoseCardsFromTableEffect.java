package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be lost.
 */
public class LoseCardsFromTableEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private boolean _allCardsSituation;
    private boolean _toBottomOfPile;
    private LoseCardsFromTableEffect _that;

    /**
     * Creates an effect that causes the specified cards on table to be lost.
     * @param action the action performing this effect
     * @param cards the cards
     */
    public LoseCardsFromTableEffect(Action action, Collection<PhysicalCard> cards) {
        this(action, cards, false, false);
    }

    /**
     * Creates an effect that causes the specified cards on table to be lost.
     * @param action the action performing this effect
     * @param cards the cards
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     */
    public LoseCardsFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean allCardsSituation) {
        this(action, cards, allCardsSituation, false);
    }

    /**
     * Creates an effect that causes the specified cards on table to be lost.
     * @param action the action performing this effect
     * @param cards the cards
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param toBottomOfPile true if cards are placed on the bottom of the Lost Pile, otherwise false
     */
    public LoseCardsFromTableEffect(Action action, Collection<PhysicalCard> cards, boolean allCardsSituation, boolean toBottomOfPile) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _cards.addAll(cards);
        _allCardsSituation = allCardsSituation;
        _toBottomOfPile = toBottomOfPile;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Determines if the cards are lost due to being 'eaten'.
     * @return true or false
     */
    protected boolean asEaten() {
        return false;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId != null ? _performingPlayerId : game.getGameState().getCurrentPlayerId());

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Filter for cards that are still on the table
                        if (_allCardsSituation) {
                            _cards = Filters.filter(_cards, game, Filters.and(Filters.onTable,
                                    Filters.or(Filters.character, Filters.creature, Filters.vehicle, Filters.starship, Filters.weapon, Filters.device)));
                        }
                        else {
                            _cards = Filters.filter(_cards, game, Filters.onTable);
                        }

                        if (!_cards.isEmpty()) {
                            subAction.appendEffect(
                                    new ChooseNextCardToLose(subAction, game, _cards));
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to lose from table.
     */
    private class ChooseNextCardToLose extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to lose from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToLose(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to be lost", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out losing card from table
                SubAction loseCardsSubAction = new SubAction(_subAction);
                loseCardsSubAction.appendEffect(
                        new LoseCardsFromTableSimultaneouslyEffect(loseCardsSubAction, selectedCards, _toBottomOfPile, _allCardsSituation, !_allCardsSituation) {
                            @Override
                            protected boolean asEaten() {
                                return _that.asEaten();
                            }
                        });
                // Stack sub-action
                _subAction.stackSubAction(loseCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.onTable);

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextCardToLose(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
