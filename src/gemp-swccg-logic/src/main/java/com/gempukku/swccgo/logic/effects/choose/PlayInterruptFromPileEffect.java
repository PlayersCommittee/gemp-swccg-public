package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.*;

/**
 * An effect that causes the player performing the action to play an Interrupt from the specified card pile.
 */
class PlayInterruptFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private Zone _cardPile;
    private Filter _interruptFilter;
    private Effect _effect;
    private EffectResult _effectResult;
    private boolean _reshuffle;
    private boolean _placeOutOfPlay;
    private boolean _cardSelected;
    private PlayInterruptFromPileEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and play an Interrupt a card accepted by the card filter
     * from the specified card pile to the specified system.
     * @param action the action performing this effect
     * @param zone the card pile
     * @param interruptFilter the interrupt filter
     * @param effect the effect to response to, or null
     * @param effectResult result the effect result to response to, or null
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param placeOutOfPlay true if Interrupt is placed out of play, otherwise false
     */
    protected PlayInterruptFromPileEffect(Action action, Zone zone, Filter interruptFilter, Effect effect, EffectResult effectResult, boolean reshuffle, boolean placeOutOfPlay) {
        super(action);
        _playerId = action.getPerformingPlayer();
        _cardPile = zone;
        _interruptFilter = Filters.and(Filters.Interrupt, interruptFilter);
        _effect = effect;
        _effectResult = effectResult;
        _reshuffle = reshuffle;
        _placeOutOfPlay = placeOutOfPlay;
        _that = this;
    }

    public String getChoiceText(SwccgGame game) {
        if (_effect != null) {
            return "Choose Interrupt to play from " + _cardPile.getHumanReadable() + " as response to " + _effect.getText(game);
        }
        else if (_effectResult != null) {
            return "Choose Interrupt to play from " + _cardPile.getHumanReadable() + " as response to " + _effectResult.getText(game);
        }
        else {
            return "Choose Interrupt to play from " + _cardPile.getHumanReadable();
        }
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        if (_effect != null) {
            _interruptFilter = Filters.and(_interruptFilter, Filters.playableInterruptAsResponse(_action.getActionSource(), _effect));
        }
        else if (_effectResult != null) {
            _interruptFilter = Filters.and(_interruptFilter, Filters.playableInterruptAsResponse(_action.getActionSource(), _effectResult));
        }
        else {
            _interruptFilter = Filters.and(_interruptFilter, Filters.playable(_action.getActionSource()));
        }

        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new ChooseCardFromPileEffect(subAction, subAction.getPerformingPlayer(), _cardPile, subAction.getPerformingPlayer(), _interruptFilter) {
                    @Override
                    protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                        _cardSelected = true;
                        PlayCardAction playCardAction;

                        if (_effect != null) {
                            playCardAction = selectedCard.getBlueprint().getPlayInterruptAsResponseAction(subAction.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), _effect, null);
                        }
                        else if (_effectResult != null) {
                            playCardAction = selectedCard.getBlueprint().getPlayInterruptAsResponseAction(subAction.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), null, _effectResult);
                        }
                        else {
                            playCardAction = selectedCard.getBlueprint().getPlayCardAction(subAction.getPerformingPlayer(), game, selectedCard, _action.getActionSource(), false, 0, null, null, null, null, null, false, 0, null, null);
                        }
                        playCardAction.setReshuffle(_reshuffle);
                        playCardAction.setPlaceOutOfPlay(_placeOutOfPlay);
                        subAction.insertEffect(
                                new StackActionEffect(subAction, playCardAction));
                    }

                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return _that.getChoiceText(game);
                    }
                }
        );
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_cardSelected && _reshuffle) {
                            subAction.appendEffect(
                                    new ShufflePileEffect(subAction, subAction.getActionSource(), subAction.getPerformingPlayer(), subAction.getPerformingPlayer(), _cardPile, true));
                        }
                    }
                }
        );
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _cardSelected;
    }
}
