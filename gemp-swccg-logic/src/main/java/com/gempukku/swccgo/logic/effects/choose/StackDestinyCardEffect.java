package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that causes the player performing the action to stack the just drawn destiny card on a card.
 */
public class StackDestinyCardEffect extends AbstractSubActionEffect {
    private PhysicalCard _stackOn;

    /**
     * Creates an effect that causes the player performing the action to stack the just drawn destiny card on a card.
     * @param action the action performing this effect
     * @param stackOn the card to stack on
     */
    public StackDestinyCardEffect(Action action, PhysicalCard stackOn) {
        super(action);
        _stackOn = stackOn;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        GameState gameState = game.getGameState();
                        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
                        if (drawDestinyState != null) {
                            PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
                            if (destinyCard != null
                                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {
                                gameState.removeCardFromZone(destinyCard);
                                gameState.stackCard(destinyCard, _stackOn, false, false, false);
                                gameState.sendMessage(_action.getPerformingPlayer() + " stacks just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", on " + GameUtils.getCardLink(_stackOn));
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
