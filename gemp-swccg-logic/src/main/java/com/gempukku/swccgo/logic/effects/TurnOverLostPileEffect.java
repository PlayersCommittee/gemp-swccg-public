package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that turns over the specified player's Lost Pile.
 */
public class TurnOverLostPileEffect extends AbstractSuccessfulEffect {
    private String _zoneOwner;
    private boolean _faceDown;

    /**
     * Creates an effect that turns over the specified player's Lost Pile.
     * @param action the action performing this effect
     * @param zoneOwner the Lost Pile owner
     * @param faceDown true if turned face down, otherwise turned face up
     */
    public TurnOverLostPileEffect(Action action, String zoneOwner, boolean faceDown) {
        super(action);
        _zoneOwner = zoneOwner;
        _faceDown = faceDown;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (gameState.isLostPileTurnedOver(_zoneOwner) != _faceDown) {
            gameState.sendMessage(_zoneOwner + "'s Lost Pile is turned " + (_faceDown ? "face down" : "face up"));
            gameState.turnOverLostPile(_zoneOwner, _faceDown);
        }
    }
}
