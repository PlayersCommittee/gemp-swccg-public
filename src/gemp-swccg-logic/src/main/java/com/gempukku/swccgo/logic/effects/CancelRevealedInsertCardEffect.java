package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.InsertCardRevealedResult;

/**
 * Creates an effect that cancels a revealed 'insert' card.
 */
public class CancelRevealedInsertCardEffect extends AbstractSubActionEffect {
    private final InsertCardRevealedResult _effectResult;

    /**
     * Creates an effect that cancels a revealed 'insert' card.
     * @param action the action performing this effect
     * @param effectResult the insert card revealed effect result
     */
    public CancelRevealedInsertCardEffect(Action action, InsertCardRevealedResult effectResult) {
        super(action);
        _effectResult = effectResult;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        final PhysicalCard actionSource = _action.getActionSource();
        final String performingPlayer = _action.getPerformingPlayer();
        final PhysicalCard cardToCancel = _effectResult.getCard();

        final SubAction subAction = new SubAction(_action);

        // Perform the process of canceling the card
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (cardToCancel.isInsertCardRevealed()) {

                            if (performingPlayer != null)
                                gameState.sendMessage(performingPlayer + " cancels " + GameUtils.getCardLink(cardToCancel) + " using " + GameUtils.getCardLink(actionSource));
                            else
                                gameState.sendMessage(GameUtils.getCardLink(actionSource) + " cancels " + GameUtils.getCardLink(cardToCancel));

                            // Check if card should be placed in Used Pile or Lost Pile
                            if (modifiersQuerying.isPlacedInUsedPileWhenCanceled(gameState, cardToCancel, performingPlayer, actionSource)) {
                                if (actionSource != null) {
                                    gameState.activatedCard(null, actionSource);
                                }
                                subAction.appendEffect(
                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(subAction, cardToCancel, Zone.USED_PILE, false));
                            } else {
                                subAction.appendEffect(
                                        new PutCardFromReserveDeckOnTopOfCardPileEffect(subAction, cardToCancel, Zone.LOST_PILE, false));
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

