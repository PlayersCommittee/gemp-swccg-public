package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.*;

/**
 * An effect that causes one more cards on table (including stacked cards) to be canceled simultaneously.
 * Any cards attached to the cancel cards are lost or placed in specified zone as required.  This effect emits the "about to
 * be canceled" effect result and the "just canceled" effect result at the appropriate time. This effect should be not be used
 * directly be a card, but instead just by rules or other effects.
 */
class CancelCardsOnTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToCancel;
    private Zone _attachedCardsGoToZone;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private CancelCardsOnTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedWhenCanceled = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _canceled = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _toPlaceInLostPile = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _toPlaceInUsedPile = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _toPlaceInOtherPile = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be canceled simultaneously.
     * @param action the action performing this effect
     * @param cardsToCancel the cards to cancel
     */
    public CancelCardsOnTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToCancel) {
        this(action, cardsToCancel, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes one more cards on table to be canceled simultaneously.
     * @param action the action performing this effect
     * @param cardsToCancel the cards to cancel
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public CancelCardsOnTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToCancel, Zone attachedCardsGoToZone) {
        super(action);
        _originalCardsToCancel = Collections.unmodifiableCollection(cardsToCancel);
        _attachedCardsGoToZone = attachedCardsGoToZone;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be canceled" for cards specified cards to cancel.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being canceled.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToCancel : _originalCardsToCancel) {
            effectResults.add(new AboutToCancelCardOnTableResult(subAction, cardToCancel, _that));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Trigger is "about to be lost" for any attached cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _canceled.addAll(Filters.filter(_originalCardsToCancel, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked), Filters.not(Filters.in(_preventedCards)))));
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _canceled, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());
                        Collection<PhysicalCard> attachedCardsAboutToLeaveTable = Filters.filter(_attachedCardsToLeaveTable, game, Zone.ATTACHED);
                        if (_attachedCardsGoToZone == Zone.LOST_PILE) {
                            for (PhysicalCard attachedCardAboutToLeaveTable : attachedCardsAboutToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToLoseCardFromTableResult(subAction, attachedCardAboutToLeaveTable, _that, true, attachedCardsAboutToLeaveTable));
                            }
                        }
                        else {
                            for (PhysicalCard attachedCardAboutToLeaveTable : attachedCardsAboutToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToPlaceCardInCardPileFromTableResult(subAction, attachedCardAboutToLeaveTable, _attachedCardsGoToZone, _that));
                            }
                        }
                    }
                }
        );

        // 3) Update cards that are actually leaving table
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_attachedCardsToLeaveTable);
                        _attachedCardsToLeaveTable.clear();
                        _attachedCardsToLeaveTable.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        if (_attachedCardsGoToZone == Zone.LOST_PILE) {
                            _toPlaceInLostPile.addAll(_attachedCardsToLeaveTable);
                        }
                        else if (_attachedCardsGoToZone == Zone.USED_PILE) {
                            _toPlaceInUsedPile.addAll(_attachedCardsToLeaveTable);
                        }
                        else {
                            _toPlaceInOtherPile.addAll(_attachedCardsToLeaveTable);
                        }

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _canceled) {
                            card.setLeavingTable(true);
                        }
                        for (PhysicalCard card : _attachedCardsToLeaveTable) {
                            card.setLeavingTable(true);
                        }
                    }
                }
        );

        // 3) Persist information about the cards before and then remove the cards from the table.
        //    Remove locations after non-locations since we want to make sure no cards are at the location when it is
        //    removed from the table (and the user interface).
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_canceled.isEmpty() || !_toPlaceInLostPile.isEmpty() || !_toPlaceInUsedPile.isEmpty() || !_toPlaceInOtherPile.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardCanceled : _canceled) {
                                cardCanceled.updateRememberedInPlayCardInfo(game);

                                PhysicalCard attachedTo = cardCanceled.getAttachedTo();
                                if (attachedTo != null) {
                                    _wasAttachedWhenCanceled.put(cardCanceled, attachedTo);
                                }
                            }

                            for (PhysicalCard cardPlacedInCardPile : _toPlaceInLostPile) {
                                cardPlacedInCardPile.updateRememberedInPlayCardInfo(game);

                                _wasAttachedToWhenLostOrForfeited.put(cardPlacedInCardPile, cardPlacedInCardPile.getAttachedTo());
                                PhysicalCard lostFromLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardPlacedInCardPile);
                                if (lostFromLocation != null) {
                                    _locationLostOrForfeitedFrom.put(cardPlacedInCardPile, lostFromLocation);
                                }

                                Collection<PhysicalCard> presentWithWhenLost = Filters.filterActive(game, null, Filters.presentWith(cardPlacedInCardPile));
                                _wasPresentWithWhenLostOrForfeited.put(cardPlacedInCardPile, presentWithWhenLost);
                            }

                            for (PhysicalCard cardPlacedInCardPile : _toPlaceInUsedPile) {
                                cardPlacedInCardPile.updateRememberedInPlayCardInfo(game);
                            }

                            for (PhysicalCard cardToLeaveTable : _toPlaceInOtherPile) {
                                cardToLeaveTable.updateRememberedInPlayCardInfo(game);
                            }

                            // Remove cards from the table
                            gameState.removeCardsFromZone(_canceled);
                            gameState.removeCardsFromZone(_toPlaceInLostPile);
                            gameState.removeCardsFromZone(_toPlaceInUsedPile);
                            gameState.removeCardsFromZone(_toPlaceInOtherPile);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _canceled) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _toPlaceInLostPile) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _toPlaceInUsedPile) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _toPlaceInOtherPile) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (_action.getActionSource() != null) {

                                if (!_canceled.isEmpty()) {
                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_canceled) + " on table to be canceled using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_canceled) + " on table to be canceled");
                                }

                                if (!_toPlaceInLostPile.isEmpty()) {
                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_toPlaceInLostPile) + " on table to be lost using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_toPlaceInLostPile) + " on table to be lost");
                                }

                                if (!_toPlaceInUsedPile.isEmpty()) {
                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_toPlaceInUsedPile) + " on table to be placed in Used Pile using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_toPlaceInLostPile) + " on table to be lost");
                                }

                                if (!_toPlaceInOtherPile.isEmpty()) {
                                    String destinationText = "placed in " + _attachedCardsGoToZone.getHumanReadable();

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_toPlaceInOtherPile) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_toPlaceInOtherPile) + " on table to be " + destinationText);
                                }
                            }

                            for (PhysicalCard canceledCard : _canceled) {
                                if (modifiersQuerying.isPlacedInUsedPileWhenCanceled(gameState, canceledCard, _action.getPerformingPlayer(), _action.getActionSource())) {
                                    if (_action.getActionSource() != null) {
                                        gameState.activatedCard(null, _action.getActionSource());
                                    }
                                    _toPlaceInUsedPile.add(canceledCard);
                                }
                                else {
                                    _toPlaceInLostPile.add(canceledCard);
                                }
                            }
                        }
                    }
                }
        );

        // 4) Choose order that cards are placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_toPlaceInLostPile.isEmpty() || !_toPlaceInUsedPile.isEmpty() || !_toPlaceInOtherPile.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            if (!_toPlaceInLostPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _toPlaceInLostPile, Zone.LOST_PILE));
                            }
                            if (!_toPlaceInUsedPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _toPlaceInUsedPile, Zone.USED_PILE));
                            }
                            if (!_toPlaceInOtherPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _toPlaceInOtherPile, _attachedCardsGoToZone));
                            }
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 5) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_toPlaceInLostPile.isEmpty()) {

                            for (PhysicalCard placedInLostPileCard : _toPlaceInLostPile) {
                                if (_canceled.contains(placedInLostPileCard)) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new CancelCardOnTableResult(subAction, placedInLostPileCard, _wasAttachedWhenCanceled.get(placedInLostPileCard)));
                                }
                                else {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new LostCardFromTableResult(subAction, placedInLostPileCard, _wasAttachedToWhenLostOrForfeited.get(placedInLostPileCard), _locationLostOrForfeitedFrom.get(placedInLostPileCard), _wasPresentWithWhenLostOrForfeited.get(placedInLostPileCard)));
                                }
                            }
                        }
                        if (!_toPlaceInUsedPile.isEmpty()) {

                            for (PhysicalCard placedInUsedPileCard : _toPlaceInUsedPile) {
                                if (_canceled.contains(placedInUsedPileCard)) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new CancelCardOnTableResult(subAction, placedInUsedPileCard, _wasAttachedWhenCanceled.get(placedInUsedPileCard)));
                                }
                                else {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInUsedPileFromTableResult(subAction, placedInUsedPileCard));
                                }
                            }
                        }
                        if (!_toPlaceInOtherPile.isEmpty()) {

                            for (PhysicalCard putToPileCard : _toPlaceInOtherPile) {
                                if (_attachedCardsGoToZone == Zone.RESERVE_DECK) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInReserveDeckFromTableResult(subAction, putToPileCard));
                                }
                                else if (_attachedCardsGoToZone == Zone.FORCE_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInForcePileFromTableResult(subAction, putToPileCard));
                                }
                            }
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
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
}
