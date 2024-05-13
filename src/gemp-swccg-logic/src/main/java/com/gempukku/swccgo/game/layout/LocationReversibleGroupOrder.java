package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.*;

/**
 * Represents the valid reversible ordering of a collection of location groups.
 */
public class LocationReversibleGroupOrder implements LocationGroupOrder {
    private List<LocationGroup> _groupsInForwardOrder = new ArrayList<LocationGroup>();
    private List<LocationGroup> _groupsInReverseOrder = new ArrayList<LocationGroup>();
    private List<List<LocationGroup>> _finalOrder;

    /**
     * Needed to generate snapshot.
     */
    public LocationReversibleGroupOrder() {
    }

    @Override
    public void generateSnapshot(LocationGroupOrder selfSnapshot, SnapshotData snapshotData) {
        LocationReversibleGroupOrder snapshot = (LocationReversibleGroupOrder) selfSnapshot;

        // Set each field
        for (LocationGroup locationGroup : _groupsInForwardOrder) {
            snapshot._groupsInForwardOrder.add(snapshotData.getDataForSnapshot(locationGroup));
        }
        for (LocationGroup locationGroup : _groupsInReverseOrder) {
            snapshot._groupsInReverseOrder.add(snapshotData.getDataForSnapshot(locationGroup));
        }
        if (_finalOrder != null) {
            snapshot._finalOrder = new ArrayList<List<LocationGroup>>();
            for (List<LocationGroup> locationGroupList : _finalOrder) {
                List<LocationGroup> snapShotList = new ArrayList<LocationGroup>();
                snapshot._finalOrder.add(snapShotList);
                for (LocationGroup locationGroup : locationGroupList) {
                    snapShotList.add(snapshotData.getDataForSnapshot(locationGroup));
                }
            }
        }
    }

    /**
     * Creates a reversible location group ordering of the specified location groups.
     * @param locationGroups the location groups
     */
    public LocationReversibleGroupOrder(LocationGroup... locationGroups) {
        _groupsInForwardOrder = Arrays.asList(locationGroups);
        _groupsInReverseOrder = new ArrayList<LocationGroup>(_groupsInForwardOrder);
        Collections.reverse(_groupsInReverseOrder);
        if (locationGroups.length < 2)
            _finalOrder = Collections.singletonList(_groupsInForwardOrder);
    }

    @Override
    public List<LocationGroup> getDefaultArrangement() {
        return _groupsInForwardOrder;
    }

    @Override
    public Collection<List<LocationGroup>> getValidArrangements(boolean forCheckingOnly) {
        // If the final ordering has been set, then just return that order
        if (_finalOrder != null)
            return _finalOrder;

        // Needs to figure out which orders are valid
        Integer leftMostGroupIndex = null;
        Integer rightMostGroupIndex = null;
        for (int i = 0; i < _groupsInForwardOrder.size(); ++i) {
            Integer locationZoneIndex = _groupsInForwardOrder.get(i).getLocationZoneIndex();
            if (locationZoneIndex != null) {
                if (leftMostGroupIndex == null)
                    leftMostGroupIndex = locationZoneIndex;
                rightMostGroupIndex = locationZoneIndex;
            }
        }

        // If no groups are represented, then just return the forward order since there is no difference between the two
        // orders at that point.
        if (leftMostGroupIndex == null) {
            return Collections.singleton(_groupsInForwardOrder);
        }

        // If only for checking, or if exactly one group is represented, then return both orders, since either is valid.
        if (forCheckingOnly || leftMostGroupIndex.equals(rightMostGroupIndex)) {
            Set<List<LocationGroup>> orders = new HashSet<List<LocationGroup>>();
            orders.add(_groupsInForwardOrder);
            orders.add(_groupsInReverseOrder);
            return orders;
        }

        // Check which order is valid with the location layout on the table, set that as the final ordering and return it.
        if (leftMostGroupIndex < rightMostGroupIndex)
            _finalOrder = Collections.singletonList(_groupsInForwardOrder);
        else
            _finalOrder = Collections.singletonList(_groupsInReverseOrder);

        return _finalOrder;
    }
}
