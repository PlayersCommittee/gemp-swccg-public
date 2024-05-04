package com.gempukku.swccgo.game.state.actions;


import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

/**
 * This class contains the state information for a game text action.
 */
public class GameTextActionState implements Snapshotable<GameTextActionState> {
    private int _id;
    private GameTextAction _gameTextAction;

    /**
     * Needed to generate snapshot.
     */
    public GameTextActionState() {
    }

    @Override
    public void generateSnapshot(GameTextActionState selfSnapshot, SnapshotData snapshotData) {
        GameTextActionState snapshot = selfSnapshot;

        // Set each field
        snapshot._id = _id;
        snapshot._gameTextAction = snapshotData.getDataForSnapshot(_gameTextAction);
    }

    /**
     * Creates state information for a game text action.
     * @param id the unique id
     * @param gameTextAction the game text action
     */
    public GameTextActionState(int id, GameTextAction gameTextAction) {
        _id = id;
        _gameTextAction = gameTextAction;
    }

    /**
     * Gets the unique id.
     * @return the unique id
     */
    public Integer getId() {
        return _id;
    }

    /**
     * Gets the game text action.
     * @return the game text action
     */
    public GameTextAction getGameTextAction() {
        return _gameTextAction;
    }
}
