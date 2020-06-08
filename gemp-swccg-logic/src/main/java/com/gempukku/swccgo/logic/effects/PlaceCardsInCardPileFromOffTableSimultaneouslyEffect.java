package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.PutCardInForcePileFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInReserveDeckFromOffTableResult;
import com.gempukku.swccgo.logic.timing.results.PutCardInUsedPileFromOffTableResult;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed in a card pile simultaneously.
 * This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class PlaceCardsInCardPileFromOffTableSimultaneouslyEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _originalCardsToPlaceInCardPile;
    private Zone _cardPile;
    private String _cardPileOwner;
    private boolean _toBottomOfPile;
    private PlaceCardsInCardPileFromOffTableSimultaneouslyEffect _that;

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, in hand, etc.) to be placed in a
     * card pile simultaneously.
     * @param action the action performing this effect
     * @param cardsToPlaceInCardPile the cards to place in card pile
     * @param cardPile the card pile
     * @param cardPileOwner the owner of the card pile, or null if cards are placed in card pile of current owner
     * @param toBottomOfPile true if cards are placed on the bottom of the card pile, otherwise false
     */
    public PlaceCardsInCardPileFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToPlaceInCardPile, Zone cardPile, String cardPileOwner, boolean toBottomOfPile) {
        super(action);
        _originalCardsToPlaceInCardPile = Collections.unmodifiableCollection(cardsToPlaceInCardPile);
        _cardPile = cardPile;
        if (_cardPile == Zone.LOST_PILE)
            throw new UnsupportedOperationException("Should not call this with cardPile " + cardPile);

        _cardPileOwner = cardPileOwner;
        _toBottomOfPile = toBottomOfPile;
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

        // 1) Remove the cards from the existing zone.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToPlaceInCardPile.isEmpty()) {

                            // Remove cards from existing zone
                            gameState.removeCardsFromZone(_originalCardsToPlaceInCardPile);

                            // Add card to void zone before allowing player to choose order to place cards in card piles.
                            for (PhysicalCard card : _originalCardsToPlaceInCardPile) {
                                // Update card owner if going to specified owner's card pile
                                if (_cardPileOwner != null) {
                                    card.setOwner(_cardPileOwner);
                                }
                                gameState.addCardToTopOfZone(card, Zone.VOID, card.getOwner());
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
                        if (!_originalCardsToPlaceInCardPile.isEmpty()) {

                            SubAction putInCardPileSubAction = new SubAction(subAction);
                                putInCardPileSubAction.appendEffect(
                                        new PutCardsInCardPileEffect(subAction, game, _originalCardsToPlaceInCardPile, _cardPile, _toBottomOfPile));
                            // Stack sub-action
                            subAction.stackSubAction(putInCardPileSubAction);
                        }
                    }
                }
        );

        // 6) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_originalCardsToPlaceInCardPile.isEmpty()) {

                            for (PhysicalCard putToPileCard : _originalCardsToPlaceInCardPile) {
                                if (_cardPile == Zone.RESERVE_DECK) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInReserveDeckFromOffTableResult(subAction, putToPileCard));
                                }
                                else if (_cardPile == Zone.FORCE_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInForcePileFromOffTableResult(subAction, putToPileCard));
                                }
                                else if (_cardPile == Zone.USED_PILE) {
                                    game.getActionsEnvironment().emitEffectResult(
                                            new PutCardInUsedPileFromOffTableResult(subAction, putToPileCard));
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
