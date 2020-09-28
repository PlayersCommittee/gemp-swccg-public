package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect that causes the specified player to choose cards on table to be lost.
 */
public class ChooseCardsToLoseFromTableEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _minimum;
    private int _maximum;
    private boolean _allCardsSituation;
    private Set<TargetingReason> _targetingReasons = new HashSet<TargetingReason>();
    private Filterable _cardFilter;

    /**
     * Creates an effect that causes the specified player to choose cards on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param allCardsSituation true if treated as an all cards situation, otherwise false
     * @param cardFilter the card filter
     */
    public ChooseCardsToLoseFromTableEffect(Action action, String playerId, int minimum, int maximum, boolean allCardsSituation, Filterable cardFilter) {
        this(action, playerId, null, minimum, maximum, allCardsSituation, cardFilter);
    }

    /**
     * Creates an effect that causes the specified player to choose cards on table to be lost.
     * @param action the action performing this effect
     * @param playerId the player
     * @param additionalTargetingReason the additional targeting reason (in addition to "to be lost")
     * @param minimum the minimum number of cards to target
     * @param maximum the maximum number of cards to target
     * @param allCardsSituation true if treated as an all cards situation, otherwise false
     * @param cardFilter the card filter
     */
    protected ChooseCardsToLoseFromTableEffect(Action action, String playerId, TargetingReason additionalTargetingReason, int minimum, int maximum, boolean allCardsSituation, Filterable cardFilter) {
        super(action);
        _performingPlayerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _allCardsSituation = allCardsSituation;
        _targetingReasons.add(TargetingReason.TO_BE_LOST);
        if (additionalTargetingReason != null)
            _targetingReasons.add(additionalTargetingReason);
        _cardFilter = cardFilter;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new TargetCardsOnTableEffect(subAction, _performingPlayerId, "Choose card" + GameUtils.s(_maximum) + " to be lost", _minimum, _maximum, _targetingReasons, _cardFilter) {
                    @Override
                    protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> targetedCards) {
                        subAction.addAnimationGroup(targetedCards);
                        // Allow response(s)
                        if (_allCardsSituation) {
                            subAction.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                    new UnrespondableEffect(subAction) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                             subAction.appendEffect(
                                                    new ChooseNextCardToLose(subAction, game, targetedCards));
                                        }
                                    }
                            );
                        }
                        else {
                            subAction.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " lost",
                                    new RespondableEffect(subAction) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final Collection<PhysicalCard> cardsToLose = targetingAction.getPrimaryTargetCards(targetGroupId);

                                            subAction.appendEffect(
                                                    new ChooseNextCardToLose(subAction, game, cardsToLose));
                                        }
                                    }
                            );
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
                        new LoseCardsFromTableSimultaneouslyEffect(loseCardsSubAction, selectedCards, false, _allCardsSituation, true));
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
                                                new ChooseCardsToLoseFromTableEffect.ChooseNextCardToLose(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
