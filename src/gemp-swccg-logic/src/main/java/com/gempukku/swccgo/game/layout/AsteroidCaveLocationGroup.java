package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;

/**
 * Represents a location group for a Big One: Asteroid Cave.
 */
public class AsteroidCaveLocationGroup extends LocationGroup {
    private String _systemName;

    /**
     * Needed to generate snapshot.
     */
    public AsteroidCaveLocationGroup() {
    }

    @Override
    public void generateSnapshot(LocationGroup selfSnapshot, SnapshotData snapshotData) {
        super.generateSnapshot(selfSnapshot, snapshotData);
        AsteroidCaveLocationGroup snapshot = (AsteroidCaveLocationGroup) selfSnapshot;

        // Set each field
        snapshot._systemName = _systemName;
    }

    /**
     * Creates a location group for an Asteroid Cave on a Big One orbiting the specified system.
     */
    public AsteroidCaveLocationGroup(String systemName) {
        super("Big One: Asteroid Cave", Filters.and(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, Filters.isOrbiting(systemName)));
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
        return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.Big_One, Filters.isOrbiting(_systemName)));
    }
}
