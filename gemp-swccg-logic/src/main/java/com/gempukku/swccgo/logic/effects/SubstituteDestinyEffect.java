package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that substitutes a destiny draw with a specified value.
 */
public class SubstituteDestinyEffect extends AbstractSuccessfulEffect {
    private float _substituteValue;

    /**
     * Creates an effect that substitutes a destiny draw with a specified value.
     * @param action the action performing this effect.
     * @param substituteValue the substitute value
     */
    public SubstituteDestinyEffect(Action action, float substituteValue) {
        super(action);
        _substituteValue = substituteValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.getDrawnDestinyCard() != null) {
                gameState.sendMessage(_action.getPerformingPlayer() + " cancels " + drawDestinyEffect.getPlayerDrawingDestiny() + "'s "
                        + drawDestinyEffect.getDestinyType().getHumanReadable() + " draw of " + GameUtils.getCardLink(drawDestinyEffect.getDrawnDestinyCard())
                        + " and substitutes " + GuiUtils.formatAsString(_substituteValue) + " as value for " + drawDestinyEffect.getDestinyType().getHumanReadable());
            }
            else {
                gameState.sendMessage(_action.getPerformingPlayer() + " substitutes " + GuiUtils.formatAsString(_substituteValue)
                        + " as value for " + drawDestinyEffect.getDestinyType().getHumanReadable());
            }
            drawDestinyEffect.setSubstituteDestiny(_substituteValue);
        }
    }
}
