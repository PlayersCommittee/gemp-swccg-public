package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.StandardEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;
import com.gempukku.swccgo.logic.timing.results.RemovedFromCardPileResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An effect to take cards into hand from the specified card pile.
 */
abstract class TakeCardsIntoHandFromPileEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private String _playerId;
    private int _minimum;
    private int _maximum;
    private boolean _topmost;
    private Filter _filters;
    private Collection<PhysicalCard> _cardsToTakeIntoHand;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private Zone _zone;
    private String _cardPileOwner;
    private boolean _reshuffle;
    private boolean _hidden;
    private boolean _justLost;
    private Collection<PhysicalCard> _cardsTakenIntoHand = new ArrayList<PhysicalCard>();
    private int _numTakenIntoHand;
    private TakeCardsIntoHandFromPileEffect _that;

    /**
     * Creates an effect that causes the player to search the specified card pile and take cards into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param zone the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardsIntoHandFromPileEffect(Action action, String playerId, int minimum, int maximum, Zone zone, String cardPileOwner, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = false;
        _filters = Filters.any;
        _reshuffle = reshuffle;
        _hidden = true;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to search the specified card pile and take cards accepted by the specified
     * filter into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to take into hand
     * @param maximum the maximum number of cards to take into hand
     * @param zone the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    protected TakeCardsIntoHandFromPileEffect(Action action, String playerId, int minimum, int maximum, Zone zone, String cardPileOwner, boolean topmost, Filter filters, boolean reshuffle) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = topmost;
        _filters = Filters.or(filters, Filters.hasPermanentAboard(filters));
        _reshuffle = reshuffle;
        _hidden = false;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to search the specified card pile and take cards accepted by the specified
     * filter into hand.
     *
     * @param action        the action performing this effect
     * @param playerId      the player
     * @param minimum       the minimum number of cards to take into hand
     * @param maximum       the maximum number of cards to take into hand
     * @param zone          the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param topmost       true if only the topmost cards should be chosen from, otherwise false
     * @param filters       the filter
     * @param reshuffle     true if pile is reshuffled, otherwise false
     * @param hidden        true if cards are not revealed, otherwise false
     */
    protected TakeCardsIntoHandFromPileEffect(Action action, String playerId, int minimum, int maximum, Zone zone, String cardPileOwner, boolean topmost, Filter filters, boolean reshuffle, boolean hidden) {
        super(action);
        _playerId = playerId;
        _minimum = minimum;
        _maximum = maximum;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = topmost;
        _filters = Filters.or(filters, Filters.hasPermanentAboard(filters));
        _reshuffle = reshuffle;
        _hidden = hidden;
        _that = this;
    }

    /**
     * Creates an effect that causes the player to take the specified cards into hand from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zone the card pile to take cards from
     * @param cardPileOwner the card pile owner
     * @param cards the cards
     * @param hidden true if cards are not revealed, otherwise false
     * @param justLost true if cards were just lost, otherwise false
     */
    protected TakeCardsIntoHandFromPileEffect(Action action, String playerId, Zone zone, String cardPileOwner, Collection<PhysicalCard> cards, boolean hidden, boolean justLost) {
        super(action);
        _playerId = playerId;
        _cardsToTakeIntoHand = cards;
        _zone = zone;
        _cardPileOwner = cardPileOwner;
        _topmost = false;
        _reshuffle = false;
        _hidden = hidden;
        _justLost = justLost;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getCardPile(_cardPileOwner, _zone).isEmpty();
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ActionsEnvironment actionsEnvironment = game.getActionsEnvironment();

        // If hidden is specified, then check if card pile is actually face up and update value of hidden
        if (_hidden) {
            _hidden = !gameState.isCardPileFaceUp(_playerId, _zone)
                    || (gameState.getCardPile(_playerId, _zone).size() > 1);
        }

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to remove just-lost card from Lost Pile" if for just-lost cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being returned to hand.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (_cardsToTakeIntoHand != null) {
                            _cardsToTakeIntoHand = Filters.filter(_cardsToTakeIntoHand, game, Filters.zoneOfPlayer(_zone, _cardPileOwner));

                            if (_justLost && GameUtils.getZoneFromZoneTop(_zone) == Zone.LOST_PILE) {
                                // Emit effect result that attempting to remove a just lost card from Lost Pile
                                for (PhysicalCard cardToTakeIntoHand : _cardsToTakeIntoHand) {
                                    if (!game.getModifiersQuerying().mayNotRemoveJustLostCardFromLostPile(gameState, cardToTakeIntoHand)) {
                                        actionsEnvironment.emitEffectResult(
                                                new AboutToRemoveJustLostCardFromLostPileResult(subAction, _playerId, cardToTakeIntoHand, _that));
                                    } else {
                                        preventEffectOnCard(cardToTakeIntoHand);
                                    }
                                }
                            }
                        }
                    }
                }
        );

        // 2) If not prevented, continue taking cards into hand
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        // Check if the explicit cards to be taken into hand are prevented from being taken into hand
                        if (_cardsToTakeIntoHand != null) {
                            _cardsToTakeIntoHand = Filters.filter(_cardsToTakeIntoHand, game, Filters.not(Filters.in(_preventedCards)));
                            if (_cardsToTakeIntoHand.isEmpty()) {
                                return;
                            }
                            subAction.appendEffect(getTakeCardsIntoHandEffect(subAction));
                        }
                        else {
                            subAction.appendEffect(getChooseOneCardToTakeIntoHandEffect(subAction));
                        }

                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Shuffle the card pile
                                        if (_reshuffle) {
                                            subAction.insertEffect(
                                                    new ShufflePileEffect(subAction, subAction.getActionSource(), _playerId, _cardPileOwner, _zone, true));
                                        } else if (!_cardsTakenIntoHand.isEmpty()) {
                                            actionsEnvironment.emitEffectResult(
                                                    new RemovedFromCardPileResult(subAction));
                                        }
                                    }
                                }
                        );
                        subAction.appendEffect(
                                new PassthruEffect(subAction) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        // Only callback with the cards still in the player's hand
                                        cardsTakenIntoHand(Filters.filter(_cardsTakenIntoHand, game, Filters.inHand(_playerId)));
                                    }
                                }
                        );
                    }
                }
        );
        return subAction;
    }

    private StandardEffect getChooseOneCardToTakeIntoHandEffect(final SubAction subAction) {
        return new ChooseCardsFromPileEffect(subAction, _playerId, _zone, _cardPileOwner, _numTakenIntoHand < _minimum ? 1 : 0, 1, _maximum - _numTakenIntoHand, false, _topmost, _filters) {
            @Override
            public String getChoiceText(int numCardsToChoose) {
                return "Choose card" + GameUtils.s(numCardsToChoose) + " to take into hand";
            }
            @Override
            protected void cardsSelected(final SwccgGame game, Collection<PhysicalCard> cards) {
                if (!cards.isEmpty()) {
                    final PhysicalCard card = cards.iterator().next();
                    String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(card);
                    String msgText = _playerId + " takes " + cardInfo + " into hand from " + (_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ")) + _zone.getHumanReadable();
                    subAction.insertEffect(
                            new TakeOneCardIntoHandFromOffTableEffect(subAction, _playerId, cards.iterator().next(), msgText) {
                                @Override
                                protected void afterCardTakenIntoHand() {
                                    _cardsTakenIntoHand.add(card);
                                    // Increment by acceptsCount since squadrons can count as more than one based on filter
                                    _numTakenIntoHand += _filters.acceptsCount(game, card);
                                    if (_numTakenIntoHand < _that._maximum
                                            && _that.isPlayableInFull(game)) {
                                        subAction.insertEffect(
                                                getChooseOneCardToTakeIntoHandEffect(subAction));
                                    }
                                }
                            }
                    );
                }
            }
            @Override
            public boolean isSkipTriggerPlayerLookedAtCardsInPile() {
                return _numTakenIntoHand > 0;
            }
        };
    }

    private StandardEffect getTakeCardsIntoHandEffect(final SubAction subAction) {
        return new PassthruEffect(subAction) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                if (_cardsToTakeIntoHand.isEmpty())
                    return;

                final GameState gameState = game.getGameState();
                String cardInfo = _hidden ? GameUtils.numCards(_cardsToTakeIntoHand) : GameUtils.getAppendedNames(_cardsToTakeIntoHand);
                String msgText = _playerId + " takes " + cardInfo + " into hand from " + (_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ")) + _zone.getHumanReadable();

                gameState.removeCardsFromZone(_cardsToTakeIntoHand);
                for (PhysicalCard card : _cardsToTakeIntoHand) {
                    card.setOwner(_playerId);
                    gameState.addCardToZone(card, Zone.HAND, _playerId);
                }
                gameState.sendMessage(msgText);
            }
        };
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _numTakenIntoHand >= _minimum;
    }

    /**
     * Prevents the specified card from being affected by the effect.
     * @param card the card
     */
    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    /**
     * Determines if the specified card was prevented from being affected by the effect.
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }

    /**
     * A callback method for the cards taken into hand.
     * @param cards the cards taken into hand
     */
    protected abstract void cardsTakenIntoHand(Collection<PhysicalCard> cards);
}
