package com.gempukku.swccgo.logic.timing;


/**
 * An interface that defines the methods that all snapshotable classes must implement.
 */
public interface Snapshotable<T extends Snapshotable> {

    /**
     * Generates a snapshot of the snapshotable.
     * @param selfSnapshot the snapshot object
     * @param snapshotData snapshot data
     */
    void generateSnapshot(T selfSnapshot, SnapshotData snapshotData);
}
