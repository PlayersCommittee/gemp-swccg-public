package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * Represents a location group for clouds (except Bespin: Cloud City).
 */
public class CloudsLocationGroup extends LocationGroup {
    private String _systemName;

    /**
     * Needed to generate snapshot.
     */
    public CloudsLocationGroup() {
    }

    @Override
    public void generateSnapshot(LocationGroup selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        CloudsLocationGroup snapshot = (CloudsLocationGroup) selfSnapshot;

        // Set each field
        snapshot._systemName = _systemName;
    }

    /**
     * Creates a location group for clouds (except Bespin: Cloud City) that are part of the specified system.
     */
    public CloudsLocationGroup(String systemName) {
        super("Clouds", Filters.and(Filters.cloud_sector, Filters.partOfSystem(systemName), Filters.not(Filters.Bespin_Cloud_City)));
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
                Filters.and(Filters.cloud_sector, Filters.partOfSystem(_systemName))));
    }
}
