package com.gempukku.swccgo.cards.effects;

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

public class ConvertLocationsByRaisingToTopEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private Collection<PhysicalCard> _topLocations = new ArrayList<>();
    private boolean _convertToOwnersOnly;

    public ConvertLocationsByRaisingToTopEffect(Action action, Collection<PhysicalCard> topLocations, boolean convertToOwnersOnly) {
        super(action);
        _performingPlayerId = action.getPerformingPlayer();
        _topLocations.addAll(topLocations);
        _convertToOwnersOnly = convertToOwnersOnly;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId != null ? _performingPlayerId : game.getGameState().getCurrentPlayerId());

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Filter for cards that are still on the table and are on top
                        _topLocations = Filters.filterTopLocationsOnTable(game, Filters.in(_topLocations));

                        if (!_topLocations.isEmpty()) {
                            subAction.appendEffect(
                                    new ConvertLocationsByRaisingToTopEffect.ChooseNextLocationToRaiseToTop(subAction, game, _topLocations));
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
     * A private effect for choosing the next location to convert
     */
    private class ChooseNextLocationToRaiseToTop extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next location to convert.
         * @param subAction the action
         * @param remainingCards the remaining cards to convert
         */
        public ChooseNextLocationToRaiseToTop(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose location to convert", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out converting a location
                SubAction convertLocationSubaction = new SubAction(_subAction);
                convertLocationSubaction.appendEffect(
                        new ConvertLocationByRaisingToTopEffect(_action, selectedCard, _convertToOwnersOnly));
                // Stack sub-action
                _subAction.stackSubAction(convertLocationSubaction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table and on top
                                    _remainingCards = Filters.filterTopLocationsOnTable(game, Filters.in(_remainingCards));

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ConvertLocationsByRaisingToTopEffect.ChooseNextLocationToRaiseToTop(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
