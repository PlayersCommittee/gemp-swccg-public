package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that 'shuts down' the Death Star's Power.
 */
public class ShutDownDeathStarPowerEffect extends AbstractSuccessfulEffect {

    /**
     * An effect that 'shuts down' the Death Star's Power
     *
     * @param action the action performing this effect
     */
    public ShutDownDeathStarPowerEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        gameState.sendMessage("Death Star's Power is 'shut down'");
        game.getModifiersQuerying().deathStarPowerIsShutDown();
    }
}
