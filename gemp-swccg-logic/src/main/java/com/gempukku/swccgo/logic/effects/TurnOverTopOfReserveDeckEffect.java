package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that turns over the top card of the Reserve Deck of a player
 */
public class TurnOverTopOfReserveDeckEffect extends AbstractSuccessfulEffect {
    private boolean _faceUp;
    private String _playerId;

    /**
     * Creates an effect that turns over the top card of the Reserve Deck of a player
     * @param action the action performing this effect.
     * @param faceUp true if Used Piles are turned face up, false if Used Piles are turned face down
     */
    public TurnOverTopOfReserveDeckEffect(Action action, String playerId, boolean faceUp) {
        super(action);
        _playerId = playerId;
        _faceUp = faceUp;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (gameState.isTopCardOfReserveDeckRevealed(_playerId)!=_faceUp) {
            gameState.sendMessage("The top card of " + _playerId + "'s Reserve Deck is turned " + (_faceUp ? "face up" : "face down"));
            gameState.turnOverTopCardOfReserveDeck(_playerId, _faceUp);
        }
    }
}
