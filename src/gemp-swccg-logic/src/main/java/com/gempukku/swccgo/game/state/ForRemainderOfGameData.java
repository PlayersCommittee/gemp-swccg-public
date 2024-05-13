package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.ArrayList;
import java.util.List;

/**
 * Data that is stored in for remainder of game data.
 */
public class ForRemainderOfGameData implements Snapshotable<ForRemainderOfGameData> {
    private boolean _booleanValue;
    private List<PhysicalCard> _physicalCards = new ArrayList<PhysicalCard>();

    @Override
    public void generateSnapshot(ForRemainderOfGameData selfSnapshot, SnapshotData snapshotData) {
        ForRemainderOfGameData snapshot = selfSnapshot;

        // Set each field
        snapshot._booleanValue = _booleanValue;
        for (PhysicalCard card : _physicalCards) {
            snapshot._physicalCards.add(snapshotData.getDataForSnapshot(card));
        }
    }

    /**
     * Creates data that is stored in for remainder of game data.
     */
    public ForRemainderOfGameData() {
    }

    /**
     * Creates data that is stored in for remainder of game data.
     * @param booleanValue the boolean value of the data
     */
    public ForRemainderOfGameData(boolean booleanValue) {
        _booleanValue = booleanValue;
    }

    /**
     * Creates data that is stored in while in play data.
     * @param physicalCards a physical card list
     */
    public ForRemainderOfGameData(List<PhysicalCard> physicalCards) {
        _physicalCards.addAll(physicalCards);
    }

    /**
     * Gets the boolean value of the data.
     * @return true or false
     */
    public boolean getBooleanValue() {
        return _booleanValue;
    }

    /**
     * Gets the physical cards data.
     * @return the physical cards data
     */
    public List<PhysicalCard> getPhysicalCards() {
        return _physicalCards;
    }
}
