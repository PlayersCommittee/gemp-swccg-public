package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.GoMissingEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that causes the specified player to choose characters on table to go missing.
 */
public class ChooseCharactersToGoMissingEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _minimum;
    private int _maximum;
    private Filterable _characterFilter;

    /**
     * Creates an effect that causes the specified player to choose characters on table to go missing.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of characters to target
     * @param maximum the maximum number of characters to target
     * @param characterFilter the character filter
     */
    public ChooseCharactersToGoMissingEffect(Action action, String playerId, int minimum, int maximum, Filterable characterFilter) {
        super(action);
        _performingPlayerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _characterFilter = characterFilter;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new TargetCardsOnTableEffect(subAction, _performingPlayerId, "Choose character" + GameUtils.s(_maximum) + " to go missing", _minimum, _maximum, TargetingReason.TO_BE_MISSING, _characterFilter) {
                    @Override
                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                        subAction.addAnimationGroup(targetedCards);
                        // Allow response(s)
                        subAction.allowResponses("Make " + GameUtils.getAppendedNames(targetedCards) + " go missing",
                                new RespondableEffect(subAction) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final Collection<PhysicalCard> cardsToGoMissing = targetingAction.getPrimaryTargetCards(targetGroupId);

                                        subAction.appendEffect(
                                                new ChooseNextCardToGoMissing(subAction, game, cardsToGoMissing));
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
     * A private effect for choosing the next card to go missing on table.
     */
    private class ChooseNextCardToGoMissing extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to go missing on table.
         * @param subAction the action
         * @param remainingCards the remaining cards to go missing
         */
        public ChooseNextCardToGoMissing(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to go missing", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out making card on table to go missing
                SubAction makeCardsGoMissingSubAction = new SubAction(_subAction);

                makeCardsGoMissingSubAction.appendEffect(
                        new GoMissingEffect(makeCardsGoMissingSubAction, selectedCard));
                // Stack sub-action
                _subAction.stackSubAction(makeCardsGoMissingSubAction);

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
                                                new ChooseCharactersToGoMissingEffect.ChooseNextCardToGoMissing(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
