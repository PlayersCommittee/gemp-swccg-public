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
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.AboutToPlaceCardOutOfPlayFromTableResult;
import com.gempukku.swccgo.logic.timing.results.LostCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PlacedCardOutOfPlayFromTableResult;

import java.util.*;

/**
 * An effect that causes one more cards on table (including converted locations and stacked cards) to be placed out of play simultaneously.
 * An cards attached or aboard the placed out of play cards are lost or placed in specified zone (or released) as required.
 * This effect emits the "about to be placed out of play" effect result and the "just placed out of play" effect result
 * at the appropriate time. This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class PlaceCardsOutOfPlayFromTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToPlaceOutOfPlay;
    private boolean _releaseCaptives;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private PlaceCardsOutOfPlayFromTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _placedOutOfPlay = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _releasedCaptives = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be placed out of play simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceOutOfPlay the cards to place out of play
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public PlaceCardsOutOfPlayFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceOutOfPlay, boolean releaseCaptives) {
        super(action);
        _originalCardsToPlaceOutOfPlay = Collections.unmodifiableCollection(cardsToPlaceOutOfPlay);
        _releaseCaptives = releaseCaptives;
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

        // 1) Trigger is "about to be placed out of play" for cards specified cards to be placed out of play.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being placed out of play.
        List<EffectResult> effectResults = new ArrayList<EffectResult>();
        for (PhysicalCard cardToPlaceOutOfPlay : _originalCardsToPlaceOutOfPlay) {
            effectResults.add(new AboutToPlaceCardOutOfPlayFromTableResult(subAction, cardToPlaceOutOfPlay, _that));
        }
        subAction.appendEffect(new TriggeringResultsEffect(subAction, effectResults));

        // 2) Trigger is "about to be lost" for any attached cards.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _placedOutOfPlay.addAll(Filters.filter(_originalCardsToPlaceOutOfPlay, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked), Filters.not(Filters.in(_preventedCards)))));
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _placedOutOfPlay, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());

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
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_attachedCardsToLeaveTable);
                        _attachedCardsToLeaveTable.clear();
                        _attachedCardsToLeaveTable.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        _releasedCaptives.clear();
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _placedOutOfPlay, _releaseCaptives, new ArrayList<PhysicalCard>(), _releasedCaptives);

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _placedOutOfPlay) {
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
                        if (!_placedOutOfPlay.isEmpty() || !_attachedCardsToLeaveTable.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardToPlaceOutOfPlay : _placedOutOfPlay) {
                                cardToPlaceOutOfPlay.updateRememberedInPlayCardInfo(game);
                            }

                            for (PhysicalCard cardToLose : _attachedCardsToLeaveTable) {
                                cardToLose.updateRememberedInPlayCardInfo(game);

                                _wasAttachedToWhenLostOrForfeited.put(cardToLose, cardToLose.getAttachedTo());

                                PhysicalCard lostFromLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardToLose);
                                if (lostFromLocation != null) {
                                    _locationLostOrForfeitedFrom.put(cardToLose, lostFromLocation);
                                }

                                Collection<PhysicalCard> presentWithWhenLost = Filters.filterActive(game, null, Filters.presentWith(cardToLose));
                                _wasPresentWithWhenLostOrForfeited.put(cardToLose, presentWithWhenLost);
                            }

                            // Remove cards from the table
                            gameState.removeCardsFromZone(_placedOutOfPlay);
                            gameState.removeCardsFromZone(_attachedCardsToLeaveTable);

                            // Place cards going out of play to the out of play zone
                            for (PhysicalCard card : _placedOutOfPlay) {
                                game.getGameState().addCardToTopOfZone(card, Zone.OUT_OF_PLAY, card.getOwner());
                            }

                            // Add card to void zone before allowing player to choose order to place cards in card pile.
                            for (PhysicalCard card : _attachedCardsToLeaveTable) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (_action.getActionSource() != null) {

                                if (!_placedOutOfPlay.isEmpty()) {
                                    String destinationText = "placed out of play" + (asEaten() ? " due to being 'eaten'" : "");

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_placedOutOfPlay) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_placedOutOfPlay) + " on table to be " + destinationText);
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "lost";

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText + " using " + GameUtils.getCardLink(_action.getActionSource()));
                                    else
                                        game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText);
                                }
                            }
                            else {
                                if (!_placedOutOfPlay.isEmpty()) {
                                    String destinationText = "placed out of play" + (asEaten() ? " due to being 'eaten'" : "");

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_placedOutOfPlay) + " on table to be " + destinationText);
                                    else
                                        game.getGameState().sendMessage(GameUtils.getAppendedNames(_placedOutOfPlay) + " on table is " + destinationText);
                                }

                                if (!_attachedCardsToLeaveTable.isEmpty()) {
                                    String destinationText = "lost";

                                    if (_action.getPerformingPlayer() != null)
                                        game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table to be " + destinationText);
                                    else
                                        game.getGameState().sendMessage(GameUtils.getAppendedNames(_attachedCardsToLeaveTable) + " on table is " + destinationText);
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
                        if (!_attachedCardsToLeaveTable.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            putInCardPileSubAction.appendEffect(
                                    new PutCardsInCardPileEffect(subAction, game, _attachedCardsToLeaveTable, Zone.LOST_PILE));
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 7) Emit effect results for cards placed out of play and in Lost Pile
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_placedOutOfPlay.isEmpty()) {

                            for (PhysicalCard placedOutOfPlayCard : _placedOutOfPlay) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new PlacedCardOutOfPlayFromTableResult(subAction, placedOutOfPlayCard));
                            }
                        }
                        if (!_attachedCardsToLeaveTable.isEmpty()) {

                            for (PhysicalCard putToPileCard : _attachedCardsToLeaveTable) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new LostCardFromTableResult(subAction, putToPileCard, _wasAttachedToWhenLostOrForfeited.get(putToPileCard), _locationLostOrForfeitedFrom.get(putToPileCard), _wasPresentWithWhenLostOrForfeited.get(putToPileCard)));
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
