package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to provide the base implementation of lightsaber combat directions. Cards that provide the lightsaber
 * combat directions will provide a concrete implementation that extends this abstract class.
 */
public interface LightsaberCombatDirections {

    /**
     * Gets the evaluator for the base of the lightsaber combat total (not including lightsaber combat destinies).
     * @param playerId the player
     * @param lightsaberCombatState the lightsaber combat state information
     * @return the evaluator
     */
    Evaluator getBaseLightsaberCombatTotal(String playerId, LightsaberCombatState lightsaberCombatState);

    /**
     * Gets the default number of lightsaber combat destiny draws.
     * @param playerId the player
     * @param lightsaberCombatState the lightsaber combat state information
     * @return the default number of lightsaber combat destiny draws
     */
    int getBaseNumLightsaberCombatDestinyDraws(String playerId, LightsaberCombatState lightsaberCombatState);

    /**
     * Performs the lightsaber combat directions.
     * @param lightsaberCombatAction the action performing the lightsaber combat
     * @param game the game
     * @param lightsaberCombatState the lightsaber combat state information
     */
    void performLightsaberCombatDirections(Action lightsaberCombatAction, SwccgGame game, LightsaberCombatState lightsaberCombatState);

    /**
     * Performs the lightsaber combat results.
     * @param lightsaberCombatAction the action performing lightsaber combat
     * @param game the game
     * @param lightsaberCombatState the lightsaber combat state information
     */
    void performLightsaberCombatResults(Action lightsaberCombatAction, SwccgGame game, LightsaberCombatState lightsaberCombatState);
}
