package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LocationPlacementDirection;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.*;

/**
 * Represents a group of locations associated with a system, starship, vehicle, or holosite.
 * This class is extended by other classes to define additional rules for that layout.
 */
public abstract class AbstractLocationLayout implements LocationLayout {
    protected String _parentTitle;
    protected Persona _parentPersona;
    protected PhysicalCard _parentCard;
    protected int _orderIndex;
    protected List<LocationGroupOrder> _groupOrders = new ArrayList<LocationGroupOrder>();

    /**
     * Needed to generate snapshot.
     */
    public AbstractLocationLayout() {
    }

    @Override
    public void generateSnapshot(LocationLayout selfSnapshot, SnapshotData snapshotData) {
        AbstractLocationLayout snapshot = (AbstractLocationLayout) selfSnapshot;

        // Set each field
        snapshot._parentTitle = _parentTitle;
        snapshot._parentPersona = _parentPersona;
        snapshot._parentCard = snapshotData.getDataForSnapshot(_parentCard);
        snapshot._orderIndex = _orderIndex;
        for (LocationGroupOrder locationGroupOrder : _groupOrders) {
            snapshot._groupOrders.add(snapshotData.getDataForSnapshot(locationGroupOrder));
        }
    }

    /**
     * Gets the name of the parent system for this layout.
     * @return the system name, or null
     */
    @Override
    public final String getParentSystemTitle() {
        return _parentTitle;
    }

    /**
     * Gets the persona of the parent starship or vehicle for this layout.
     * @return the starship or vehicle persona, or null
     */
    @Override
    public final Persona getParentStarshipOrVehiclePersona() {
        return _parentPersona;
    }

    /**
     * Gets the parent starship or vehicle card for this layout.
     * @return the starship or vehicle card, or null
     */
    @Override
    public final PhysicalCard getParentStarshipOrVehicleCard() {
        return _parentCard;
    }

    /**
     * Gets the relative order of this layout in the location layout environment (lower numbers are left of higher numbers).
     * System layouts will use their parsec number, and other groups will use a negative number so they are left of the systems.
     * @return the order index
     */
    @Override
    public final int getOrderIndex() {
        return _orderIndex;
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
        GameState gameState = game.getGameState();

        // Get the system name, starship or vehicle persona, or starship or vehicle card to be the parent
        String parentNameOfCardToDeploy = cardToDeploy.getPartOfSystem() != null ? cardToDeploy.getPartOfSystem() : cardToDeploy.getSystemOrbited();
        Persona parentPersonaOfCardToDeploy = cardToDeploy.getBlueprint().getRelatedStarshipOrVehiclePersona();
        PhysicalCard parentStarshipOrVehicle = cardToDeploy.getRelatedStarshipOrVehicle();
        Uniqueness uniquenessOfCardToDeploy = modifiersQuerying.getUniqueness(gameState, cardToDeploy);

        // Determine the starship or vehicle persona to match if a specific starship or vehicle card is specified
        Set<Persona> parentPersonasToMatch = new HashSet<Persona>();
        if (parentPersonaOfCardToDeploy != null)
            parentPersonasToMatch.add(parentPersonaOfCardToDeploy);
        else if (parentStarshipOrVehicle != null)
            parentPersonasToMatch.addAll(parentStarshipOrVehicle.getBlueprint().getPersonas());

        boolean parentMatches = (_parentTitle != null && _parentTitle.equals(parentNameOfCardToDeploy))
                || (parentPersonasToMatch.contains(_parentPersona))
                || (_parentCard != null && _parentCard == parentStarshipOrVehicle);
        if (!parentMatches)
            return placesToDeploy;

        // Check that parent system or starship/vehicle is not 'blown away'
        if (cardToDeploy.getPartOfSystem() != null) {
            if (modifiersQuerying.isBlownAway(gameState, Filters.title(cardToDeploy.getPartOfSystem()))) {
                return placesToDeploy;
            }
        }
        else if (parentPersonaOfCardToDeploy != null) {
            if (modifiersQuerying.isBlownAway(gameState, Filters.persona(parentPersonaOfCardToDeploy))) {
                return placesToDeploy;
            }
        }

        // Check all the possible group orders
        for (List<LocationGroup> locationGroup : getPossibleGroupOrders(forCheckingOnly)) {

            PhysicalCard lastCardVisited = null;
            LocationPlacement needsLeftOfAnother = null;

            // Check if location can go in any of the location groups for this system
            for (LocationGroup group : locationGroup) {
                List<PhysicalCard> cardsInGroup = group.getTopCardsInGroup();

                boolean cardMatchesGroup = group.getFilters().accepts(game, cardToDeploy) && group.isGroupEnabled(gameState, modifiersQuerying);

                if (cardsInGroup.isEmpty()) {
                    // If card matches this group, then a LocationPlacement will be created for it.
                    if (cardMatchesGroup) {
                        // If the group is currently empty, then either create a LocationPlacement as "right of" the last card in another group visited
                        // or set "needsLeftOfAnother" so when the next group that has a card is visited, a LocationPlacement of "left of" will be created.
                        if (lastCardVisited != null) {
                            placesToDeploy.add(new LocationPlacement(parentNameOfCardToDeploy, parentPersonaOfCardToDeploy, parentStarshipOrVehicle, lastCardVisited, LocationPlacementDirection.RIGHT_OF));
                            needsLeftOfAnother = null;
                        }
                        else {
                            LocationPlacement placement = new LocationPlacement(parentNameOfCardToDeploy, parentPersonaOfCardToDeploy, parentStarshipOrVehicle, null, LocationPlacementDirection.LEFT_OF);
                            placesToDeploy.add(placement);
                            needsLeftOfAnother = placement;
                        }
                    }
                }
                else {
                    boolean onlyNextToSpaceportSites = false;

                    // Visit each card in group
                    for (int i = 0; i < cardsInGroup.size(); ++i) {
                        PhysicalCard prevCardInGroup = (i > 0) ? cardsInGroup.get(i - 1) : null;
                        PhysicalCard cardInGroup = cardsInGroup.get(i);
                        PhysicalCard nextCardInGroup = (i < cardsInGroup.size() - 1) ? cardsInGroup.get(i + 1) : null;

                        lastCardVisited = cardInGroup;

                        // If a placement "needs to be left of another card" set it relative to this card
                        if (needsLeftOfAnother != null) {
                            needsLeftOfAnother.setOtherCard(lastCardVisited);
                            needsLeftOfAnother = null;
                        }

                        // If this card matches group, add each card in group as with valid placement (making sure to not put a non-spaceport site between spaceport sites)
                        if (cardMatchesGroup) {

                            // Check if deploying site between sites is prevented
                            boolean leftOfPreventedDueToBetweenSites = false;
                            boolean convertOrRebuildPreventedDueToBetweenSites = false;
                            boolean rightOfPreventedDueToBetweenSites = false;
                            for (int j = i; j >= 0; --j) {
                                for (int k = i; k < cardsInGroup.size(); ++k) {
                                    if (modifiersQuerying.isSitePreventedFromDeployingBetweenSites(gameState, cardsInGroup.get(j), cardsInGroup.get(k))) {
                                        if (j < i) {
                                            leftOfPreventedDueToBetweenSites = true;
                                        }
                                        if (k > i) {
                                            rightOfPreventedDueToBetweenSites = true;
                                        }
                                        if (leftOfPreventedDueToBetweenSites && rightOfPreventedDueToBetweenSites) {
                                            convertOrRebuildPreventedDueToBetweenSites = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            // Special check for Big One: Asteroid Cave or Space Slug Belly as the title changes
                            int numAlreadyInGroup = Filters.filter(cardsInGroup, game,
                                    Filters.and(Filters.sameTitleAs(cardToDeploy), Filters.not(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly), Filters.uniqueness(uniquenessOfCardToDeploy))).size();
                            if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(game, cardInGroup)) {
                                numAlreadyInGroup += Filters.filter(cardsInGroup, game,
                                        Filters.and(Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly, Filters.uniqueness(uniquenessOfCardToDeploy))).size();
                            }
                            boolean onlyRoomToConvertOrRebuild = cardToDeploy.getBlueprint().getUniqueness() != null && (numAlreadyInGroup >= cardToDeploy.getBlueprint().getUniqueness().getValue());
                            boolean isConversionValid = Filters.canBeConvertedByDeployment(cardToDeploy).accepts(game, cardInGroup) && !convertOrRebuildPreventedDueToBetweenSites;
                            boolean isRebuildValid = Filters.canBeRebuiltByDeployment(cardToDeploy).accepts(game, cardInGroup) && !convertOrRebuildPreventedDueToBetweenSites;

                            // Check if the uniqueness limit is reached, so conversion or rebuild is the only option.
                            if (onlyRoomToConvertOrRebuild) {
                                // Check if this location can be converted or rebuilt
                                if (isConversionValid || isRebuildValid) {
                                    placesToDeploy.add(new LocationPlacement(parentNameOfCardToDeploy, parentPersonaOfCardToDeploy, parentStarshipOrVehicle, cardInGroup, LocationPlacementDirection.REPLACE));
                                }
                            } else {
                                // Determine possible placements
                                LocationPlacementDirection direction;
                                if (leftOfPreventedDueToBetweenSites && rightOfPreventedDueToBetweenSites)
                                    direction = isConversionValid ? LocationPlacementDirection.REPLACE : null;
                                else if (leftOfPreventedDueToBetweenSites)
                                    direction = isConversionValid ? LocationPlacementDirection.RIGHT_OF_OR_REPLACE : LocationPlacementDirection.RIGHT_OF;
                                else if (rightOfPreventedDueToBetweenSites)
                                    direction = isConversionValid ? LocationPlacementDirection.LEFT_OF_OR_REPLACE : LocationPlacementDirection.LEFT_OF;
                                else
                                    direction = isConversionValid ? LocationPlacementDirection.LEFT_OR_RIGHT_OF_OR_REPLACE : LocationPlacementDirection.LEFT_OR_RIGHT_OF;

                                if (direction != null) {
                                    if (lastCardVisited.getBlueprint().hasKeyword(Keyword.SPACEPORT_SITE)) {
                                        if (cardToDeploy.getBlueprint().hasKeyword(Keyword.SPACEPORT_SITE)) {
                                            onlyNextToSpaceportSites = true;
                                        } else {
                                            // This makes sure a non-spaceport site is not placed between two spaceport sites
                                            boolean leftNotValid = (prevCardInGroup != null && prevCardInGroup.getBlueprint().hasKeyword(Keyword.SPACEPORT_SITE));
                                            boolean rightNotValid = (nextCardInGroup != null && nextCardInGroup.getBlueprint().hasKeyword(Keyword.SPACEPORT_SITE));

                                            if (leftNotValid && rightNotValid) {
                                                if (direction == LocationPlacementDirection.LEFT_OR_RIGHT_OF_OR_REPLACE)
                                                    direction = LocationPlacementDirection.REPLACE;
                                                else
                                                    direction = null;
                                            } else if (leftNotValid) {
                                                if (direction == LocationPlacementDirection.LEFT_OR_RIGHT_OF_OR_REPLACE)
                                                    direction = LocationPlacementDirection.RIGHT_OF_OR_REPLACE;
                                                else
                                                    direction = LocationPlacementDirection.RIGHT_OF;
                                            } else if (rightNotValid) {
                                                if (direction == LocationPlacementDirection.LEFT_OR_RIGHT_OF_OR_REPLACE)
                                                    direction = LocationPlacementDirection.LEFT_OF_OR_REPLACE;
                                                else
                                                    direction = LocationPlacementDirection.LEFT_OF;
                                            }
                                        }
                                    }
                                }

                                if (direction != null) {
                                    placesToDeploy.add(new LocationPlacement(parentNameOfCardToDeploy, parentPersonaOfCardToDeploy, parentStarshipOrVehicle, lastCardVisited, direction));
                                }
                            }
                        }
                    }

                    // Special rule: Spaceport sites must deploy next to other spaceport sites, so if any of placement choices for a spaceport site
                    // are next to another spaceport site, remove any choices that are not next to spaceport sites.
                    if (onlyNextToSpaceportSites) {
                        for (Iterator<LocationPlacement> iterator = placesToDeploy.iterator(); iterator.hasNext(); ) {
                            LocationPlacement placeToDeploy = iterator.next();
                            if (!placeToDeploy.getOtherCard().getBlueprint().hasKeyword(Keyword.SPACEPORT_SITE)) {
                                // Remove the current element from the iterator and the list.
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }

        if (modifiersQuerying.onlyDeploysAdjacentToSpecificLocations(gameState, cardToDeploy)) {
            if (specialLocationConditions == null)
                specialLocationConditions = Filters.or(Filters.adjacentSiteTo(cardToDeploy, modifiersQuerying.getFilterForOnlyDeploysAdjacentToSpecificLocations(gameState, cardToDeploy)), Filters.adjacentSectorTo(cardToDeploy, modifiersQuerying.getFilterForOnlyDeploysAdjacentToSpecificLocations(gameState, cardToDeploy)));
            else
                specialLocationConditions = Filters.and(specialLocationConditions, Filters.or(Filters.adjacentSiteTo(cardToDeploy, modifiersQuerying.getFilterForOnlyDeploysAdjacentToSpecificLocations(gameState, cardToDeploy)), Filters.adjacentSectorTo(cardToDeploy, modifiersQuerying.getFilterForOnlyDeploysAdjacentToSpecificLocations(gameState, cardToDeploy))));
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

    /**
     * Filters out placements that would cause the deployed location to not satisfy the special location conditions.
     * @param game the game
     * @param cardToDeploy the location to deploy
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy
     * @param locationPlacements the location placements to consolidate
     */
    protected final List<LocationPlacement> checkSpecialConditions(SwccgGame game, PhysicalCard cardToDeploy, Filter specialLocationConditions, List<LocationPlacement> locationPlacements) {
        List<LocationPlacement> validPlacements = new LinkedList<LocationPlacement>();

        for (LocationPlacement locationPlacement : locationPlacements) {
            LocationPlacementDirection direction = locationPlacement.getDirection();
            if (direction.isLeftOf()) {
                LocationPlacement placement = new LocationPlacement(locationPlacement.getParentSystem(), locationPlacement.getParentStarshipOrVehiclePersona(),
                        locationPlacement.getParentStarshipOrVehicleCard(), locationPlacement.getOtherCard(), LocationPlacementDirection.LEFT_OF);
                if (isSpecialLocationConditionsFulfilled(game, cardToDeploy, specialLocationConditions, placement)) {
                    validPlacements.add(placement);
                }
            }
            if (direction.isReplace()) {
                LocationPlacement placement = new LocationPlacement(locationPlacement.getParentSystem(), locationPlacement.getParentStarshipOrVehiclePersona(),
                        locationPlacement.getParentStarshipOrVehicleCard(), locationPlacement.getOtherCard(), LocationPlacementDirection.REPLACE);
                if (isSpecialLocationConditionsFulfilled(game, cardToDeploy, specialLocationConditions, placement)) {
                    validPlacements.add(placement);
                }
            }
            if (direction.isRightOf()) {
                LocationPlacement placement = new LocationPlacement(locationPlacement.getParentSystem(), locationPlacement.getParentStarshipOrVehiclePersona(),
                        locationPlacement.getParentStarshipOrVehicleCard(), locationPlacement.getOtherCard(), LocationPlacementDirection.RIGHT_OF);
                if (isSpecialLocationConditionsFulfilled(game, cardToDeploy, specialLocationConditions, placement)) {
                    validPlacements.add(placement);
                }
            }
        }

        return validPlacements;
    }

    /**
     * Filters out placements that would cause the deployed location to not satisfy the special location conditions.
     * @param game the game
     * @param cardToDeploy the location to deploy
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy
     * @param placement the location placement to check
     */
    private boolean isSpecialLocationConditionsFulfilled(SwccgGame game, PhysicalCard cardToDeploy, Filter specialLocationConditions, LocationPlacement placement) {
        return game.getGameState().checkLocationConditionsWithLocationAddedToTable(game, cardToDeploy, placement, specialLocationConditions);
    }

    /**
     * Consolidates the location placements. When the "other card" is the same in multiple location placements, the
     * union of the directions are combined into a single location placement.
     * @param cardToDeploy the location to deploy
     * @param locationPlacements the location placements to consolidate
     */
    protected final List<LocationPlacement> consolidatePlacements(PhysicalCard cardToDeploy, List<LocationPlacement> locationPlacements) {
        List<LocationPlacement> consolidatedPlacements = new LinkedList<LocationPlacement>();

        while (!locationPlacements.isEmpty()) {
            LocationPlacement consolidatedPlacement = null;

            for (Iterator<LocationPlacement> iterator = locationPlacements.iterator(); iterator.hasNext(); ) {
                LocationPlacement placement = iterator.next();
                if (consolidatedPlacement == null) {
                    consolidatedPlacement = new LocationPlacement(placement.getParentSystem(), placement.getParentStarshipOrVehiclePersona(),
                            placement.getParentStarshipOrVehicleCard(), placement.getOtherCard(), placement.getDirection());
                    iterator.remove();
                }
                else if (((consolidatedPlacement.getParentSystem() == null && placement.getParentSystem() == null)
                        || (consolidatedPlacement.getParentSystem() != null && consolidatedPlacement.getParentSystem().equals(placement.getParentSystem())))
                        && ((consolidatedPlacement.getParentStarshipOrVehiclePersona() == null && placement.getParentStarshipOrVehiclePersona() == null)
                        || (consolidatedPlacement.getParentStarshipOrVehiclePersona() != null && consolidatedPlacement.getParentStarshipOrVehiclePersona() == placement.getParentStarshipOrVehiclePersona()))
                        && ((consolidatedPlacement.getParentStarshipOrVehicleCard() == null && placement.getParentStarshipOrVehicleCard() == null)
                        || (consolidatedPlacement.getParentStarshipOrVehicleCard() != null && consolidatedPlacement.getParentStarshipOrVehicleCard() == placement.getParentStarshipOrVehicleCard()))
                        && ((consolidatedPlacement.getOtherCard() == null && placement.getOtherCard() == null)
                        || consolidatedPlacement.getOtherCard() != null && consolidatedPlacement.getOtherCard() == placement.getOtherCard())) {
                    // The directions can be combined
                    LocationPlacementDirection direction = LocationPlacementDirection.getUnion(consolidatedPlacement.getDirection(), placement.getDirection());
                    if (direction == null)
                        throw new UnsupportedOperationException("Unable to get valid placement direction for  " + GameUtils.getFullName(cardToDeploy));

                    consolidatedPlacement.setDirection(direction);
                    iterator.remove();
                }
            }

            if (consolidatedPlacement != null)
                consolidatedPlacements.add(consolidatedPlacement);
        }

        return consolidatedPlacements;
    }

    /**
     * Deploys the location to the location layout.
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param locationPlacement the location placement
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     */
    @Override
    public final void deployToLayout(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, LocationPlacement locationPlacement, boolean forCheckingOnly) {

        // Figure out a location index to give this card so the group is put in the correct order during refreshLocationIndexes().
        if (locationPlacement.getOtherCard() != null) {
            boolean incrementIndexes = false;
            List<PhysicalCard> topLocationsInOrder = getTopLocationsInOrder();
            for (PhysicalCard card : topLocationsInOrder) {
                // If cardToDeploy found where it needs to be inserted, need to increment the index of cards to the right of it.
                if (incrementIndexes) {
                    card.setLocationZoneIndex(card.getLocationZoneIndex() + 1);
                }
                else if (Filters.sameCardId(card).accepts(game, locationPlacement.getOtherCard())) {
                    // If this card is being put left of the other card have its index number
                    // be less than the other card
                    if (locationPlacement.getDirection().isLeftOf()) {
                        cardToDeploy.setLocationZoneIndex(card.getLocationZoneIndex());
                        card.setLocationZoneIndex(card.getLocationZoneIndex() + 1);
                    }
                    // If this card is being put right of the other card have its index number
                    // be more than the other card
                    else if (locationPlacement.getDirection().isRightOf()) {
                        cardToDeploy.setLocationZoneIndex(card.getLocationZoneIndex() + 1);
                    }
                    // Otherwise it is a conversion and do not need to increment any indexes
                    else {
                        cardToDeploy.setLocationZoneIndex(card.getLocationZoneIndex());
                        break;
                    }
                    incrementIndexes = true;
                }
            }
        }

        // Just using the first possible group order since we are just looking for the group
        for (LocationGroup group : getPossibleGroupOrders(forCheckingOnly).iterator().next()) {

            boolean cardMatchesFilters = group.getFilters().accepts(game.getGameState(), modifiersQuerying, cardToDeploy) && group.isGroupEnabled(game.getGameState(), modifiersQuerying);

            if (cardMatchesFilters) {
                // Check if card should be placed in first available spot
                if (locationPlacement.getOtherCard() == null) {
                    group.addLocation(0, cardToDeploy);
                    return;
                }

                List<PhysicalCard> cardsInGroup = group.getTopCardsInGroup();

                // Check if other card that card will be deployed relative to is in this group
                for (int j = 0; j < cardsInGroup.size(); ++j) {
                    if (Filters.sameCardId(cardsInGroup.get(j)).accepts(game, locationPlacement.getOtherCard())) {
                        if (locationPlacement.getDirection().isLeftOf()) {
                            group.addLocation(j, cardToDeploy);
                            return;
                        }
                        else if (locationPlacement.getDirection().isRightOf()) {
                            group.addLocation(j + 1, cardToDeploy);
                            return;
                        }
                        else {
                            group.convertOrRebuildLocation(cardToDeploy, locationPlacement.getOtherCard());
                            return;
                        }
                    }
                }

                // If we get here then the other location is not in this group, so if the placement is "left of", put card
                // in the last spot in the group. If placement is "right of", put card in first spot in this group.
                if (locationPlacement.getDirection().isLeftOf()) {
                    group.addLocation(Integer.MAX_VALUE, cardToDeploy);
                    return;
                }
                else if (locationPlacement.getDirection().isRightOf()) {
                    group.addLocation(0, cardToDeploy);
                    return;
                }

                throw new UnsupportedOperationException("Unable to place " + GameUtils.getFullName(cardToDeploy) + " in matching group");
            }
        }

        throw new UnsupportedOperationException("Unable to find a matching group for " + GameUtils.getFullName(cardToDeploy));
    }

    /**
     * Remove location from layout.
     * @param location the location
     * @param forCheckingOnly true if only removing temporarily placed location on table to check conditions, otherwise false
     * @return true if location found and removed, otherwise false
     */
    @Override
    public final boolean removeLocation(PhysicalCard location, boolean forCheckingOnly) {
        // Just using the first possible group order since we are just looking for the group
        for (LocationGroup group : getPossibleGroupOrders(forCheckingOnly).iterator().next()) {
            if (group.removeLocation(location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the relative order of this layout in the location layout environment (lower numbers are left of higher numbers).
     * System layouts will use their parsec number, and other groups will use a negative number so they are left of the systems.
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     * @return the order index
     */
    public final Collection<List<LocationGroup>> getPossibleGroupOrders(boolean forCheckingOnly) {
        List<List<LocationGroup>> validGroupOrders = new LinkedList<List<LocationGroup>>();

        for (LocationGroupOrder groupOrder : _groupOrders) {
            // For each group ordering, add the possible arrangements to the valid orders.
            // Each time there are multiple possibilities, need to duplicate all the existing possibilities
            // and add the new ones to them.
            Collection<List<LocationGroup>> possibleGroupOrders = groupOrder.getValidArrangements(forCheckingOnly);
            if (!possibleGroupOrders.isEmpty()) {
                // Make sure there is at least one list in validGroupOrders.
                if (validGroupOrders.isEmpty()) {
                    validGroupOrders.add(new LinkedList<LocationGroup>());
                }
                int totalValidGroupOrders = validGroupOrders.size();

                // Copy the existing groups to make room for multiple possibilities.
                for (int i=0; i<possibleGroupOrders.size() - 1; ++i) {
                    for (int j=0; j<totalValidGroupOrders; ++j) {
                        validGroupOrders.add(new LinkedList<LocationGroup>(validGroupOrders.get(j)));
                    }
                }

                // Append the new possible orders to the valid group orders
                int validGroupOrdersIndexMultiplier = 0;
                for (List<LocationGroup> possibleGroupOrder : possibleGroupOrders) {
                    for (int i = 0; i<totalValidGroupOrders; ++i) {
                        int indexToUpdate = i + (validGroupOrdersIndexMultiplier * totalValidGroupOrders);
                        validGroupOrders.get(indexToUpdate).addAll(possibleGroupOrder);
                    }
                    validGroupOrdersIndexMultiplier++;
                }
            }
        }

        return validGroupOrders;
    }

    /**
     * Gets the existing groups represented on the table in order from left to right.
     * @return the groups in order from left to right
     */
    public final List<LocationGroup> getExistingGroupsInOrder() {
        SortedMap<Integer, LocationGroup> existingGroupsInOrder = new TreeMap<Integer, LocationGroup>();

        for (LocationGroupOrder groupOrder : _groupOrders) {
            // Just look at the first possible group group because we are going to figure out the existing locations anyway.
            List<LocationGroup> possibleGroupOrder = groupOrder.getDefaultArrangement();
            for (LocationGroup locationGroup : possibleGroupOrder) {
                Integer locationIndex = locationGroup.getLocationZoneIndex();
                if (locationIndex != null)
                    existingGroupsInOrder.put(locationIndex, locationGroup);
            }
        }

        return new ArrayList<LocationGroup>(existingGroupsInOrder.values());
    }

    /**
     * Gets the top locations in order (left to right).
     * @return the top locations
     */
    public final List<PhysicalCard> getTopLocationsInOrder() {
        List<PhysicalCard> locationsInOrder = new LinkedList<PhysicalCard>();
        for (LocationGroup group : getExistingGroupsInOrder()) {
            locationsInOrder.addAll(group.getTopCardsInGroup());
        }

        return locationsInOrder;
    }

    /**
     * Gets the converted locations (that have another location on top) in order (left to right).
     * @return the converted locations
     */
    @Override
    public final List<List<PhysicalCard>> getConvertedLocationsInOrder() {
        List<List<PhysicalCard>> convertedLocationsInOrder = new LinkedList<List<PhysicalCard>>();
        for (LocationGroup group : getExistingGroupsInOrder()) {
            List<List<PhysicalCard>> cardsInGroup = group.getConvertedCardsInGroup();
            convertedLocationsInOrder.addAll(cardsInGroup);
        }

        return convertedLocationsInOrder;
    }

    /**
     * Gets all the locations (top and converted) in order (left to right). Within each sub-list, the top location
     * is first.
     * @return the locations
     */
    @Override
    public final List<List<PhysicalCard>> getLocationsInOrder() {
        List<List<PhysicalCard>> locationsInOrder = new LinkedList<List<PhysicalCard>>();
        for (LocationGroup group : getExistingGroupsInOrder()) {
            List<List<PhysicalCard>> cardsInGroup = group.getCardsInGroup();
            locationsInOrder.addAll(cardsInGroup);
        }

        return locationsInOrder;
    }
}
