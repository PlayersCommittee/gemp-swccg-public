package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.logic.timing.Snapshotable;

public interface LimitCounter extends Snapshotable<LimitCounter> {
    int incrementToLimit(int limit, int incrementBy);

    int getUsedLimit();
}
