package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractHolositeLayout extends AbstractLocationLayout {

    /**
     * Creates a layout for the holosites.
     */
    protected AbstractHolositeLayout() {
        _orderIndex = -3; // To make holosites show up left of the starship/vehicle sites
    }

    /**
     * Gets the places the specified card can deploy in this location layout.
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     * @return the places the specified card can deploy in this location layout
     */
    @Override
    public List<LocationPlacement> getPlacesToDeployLocation(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, Filter specialLocationConditions, boolean forCheckingOnly) {
        List<LocationPlacement> placesToDeploy = new LinkedList<LocationPlacement>();

        // Check if location can go in any of the location groups for this system
        for (LocationGroup group : getPossibleGroupOrders(forCheckingOnly).iterator().next()) {
            List<PhysicalCard> cardsInGroup = group.getTopCardsInGroup();

            boolean cardMatchesGroup = group.getFilters().accepts(game.getGameState(), modifiersQuerying, cardToDeploy) && group.isGroupEnabled(game.getGameState(), modifiersQuerying);
            if (cardMatchesGroup) {

                if (cardsInGroup.isEmpty()) {
                    placesToDeploy.add(new LocationPlacement(null, null, null, null, LocationPlacementDirection.LEFT_OF));
                }
                else {
                    PhysicalCard existingHolosite = cardsInGroup.get(0);
                    boolean isConversionValid = Filters.canBeConvertedByDeployment(cardToDeploy).accepts(game.getGameState(), modifiersQuerying, existingHolosite);
                    if (isConversionValid) {
                        placesToDeploy.add(new LocationPlacement(null, null, null, existingHolosite, LocationPlacementDirection.REPLACE));
                    }
                }
            }
        }

        // If there are special location conditions, then need to filter out any deployments that would cause the location
        // to not satisfy those conditions.
        if (specialLocationConditions != null) {
            placesToDeploy = checkSpecialConditions(game, cardToDeploy, specialLocationConditions, placesToDeploy);
        }

        // Example: One placement says "left of" and another says "right of", if both have the same "other card",
        // have a single placement that says "left or right of" that "other card".
        placesToDeploy = consolidatePlacements(cardToDeploy, placesToDeploy);

        return placesToDeploy;
    }
}
