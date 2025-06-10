package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Represents a location group for Death Star: Trench.
 */
public class TrenchLocationGroup extends LocationGroup {

    /**
     * Needed to generate snapshot.
     */
    public TrenchLocationGroup() {
        super("Trench", Filters.Death_Star_Trench);
    }

    /**
     * Determines if this group is enabled for cards to deploy to it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    @Override
    public boolean isGroupEnabled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.Death_Star_system);
    }
}
