package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to place the just drawn destiny card out of play.
 */
public class PlaceDestinyCardOutOfPlayEffect extends AbstractSubActionEffect {

    /**
     * Creates an effect that causes the player performing the action to place the just drawn destiny card out of play.
     * @param action the action performing this effect
     */
    public PlaceDestinyCardOutOfPlayEffect(Action action) {
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
            if (destinyCard != null
                    && destinyCard.getZone().isUnresolvedDestinyDraw()) {

                game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " causes just drawn destiny card, " + GameUtils.getCardLink(destinyCard) + ", to be placed out of play");
                subAction.appendEffect(
                        new PlaceCardOutOfPlayFromOffTableEffect(subAction, destinyCard));
            }
        }

        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
