package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect that causes the specified player to choose characters on table to be captured.
 */
public class ChooseCharactersOnTableToCaptureEffect extends AbstractSubActionEffect {
    private String _performingPlayerId;
    private int _minimum;
    private int _maximum;
    private Filterable _characterFilter;
    private PhysicalCard _cardFiringWeapon;

    /**
     * Creates an effect that causes the specified player to choose characters on table to be captured.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of characters to target
     * @param maximum the maximum number of characters to target
     * @param characterFilter the character filter
     * @param cardFiringWeapon the card that fired weapon that caused capture, or null
     */
    public ChooseCharactersOnTableToCaptureEffect(Action action, String playerId, int minimum, int maximum, Filterable characterFilter, PhysicalCard cardFiringWeapon) {
        super(action);
        _performingPlayerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _characterFilter = characterFilter;
        _cardFiringWeapon = cardFiringWeapon;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _performingPlayerId);
        subAction.appendTargeting(
                new TargetCardsOnTableEffect(subAction, _performingPlayerId, "Choose character" + GameUtils.s(_maximum) + " to capture", _minimum, _maximum, TargetingReason.TO_BE_CAPTURED, _characterFilter) {
                    @Override
                    protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                        subAction.addAnimationGroup(targetedCards);
                        // Allow response(s)
                        subAction.allowResponses("Capture " + GameUtils.getAppendedNames(targetedCards),
                                new RespondableEffect(subAction) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the targeted card(s) from the action using the targetGroupId.
                                        // This needs to be done in case the target(s) were changed during the responses.
                                        final Collection<PhysicalCard> cardsToCapture = targetingAction.getPrimaryTargetCards(targetGroupId);

                                        subAction.appendEffect(
                                                new ChooseNextCardToCapture(subAction, game, cardsToCapture));
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
     * A private effect for choosing the next card to capture on table.
     */
    private class ChooseNextCardToCapture extends ChooseCardsOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;
        private Collection<PhysicalCard> _remainingCards;

        /**
         * Creates an effect for choosing the next card to capture on table.
         * @param subAction the action
         * @param remainingCards the remaining cards to capture
         */
        public ChooseNextCardToCapture(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, subAction.getPerformingPlayer(), "Choose card to be captured", 1, 1, remainingCards);
            _subAction = subAction;
            _game = game;
            _remainingCards = remainingCards;
        }

        @Override
        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
            for (PhysicalCard selectedCard : selectedCards) {

                // SubAction to carry out capturing card on table
                SubAction captureCardsSubAction = new SubAction(_subAction);

                captureCardsSubAction.appendEffect(
                        new CaptureCharacterOnTableEffect(captureCardsSubAction, selectedCard, _cardFiringWeapon));
                // Stack sub-action
                _subAction.stackSubAction(captureCardsSubAction);

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
                                                new ChooseCharactersOnTableToCaptureEffect.ChooseNextCardToCapture(_subAction, _game, _remainingCards));
                                    }
                                }
                            }
                    );
                }
            }
        }
    }
}
