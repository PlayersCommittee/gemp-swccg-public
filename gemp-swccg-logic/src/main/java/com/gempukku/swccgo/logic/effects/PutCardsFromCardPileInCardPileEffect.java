package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromPileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;

/**
 * An effect to put cards from a specified card pile into another the specified card pile.
 */
class PutCardsFromCardPileInCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private boolean _allCards;
    private boolean _bottom;
    private Filterable _filters;
    private Zone _fromCardPile;
    private Zone _toCardPile;
    private String _cardPileOwner;
    private boolean _hidden;
    private int _putInCardPileSoFar;
    private PutCardsFromCardPileInCardPileEffect _that;

    /**
     * Creates an effect that causes the player to put all cards accepted by the specified filter from the specified card pile
     * into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param fromCardPile the card pile to take cards from
     * @param toCardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param filters the filter
     */
    protected PutCardsFromCardPileInCardPileEffect(Action action, String playerId, String cardPileOwner, Zone fromCardPile, Zone toCardPile, boolean bottom, Filterable filters) {
        super(action);
        _playerId = playerId;
        _allCards = true;
        _minimum = Integer.MAX_VALUE;
        _maximum = Integer.MAX_VALUE;
        _fromCardPile = fromCardPile;
        _toCardPile = toCardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _filters = filters;
        _hidden = false;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !game.getGameState().isCardPileFaceUp(_cardPileOwner, _toCardPile)
                    || (game.getGameState().getCardPile(_cardPileOwner, _toCardPile).size() > 1 && _bottom);
        }

        SubAction subAction = new SubAction(_action);
        subAction.appendEffect(getChooseOneCardToPutInCardPileEffect(subAction));
        return subAction;
    }

    private StandardEffect getChooseOneCardToPutInCardPileEffect(final SubAction subAction) {
        return new ChooseCardsFromPileEffect(subAction, _playerId, _fromCardPile, _cardPileOwner, _putInCardPileSoFar < _minimum ? 1 : 0, 1, _maximum - _putInCardPileSoFar, false, false, _filters) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                String whereInPile = _bottom ? "bottom of " : "";
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on " + whereInPile + _toCardPile.getHumanReadable();
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    String cardInfo = _hidden ? GameUtils.numCards(cards) : GameUtils.getAppendedNames(cards);
                    String whereInPile = _bottom ? "bottom of " : "";
                    String cardPileOwnerInfo = !_playerId.equals(_cardPileOwner) ? (" " + _cardPileOwner + "'s ") : "";
                    String msgText = _playerId + " puts " + cardInfo + " from hand on " + whereInPile + cardPileOwnerInfo + _toCardPile.getHumanReadable();
                    final PhysicalCard card = cards.iterator().next();
                    final int amountToIncrementCount = Filters.and(_filters).acceptsCount(game, card);
                    subAction.appendEffect(
                            new PutOneCardFromCardPileInCardPileEffect(subAction, card, _fromCardPile, _toCardPile, _cardPileOwner, _bottom, msgText) {
                                @Override
                                protected void scheduleNextStep() {
                                    _putInCardPileSoFar += amountToIncrementCount;
                                    if (_putInCardPileSoFar < _that._maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.appendEffect(
                                                getChooseOneCardToPutInCardPileEffect(subAction));
                                    }
                                }
                            });
                }
            }
            @Override
            public boolean isSkipTriggerPlayerLookedAtCardsInPile() {
                return _putInCardPileSoFar > 0;
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return (_allCards && _putInCardPileSoFar > 0) || (_putInCardPileSoFar >= _minimum);
    }
}
