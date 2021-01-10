package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;

/**
 * An effect to release the specified captured starships.
 */
public class ReleaseCapturedStarshipEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _remainingCards;

    /**
     * Creates an effect to release the specified captured starships.
     * @param action the action performing this effect.
     * @param captives the captives to release
     */
    public ReleaseCapturedStarshipEffect(Action action, Collection<PhysicalCard> captives) {
        super(action);
        _playerId = _action.getPerformingPlayer();
        _remainingCards = captives;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.captured_starship, Filters.onTable));

        if (!_remainingCards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextStarshipToRelease(subAction, game, _remainingCards));
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }

    /**
     * A private effect for choosing the next character to be released.
     */
    private class ChooseNextStarshipToRelease extends ChooseCardOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;

        /**
         * Creates an effect for choosing the next character to be released.
         * @param subAction the action
         * @param remainingCards the remaining cards to choose from to be released
         */
        public ChooseNextStarshipToRelease(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, _playerId, "Choose starship to release", remainingCards);
            _subAction = subAction;
            _game = game;
        }

        @Override
        protected void cardSelected(final PhysicalCard selectedCard) {

            // Perform the ReleaseOneCharacterOnTableEffect on the selected card

            _subAction.appendEffect(
                    new ReleaseOneCapturedStarshipEffect(_subAction, selectedCard));

            _subAction.appendEffect(
                    new PassthruEffect(_subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _remainingCards.remove(selectedCard);
                            _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.captured_starship, Filters.onTable));
                            if (!_remainingCards.isEmpty()) {
                                _subAction.appendEffect(
                                        new ChooseNextStarshipToRelease(_subAction, game, _remainingCards));
                            }
                        }
                    }
            );
        }
    }
}
