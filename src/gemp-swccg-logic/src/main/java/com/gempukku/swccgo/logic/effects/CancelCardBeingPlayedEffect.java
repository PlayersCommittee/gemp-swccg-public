package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * Creates an effect that cancels a card being played/deployed.
 */
public class CancelCardBeingPlayedEffect extends AbstractSubActionEffect {
    private final RespondablePlayingCardEffect _playingCardEffect;

    /**
     * Creates an effect that cancels a card being played/deployed.
     * @param action the action performing this effect
     * @param playingCardEffect the playing card effect
     */
    public CancelCardBeingPlayedEffect(Action action, RespondablePlayingCardEffect playingCardEffect) {
        super(action);
        _playingCardEffect = playingCardEffect;
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
        final PhysicalCard cardToCancel = _playingCardEffect.getCard();

        final SubAction subAction = new SubAction(_action);

        if (!_playingCardEffect.isCanceled()) {
            // Perform the process of canceling the card
            subAction.appendEffect(
                    new PassthruEffect(subAction) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {

                            if (performingPlayer != null)
                                gameState.sendMessage(performingPlayer + " cancels " + GameUtils.getCardLink(cardToCancel) + " using " + GameUtils.getCardLink(actionSource));
                            else
                                gameState.sendMessage(GameUtils.getCardLink(actionSource) + " cancels " + GameUtils.getCardLink(cardToCancel));

                            _playingCardEffect.cancel(actionSource);

                            // Check if card is not longer in the "void" (which means it was already moved somewhere else)
                            // or if it is to be placed out of play
                            if (cardToCancel.getZone() == Zone.VOID && !_playingCardEffect.isToBePlacedOutOfPlay()) {

                                // Check if card should be placed in Used Pile or Lost Pile
                                if (modifiersQuerying.isPlacedInUsedPileWhenCanceled(gameState, cardToCancel, performingPlayer, actionSource)) {
                                    if (actionSource != null) {
                                        gameState.activatedCard(null, actionSource);
                                    }
                                    subAction.appendEffect(
                                            new PutCardFromVoidInUsedPileEffect(subAction, performingPlayer, cardToCancel));
                                }
                                else {
                                    subAction.appendEffect(
                                            new PutCardFromVoidInLostPileEffect(subAction, performingPlayer, cardToCancel));
                                }
                            }
                        }
                    }
            );
        }
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}

