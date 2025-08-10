package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.CaptureOption;
import com.gempukku.swccgo.common.ReleaseOption;
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
 * An effect that causes one more cards are table (including converted locations and stacked cards) to be placed in a card pile simultaneously.
 * Any cards attached or aboard the cards are lost or placed in specified zone (or released) as required.  This effect
 * emits the "about to be placed in card pile" effect result and the "just placed in card pile" effect result at the
 * appropriate time. This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class PlaceCardsInCardPileFromTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToPlaceInCardPile;
    private Zone _cardPile;
    private boolean _toBottomOfPile;
    private boolean _releaseCaptives;
    private Zone _attachedCardsGoToZone;
    private boolean _asCaptureEscape;
    private boolean _asReleaseEscape;
    private boolean _captiveWasUndercover;
    private boolean _captiveWasMissing;
    private boolean _allCardsSituation;
    private boolean _lostCardsDoNotCountAsJustLost;
    private PhysicalCard _cardFiringWeaponToCapture;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private PlaceCardsInCardPileFromTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _placedInCardPile = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _releasedCaptives = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be placed in a card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public PlaceCardsInCardPileFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, Zone cardPile, boolean toBottomOfPile, boolean releaseCaptives) {
        this(action, cardsToPlaceInCardPile, cardPile, toBottomOfPile, releaseCaptives, Zone.LOST_PILE, false, false, false, null, false);
    }

    /**
     * Creates an effect that causes one more cards on table to be placed in a card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     * @param asCaptureEscape true if this is due to Escape option of capturing, otherwise false
     * @param captiveWasUndercover true if the captured character was undercover, otherwise false
     * @param captiveWasMissing true if the captured character was missing, otherwise false
     * @param cardFiringWeaponToCapture the card that fired weapon that caused capture, or null
     * @param asReleaseEscape true if this is due to Escape option of releasing, otherwise false
     */
    public PlaceCardsInCardPileFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, Zone cardPile, boolean toBottomOfPile, boolean releaseCaptives, Zone attachedCardsGoToZone, boolean asCaptureEscape, boolean captiveWasUndercover, boolean captiveWasMissing, PhysicalCard cardFiringWeaponToCapture, boolean asReleaseEscape) {
       this(action, cardsToPlaceInCardPile, cardPile, toBottomOfPile, releaseCaptives, attachedCardsGoToZone, asCaptureEscape, captiveWasUndercover, captiveWasMissing, cardFiringWeaponToCapture, asReleaseEscape, false, false);
    }

    /**
     * Creates an effect that causes one more cards on table to be placed in a card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     * @param asCaptureEscape true if this is due to Escape option of capturing, otherwise false
     * @param captiveWasUndercover true if the captured character was undercover, otherwise false
     * @param captiveWasMissing true if the captured character was missing, otherwise false
     * @param cardFiringWeaponToCapture the card that fired weapon that caused capture, or null
     * @param asReleaseEscape true if this is due to Escape option of releasing, otherwise false
     * @param allCardsSituation
     */
    public PlaceCardsInCardPileFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, Zone cardPile, boolean toBottomOfPile, boolean releaseCaptives, Zone attachedCardsGoToZone, boolean asCaptureEscape, boolean captiveWasUndercover, boolean captiveWasMissing, PhysicalCard cardFiringWeaponToCapture, boolean asReleaseEscape, boolean allCardsSituation) {
        this(action, cardsToPlaceInCardPile, cardPile, toBottomOfPile, releaseCaptives, attachedCardsGoToZone, asCaptureEscape, captiveWasUndercover, captiveWasMissing, cardFiringWeaponToCapture, asReleaseEscape, allCardsSituation, false);
    }

    /**
     * Creates an effect that causes one more cards on table to be placed in a card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param cardPile the card pile
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     * @param attachedCardsGoToZone the zone that any attached cards go to (instead of Lost Pile)
     * @param asCaptureEscape true if this is due to Escape option of capturing, otherwise false
     * @param captiveWasUndercover true if the captured character was undercover, otherwise false
     * @param captiveWasMissing true if the captured character was missing, otherwise false
     * @param cardFiringWeaponToCapture the card that fired weapon that caused capture, or null
     * @param asReleaseEscape true if this is due to Escape option of releasing, otherwise false
     * @param allCardsSituation true if this should be treated as an all cards situation
     * @param lostCardsDoNotCountAsJustLost true if lost cards should not count as "just lost"
     */
    public PlaceCardsInCardPileFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, Zone cardPile, boolean toBottomOfPile, boolean releaseCaptives, Zone attachedCardsGoToZone, boolean asCaptureEscape, boolean captiveWasUndercover, boolean captiveWasMissing, PhysicalCard cardFiringWeaponToCapture, boolean asReleaseEscape, boolean allCardsSituation, boolean lostCardsDoNotCountAsJustLost) {
        super(action);
        _originalCardsToPlaceInCardPile = Collections.unmodifiableCollection(cardsToPlaceInCardPile);
        _cardPile = cardPile;
        _toBottomOfPile = toBottomOfPile;
        _releaseCaptives = releaseCaptives;
        _attachedCardsGoToZone = attachedCardsGoToZone;
        _asCaptureEscape = asCaptureEscape;
        _captiveWasUndercover = captiveWasUndercover;
        _captiveWasMissing = captiveWasMissing;
        _cardFiringWeaponToCapture = cardFiringWeaponToCapture;
        _asReleaseEscape = asReleaseEscape;
        _allCardsSituation = allCardsSituation;
        _lostCardsDoNotCountAsJustLost = lostCardsDoNotCountAsJustLost;
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

        // 1) Trigger is "about to be placed in card pile" for cards specified cards to place in card pile.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being placed in card pile.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToPlaceInCardPile : _originalCardsToPlaceInCardPile) {
            effectResults.add(new AboutToPlaceCardInCardPileFromTableResult(subAction, cardToPlaceInCardPile, _cardPile, _that, _allCardsSituation));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Trigger is "about to be lost/placed in card pile" for any attached cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost/placed in card pile.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _placedInCardPile.addAll(Filters.filter(_originalCardsToPlaceInCardPile, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked), Filters.not(Filters.in(_preventedCards)))));
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _placedInCardPile, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());
                        Collection<PhysicalCard> attachedCardsToLeaveTable = Filters.filter(_attachedCardsToLeaveTable, game, Zone.ATTACHED);
                        if (_attachedCardsGoToZone == Zone.LOST_PILE) {
                            for (PhysicalCard attachedCardAboutToLeaveTable : attachedCardsToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToLoseCardFromTableResult(subAction, attachedCardAboutToLeaveTable, _that, true, attachedCardsToLeaveTable));
                            }
                        }
                        else {
                            for (PhysicalCard attachedCardAboutToLeaveTable : attachedCardsToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(new AboutToPlaceCardInCardPileFromTableResult(subAction, attachedCardAboutToLeaveTable, _attachedCardsGoToZone, _that, _allCardsSituation));
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
                        _attachedCardsToLeaveTable.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        _releasedCaptives.clear();
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _placedInCardPile, _releaseCaptives, new ArrayList<PhysicalCard>(), _releasedCaptives);

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _placedInCardPile) {
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
                        if (!_placedInCardPile.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardPlacedInCardPile : _placedInCardPile) {
                                cardPlacedInCardPile.updateRememberedInPlayCardInfo(game);
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
                            gameState.removeCardsFromZone(_placedInCardPile);
                            gameState.removeCardsFromZone(_attachedCardsToLeaveTable);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _placedInCardPile) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _attachedCardsToLeaveTable) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (_action.getActionSource() != null) {

                                if (!_placedInCardPile.isEmpty()) {
                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_placedInCardPile) + " on table to be placed in " + _cardPile.getHumanReadable() + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_placedInCardPile) + " on table to be placed in " + _cardPile.getHumanReadable());
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "lost";
                                    if (_attachedCardsGoToZone != Zone.LOST_PILE) {
                                        destinationText = "placed in " + _attachedCardsGoToZone.getHumanReadable();
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
                        if (!_placedInCardPile.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            if (!_placedInCardPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _placedInCardPile, _cardPile, _toBottomOfPile, _lostCardsDoNotCountAsJustLost));
                            }
                            if (!_attachedCardsToLeaveTable.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _attachedCardsToLeaveTable, _attachedCardsGoToZone));
                            }
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 7) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_placedInCardPile.isEmpty()) {

                            for (PhysicalCard putToPileCard : _placedInCardPile) {
                                if (_cardPile == Zone.RESERVE_DECK) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInReserveDeckFromTableResult(subAction, putToPileCard));
                                }
                                else if (_cardPile == Zone.FORCE_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInForcePileFromTableResult(subAction, putToPileCard));
                                }
                                else if (_cardPile == Zone.USED_PILE) {
                                    if (_asCaptureEscape) {
                                        game.getActionsEnvironment().emitEffectResult(
                                                new CaptureCharacterResult(subAction.getPerformingPlayer(), subAction.getActionSource(), _cardFiringWeaponToCapture, putToPileCard, _captiveWasUndercover, _captiveWasMissing, CaptureOption.ESCAPE));
                                    }
                                    else if (_asReleaseEscape) {
                                        game.getActionsEnvironment().emitEffectResult(
                                                new ReleaseCaptiveResult(subAction.getPerformingPlayer(), putToPileCard, ReleaseOption.ESCAPE));
                                    }
                                    else {
                                        game.getActionsEnvironment().emitEffectResult(
                                                new PutCardInUsedPileFromTableResult(subAction, putToPileCard));
                                    }
                                }
                                else if (_cardPile == Zone.LOST_PILE && !_lostCardsDoNotCountAsJustLost) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new LostCardFromTableResult(subAction, putToPileCard, _wasAttachedToWhenLostOrForfeited.get(putToPileCard), _locationLostOrForfeitedFrom.get(putToPileCard), _wasPresentWithWhenLostOrForfeited.get(putToPileCard)));
                                }
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
                                else if (_attachedCardsGoToZone == Zone.LOST_PILE) {
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
