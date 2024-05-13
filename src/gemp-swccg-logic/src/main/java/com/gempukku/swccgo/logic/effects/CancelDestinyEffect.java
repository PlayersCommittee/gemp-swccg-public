package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the just drawn destiny.
 */
public class CancelDestinyEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that cancels the just drawn destiny.
     * @param action the action performing this effect
     */
    public CancelDestinyEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()
                    && drawDestinyEffect.getDrawnDestinyCard() != null
                    && !drawDestinyEffect.mayNotBeCanceledByPlayer(_action.getPerformingPlayer())) {

                gameState.sendMessage(_action.getPerformingPlayer() + " cancels " + drawDestinyEffect.getPlayerDrawingDestiny() + "'s "
                        + drawDestinyEffect.getDestinyType().getHumanReadable() + " draw of " + GameUtils.getCardLink(drawDestinyEffect.getDrawnDestinyCard()));
                drawDestinyEffect.cancelDestiny(false);
            }
        }
    }
}
