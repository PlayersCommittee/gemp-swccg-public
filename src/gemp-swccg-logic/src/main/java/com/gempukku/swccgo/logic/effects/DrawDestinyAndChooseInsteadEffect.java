package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the current destiny draw to instead draw X and choose Y.
 */
public class DrawDestinyAndChooseInsteadEffect extends AbstractSuccessfulEffect {
    private int _drawX;
    private int _chooseY;
    private boolean _mayTakeOtherIntoHandOrReturnToTopOfReserve;

    /**
     * Creates an effect that causes the current destiny draw to instead draw X and choose Y.
     * @param action the action performing this effect
     * @param drawX number of destiny to draw
     * @param chooseY number of destiny to choose
     */
    public DrawDestinyAndChooseInsteadEffect(Action action, int drawX, int chooseY) {
        this(action, drawX, chooseY, false);
    }

    /**
     * Creates an effect that causes the current destiny draw to instead draw X and choose Y.
     * @param action the action performing this effect
     * @param drawX number of destiny to draw
     * @param chooseY number of destiny to choose
     * @param mayTakeOtherIntoHandOrReturnToTopOfReserve if true, each unchosen destiny may be taken into hand or
     *                                                   returned to the top of Reserve Deck
     */
    public DrawDestinyAndChooseInsteadEffect(Action action, int drawX, int chooseY, boolean mayTakeOtherIntoHandOrReturnToTopOfReserve) {
        super(action);
        _drawX = drawX;
        _chooseY = chooseY;
        _mayTakeOtherIntoHandOrReturnToTopOfReserve = mayTakeOtherIntoHandOrReturnToTopOfReserve;
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
                if (_mayTakeOtherIntoHandOrReturnToTopOfReserve) {
                    drawDestinyEffect.setMayTakeOtherIntoHandOrReturnToTopOfReserve(true);
                }
            }
        }
    }
}
