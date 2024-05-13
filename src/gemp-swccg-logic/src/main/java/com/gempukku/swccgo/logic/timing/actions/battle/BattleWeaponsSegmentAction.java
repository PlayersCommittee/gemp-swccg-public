package com.gempukku.swccgo.logic.timing.actions.battle;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.PlayOrder;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * An action that carries out the weapon segment of a battle.
 */
public class BattleWeaponsSegmentAction extends SystemQueueAction {
    private PlayOrder _playOrder;
    private int _consecutivePasses;
    private boolean _firstAction = true;

    /**
     * Needed to generate snapshot.
     */
    public BattleWeaponsSegmentAction() {
    }

    @Override
    public void generateSnapshot(Action selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        BattleWeaponsSegmentAction snapshot = (BattleWeaponsSegmentAction) selfSnapshot;

        snapshot._playOrder = snapshotData.getDataForSnapshot(_playOrder);
        snapshot._consecutivePasses = _consecutivePasses;
        snapshot._firstAction = _firstAction;
    }

    /**
     * Creates an action that carries out the weapon segment of a battle.
     * @param game the game
     */
    public BattleWeaponsSegmentAction(SwccgGame game) {
        _playOrder = game.getGameState().getPlayerOrder().getPlayOrder(game.getModifiersQuerying().getPlayerToTakeFirstBattleWeaponsSegmentAction(game.getGameState()), true);
        appendEffect(
                new BattleCheckIfWeaponsSegmentFinishedEffect(this));
    }

    /**
     * Sets the play order.
     * @param playOrder the play order
     */
    public void setPlayOrder(PlayOrder playOrder) {
        _playOrder = playOrder;
    }

    /**
     * Gets the play order.
     * @return the play order
     */
    public PlayOrder getPlayOrder() {
        return _playOrder;
    }

    /**
     * Sets the number of consecutive passes.
     * @param consecutivePasses the number of consecutive passes
     */
    public void setConsecutivePasses(int consecutivePasses) {
        _consecutivePasses = consecutivePasses;
    }

    /**
     * Gets the number of consecutive passes.
     * @return the number of consecutive passes
     */
    public int getConsecutivePasses() {
        return _consecutivePasses;
    }

    /**
     * Sets whether it is the first action of weapons segment.
     * @param firstAction true or false
     */
    public void setFirstAction(boolean firstAction) {
        _firstAction = firstAction;
    }

    /**
     * Determines if first action of weapons segment.
     * @return true or false
     */
    public boolean isFirstAction() {
        return _firstAction;
    }
}
