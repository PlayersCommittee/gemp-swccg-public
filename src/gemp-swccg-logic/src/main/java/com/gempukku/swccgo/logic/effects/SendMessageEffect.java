package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that sends a message.
 */
public class SendMessageEffect extends AbstractSuccessfulEffect {
    private String _message;

    /**
     * Creates an effect that sends a message.
     * @param action the action performing this effect
     * @param message the message
     */
    public SendMessageEffect(Action action, String message) {
        super(action);
        _message = message;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        game.getGameState().sendMessage(_message);
    }
}
