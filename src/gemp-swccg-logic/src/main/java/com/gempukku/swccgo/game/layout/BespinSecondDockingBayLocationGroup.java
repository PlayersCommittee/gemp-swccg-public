package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

/**
 * Represents a location group for the 2nd docking bay deployed to Bespin.
 */
public class BespinSecondDockingBayLocationGroup extends LocationGroup {

    /**
     * Needed to generate snapshot.
     */
    public BespinSecondDockingBayLocationGroup() {
        super("2nd docking bay", Filters.and(Filters.docking_bay, Filters.partOfSystem(Title.Bespin)));
    }

    /**
     * Determines if this group is enabled for cards to deploy to it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    @Override
    public boolean isGroupEnabled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Filters.canSpotFromTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.Bespin_location, Filters.docking_bay));
    }
}
