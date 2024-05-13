package com.gempukku.swccgo.game.layout;


import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.List;

/**
 * This class contains the layout information for a specific location layout.
 */
public interface LocationLayout extends Snapshotable<LocationLayout> {

    /**
     * Gets the top locations in order (left to right).
     * @return the top locations
     */
    List<PhysicalCard> getTopLocationsInOrder();

    /**
     * Gets the name of the parent system for this layout.
     * @return the system name, or null
     */
    String getParentSystemTitle();

    /**
     * Gets the persona of the parent starship or vehicle for this layout.
     * @return the starship or vehicle persona, or null
     */
    Persona getParentStarshipOrVehiclePersona();

    /**
     * Gets the parent starship or vehicle card for this layout.
     * @return the starship or vehicle card, or null
     */
    PhysicalCard getParentStarshipOrVehicleCard();

    /**
     * Gets the relative order of this layout in the location layout environment (lower numbers are left of higher numbers).
     * System layouts will use their parsec number, and other groups will use a negative number so they are left of the systems.
     * @return the order index
     */
    int getOrderIndex();

    /**
     * Gets the places the specified card can deploy in this location layout.
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     * @return the places the specified card can deploy in this location layout
     */
    List<LocationPlacement> getPlacesToDeployLocation(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, Filter specialLocationConditions, boolean forCheckingOnly);

    /**
     * Deploys the location to the location layout.
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param locationPlacement the location placement
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     */
    void deployToLayout(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, LocationPlacement locationPlacement, boolean forCheckingOnly);

    /**
     * Remove location from layout.
     * @param location the location
     * @param forCheckingOnly true if only removing temporarily placed location on table to check conditions, otherwise false
     * @return true if location found and removed, otherwise false
     */
    boolean removeLocation(PhysicalCard location, boolean forCheckingOnly);

    /**
     * Gets the converted locations (that have another location on top) in order (left to right).
     * @return the converted locations
     */
     List<List<PhysicalCard>> getConvertedLocationsInOrder();

    /**
     * Gets all the locations (top and converted) in order (left to right). Within each sub-list, the top location
     * is first.
     * @return the locations
     */
     List<List<PhysicalCard>> getLocationsInOrder();
}
