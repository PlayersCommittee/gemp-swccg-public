package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to stack a card from the specified card pile.
 */
class StackCardFromPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackOn;
    private Zone _cardPile;
    private Filter _cardFilter;
    private boolean _reshuffle;
    private boolean _faceDown;
    private boolean _viaJediTest5;
    private boolean _cardSelected;
    private StackCardFromPileEffect _that;

    /**
     * Creates an effect that causes the player performing the action to choose and stack a card accepted by the card filter
     * from the specified card pile on a card accepted by the stackOn filter.
     * @param action the action performing this effect
     * @param playerId the player to stack the card
     * @param stackOn the card to stack a card on
     * @param zone the card pile
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param viaJediTest5 true if stacked upside-down to be used as substitute destiny via Jedi Test #5, otherwise false
     */
    protected StackCardFromPileEffect(Action action, String playerId, PhysicalCard stackOn, Zone zone, Filter cardFilter, boolean reshuffle, boolean viaJediTest5) {
        super(action);
        _playerId = playerId;
        _stackOn = stackOn;
        _cardPile = zone;
        _cardFilter = Filters.or(cardFilter, Filters.hasPermanentAboard(cardFilter));
        _reshuffle = reshuffle;
        _faceDown = false;
        _viaJediTest5 = viaJediTest5;
        _that = this;
    }

    public String getChoiceText() {
        return "Choose card from " + _cardPile.getHumanReadable() + " to stack on " + GameUtils.getCardLink(_stackOn);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new ChooseCardFromPileEffect(subAction, subAction.getPerformingPlayer(), _cardPile, subAction.getPerformingPlayer(), _cardFilter) {
                    @Override
                    protected void cardSelected(final SwccgGame game, final PhysicalCard selectedCard) {
                        _cardSelected = true;

                        subAction.appendEffect(
                                new StackOneCardFromPileEffect(subAction, subAction.getPerformingPlayer(), selectedCard, _stackOn, _faceDown, _reshuffle, _viaJediTest5));
                    }

                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return _that.getChoiceText();
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
