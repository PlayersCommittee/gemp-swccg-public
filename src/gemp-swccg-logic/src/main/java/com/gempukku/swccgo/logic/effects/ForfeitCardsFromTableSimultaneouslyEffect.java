package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.*;

/**
 * An effect that causes one more cards on table to be forfeited simultaneously.
 * Any cards attached or aboard the forfeited cards are lost (or released) as required.  This effect emits the effect results
 * at the appropriate time. This effect should be not be used directly be a card, but instead just by rules
 * or other effects.
 */
public class ForfeitCardsFromTableSimultaneouslyEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private Collection<PhysicalCard> _originalCardsToForfeit;
    private Zone _forfeitCardsToZone;
    private Float _forfeitValueToUse;
    private boolean _toBottomOfPile;
    private boolean _releaseCaptives;
    private Set<PhysicalCard> _preventedCards = new HashSet<PhysicalCard>();
    private Map<PhysicalCard, Float> _totalBattleDamageToSatisfyMap = new HashMap<PhysicalCard, Float>();
    private Map<PhysicalCard, Float> _totalAttritionToSatisfyMap = new HashMap<PhysicalCard, Float>();
    private boolean _satisfyAllBattleDamage;
    private boolean _satisfyAllAttrition;
    private ForfeitCardsFromTableSimultaneouslyEffect _that;

    private Map<PhysicalCard, PhysicalCard> _wasAttachedToWhenLostOrForfeited = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, PhysicalCard> _locationLostOrForfeitedFrom = new HashMap<PhysicalCard, PhysicalCard>();
    private Map<PhysicalCard, Collection<PhysicalCard>> _wasPresentWithWhenLostOrForfeited = new HashMap<PhysicalCard, Collection<PhysicalCard>>();
    private Collection<PhysicalCard> _forfeitedCards = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _forfeitedFromPlay = new ArrayList<PhysicalCard>();
    private Map<PhysicalCard, Float> _forfeitedAndRemainsInPlay = new HashMap<PhysicalCard, Float>();
    private Collection<PhysicalCard> _lostFromPlay = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _attachedCardsToLeaveTable = new ArrayList<PhysicalCard>();
    private Collection<PhysicalCard> _releasedCaptives = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards on table to be forfeited simultaneously.
     * @param action the action performing this effect
     * @param cardsToForfeit the cards to forfeit
     */
    public ForfeitCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToForfeit) {
        this(action, cardsToForfeit, null, false);
    }

    /**
     * Creates an effect that causes one more cards on table to be forfeited simultaneously.
     * @param action the action performing this effect
     * @param cardsToForfeit the cards to forfeit
     * @param forfeitValueToUse specifies a forfeit value to use, otherwise null means to use actual forfeit value
     * @param forfeitToUsedPile true if cards are forfeited to Used Pile (instead of Lost Pile), otherwise false
     */
    public ForfeitCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToForfeit, Float forfeitValueToUse, boolean forfeitToUsedPile) {
        this(action, cardsToForfeit, forfeitValueToUse, forfeitToUsedPile, false, true);
    }

    /**
     * Creates an effect that causes one more cards on table to be forfeited simultaneously.
     * @param action the action performing this effect
     * @param cardsToForfeit the cards to forfeit
     * @param forfeitValueToUse specifies a forfeit value to use, otherwise null means to use actual forfeit value
     * @param forfeitToUsedPile true if cards are forfeited to Used Pile (instead of Lost Pile), otherwise false
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     * @param releaseCaptives true if captives are released, otherwise false
     */
    public ForfeitCardsFromTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToForfeit, Float forfeitValueToUse, boolean forfeitToUsedPile, boolean toBottomOfPile, boolean releaseCaptives) {
        super(action);
        _originalCardsToForfeit = Collections.unmodifiableCollection(cardsToForfeit);
        _forfeitValueToUse = forfeitValueToUse;
        _forfeitCardsToZone = forfeitToUsedPile ? Zone.USED_PILE : Zone.LOST_PILE;
        _toBottomOfPile = toBottomOfPile;
        _releaseCaptives = releaseCaptives;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Determines if the cards are to be forfeited to Lost Pile.
     * @return true or false
     */
    public boolean isForfeitToLostPile() {
        return _forfeitCardsToZone == Zone.LOST_PILE;
    }


    /**
     * Sets the cards to be forfeited to Used Pile instead of Lost Pile.
     */
    public void setForfeitToUsedPile() {
        _forfeitCardsToZone = Zone.USED_PILE;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final BattleState battleState = gameState.getBattleState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final SubAction subAction = new SubAction(_action);

        // 1) Satisfy battle damage and attrition.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _forfeitedCards = Filters.filter(_originalCardsToForfeit, game, Filters.and(Filters.or(Filters.onTable, Filters.stacked)));
                        for (PhysicalCard cardToForfeit : _forfeitedCards) {
                            if (modifiersQuerying.isRemainsInPlayAndReducesForfeitWhenForfeited(gameState, cardToForfeit))
                                _forfeitedAndRemainsInPlay.put(cardToForfeit, 0f);
                            else
                                _forfeitedFromPlay.add(cardToForfeit);
                        }

                        gameState.sendMessage(_action.getPerformingPlayer() + " choose to forfeit " + GameUtils.getAppendedNames(_forfeitedCards));

                        float battleDamageRemaining = battleState.getBattleDamageRemaining(game, _action.getPerformingPlayer());
                        float attritionRemaining = battleState.getAttritionRemaining(game, _action.getPerformingPlayer());

                        // Determine the battle damage and attrition each forfeited card satisfies
                        for (PhysicalCard cardToForfeit : _forfeitedCards) {

                            float forfeitValue = (_forfeitValueToUse != null) ? _forfeitValueToUse : modifiersQuerying.getForfeitWhenForfeiting(gameState, cardToForfeit);

                            _totalBattleDamageToSatisfyMap.put(cardToForfeit, Math.min(battleDamageRemaining, forfeitValue));
                            if (!_satisfyAllBattleDamage) {
                                _satisfyAllBattleDamage = modifiersQuerying.isSatisfyAllBattleDamageWhenForfeited(gameState, cardToForfeit);
                            }
                            float amountToReduceForfeit = Math.min(battleDamageRemaining, forfeitValue);

                            if (!modifiersQuerying.cannotSatisfyAttrition(gameState, cardToForfeit)) {
                                _totalAttritionToSatisfyMap.put(cardToForfeit, Math.min(attritionRemaining, forfeitValue));
                                if (!_satisfyAllAttrition) {
                                    _satisfyAllAttrition = modifiersQuerying.isSatisfyAllAttritionWhenForfeited(gameState, cardToForfeit);
                                }
                                amountToReduceForfeit = Math.max(Math.min(attritionRemaining, forfeitValue), amountToReduceForfeit);
                            }

                            if (_forfeitedAndRemainsInPlay.containsKey(cardToForfeit)) {
                                _forfeitedAndRemainsInPlay.put(cardToForfeit, amountToReduceForfeit);
                            }
                        }

                        // Satisfy battle damage
                        if (_satisfyAllBattleDamage) {
                            battleState.satisfyAllBattleDamage(_action.getPerformingPlayer());
                        } else {
                            for (Float amount : _totalBattleDamageToSatisfyMap.values()) {
                                battleState.increaseBattleDamageSatisfied(_action.getPerformingPlayer(), amount);
                            }
                        }

                        // Satisfy attrition
                        if (_satisfyAllAttrition) {
                            battleState.satisfyAllAttrition(_action.getPerformingPlayer());
                        } else {
                            for (Float amount : _totalAttritionToSatisfyMap.values()) {
                                battleState.increaseAttritionSatisfied(_action.getPerformingPlayer(), amount);
                            }
                        }

                        if (!_forfeitedAndRemainsInPlay.isEmpty()) {

                            for (PhysicalCard forfeitedAndRemainsInPlay : _forfeitedAndRemainsInPlay.keySet()) {
                                float amountToReduce = _forfeitedAndRemainsInPlay.get(forfeitedAndRemainsInPlay);
                                if (amountToReduce > 0) {
                                    game.getGameState().activatedCard(_action.getPerformingPlayer(), forfeitedAndRemainsInPlay);
                                    game.getModifiersEnvironment().addUntilEndOfGameModifier(
                                            new ForfeitModifier(forfeitedAndRemainsInPlay, -amountToReduce, true));
                                    gameState.sendMessage(GameUtils.getCardLink(forfeitedAndRemainsInPlay) + "'s forfeit is reduced by " + GuiUtils.formatAsString(amountToReduce));
                                    game.getActionsEnvironment().emitEffectResult(
                                            new ResetOrModifyCardAttributeResult(null, forfeitedAndRemainsInPlay));
                                }
                            }
                        }

                        // Set that the current lose or forfeit effect was fulfilled
                        if (battleState.getCurrentLoseOrForfeitEffect() != null) {
                            battleState.getCurrentLoseOrForfeitEffect().setFulfilledByOtherAction();
                        }
                    }
                }
        );

        // 2) If forfeited cards are going to leave table, trigger is "about to be forfeited" for cards specified cards to leave table due to being forfeited.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from leaving table, or setForfeitToUsedPile
        // method can be called to cause the cards to be forfeited to Used Pile instead.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        for (PhysicalCard cardToForfeit : _forfeitedFromPlay) {
                            game.getActionsEnvironment().emitEffectResult(
                                    new AboutToForfeitCardFromTableResult(subAction, cardToForfeit, _that));
                        }
                    }
                }
        );

        // 3) Trigger is "about to be lost" for any attached cards, if forfeited card is still going to leave table.
        // When responding to the trigger, the preventEffectOnCard method can be called to prevent specified cards from being lost.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_forfeitedFromPlay);
                        _forfeitedFromPlay.clear();
                        _forfeitedFromPlay.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));

                        CardsLeavePlayUtils.cardsToLeavePlay(game, _forfeitedFromPlay, true, _attachedCardsToLeaveTable, new ArrayList<PhysicalCard>());
                        _lostFromPlay.addAll(_attachedCardsToLeaveTable);
                        Collection<PhysicalCard> attachedCardsAboutToBeLost = Filters.filter(_attachedCardsToLeaveTable, game, Zone.ATTACHED);
                        for (PhysicalCard attachedCardAboutToBeLost : attachedCardsAboutToBeLost) {
                            game.getActionsEnvironment().emitEffectResult(new AboutToLoseCardFromTableResult(subAction, attachedCardAboutToBeLost, _that, true, attachedCardsAboutToBeLost));
                        }
                        _attachedCardsToLeaveTable.clear();
                    }
                }
        );

        // 4) Update cards that are actually leaving table and captives to be released
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        List<PhysicalCard> cardsToCheck = new ArrayList<PhysicalCard>(_lostFromPlay);
                        _lostFromPlay.clear();
                        _lostFromPlay.addAll(Filters.filter(cardsToCheck, game, Filters.not(Filters.in(_preventedCards))));
                        _releasedCaptives.clear();
                        CardsLeavePlayUtils.cardsToLeavePlay(game, _forfeitedFromPlay, _releaseCaptives, new ArrayList<PhysicalCard>(), _releasedCaptives);

                        // Mark cards as leaving table during this time, this is checked to ensured released captives
                        // do not get recaptured by the escort leaving table during this process
                        for (PhysicalCard card : _forfeitedFromPlay) {
                            card.setLeavingTable(true);
                        }
                        for (PhysicalCard card : _lostFromPlay) {
                            card.setLeavingTable(true);
                        }
                    }
                }
        );

        // 5) Release captives
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

        // 6) Persist information about the cards before and then remove the cards from the table.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_forfeitedFromPlay.isEmpty() || !_forfeitedAndRemainsInPlay.keySet().isEmpty() || !_lostFromPlay.isEmpty()) {

                            // Make sure cards remember current information and get location indexes
                            for (PhysicalCard cardPlacedInCardPile : _forfeitedFromPlay) {
                                cardPlacedInCardPile.updateRememberedInPlayCardInfo(game);

                                _wasAttachedToWhenLostOrForfeited.put(cardPlacedInCardPile, cardPlacedInCardPile.getAttachedTo());

                                // Record the location the card is forfeited from
                                PhysicalCard forfeitedFromLocation = modifiersQuerying.getLocationThatCardIsAt(gameState, cardPlacedInCardPile);
                                if (forfeitedFromLocation != null) {
                                    _locationLostOrForfeitedFrom.put(cardPlacedInCardPile, forfeitedFromLocation);
                                    modifiersQuerying.forfeitedFromLocation(forfeitedFromLocation, cardPlacedInCardPile);
                                }

                                Collection<PhysicalCard> presentWithWhenLost = Filters.filterActive(game, null, Filters.presentWith(cardPlacedInCardPile));
                                _wasPresentWithWhenLostOrForfeited.put(cardPlacedInCardPile, presentWithWhenLost);
                            }

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

                            // Remove cards from the table
                            gameState.removeCardsFromZone(_forfeitedFromPlay);
                            gameState.removeCardsFromZone(_lostFromPlay);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _forfeitedFromPlay) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                            for (PhysicalCard card : _lostFromPlay) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }

                            if (!_forfeitedFromPlay.isEmpty()) {
                                game.getGameState().sendMessage(_action.getPerformingPlayer() + " forfeits " + GameUtils.getAppendedNames(_forfeitedFromPlay) + " from table");
                            }

                            if (!_forfeitedAndRemainsInPlay.keySet().isEmpty()) {
                                game.getGameState().sendMessage(_action.getPerformingPlayer() + " 'forfeits' " + GameUtils.getAppendedNames(_forfeitedAndRemainsInPlay.keySet()));
                            }

                            if (!_lostFromPlay.isEmpty()) {
                                game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_lostFromPlay) + " on table to be lost");
                            }
                        }
                    }
                }
        );

        // 8) Choose order that cards are placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_forfeitedFromPlay.isEmpty() || !_lostFromPlay.isEmpty()) {

                            List<PhysicalCard> toBePlacedInLostPile = new ArrayList<PhysicalCard>(_lostFromPlay);
                            List<PhysicalCard> toBePlacedInAnotherPile = new ArrayList<PhysicalCard>();

                            if (_forfeitCardsToZone == Zone.LOST_PILE) {
                                toBePlacedInLostPile.addAll(_forfeitedFromPlay);
                            }
                            else {
                                toBePlacedInAnotherPile.addAll(_forfeitedFromPlay);
                            }

                            // Place the cards in the card piles
                            SubAction putInCardPileSubAction = new SubAction(subAction);
                            if (!toBePlacedInLostPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, toBePlacedInLostPile, Zone.LOST_PILE, _toBottomOfPile));
                            }
                            if (!toBePlacedInAnotherPile.isEmpty()) {
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, toBePlacedInAnotherPile, _forfeitCardsToZone, _toBottomOfPile));
                            }
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 9) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_forfeitedFromPlay.isEmpty()) {

                            for (PhysicalCard forfeitedFromPlayCard : _forfeitedFromPlay) {
                                if (_forfeitCardsToZone == Zone.USED_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new ForfeitedCardToUsedPileFromTableResult(subAction, forfeitedFromPlayCard));
                                }
                                else {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new ForfeitedCardToLostPileFromTableResult(subAction, forfeitedFromPlayCard, _wasAttachedToWhenLostOrForfeited.get(forfeitedFromPlayCard), _locationLostOrForfeitedFrom.get(forfeitedFromPlayCard), _wasPresentWithWhenLostOrForfeited.get(forfeitedFromPlayCard)));
                                }
                            }
                        }
                        if (!_lostFromPlay.isEmpty()) {

                            for (PhysicalCard lostFromPlayCard : _lostFromPlay) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new LostCardFromTableResult(subAction, lostFromPlayCard, _wasAttachedToWhenLostOrForfeited.get(lostFromPlayCard), _locationLostOrForfeitedFrom.get(lostFromPlayCard), _wasPresentWithWhenLostOrForfeited.get(lostFromPlayCard)));
                            }
                        }
                    }
                }
        );

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
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
