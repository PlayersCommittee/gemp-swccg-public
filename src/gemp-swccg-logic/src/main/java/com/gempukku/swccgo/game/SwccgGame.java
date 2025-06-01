package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.GameEndReason;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.communication.InGameStatisticsListener;
import com.gempukku.swccgo.communication.UserFeedback;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GameResultListener;
import com.gempukku.swccgo.logic.timing.GameSnapshot;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This interface represents a Gemp-Swccg game.
 *
 * The class that implements this interface is used as the "anchor" object that has references other object needed by
 * the game engine.
 */
public interface SwccgGame {
    /**
     * Flag that indicates whether the game is being ran in a unit test context.
     * Controls certain behaviors such as labeling what turn procedure events are
     * being responded to.
     */
    boolean isTestEnvironment();

    /**
     * Gets the game state.
     * @return the game state
     */
    GameState getGameState();

    /**
     * Gets the game stats.
     * @return the game stats
     */
    GameStats getGameStats();

    /**
     * Gets the modifiers environment.
     * @return the modifiers environment
     */
    ModifiersEnvironment getModifiersEnvironment();

    /**
     * Gets the modifiers querying.
     * @return the modifiers querying
     */
    ModifiersQuerying getModifiersQuerying();

    /**
     * Gets the actions environment.
     * @return the actions environment
     */
    ActionsEnvironment getActionsEnvironment();

    /**
     * Gets the user feedback.
     * @return the user feedback.
     */
    UserFeedback getUserFeedback();

    /**
     * Check if either player's Life Force is depleted. If so sets the winner and loser of the game.
     */
    void checkLifeForceDepleted();

    /**
     * Ends the game, by specifying the losing player and reason for losing.
     * @param playerId the player
     * @param reason the reason
     */
    void playerLost(String playerId, GameEndReason reason);

    /**
     * Gets the Dark side player.
     * @return the Dark side player
     */
    String getDarkPlayer();

    /**
     * Gets the Light side player.
     * @return the Light side player
     */
    String getLightPlayer();

    /**
     * Gets the player on the specified side of the Force.
     * @param side the side of the Force
     * @return the player
     */
    String getPlayer(Side side);

    /**
     * Gets the side of the Force of the specified player.
     * @param playerId the player
     * @return the side of the Force
     */
    Side getSide(String playerId);

    /**
     * Gets the opponent of the specified player.
     * @param playerId the player
     * @return the opponent
     */
    String getOpponent(String playerId);

    /**
     * Gets the winning player.
     * @return the winning player, or null if no player has won yet
     */
    String getWinner();

    /**
     * Gets a card by the permanent card ID.
     * @param permanentCardId the permanent card ID
     * @return the card
     */
    PhysicalCard findCardByPermanentId(Integer permanentCardId);

    /**
     * The format used for this game.
     * @return the format
     */
    SwccgFormat getFormat();

    /**
     * Determines if the specified player chose to auto-pass during the specified phase if that player has no valid actions.
     * @param playerId the player
     * @param phase the phase
     * @return true if player auto-passes, otherwise false
     */
    boolean shouldAutoPass(String playerId, Phase phase);

    /**
     * Sets the phases in which the game should auto-pass for the specified player if the player has no valid actions.
     * @param playerId the player
     * @param phases the phases
     */
    void setPlayerAutoPassSettings(String playerId, Set<Phase> phases);

    /**
     * Adds a game results listener.
     * @param listener the listener
     */
    void addGameResultListener(GameResultListener listener);

    /**
     * Removes a game results listener.
     * @param listener the listener
     */
    void removeGameResultListener(GameResultListener listener);

    /**
     * Starts the game.
     */
    void startGame();

    /**
     * Performs the process of the game until there is a winner, or until response from a player is needed.
     */
    void carryOutPendingActionsUntilDecisionNeeded();

    /**
     * Determines if the game is finished.
     * @return true if the game is finished, otherwise false
     */
    boolean isFinished();

    /**
     * Determines if the game is cancelled.
     * @return true if the game is cancelled, otherwise false
     */
    boolean isCancelled();

    /**
     * Cancel the game due to an error.
     */
    void abortGame();

    /**
     * Cancel the game due to not having both players perform an action.
     */
    void performAutoCancelGame();

    /**
     * Request the game to be cancelled.
     * @param playerId the player requesting the game to be cancelled.
     */
    void requestCancel(String playerId);

    /**
     * Request the game timer be extended a specified number of minutes.
     * @param playerId the player requesting the game timer be extended
     * @param minutes the number of minutes
     */
    void requestExtendGameTimer(String playerId, int minutes);

    /**
     * Gets the number of minutes the game timer is extended.
     * @return number of minutes, or 0 if not extended
     */
    int getGameTimerExtendedInMinutes();

    /**
     * Request the per-player-action timer to be disabled.
     * @param playerId the player requesting the per-player-action timer to be disabled.
     */
    void requestDisableActionTimer(String playerId);

    /**
     * Determines if the per-player-action timer is disabled.
     * @return true or false
     */
    boolean isActionTimerDisabled();

    /**
     * Adds a game state listener
     * @param playerId the player
     * @param gameStateListener the game state listener
     */
    void addGameStateListener(String playerId, GameStateListener gameStateListener);

    /**
     * Removes a game state listener
     * @param gameStateListener the game state listener
     */
    void removeGameStateListener(GameStateListener gameStateListener);

    /**
     * Gets all game state listeners
     */
    Collection<GameStateListener> getAllGameStateListeners();

    /**
     * A player has requested a revert, which will then go through the snapshot selection process and offer the
     * revert to their opponent, who may reject it.
     * @param playerId The player requesting a revert.
     * @param onAbort Callback to be executed if the revert is aborted for any reason (including no snapshots,
     *                player changed their mind, or opponent rejected).
     */
    void requestRevert(final String playerId, Runnable onAbort);

    /**
     * A player has requested a revert, which will then go through the snapshot selection process and offer the
     * revert to their opponent, who may reject it.
     * @param playerId The player requesting a revert
     * @param onInvalid If the revert is found to be invalid due to no snapshots existing, this will be called.
     * @param onRejected If the player changes their mind after seeing the snapshot list, or if their opponent
     *                   rejects the revert, this will be called.
     */
    void requestRevert(final String playerId, Runnable onInvalid, Runnable onRejected);


    /**
     * Gets the game snapshots.
     * @return the game snapshots
     */
    List<GameSnapshot> getSnapshots();

    /**
     * Requests a game snapshot to be restored as current state of the game.
     * @param snapshotId the snapshot ID
     */
    void requestRestoreSnapshot(int snapshotId);

    /**
     * Determines if a snapshot is pending to be restored.
     * @return true or false
     */
    boolean isRestoreSnapshotPending();

    /**
     * Creates a snapshot of the current state of the game.
     * @param description the description
     */
    void takeSnapshot(String description);

    String getDeckString(Side side);


    /**
     * Adds a game statistics listener
     * @param listener the game statistics listener
     */
    void addInGameStatisticsListener(InGameStatisticsListener listener);

    /**
     * Removes a game statistics listener
     * @param listener the game statistics listener
     */
    void removeInGameStatisticsListener(InGameStatisticsListener listener);

    /**
     * Gets all game statistics listeners
     */
    Collection<InGameStatisticsListener> getAllInGameStatisticsListeners();

    /**
     * Removes all of the game statistics listeners
     */

    void removeAllInGameStatisticsListeners();


    /**
     *  Gets the number of seconds elapsed from this player's clock
     * @param player
     * @return number of seconds elapsed from this player's clock
     */
    Integer getSecondsElapsed(String player);

    /**
     * Determines whether bonus abilities should be used during the game (for April Fools' day or other special occasions)
     * @return true if bonus abilities should be used during the game
     */
    boolean useBonusAbilities();
}
