package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that declares if the Senate is in session.
 */
public class DeclareSenateInSessionEffect extends AbstractSuccessfulEffect {

    /**
     * An effect that declares if the Senate is in session.
     *
     * @param action the action performing this effect
     */
    public DeclareSenateInSessionEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        gameState.sendMessage("The Senate is now in session");
        game.getModifiersQuerying().declareSenateIsInSession();
    }
}
