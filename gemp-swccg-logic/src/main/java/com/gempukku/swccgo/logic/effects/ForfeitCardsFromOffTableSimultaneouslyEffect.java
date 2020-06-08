package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToLostPileFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.ForfeitedCardToUsedPileFromOffTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be forfeited simultaneously.
 * This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class ForfeitCardsFromOffTableSimultaneouslyEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _originalCardsToForfeit;
    private Zone _forfeitCardsToZone;
    private boolean _toBottomOfPile;

    private Map<PhysicalCard, Float> _totalBattleDamageToSatisfyMap = new HashMap<PhysicalCard, Float>();
    private Map<PhysicalCard, Float> _totalAttritionToSatisfyMap = new HashMap<PhysicalCard, Float>();

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be forfeited
     * simultaneously.
     * @param action the action performing this effect
     * @param cardsToForfeit the cards to forfeit
     * @param forfeitToUsedPile true if cards are forfeited to Used Pile (instead of Lost Pile), otherwise false
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public ForfeitCardsFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToForfeit, boolean forfeitToUsedPile, boolean toBottomOfPile) {
        super(action);
        _originalCardsToForfeit = Collections.unmodifiableCollection(cardsToForfeit);
        _forfeitCardsToZone = forfeitToUsedPile ? Zone.USED_PILE : Zone.LOST_PILE;
        _toBottomOfPile = toBottomOfPile;
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

        // 1) Remove the cards from the existing zone.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToForfeit.isEmpty()) {

                            // Determine the battle damage and attrition each forfeited card satisfies
                            for (PhysicalCard cardToForfeit : _originalCardsToForfeit) {

                                float forfeitValue = modifiersQuerying.getForfeitWhenForfeiting(gameState, cardToForfeit);
                                _totalBattleDamageToSatisfyMap.put(cardToForfeit, forfeitValue);

                                if (!modifiersQuerying.cannotSatisfyAttrition(gameState, cardToForfeit)) {
                                    _totalAttritionToSatisfyMap.put(cardToForfeit, forfeitValue);
                                }
                            }

                            // Remove cards from existing zone
                            gameState.removeCardsFromZone(_originalCardsToForfeit);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _originalCardsToForfeit) {
                                game.getGameState().addCardToTopOfZone(card, Zone.VOID, card.getOwner());
                            }
                        }
                    }
                }
        );

        // 2) Choose order that cards are placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToForfeit.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _originalCardsToForfeit, _forfeitCardsToZone, _toBottomOfPile));
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 3) Satisfy battle damage and attrition
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        BattleState battleState = gameState.getBattleState();

                        // Satisfy battle damage
                        for (Float amount : _totalBattleDamageToSatisfyMap.values()) {
                            battleState.increaseBattleDamageSatisfied(_action.getPerformingPlayer(), amount);
                        }

                        // Satisfy attrition
                        for (Float amount : _totalAttritionToSatisfyMap.values()) {
                            battleState.increaseAttritionSatisfied(_action.getPerformingPlayer(), amount);
                        }

                        // Set that the current lose or forfeit effect was fulfilled
                        battleState.getCurrentLoseOrForfeitEffect().setFulfilledByOtherAction();
                    }
                }
        );

        // 4) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToForfeit.isEmpty()) {

                            for (PhysicalCard forfeitedFromPlayCard : _originalCardsToForfeit) {
                                if (_forfeitCardsToZone == Zone.USED_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new ForfeitedCardToUsedPileFromOffTableResult(subAction, forfeitedFromPlayCard));
                                }
                                else {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new ForfeitedCardToLostPileFromOffTableResult(subAction, forfeitedFromPlayCard));
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
        return true;
    }
}
