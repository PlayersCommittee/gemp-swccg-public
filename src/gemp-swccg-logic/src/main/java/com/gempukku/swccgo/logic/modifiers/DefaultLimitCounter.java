package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.logic.timing.SnapshotData;

public class DefaultLimitCounter implements LimitCounter {
    private int _count;

    /**
     * Needed to generate snapshot.
     */
    public DefaultLimitCounter() {
    }

    @Override
    public void generateSnapshot(LimitCounter selfSnapshot, SnapshotData snapshotData) {
        DefaultLimitCounter snapshot = (DefaultLimitCounter) selfSnapshot;

        // Set each field
        snapshot._count = _count;
    }

    @Override
    public int incrementToLimit(int limit, int incrementBy) {
        int maxIncrement = limit - _count;
        int finalIncrement = Math.min(maxIncrement, incrementBy);
        _count += finalIncrement;
        return finalIncrement;
    }

    @Override
    public int getUsedLimit() {
        return _count;
    }
}
