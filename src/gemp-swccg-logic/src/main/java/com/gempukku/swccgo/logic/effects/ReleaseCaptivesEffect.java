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
 * An effect to release the specified captives.
 */
public class ReleaseCaptivesEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Collection<PhysicalCard> _remainingCards;
    private boolean _leaveFrozenUnattended;

    /**
     * Creates an effect to release the specified captives.
     * @param action the action performing this effect.
     * @param captives the captives to release
     */
    public ReleaseCaptivesEffect(Action action, Collection<PhysicalCard> captives) {
        this(action, captives, false);
    }

    /**
     * Creates an effect to release the specified captives.
     * @param action the action performing this effect.
     * @param captives the captives to release
     * @param leaveFrozenUnattended true if frozen captives are to be left unattended instead of release, otherwise false
     */
    public ReleaseCaptivesEffect(Action action, Collection<PhysicalCard> captives, boolean leaveFrozenUnattended) {
        super(action);
        _playerId = _action.getPerformingPlayer();
        _remainingCards = captives;
        _leaveFrozenUnattended = leaveFrozenUnattended;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        SubAction subAction = new SubAction(_action);

        _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.captive, Filters.character, Filters.onTable));

        if (!_remainingCards.isEmpty()) {
            subAction.appendEffect(
                    new ChooseNextCharacterToRelease(subAction, game, _remainingCards));
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
    private class ChooseNextCharacterToRelease extends ChooseCardOnTableEffect {
        private SubAction _subAction;
        private SwccgGame _game;

        /**
         * Creates an effect for choosing the next character to be released.
         * @param subAction the action
         * @param remainingCards the remaining cards to choose from to be released
         */
        public ChooseNextCharacterToRelease(SubAction subAction, SwccgGame game, Collection<PhysicalCard> remainingCards) {
            super(subAction, _playerId, "Choose character to release" + (_leaveFrozenUnattended && Filters.canSpot(remainingCards, game, Filters.and(Filters.escortedCaptive, Filters.frozenCaptive, Filters.presentAt(Filters.site))) ? " (or leave unattended if frozen)" : ""), remainingCards);
            _subAction = subAction;
            _game = game;
        }

        @Override
        protected void cardSelected(final PhysicalCard selectedCard) {

            // Perform the ReleaseOneCharacterOnTableEffect on the selected card
            if (_leaveFrozenUnattended && Filters.and(Filters.escortedCaptive, Filters.frozenCaptive, Filters.presentAt(Filters.site)).accepts(_game, selectedCard)) {
                PhysicalCard escort = selectedCard.getAttachedTo();
                final PhysicalCard site = _game.getModifiersQuerying().getLocationThatCardIsPresentAt(_game.getGameState(), selectedCard);
                _subAction.appendEffect(
                        new LeaveFrozenCaptiveUnattendedEffect(_subAction, escort, selectedCard, site));
            }
            else {
                _subAction.appendEffect(
                        new ReleaseOneCaptiveEffect(_subAction, selectedCard));
            }
            _subAction.appendEffect(
                    new PassthruEffect(_subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            _remainingCards.remove(selectedCard);
                            _remainingCards = Filters.filter(_remainingCards, game, Filters.and(Filters.captive, Filters.character, Filters.onTable));
                            if (!_remainingCards.isEmpty()) {
                                _subAction.appendEffect(
                                        new ChooseNextCharacterToRelease(_subAction, game, _remainingCards));
                            }
                        }
                    }
            );
        }
    }
}
