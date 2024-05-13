package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.Collection;
import java.util.List;

/**
 * Represents the valid ordering of a collection of location groups.
 */
public interface LocationGroupOrder extends Snapshotable<LocationGroupOrder> {

    /**
     * Gets the default left-to-right arrangement of groups within this group order.
     * @return the default group order
     */
    List<LocationGroup> getDefaultArrangement();

    /**
     * Gets the valid left-to-right arrangement of groups within this group order.
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     * @return the valid group orders
     */
    Collection<List<LocationGroup>> getValidArrangements(boolean forCheckingOnly);
}
