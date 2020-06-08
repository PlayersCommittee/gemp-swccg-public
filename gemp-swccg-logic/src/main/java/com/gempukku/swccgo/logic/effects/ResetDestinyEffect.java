package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that resets the just drawn destiny.
 */
public class ResetDestinyEffect extends AbstractSuccessfulEffect {
    private float _resetValue;

    /**
     * Creates an effect that resets the just drawn destiny.
     * @param action the action performing this effect.
     * @param resetValue the reset value
     */
    public ResetDestinyEffect(Action action, float resetValue) {
        super(action);
        _resetValue = resetValue;
    }

    @Override
    public String getText(SwccgGame game) {
        DrawDestinyState drawDestinyState = game.getGameState().getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            return "Reset " + drawDestinyEffect.getDestinyType().getHumanReadable() + " to " + GuiUtils.formatAsString(_resetValue);
        }
        return null;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String playerId = _action.getPerformingPlayer();

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()
                    && drawDestinyEffect.getDrawnDestinyCard() != null) {

                if (modifiersQuerying.mayNotResetDestinyDraw(game.getGameState(), playerId)) {
                    gameState.sendMessage(playerId + " may not reset the " + drawDestinyEffect.getDestinyType().getHumanReadable());
                    return;
                }

                drawDestinyEffect.resetDestiny(_resetValue);
                if (playerId != null)
                    gameState.sendMessage(playerId + " resets " + drawDestinyEffect.getDestinyType().getHumanReadable() + " is reset to " + GuiUtils.formatAsString(_resetValue));
                else
                    gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " resets " + drawDestinyEffect.getDestinyType().getHumanReadable() + " to " + GuiUtils.formatAsString(_resetValue));
            }
        }
    }
}
