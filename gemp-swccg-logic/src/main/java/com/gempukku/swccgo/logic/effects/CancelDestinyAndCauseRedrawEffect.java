package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that cancels the just drawn destiny and causes a redraw.
 */

public class CancelDestinyAndCauseRedrawEffect extends AbstractStandardEffect {
    private String _playerId;
    /**
     * Creates an effect that cancels the just drawn destiny and causes a redraw.
     * @param action the action performing this effect
     */
    public CancelDestinyAndCauseRedrawEffect(Action action) {
        super(action);
    }

    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()
                    && drawDestinyEffect.getDrawnDestinyCard() != null
                    && !drawDestinyEffect.mayNotBeCanceledByPlayer(_action.getPerformingPlayer())) {

                gameState.sendMessage(_action.getPerformingPlayer() + " cancels " + drawDestinyEffect.getPlayerDrawingDestiny() + "'s "
                        + drawDestinyEffect.getDestinyType().getHumanReadable() + " draw of " + GameUtils.getCardLink(drawDestinyEffect.getDrawnDestinyCard()) + " and causes a re-draw");
                drawDestinyEffect.cancelDestiny(true);
            }
        }
    }

    @Override
    public Action getAction() {
        return _action;
    }

    @Override
    public Type getType() {
        return Type.RESPONDABLE_EFFECT;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Cancel destiny and cause a redraw";
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        String opponent = game.getOpponent(_playerId);
        return game.getGameState().getHand(opponent).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {

        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()
                    && drawDestinyEffect.getDrawnDestinyCard() != null
                    && !drawDestinyEffect.mayNotBeCanceledByPlayer(_action.getPerformingPlayer())) {

                gameState.sendMessage(_action.getPerformingPlayer() + " cancels " + drawDestinyEffect.getPlayerDrawingDestiny() + "'s "
                        + drawDestinyEffect.getDestinyType().getHumanReadable() + " draw of " + GameUtils.getCardLink(drawDestinyEffect.getDrawnDestinyCard()) + " and causes a re-draw");
                drawDestinyEffect.cancelDestiny(true);
            }
        }

        return new FullEffectResult(true);

    }
}