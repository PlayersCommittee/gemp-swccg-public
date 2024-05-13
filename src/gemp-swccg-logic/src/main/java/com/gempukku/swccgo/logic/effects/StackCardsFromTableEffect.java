package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An effect that causes the specified cards on table to be stacked on a card.
 */
public class StackCardsFromTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _cards = new ArrayList<PhysicalCard>();
    private PhysicalCard _stackOn;
    private boolean _faceDown;

    /**
     * Creates an effect that causes the specified cards on table to be stacked on a card.
     * @param action the action performing this effect
     * @param cards the cards
     * @param stackOn the card to stack on
     */
    public StackCardsFromTableEffect(Action action, Collection<PhysicalCard> cards, PhysicalCard stackOn) {
        this(action, cards, stackOn, false);
    }

    /**
     * Creates an effect that causes the specified cards on table to be stacked on a card.
     * @param action the action performing this effect
     * @param cards the cards
     * @param stackOn the card to stack on
     * @param faceDown true if cards are stacked face down, otherwise false
     */
    public StackCardsFromTableEffect(Action action, Collection<PhysicalCard> cards, PhysicalCard stackOn, boolean faceDown) {
        super(action);
        _cards.addAll(cards);
        _stackOn = stackOn;
        _faceDown = faceDown;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return Filters.canSpot(_cards, game, Filters.onTable)
                && Filters.onTable.accepts(game.getGameState(), game.getModifiersQuerying(), _stackOn);
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        // Filter for cards that are still on the table
        _cards = Filters.filter(_cards, game, Filters.onTable);

        if (!_cards.isEmpty() && Filters.onTable.accepts(game.getGameState(), game.getModifiersQuerying(), _stackOn)) {
            subAction.appendEffect(
                    new ChooseNextCardToStack(subAction, game, _cards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to be stacked from table.
     */
    private class ChooseNextCardToStack extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to be stacked from table.
         * @param subAction the action
         * @param remainingCards the remaining cards to place in the card pile
         */
        public ChooseNextCardToStack(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to stack on " + GameUtils.getFullName(_stackOn), 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out stack card from table
                SubAction stackCardsSubAction = new SubAction(_subAction);
                stackCardsSubAction.appendEffect(
                        new StackCardsFromTableSimultaneouslyEffect(stackCardsSubAction, selectedCards, _stackOn, _faceDown, true));
                // Stack sub-action
                _subAction.stackSubAction(stackCardsSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, Filters.onTable);

                                    if (!_remainingCards.isEmpty() && Filters.onTable.accepts(_game.getGameState(), _game.getModifiersQuerying(), _stackOn)) {
                                        _subAction.appendEffect(
                                                new ChooseNextCardToStack(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
