package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the player performing the action to make the just drawn destiny card lost.
 */
public class LoseDestinyCardEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that causes the player performing the action to make the just drawn destiny card lost.
     * @param action the action performing this effect
     */
    public LoseDestinyCardEffect(Action action) {
        super(action);
    }

    @Override
    public String getText(SwccgGame game) {
        return "Lose destiny card";
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
            if (destinyCard != null
                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {

                game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", to be lost");
                subAction.appendEffect(
                        new LoseCardsFromOffTableSimultaneouslyEffect(subAction, Collections.singleton(destinyCard), false));
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
