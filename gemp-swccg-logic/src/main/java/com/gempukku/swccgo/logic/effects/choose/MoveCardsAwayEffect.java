package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;


/**
 * An effect that causes the specified player to move cards away.
 */
public class MoveCardsAwayEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Filterable _cardFilter;
    private boolean _asManyAsPossible;

    /**
     * Creates an effect that causes the specified player to move cards away.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     */
    public MoveCardsAwayEffect(Action action, String playerId, Filterable cardFilter) {
        this(action, playerId, cardFilter, true);
    }

    /**
     * Creates an effect that causes the specified player to move cards away.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardFilter the card filter
     * @param asManyAsPossible true if as many as possible must be moved, otherwise false
     */
    public MoveCardsAwayEffect(Action action, String playerId, Filterable cardFilter, boolean asManyAsPossible) {
        super(action);
        _performingPlayerId = playerId;
        _cardFilter = cardFilter;
        _asManyAsPossible = asManyAsPossible;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        Collection<PhysicalCard> cardsToMoveAway = Filters.filterActive(game,
                                _action.getActionSource(), Filters.and(_cardFilter, Filters.movableAsMoveAway(_performingPlayerId, false, 0, Filters.any)));
                        if (!cardsToMoveAway.isEmpty()) {
                            subAction.appendEffect(
                                new ChooseNextCardToMoveAway(subAction, game, cardsToMoveAway, true));
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
     * A private effect for choosing the next card.
     */
    private class ChooseNextCardToMoveAway extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card.
         * @param subAction the action
         * @param game the game
         * @param remainingCards the remaining cards
         * @param mustChooseOne true if must choose one, otherwise false
         */
        public ChooseNextCardToMoveAway(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards, boolean mustChooseOne) {
            super(subAction, subAction.getPerformingPlayer(), "Choose next card to move away", mustChooseOne ? 1 : 0, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                _game.getGameState().cardAffectsCard(_performingPlayerId, _action.getActionSource(), selectedCard);

                // Check if there is a valid move away action
                final Action moveAwayAction = selectedCard.getBlueprint().getMoveAwayAction(selectedCard.getOwner(), _game, selectedCard, false, 0, false, Filters.any);

                // SubAction to carry out moving the card away
                SubAction moveAwaySubAction = new SubAction(_subAction);
                moveAwaySubAction.appendEffect(
                        new StackActionEffect(moveAwaySubAction, moveAwayAction));
                _subAction.stackSubAction(moveAwaySubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filterActive(game,
                                            _action.getActionSource(), Filters.and(_cardFilter, Filters.movableAsMoveAway(_performingPlayerId, false, 0, Filters.any)));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseNextCardToMoveAway(_subAction, _game, _remainingCards, !_asManyAsPossible));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
