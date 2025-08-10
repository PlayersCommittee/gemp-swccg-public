package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.*;

/**
 * An effect that causes one more cards on table (including converted locations and stacked cards) to be lost simultaneously.
 * Any cards attached or aboard the lost cards are also lost (or released) as required.  This effect emits the "about to
 * be lost" effect result and the "just lost" effect result at the appropriate time. This effect should be not be used
 * directly be a card, but instead just by rules or other effects.
 */
public class LoseCardsFromTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToLose;
    private boolean _toBottomOfPile;
    private boolean _allCardsSituation;
    private boolean _releaseCaptives;
    private Zone _attachedCardsGoToZone;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private LoseCardsFromTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _lostFromPlay = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _releasedCaptives = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be lost simultaneously.
     * @param action the action performing this effect
     * @param cardsToLose the cards to lose
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public LoseCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToLose, boolean allCardsSituation, boolean releaseCaptives) {
        this(action, cardsToLose, false, allCardsSituation, releaseCaptives, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes one more cards on table to be lost simultaneously.
     * @param action the action performing this effect
     * @param cardsToLose the cards to lose
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public LoseCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToLose, boolean toBottomOfPile, boolean allCardsSituation, boolean releaseCaptives) {
        this(action, cardsToLose, toBottomOfPile, allCardsSituation, releaseCaptives, Zone.LOST_PILE);
    }

    /**
     * Creates an effect that causes one more cards on table to be lost simultaneously.
     * @param action the action performing this effect
     * @param cardsToLose the cards to lose
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param allCardsSituation true if this is an "all cards situation", otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     */
    public LoseCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToLose, boolean toBottomOfPile, boolean allCardsSituation, boolean releaseCaptives, Zone attachedCardsGoToZone) {
        super(action);
        _originalCardsToLose = Collections.unmodifiableCollection(cardsToLose);
        _toBottomOfPile = toBottomOfPile;
        _allCardsSituation = allCardsSituation;
        _releaseCaptives = releaseCaptives;
        _attachedCardsGoToZone = attachedCardsGoToZone;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Determines if the cards are lost due to being 'eaten'.
     * @return true or false
     */
    protected boolean asEaten() {
        return false;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Trigger is "about to be lost" for cards specified cards to lose.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToLose : _originalCardsToLose) {
            effectResults.add(new AboutToLoseCardFromTableResult(subAction, cardToLose, _that, _allCardsSituation, _originalCardsToLose));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Trigger is "about to be lost" for any attached cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _lostFromPlay.addAll(Filters.filter(_originalCardsToLose, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked), Filters.not(Filters.in(_preventedCards)))));
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _lostFromPlay, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());
                        Collection<PhysicalCard> attachedCardsAboutToLeaveTable = Filters.filter(_attachedCardsToLeaveTable, game, Zone.ATTACHED);
                        if (_attachedCardsGoToZone == Zone.LOST_PILE) {
                            for (PhysicalCard attachedCardAboutToBeLost : attachedCardsAboutToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToLoseCardFromTableResult(subAction, attachedCardAboutToBeLost, _that, true, attachedCardsAboutToLeaveTable));
                            }
                        }
                        else {
                            for (PhysicalCard attachedCardAboutToBeLost : attachedCardsAboutToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToPlaceCardInCardPileFromTableResult(subAction, attachedCardAboutToBeLost, _attachedCardsGoToZone, _that, true));
                            }
                        }
                    }
                }
        );

        // 3) Update cards that are actually leaving table and captives to be released
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_attachedCardsToLeaveTable);
                        _attachedCardsToLeaveTable.clear();
                        if (_attachedCardsGoToZone == Zone.LOST_PILE) {
                            _lostFromPlay.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        }
                        else {
                            _attachedCardsToLeaveTable.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        }
                        _releasedCaptives.clear();
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _lostFromPlay, _releaseCaptives, new ArrayList<PhysicalCard>(), _releasedCaptives);

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _lostFromPlay) {
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
                            game.getActionsEnvironment().addActionToStack(releaseCaptivesSubAction);
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
                        if (!_lostFromPlay.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardPlacedInCardPile : _lostFromPlay) {
                                cardPlacedInCardPile.updateRememberedInPlayCardInfo(game);

                                _wasAttachedToWhenLostOrForfeited.put(cardPlacedInCardPile, cardPlacedInCardPile.getAttachedTo());

                                PhysicalCard lostFromLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardPlacedInCardPile);
                                if (lostFromLocation != null) {
                                    _locationLostOrForfeitedFrom.put(cardPlacedInCardPile, lostFromLocation);
                                }

                                Collection<PhysicalCard> presentWithWhenLost = Filters.filterActive(game, null, Filters.presentWith(cardPlacedInCardPile));
                                _wasPresentWithWhenLostOrForfeited.put(cardPlacedInCardPile, presentWithWhenLost);
                            }

                            for (PhysicalCard cardToLeaveTable : _attachedCardsToLeaveTable) {
                                cardToLeaveTable.updateRememberedInPlayCardInfo(game);
                            }

                            // Remove cards from the table
                            gameState.removeCardsFromZone(_lostFromPlay);
                            gameState.removeCardsFromZone(_attachedCardsToLeaveTable);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _lostFromPlay) {
                                gameState.addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _attachedCardsToLeaveTable) {
                                gameState.addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (_action.getActionSource() != null) {

                                if (!_lostFromPlay.isEmpty()) {
                                    String destinationText = "lost" + (asEaten() ? " due to being 'eaten'" : "");

                                    if (_action.getPerformingPlayer() != null)
                                        gameState.sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_lostFromPlay) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_lostFromPlay) + " on table to be " + destinationText);
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "placed in " + _attachedCardsGoToZone.getHumanReadable();

                                    if (_action.getPerformingPlayer() != null)
                                        gameState.sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText);
                                }
                            }
                            else {

                                if (!_lostFromPlay.isEmpty()) {
                                    String destinationText = "lost" + (asEaten() ? " due to being 'eaten'" : "");

                                    if (_action.getPerformingPlayer() != null)
                                        gameState.sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_lostFromPlay) + " on table to be " + destinationText);
                                    else
                                        gameState.sendMessage(GameUtils.getAppendedNames(_lostFromPlay) + " on table is " + destinationText);
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "placed in " + _attachedCardsGoToZone.getHumanReadable();

                                    if (_action.getPerformingPlayer() != null)
                                        gameState.sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText);
                                    else
                                        gameState.sendMessage(GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table is " + destinationText);
                                }
                            }

                        }
                    }
                }
        );

        // 6) Choose order that cards are placed in Lost Pile
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_lostFromPlay.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            if (!_lostFromPlay.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _lostFromPlay, Zone.LOST_PILE, _toBottomOfPile));
                            }
                            if (!_attachedCardsToLeaveTable.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _attachedCardsToLeaveTable, _attachedCardsGoToZone));
                            }
                            game.getActionsEnvironment().addActionToStack(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 7) Emit effect results for cards placed in Lost Pile
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_lostFromPlay.isEmpty()) {

                            for (PhysicalCard lostFromPlayCard : _lostFromPlay) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new LostCardFromTableResult(subAction, lostFromPlayCard, _wasAttachedToWhenLostOrForfeited.get(lostFromPlayCard), _locationLostOrForfeitedFrom.get(lostFromPlayCard), _wasPresentWithWhenLostOrForfeited.get(lostFromPlayCard)));
                            }
                        }
                        if (!_attachedCardsToLeaveTable.isEmpty()) {

                            for (PhysicalCard putToPileCard : _attachedCardsToLeaveTable) {
                                if (_attachedCardsGoToZone == Zone.RESERVE_DECK) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInReserveDeckFromTableResult(subAction, putToPileCard));
                                }
                                else if (_attachedCardsGoToZone == Zone.FORCE_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInForcePileFromTableResult(subAction, putToPileCard));
                                }
                                else if (_attachedCardsGoToZone == Zone.USED_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInUsedPileFromTableResult(subAction, putToPileCard));
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
