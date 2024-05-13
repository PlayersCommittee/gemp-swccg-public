package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the just drawn destiny.
 */
public class DrawDestinyAndChooseInsteadEffect extends AbstractSuccessfulEffect {
    private int _drawX;
    private int _chooseY;

    /**
     * Creates an effect that cancels the just drawn destiny.
     * @param action the action performing this effect
     */
    public DrawDestinyAndChooseInsteadEffect(Action action, int drawX, int chooseY) {
        super(action);
        _drawX = drawX;
        _chooseY = chooseY;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.canDrawAndChoose(game, _drawX)) {

                gameState.sendMessage(drawDestinyEffect.getPlayerDrawingDestiny() + " will draw " + _drawX + " "
                        + " " + drawDestinyEffect.getDestinyType().getHumanReadable() + " and choose " + _chooseY);
                drawDestinyEffect.setDrawXAndChooseY(_drawX, _chooseY);
            }
        }
    }
}
