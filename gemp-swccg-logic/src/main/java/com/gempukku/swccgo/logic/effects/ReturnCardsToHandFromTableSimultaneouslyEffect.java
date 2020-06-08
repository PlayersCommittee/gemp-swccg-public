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
 * An effect that causes one more cards on table (including converted locations and stacked cards) to be returned to hand simultaneously.
 * Any cards attached or aboard the cards are lost or placed in specified zone (or released) as required.
 * This effect emits the "about to be returned to hand" effect result and the "just returned to hand" effect result at
 * the appropriate time. This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
public class ReturnCardsToHandFromTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToReturnToHand;
    private boolean _releaseCaptives;
    private Zone _playersAttachedCardsGoToZone;
    private Zone _opponentsAttachedCardsGoToZone;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private ReturnCardsToHandFromTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _returnedToHand = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _releasedCaptives = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be returned to hand simultaneously.
     * @param action the action performing this effect
     * @param cardsToReturnToHand the cards to return to hand
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public ReturnCardsToHandFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToReturnToHand, boolean releaseCaptives) {
        this(action, cardsToReturnToHand, releaseCaptives, Zone.LOST_PILE, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes one more cards on table to be returned to hand simultaneously.
     * @param action the action performing this effect
     * @param cardsToReturnToHand the cards to return to hand
     * @param releaseCaptives true if captives are released, otherwise false
     * @param playersAttachedCardsGoToZone the zone that any of player's attached cards go to (instead of Lost Pile)
     * @param opponentsAttachedCardsGoToZone the zone that any of opponent's attached cards go to (instead of Lost Pile)
     */
    public ReturnCardsToHandFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToReturnToHand, boolean releaseCaptives, Zone playersAttachedCardsGoToZone, Zone opponentsAttachedCardsGoToZone) {
        super(action);
        _originalCardsToReturnToHand = Collections.unmodifiableCollection(cardsToReturnToHand);
        _releaseCaptives = releaseCaptives;
        _playersAttachedCardsGoToZone = playersAttachedCardsGoToZone;
        _opponentsAttachedCardsGoToZone = opponentsAttachedCardsGoToZone;
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
        final String playerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be returned to hand" for cards specified cards to be returned to hand.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being returned to hand.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToReturnToHand : _originalCardsToReturnToHand) {
            effectResults.add(new AboutToReturnCardToHandFromTableResult(subAction, cardToReturnToHand, _that));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Trigger is "about to be lost/returned to hand" for any attached cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost/returned to hand.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _returnedToHand.addAll(Filters.filter(_originalCardsToReturnToHand, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked), Filters.not(Filters.in(_preventedCards)))));
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _returnedToHand, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());
                        if (_playersAttachedCardsGoToZone == Zone.HAND) {
                            Collection<PhysicalCard> cardsToReturnToHand = playerId != null ? Filters.filter(_attachedCardsToLeaveTable, game, Filters.your(playerId)) : _attachedCardsToLeaveTable;
                            _returnedToHand.addAll(cardsToReturnToHand);
                            for (PhysicalCard cardToReturnToHand : cardsToReturnToHand) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToReturnCardToHandFromTableResult(subAction, cardToReturnToHand, _that));
                            }
                            _attachedCardsToLeaveTable.removeAll(cardsToReturnToHand);
                        }
                        if (_opponentsAttachedCardsGoToZone == Zone.HAND) {
                            Collection<PhysicalCard> cardsToReturnToHand = playerId != null ? Filters.filter(_attachedCardsToLeaveTable, game, Filters.opponents(playerId)) : _attachedCardsToLeaveTable;
                            _returnedToHand.addAll(cardsToReturnToHand);
                            for (PhysicalCard cardToReturnToHand : cardsToReturnToHand) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToReturnCardToHandFromTableResult(subAction, cardToReturnToHand, _that));
                            }
                            _attachedCardsToLeaveTable.removeAll(cardsToReturnToHand);
                        }

                        Collection<PhysicalCard> attachedCardsAboutToBeLost = Filters.filter(_attachedCardsToLeaveTable, game, Zone.ATTACHED);
                        for (PhysicalCard attachedCardAboutToBeLost : attachedCardsAboutToBeLost) {
                            game.getActionsEnvironment().emitEffectResult(new AboutToLoseCardFromTableResult(subAction, attachedCardAboutToBeLost, _that, true, attachedCardsAboutToBeLost));
                        }
                    }
                }
        );

        // 3) Update cards that are actually leaving table and captives to be released
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_returnedToHand);
                        _returnedToHand.clear();
                        _returnedToHand.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        cardsToCheck = new ArrayList<PhysicalCard>(_attachedCardsToLeaveTable);
                        _attachedCardsToLeaveTable.clear();
                        _attachedCardsToLeaveTable.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        _releasedCaptives.clear();
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _returnedToHand, _releaseCaptives, new ArrayList<PhysicalCard>(), _releasedCaptives);

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _returnedToHand) {
                            card.setLeavingTable(true);
                        }
                        for (PhysicalCard card : _attachedCardsToLeaveTable) {
                            card.setLeavingTable(true);
                        }
                    }
                }
        );

        // 4) Release captives
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_releasedCaptives.isEmpty()) {

                            SubAction releaseCaptivesSubAction = new SubAction(subAction);
                            releaseCaptivesSubAction.appendEffect(
                                    new ReleaseCaptivesEffect(releaseCaptivesSubAction, _releasedCaptives, true));
                            // Stack sub-action
                            subAction.stackSubAction(releaseCaptivesSubAction);
                        }
                    }
                }
        );

        // 5) Persist information about the cards before and then remove the cards from the table.
        //    Remove locations after non-locations since we want to make sure no cards are at the location when it is
        //    removed from the table (and the user interface).
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_returnedToHand.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardToReturnToHand : _returnedToHand) {
                                cardToReturnToHand.updateRememberedInPlayCardInfo(game);
                            }

                            for (PhysicalCard cardToLeaveTable : _attachedCardsToLeaveTable) {
                                cardToLeaveTable.updateRememberedInPlayCardInfo(game);

                                _wasAttachedToWhenLostOrForfeited.put(cardToLeaveTable, cardToLeaveTable.getAttachedTo());

                                PhysicalCard lostFromLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardToLeaveTable);
                                if (lostFromLocation != null) {
                                    _locationLostOrForfeitedFrom.put(cardToLeaveTable, lostFromLocation);
                                }

                                Collection<PhysicalCard> presentWithWhenLost = Filters.filterActive(game, null, Filters.presentWith(cardToLeaveTable));
                                _wasPresentWithWhenLostOrForfeited.put(cardToLeaveTable, presentWithWhenLost);
                            }

                            // Remove cards from the table
                            gameState.removeCardsFromZone(_returnedToHand);
                            gameState.removeCardsFromZone(_attachedCardsToLeaveTable);

                            // Place cards returning to hand into hand
                            for (PhysicalCard card : _returnedToHand) {
                                game.getGameState().addCardToTopOfZone(card, Zone.HAND, card.getOwner());
                            }

                            // Add card to void zone before allowing player to choose order to place cards in Lost Pile.
                            for (PhysicalCard card : _attachedCardsToLeaveTable) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (_action.getActionSource() != null) {

                                if (!_returnedToHand.isEmpty()) {
                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_returnedToHand) + " on table to be returned to hand using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_returnedToHand) + " on table to be returned to hand");
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "lost";
                                    if (_playersAttachedCardsGoToZone != Zone.LOST_PILE) {
                                        destinationText = "placed in " + _playersAttachedCardsGoToZone.getHumanReadable();
                                    }

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText);
                                }
                            }
                        }
                    }
                }
        );

        // 6) Choose order that cards are placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_attachedCardsToLeaveTable.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            putInCardPileSubAction.appendEffect(
                                    new PutCardsInCardPileEffect(subAction, game, _attachedCardsToLeaveTable, _playersAttachedCardsGoToZone));
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 7) Emit effect results for cards that left table
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_returnedToHand.isEmpty()) {

                            for (PhysicalCard returnedToHandCard : _returnedToHand) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new ReturnedCardToHandFromTableResult(subAction, returnedToHandCard));
                            }
                        }
                        if (!_attachedCardsToLeaveTable.isEmpty()) {

                            for (PhysicalCard putToPileCard : _attachedCardsToLeaveTable) {
                                if (_playersAttachedCardsGoToZone == Zone.RESERVE_DECK) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInReserveDeckFromTableResult(subAction, putToPileCard));
                                }
                                else if (_playersAttachedCardsGoToZone == Zone.FORCE_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInForcePileFromTableResult(subAction, putToPileCard));
                                }
                                else if (_playersAttachedCardsGoToZone == Zone.USED_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInUsedPileFromTableResult(subAction, putToPileCard));
                                }
                                else if (_playersAttachedCardsGoToZone == Zone.LOST_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new LostCardFromTableResult(subAction, putToPileCard, _wasAttachedToWhenLostOrForfeited.get(putToPileCard), _locationLostOrForfeitedFrom.get(putToPileCard), _wasPresentWithWhenLostOrForfeited.get(putToPileCard)));
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
