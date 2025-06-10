package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * Represents a location group for a Big One.
 */
public class BigOneLocationGroup extends LocationGroup {
    private String _systemName;

    /**
     * Needed to generate snapshot.
     */
    public BigOneLocationGroup() {
    }

    @Override
    public void generateSnapshot(LocationGroup selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        BigOneLocationGroup snapshot = (BigOneLocationGroup) selfSnapshot;

        // Set each field
        snapshot._systemName = _systemName;
    }

    /**
     * Creates a location group for a Big One orbiting the specified system.
     */
    public BigOneLocationGroup(String systemName) {
        super("Big One", Filters.and(Filters.Big_One, Filters.isOrbiting(systemName)));
        _systemName = systemName;
    }

    /**
     * Determines if this group is enabled for cards to deploy to it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    @Override
    public boolean isGroupEnabled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.or(Filters.and(Filters.planet_system, Filters.title(_systemName)),
                Filters.and(Filters.asteroid_sector, Filters.isOrbiting(_systemName))));
    }
}
