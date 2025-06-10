package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect to steal the specified cards on table and attach them to a specified card.
 */
public class StealCardsAndAttachFromTableEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _remainingCards;
    private PhysicalCard _attachTo;

    /**
     * Creates an effect to steal the specified cards on table.
     * @param action the action performing this effect
     * @param cardsToSteal the cards to steal
     * @param attachTo the card to attach the stolen cards to
     */
    public StealCardsAndAttachFromTableEffect(Action action, Collection<PhysicalCard> cardsToSteal, PhysicalCard attachTo) {
        super(action);
        _remainingCards = Collections.unmodifiableCollection(cardsToSteal);
        _attachTo = attachTo;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        _remainingCards = Filters.filter(_remainingCards, game, Filters.onTable);

        if (!_remainingCards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCardToSteal(subAction, game, _remainingCards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next card to be stolen.
     */
    private class ChooseNextCardToSteal extends ChooseCardOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;

        /**
         * Creates an effect for choosing the next card to be stolen.
         * @param subAction the action
         * @param remainingCards the remaining cards to choose from to be stolen
         */
        public ChooseNextCardToSteal(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to steal", remainingCards);
            _subAction = subAction;
            _game = game;
        }

        @Override
        protected void cardSelected(final PhysicalCard selectedCard) {
            final GameState gameState = _game.getGameState();
            final ModifiersQuerying modifiersQuerying = _game.getModifiersQuerying();

            // Perform the StealOneCardIntoHandEffect on the selected card
            _subAction.appendEffect(
                    new StealOneCardAndAttachEffect(_subAction, selectedCard, _attachTo));
            _subAction.appendEffect(
                    new PassthruEffect(_subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _remainingCards.remove(selectedCard);
                            _remainingCards = Filters.filter(_remainingCards, game, Filters.onTable);
                            if (!_remainingCards.isEmpty()) {
                                _subAction.appendEffect(
                                        new StealCardsAndAttachFromTableEffect.ChooseNextCardToSteal(_subAction, game, _remainingCards));
                            }
                        }
                    }
            );
        }
    }
}
