package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to take the just drawn destiny card into hand.
 */
public class TakeDestinyCardIntoHandEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that causes the player performing the action to deploy the just drawn destiny card into hand.
     * @param action the action performing this effect
     */
    public TakeDestinyCardIntoHandEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);

        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState != null) {
            PhysicalCard destinyCard = drawDestinyState.getDrawDestinyEffect().getDrawnDestinyCard();
            if (destinyCard != null && destinyCard.getOwner().equals(_action.getPerformingPlayer())
                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {
                String msgText = _action.getPerformingPlayer() + " takes just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", into hand";
                subAction.appendEffect(
                        new TakeOneCardIntoHandFromOffTableEffect(subAction, _action.getPerformingPlayer(), destinyCard, msgText) {
                            @Override
                            protected void afterCardTakenIntoHand() {
                            }
                        });
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
