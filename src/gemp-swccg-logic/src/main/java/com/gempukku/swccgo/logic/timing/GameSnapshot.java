package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersLogic;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Defines a snapshot of a game. Since the SwccgGame class is not a snapshotable,
 * this class is used as a starting point to snapshot of all the elements of the game.
 */
public class GameSnapshot implements Snapshotable<GameSnapshot> {
    private int _id;
    private String _description;
    private GameState _gameState;
    private ModifiersLogic _modifiersLogic;
    private ActionsEnvironment _actionsEnvironment;
    private TurnProcedure _turnProcedure;

    /**
     * Creates a game snapshot of the game.
     * @param id the snapshot ID
     * @param description the description
     * @param gameState the game state to snapshot
     * @param modifiersLogic the modifiers logic to snapshot
     * @param actionsEnvironment the actions environment to snapshot
     * @param turnProcedure the turn procedure to snapshot
     * @return the game snapshot
     */
    public static GameSnapshot createGameSnapshot(int id, String description, GameState gameState, ModifiersLogic modifiersLogic, ActionsEnvironment actionsEnvironment, TurnProcedure turnProcedure) {
        GameSnapshot gameSnapshot = new GameSnapshot(id, description, gameState, modifiersLogic, actionsEnvironment, turnProcedure);
        SnapshotData snapshotMetadata = new SnapshotData();
        return snapshotMetadata.getDataForSnapshot(gameSnapshot);
    }

    /**
     * Needed to generate snapshot.
     */
    public GameSnapshot() {
    }

    @Override
    public void generateSnapshot(GameSnapshot selfSnapshot, SnapshotData snapshotData) {
        GameSnapshot snapshot = selfSnapshot;

        // Set each field
        snapshot._id = _id;
        snapshot._description = _description;
        snapshot._gameState = snapshotData.getDataForSnapshot(_gameState);
        snapshot._modifiersLogic = snapshotData.getDataForSnapshot(_modifiersLogic);
        snapshot._actionsEnvironment = snapshotData.getDataForSnapshot(_actionsEnvironment);
        snapshot._turnProcedure = snapshotData.getDataForSnapshot(_turnProcedure);
    }

    /**
     * Constructs a game snapshot object that will be used to snapshot all the elements of the game.
     * @param id the snapshot ID
     * @param description the description
     * @param gameState the game state to snapshot
     * @param modifiersLogic the modifiers logic to snapshot
     * @param actionsEnvironment the actions environment to snapshot
     * @param turnProcedure the turn procedure to snapshot
     */
    private GameSnapshot(int id, String description, GameState gameState, ModifiersLogic modifiersLogic, ActionsEnvironment actionsEnvironment, TurnProcedure turnProcedure) {
        _id = id;
        _description = description;
        _gameState = gameState;
        _modifiersLogic = modifiersLogic;
        _actionsEnvironment = actionsEnvironment;
        _turnProcedure = turnProcedure;
    }

    /**
     * Gets the snapshot ID.
     * @return the snapshot ID
     */
    public int getId() {
        return _id;
    }

    /**
     * Gets the description.
     * @return the description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Gets the current player at time of snapshot.
     * @return the current player at time of snapshot
     */
    public String getCurrentPlayerId() {
        return _gameState.getCurrentPlayerId();
    }

    /**
     * Gets the turn number at time of snapshot.
     * @return the turn number at time of snapshot
     */
    public int getCurrentTurnNumber() {
        return _gameState.getPlayersLatestTurnNumber(getCurrentPlayerId());
    }

    /**
     * Gets the phase at time of snapshot.
     * @return the phase at time of snapshot
     */
    public Phase getCurrentPhase() {
        return _gameState.getCurrentPhase();
    }

    /**
     * Gets the game state.
     * @return the game state
     */
    public GameState getGameState() {
        return _gameState;
    }

    /**
     * Gets the modifiers logic.
     * @return the modifiers logic
     */
    public ModifiersLogic getModifiersLogic() {
        return _modifiersLogic;
    }

    /**
     * Gets the actions environment
     * @return the actions environement
     */
    public ActionsEnvironment getActionsEnvironment() {
        return _actionsEnvironment;
    }

    /**
     * Gets the turn procedure.
     * @return the turn procedure
     */
    public TurnProcedure getTurnProcedure() {
        return _turnProcedure;
    }
}
