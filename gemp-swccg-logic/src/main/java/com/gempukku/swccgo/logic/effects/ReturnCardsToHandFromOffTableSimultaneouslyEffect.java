package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ReturnedCardToHandFromOffTableResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An effect that causes one more cards not on table (e.g. in a card pile, etc.) to be returned to hand.
 * This effect should be not be used directly be a card, but instead just by rules or other effects.
 */
class ReturnCardsToHandFromOffTableSimultaneouslyEffect extends AbstractSubActionEffect {
    private Collection<PhysicalCard> _originalCardsToReturnToHand;
    private Collection<PhysicalCard> _returnedToHand = new ArrayList<PhysicalCard>();

    /**
     * Creates an effect that causes one more cards not on table (e.g. in a card pile, etc.) to be returned to hand.
     * @param action the action performing this effect
     * @param cardsToReturnToHand the cards to return to hand
     */
    public ReturnCardsToHandFromOffTableSimultaneouslyEffect(Action action, Collection<PhysicalCard> cardsToReturnToHand) {
        super(action);
        _originalCardsToReturnToHand = Collections.unmodifiableCollection(cardsToReturnToHand);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();

        final SubAction subAction = new SubAction(_action);

        // 1) Remove the cards from the existing zone.
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        _returnedToHand.addAll(Filters.filter(_originalCardsToReturnToHand, game, Filters.not(Filters.or(Filters.onTable, Filters.outOfPlay, Filters.stackedOn(null, Filters.grabber), Zone.OUT_OF_PLAY))));
                        if (!_returnedToHand.isEmpty()) {

                            // Remove cards from existing zone
                            gameState.removeCardsFromZone(_returnedToHand);

                            if (!_returnedToHand.isEmpty()) {
                                if (_action.getPerformingPlayer() != null)
                                    game.getGameState().sendMessage(_action.getPerformingPlayer() + " causes " + GameUtils.getAppendedNames(_returnedToHand) + " to be returned to hand using " + GameUtils.getCardLink(_action.getActionSource()));
                                else
                                    game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes " + GameUtils.getAppendedNames(_returnedToHand) + " to be returned to hand");
                            }

                            // Return cards to hand.
                            for (PhysicalCard card : _returnedToHand) {
                                gameState.addCardToZone(card, Zone.HAND, card.getOwner());
                            }
                        }
                    }
                }
        );

        // 2) Emit effect results for cards placed in card piles
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (!_returnedToHand.isEmpty()) {

                            for (PhysicalCard card : _returnedToHand) {
                                game.getActionsEnvironment().emitEffectResult(
                                        new ReturnedCardToHandFromOffTableResult(subAction, card));
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
