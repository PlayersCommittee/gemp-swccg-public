package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that prevents the just drawn destiny from being canceled.
 */
public class PreventDestinyFromBeingCanceledEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that prevents the just drawn destiny from being canceled.
     * @param action the action performing this effect
     */
    public PreventDestinyFromBeingCanceledEffect(Action action) {
        super(action);
    }

    /**
     * Creates an effect that prevents the just drawn destiny from being canceled by the specified player.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PreventDestinyFromBeingCanceledEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (_playerId != null) {
                drawDestinyEffect.setPlayerMayNotCancelDestiny(_playerId);
            }
            else {
                drawDestinyEffect.setPlayerMayNotCancelDestiny(game.getDarkPlayer());
                drawDestinyEffect.setPlayerMayNotCancelDestiny(game.getLightPlayer());
            }
        }
    }
}
