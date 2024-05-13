package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collection;

/**
 * An effect to put cards from hand into the specified card pile.
 */
class PutCardsFromHandInCardPileEffect extends AbstractSubActionEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private boolean _allCards;
    private boolean _bottom;
    private Filterable _filters;
    private Zone _zone;
    private String _cardPileOwner;
    private String _handOwner;
    private boolean _hidden;
    private int _putInCardPileSoFar;
    private PutCardsFromHandInCardPileEffect _that;

    /**
     * Creates an effect that causes the player to put all cards from hand into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to put cards on
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, Zone cardPile, boolean bottom) {
        super(action);
        _playerId = playerId;
        _allCards = true;
        _minimum = Integer.MAX_VALUE;
        _maximum = Integer.MAX_VALUE;
        _zone = cardPile;
        _cardPileOwner = playerId;
        _bottom = bottom;
        _filters = Filters.any;
        _handOwner = playerId;
        _hidden = true;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put all cards accepted by the specified filter from hand into the specified
     * card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to put cards on
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, Zone cardPile, boolean bottom, Filterable filters, boolean hidden) {
        super(action);
        _playerId = playerId;
        _allCards = true;
        _minimum = Integer.MAX_VALUE;
        _maximum = Integer.MAX_VALUE;
        _zone = cardPile;
        _cardPileOwner = playerId;
        _bottom = bottom;
        _filters = filters;
        _handOwner = playerId;
        _hidden = hidden;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put all cards accepted by the specified filter from hand into the specified
     * card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to put cards on
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, Zone cardPile, String cardPileOwner, boolean bottom, Filterable filters, boolean hidden) {
        super(action);
        _playerId = playerId;
        _allCards = true;
        _minimum = Integer.MAX_VALUE;
        _maximum = Integer.MAX_VALUE;
        _zone = cardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _filters = filters;
        _handOwner = cardPileOwner;
        _hidden = hidden;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put all cards accepted by the specified filter from hand into the specified
     * card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPile the card pile to put cards on
     * @param cardPileOwner the card pile owner
     * @param cards the cards
     * @param hidden true if cards are not revealed, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, Zone cardPile, String cardPileOwner, boolean bottom, Collection<PhysicalCard> cards, boolean hidden) {
        super(action);
        _playerId = playerId;
        _allCards = true;
        _minimum = Integer.MAX_VALUE;
        _maximum = Integer.MAX_VALUE;
        _zone = cardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _filters = Filters.in(cards);
        _handOwner = (cards != null && !cards.isEmpty()) ? cards.iterator().next().getZoneOwner() : _cardPileOwner;
        _hidden = hidden;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put cards from hand into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param cardPile the card pile to put cards on
     * @param cardPileOwner the card pile owner
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, String cardPileOwner, boolean bottom) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = cardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _filters = Filters.any;
        _handOwner = cardPileOwner;
        _hidden = true;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to put cards accepted by the specified filter from card pile owner's
     * hand into the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to put on card pile
     * @param maximum the maximum number of cards to put on card pile
     * @param cardPile the card pile to put cards on
     * @param cardPileOwner the card pile owner
     * @param bottom true if cards are to be put on the bottom of the card pile, otherwise false
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    protected PutCardsFromHandInCardPileEffect(Action action, String playerId, int minimum, int maximum, Zone cardPile, String cardPileOwner, boolean bottom, Filterable filters, boolean hidden) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = cardPile;
        _cardPileOwner = cardPileOwner;
        _bottom = bottom;
        _filters = filters;
        _handOwner = cardPileOwner;
        _hidden = hidden;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getHand(_handOwner).isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        final PhysicalCard actionSource = _action.getActionSource();
        final String performingPlayer = _action.getPerformingPlayer();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_cardPileOwner, _zone)
                    || (gameState.getCardPile(_cardPileOwner, _zone).size() > 1 && _bottom);
        }

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        String opponent = game.getOpponent(_handOwner);
                        String actionSourceOwner = actionSource != null ? actionSource.getOwner() : null;
                        if ((opponent.equals(performingPlayer) || opponent.equals(actionSourceOwner)) && modifiersQuerying.mayNotRemoveCardsFromOpponentsHand(gameState, actionSource, opponent)) {
                            gameState.sendMessage(opponent + " is not allowed to remove cards from " + _handOwner + "'s hand");
                            return;
                        }
                        subAction.appendEffect(getChooseOneCardToPutInCardPileEffect(subAction));
                    }
                }
        );
        return subAction;
    }

    public String getChoiceText(int numCardsToChoose) {
        String whereInPile = _bottom ? "bottom of " : "";
        return "Choose card" + GameUtils.s(numCardsToChoose) + " to put on " + whereInPile + _zone.getHumanReadable();
    }

    private StandardEffect getChooseOneCardToPutInCardPileEffect(final SubAction subAction) {
        return new ChooseCardsFromHandEffect(subAction, _playerId, _handOwner, _putInCardPileSoFar < _minimum ? 1 : 0, 1, _filters, false, _allCards && _putInCardPileSoFar > 0) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return _that.getChoiceText(1);
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    String cardInfo = _hidden ? GameUtils.numCards(cards) : GameUtils.getAppendedNames(cards);
                    String whereInPile = _bottom ? "bottom of " : "";
                    String cardPileOwnerInfo = !_playerId.equals(_cardPileOwner) ? (" " + _cardPileOwner + "'s ") : "";
                    String msgText = _playerId + " puts " + cardInfo + " from hand on " + whereInPile + cardPileOwnerInfo + _zone.getHumanReadable();
                    subAction.appendEffect(
                            new PutOneCardFromHandInCardPileEffect(subAction, cards.iterator().next(), _zone, _cardPileOwner, _bottom, msgText) {
                                @Override
                                protected void scheduleNextStep() {
                                    _putInCardPileSoFar++;
                                    if (_putInCardPileSoFar < _maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.appendEffect(
                                                getChooseOneCardToPutInCardPileEffect(subAction));
                                    }
                                }
                            });
                }
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return (_allCards && _putInCardPileSoFar > 0) || (_putInCardPileSoFar >= _minimum);
    }
}
