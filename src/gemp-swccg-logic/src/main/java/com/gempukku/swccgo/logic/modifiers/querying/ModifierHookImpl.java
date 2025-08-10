package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierHook;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * This class provides a way for a card to keep track of all of its
 * "always on" modifiers, so when the card becomes not active, there
 * is a way to remove that modifiers so they stop affecting the game.
 */

public class ModifierHookImpl implements ModifierHook {
    private ModifiersLogic _modifiersLogic;
    private Modifier _modifier;

    /**
     * Needed to generate snapshot.
     */
    public ModifierHookImpl() {
    }

    @Override
    public void generateSnapshot(ModifierHook selfSnapshot, SnapshotData snapshotData) {
        ModifierHookImpl snapshot = (ModifierHookImpl) selfSnapshot;

        // Set each field
        snapshot._modifiersLogic = snapshotData.getDataForSnapshot(_modifiersLogic);
        snapshot._modifier = _modifier;
    }

    /**
     * Creates a modifier hook.
     * @param modifiersLogic the modifiers logic
     * @param modifier the modifier
     */
    public ModifierHookImpl(ModifiersLogic modifiersLogic, Modifier modifier) {
        _modifiersLogic = modifiersLogic;
        _modifier = modifier;
    }

    @Override
    public void stop() {
        _modifiersLogic.removeModifier(_modifier);
    }
}
