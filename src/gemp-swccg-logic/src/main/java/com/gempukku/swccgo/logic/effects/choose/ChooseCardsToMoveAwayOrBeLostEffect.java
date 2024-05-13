package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ChooseToMoveAwayOrBeLostEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;


/**
 * An effect that causes the specified player to choose cards on table to move away (for free) or be lost.
 */
public class ChooseCardsToMoveAwayOrBeLostEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _maximum;
    private Filterable _cardFilter;

    /**
     * Creates an effect that causes the specified player to choose cards on table to move away (for free) or be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param numCards the number of cards
     * @param cardFilter the card filter
     */
    public ChooseCardsToMoveAwayOrBeLostEffect(Action action, String playerId, int numCards, Filterable cardFilter) {
        super(action);
        _performingPlayerId = playerId;
        _maximum = numCards;
        _cardFilter = cardFilter;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        int minimum = Math.min(_maximum, Filters.countActive(game, subAction.getActionSource(), _cardFilter));
                        subAction.appendTargeting(
                                new TargetCardsOnTableEffect(subAction, _performingPlayerId, "Choose card" + GameUtils.s(minimum) + " to move away (for free) or be lost", minimum, _maximum, _cardFilter) {
                                    @Override
                                    protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> targetedCards) {
                                        subAction.addAnimationGroup(targetedCards);
                                        // Allow response(s)
                                        subAction.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " move away (for free) or be lost",
                                                new UnrespondableEffect(subAction) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        subAction.appendEffect(
                                                                new ChooseNextCardToMoveAwayOrBeLost(subAction, game, targetedCards));
                                                    }
                                                }
                                        );
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

    /**
     * A private effect for choosing the next card.
     */
    private class ChooseNextCardToMoveAwayOrBeLost extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card.
         * @param subAction the action
         * @param remainingCards the remaining cards
         */
        public ChooseNextCardToMoveAwayOrBeLost(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose next card to move away (for free) or be lost", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected boolean getUseShortcut() {
            return true;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {
                _game.getGameState().cardAffectsCard(_performingPlayerId, _action.getActionSource(), selectedCard);

                // SubAction to carry out losing card from table
                SubAction moveAwayOrBeLostSubAction = new SubAction(_subAction);
                moveAwayOrBeLostSubAction.appendEffect(
                        new ChooseToMoveAwayOrBeLostEffect(moveAwayOrBeLostSubAction, selectedCard.getOwner(), selectedCard, true));
                // Stack sub-action
                _subAction.stackSubAction(moveAwayOrBeLostSubAction);

                _remainingCards.remove(selectedCard);
                if (!_remainingCards.isEmpty()) {

                    _subAction.appendEffect(
                            new PassthruEffect(_subAction) {
                                @Override
                                protected void doPlayEffect(SwccgGame game) {
                                    // Filter for cards that are still on the table
                                    _remainingCards = Filters.filter(_remainingCards, game, _cardFilter);

                                    if (!_remainingCards.isEmpty()) {
                                        _subAction.appendEffect(
                                                new ChooseCardsToMoveAwayOrBeLostEffect.ChooseNextCardToMoveAwayOrBeLost(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
