package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that turns over Used Piles.
 */
public class TurnOverUsedPilesEffect extends AbstractSuccessfulEffect {
    private boolean _faceUp;

    /**
     * Creates an effect that turns over Used Piles.
     * @param action the action performing this effect.
     * @param faceUp true if Used Piles are turned face up, false if Used Piles are turned face down
     */
    public TurnOverUsedPilesEffect(Action action, boolean faceUp) {
        super(action);
        _faceUp = faceUp;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (gameState.isUsedPilesTurnedOver()!=_faceUp) {
            gameState.sendMessage("Used Piles are turned " + (_faceUp ? "face up" : "face down"));
            gameState.turnOverUsedPiles(_faceUp);
        }
    }
}
