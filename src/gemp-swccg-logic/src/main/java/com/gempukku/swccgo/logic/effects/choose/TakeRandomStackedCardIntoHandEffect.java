package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RemovedFromStackedResult;

import java.util.List;

/**
 * An effect to take a random stacked card into hand.
 */
public class TakeRandomStackedCardIntoHandEffect extends AbstractSubActionEffect {
    private String _playerId;
    private PhysicalCard _stackedOn;

    /**
     * Creates an effect that causes the player to take a random card stacked on the specified card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that the stacked cards are stacked on
     */
    public TakeRandomStackedCardIntoHandEffect(Action action, String playerId, PhysicalCard stackedOn) {
        super(action);
        _playerId = playerId;
        _stackedOn = stackedOn;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action, _playerId);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(final SwccgGame game) {
                        // Determine the cards to choose from
                        List<PhysicalCard> randomCards = GameUtils.getRandomCards(game.getGameState().getStackedCards(_stackedOn), 1);
                        if (!randomCards.isEmpty()) {
                            PhysicalCard card = randomCards.get(0);
                            String cardInfo = card.getZone().isFaceDown() ? "a card" : GameUtils.getCardLink(card);
                            String msgText = _playerId + " randomly takes " + cardInfo + " into hand from " + GameUtils.getCardLink(card.getStackedOn());
                            subAction.appendEffect(
                                    new TakeOneCardIntoHandFromOffTableEffect(subAction, _playerId, card, msgText) {
                                        @Override
                                        protected void afterCardTakenIntoHand() {
                                            game.getActionsEnvironment().emitEffectResult(
                                                    new RemovedFromStackedResult(subAction));
                                        }
                                    });
                        }
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
