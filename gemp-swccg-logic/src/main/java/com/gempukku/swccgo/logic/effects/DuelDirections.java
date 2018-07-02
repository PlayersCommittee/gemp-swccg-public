package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to provide the base implementation of a duel directions. Cards that provide the duel directions
 * will provide a concrete implementation that extends this abstract class.
 */
public interface DuelDirections {

    /**
     * Determines if this is an epic duel.
     * @return true or false
     */
    boolean isEpicDuel();

    /**
     * Determines if this duel is an attempt to cross over the light side character to the dark side.
     * @return true or false
     */
    boolean isCrossOverToDarkSideAttempt();

    /**
     * Gets the evaluator for the base of the duel total (not including duel destinies).
     * @param playerId the player
     * @param duelState the duel state information
     * @return the evaluator
     */
    Evaluator getBaseDuelTotal(String playerId, DuelState duelState);

    /**
     * Gets the default number of duel destiny draws.
     * @param playerId the player
     * @param duelState the duel state information
     * @return the default number of duel destiny draws
     */
    int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState);

    /**
     * Performs the duel directions.
     * @param duelAction the action performing the duel
     * @param game the game
     * @param duelState the duel state information
     */
    void performDuelDirections(Action duelAction, SwccgGame game, DuelState duelState);

    /**
     * Performs the duel results.
     * @param duelAction the action performing the duel
     * @param game the game
     * @param duelState the duel state information
     */
    void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState);
}
