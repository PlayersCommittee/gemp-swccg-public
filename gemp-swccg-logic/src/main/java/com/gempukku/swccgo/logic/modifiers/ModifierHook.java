package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.logic.timing.Snapshotable;

public interface ModifierHook extends Snapshotable<ModifierHook> {
    void stop();
}
