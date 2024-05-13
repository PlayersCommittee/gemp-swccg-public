package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the valid fixed ordering of a collection of location groups.
 */
public class LocationFixedGroupOrder implements LocationGroupOrder {
    private List<LocationGroup> _groups = new ArrayList<LocationGroup>();

    /**
     * Needed to generate snapshot.
     */
    public LocationFixedGroupOrder() {
    }

    @Override
    public void generateSnapshot(LocationGroupOrder selfSnapshot, SnapshotData snapshotData) {
        LocationFixedGroupOrder snapshot = (LocationFixedGroupOrder) selfSnapshot;

        // Set each field
        for (LocationGroup locationGroup : _groups) {
            snapshot._groups.add(snapshotData.getDataForSnapshot(locationGroup));
        }
    }

    /**
     * Creates a fixed location group ordering of the specified location groups.
     * @param locationGroups the location groups
     */
    public LocationFixedGroupOrder(List<LocationGroup> locationGroups) {
        _groups = locationGroups;
    }

    @Override
    public List<LocationGroup> getDefaultArrangement() {
        return _groups;
    }

    @Override
    public Collection<List<LocationGroup>> getValidArrangements(boolean forCheckingOnly) {
        return Collections.singleton(_groups);
    }
}
