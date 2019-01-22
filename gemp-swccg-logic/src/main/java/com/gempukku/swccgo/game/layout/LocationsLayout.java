package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.*;

/**
 * This class contains the layout information for the locations in the game.
 */
public class LocationsLayout implements Snapshotable<LocationsLayout> {
    private LocationLayout _holositeLayout;
    private List<LocationLayout> _locationLayouts = new LinkedList<LocationLayout>();

    /**
     * Needed to generate snapshot.
     */
    public LocationsLayout() {
    }

    @Override
    public void generateSnapshot(LocationsLayout selfSnapshot, SnapshotData snapshotData) {
        LocationsLayout snapshot = selfSnapshot;

        // Set each field
        snapshot._holositeLayout = snapshotData.getDataForSnapshot(_holositeLayout);
        for (LocationLayout locationLayout : _locationLayouts) {
            snapshot._locationLayouts.add(snapshotData.getDataForSnapshot(locationLayout));
        }
    }

    /**
     * Creates the layout information using the specified card blueprint library.
     * @param library the library
     */
    public LocationsLayout(SwccgCardBlueprintLibrary library) {
        List<SwccgCardBlueprint> systems = new LinkedList<SwccgCardBlueprint>();
        List<Persona> starshipsAndVehicles = new LinkedList<Persona>();

        // Find the systems, starships, and vehicles for layout
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.FULL_SETS_CARD_COUNTS, 1, systems, starshipsAndVehicles);
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.PREMIUM_SETS_CARD_COUNTS, 101, systems, starshipsAndVehicles);
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.VIRTUAL_SETS_CARD_COUNTS, 200, systems, starshipsAndVehicles);
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.VIRTUAL_PREMIUM_SETS_CARD_COUNTS, 301, systems, starshipsAndVehicles);
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.DREAM_CARD_SETS_CARD_COUNTS, 401, systems, starshipsAndVehicles);
        findSystemsAndStarshipsAndVehiclesForLayout(library, CardCounts.PLAYTESTING_SETS_CARD_COUNTS, 501, systems, starshipsAndVehicles);

        // Add a layout for holosites
        _holositeLayout = new DefaultHolositeLayout();

        // Add starship/vehicle sites layouts
        for (Persona starshipOrVehicle : starshipsAndVehicles) {
            addToLayouts(new DefaultStarshipOrVehicleSitesLayout(starshipOrVehicle));
        }

        // Add a system layout for each system
        for (SwccgCardBlueprint system : systems) {
            AbstractSystemLayout systemLayout = null;
            if (system.getTitle().equals(Title.Bespin)) {
                systemLayout = new BespinLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Coruscant)) {
                systemLayout = new CoruscantLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Dagobah)) {
                systemLayout = new DagobahLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Death_Star)) {
                systemLayout = new DeathStarLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Death_Star_II)) {
                systemLayout = new DeathStarIILayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Endor)) {
                systemLayout = new EndorLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Hoth)) {
                systemLayout = new HothLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Jakku)) {
                systemLayout = new JakkuLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Naboo)) {
                systemLayout = new NabooLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Tatooine)) {
                systemLayout = new TatooineLayout(system.getTitle(), system.getParsec());
            }
            else if (system.getTitle().equals(Title.Yavin_4)) {
                systemLayout = new Yavin4Layout(system.getTitle(), system.getParsec());
            }
            else if (system.hasIcon(Icon.PLANET)) {
                systemLayout = new DefaultPlanetLayout(system.getTitle(), system.getParsec());
            }
            else if (system.hasIcon(Icon.SPACE)) {
                systemLayout = new DefaultSpaceLayout(system.getTitle(), system.getParsec());
            }

            if (systemLayout != null) {
                addToLayouts(systemLayout);
            }
        }
        addToLayouts(new DefaultPlanetLayout(Title.Mustafar, 7));
        addToLayouts(new DefaultPlanetLayout(Title.Scarif, 17));
        addToLayouts(new DefaultPlanetLayout(Title.Ahch_To, 5)); // Need to be changed when system is implemented.
    }

    /**
     * Looks through the cards in the library to determine all the systems and starships that need to have layout information.
     * @param library the card blueprint library
     * @param cardSetCounts the counts of cards in each set
     * @param setIndexOffset the set number of the first array item in cardSetCounts
     * @param systems the list of systems
     * @param starshipsAndVehicles the list of starships and vehicles with a site
     */
    private void findSystemsAndStarshipsAndVehiclesForLayout(SwccgCardBlueprintLibrary library, int[] cardSetCounts, int setIndexOffset,
                                                             List<SwccgCardBlueprint> systems, List<Persona> starshipsAndVehicles) {
        for (int i = 0; i < cardSetCounts.length; i++) {
            int setNum = setIndexOffset + i;
            for (int j = 1; j <= cardSetCounts[i]; j++) {
                String blueprintId = setNum + "_" + j;
                try {
                    if (library.getBaseBlueprintId(blueprintId).equals(blueprintId)) {
                        SwccgCardBlueprint cardBlueprint = library.getSwccgoCardBlueprint(blueprintId);
                        if (cardBlueprint != null) {
                            CardCategory cardCategory = cardBlueprint.getCardCategory();
                            if (cardCategory == CardCategory.LOCATION) {
                                if (cardBlueprint.getCardSubtype() == CardSubtype.SYSTEM) {
                                    systems.add(cardBlueprint);
                                } else if (cardBlueprint.getUniqueness() == Uniqueness.UNIQUE
                                        && (cardBlueprint.hasIcon(Icon.STARSHIP_SITE) || cardBlueprint.hasIcon(Icon.VEHICLE_SITE))) {
                                    Persona persona = cardBlueprint.getRelatedStarshipOrVehiclePersona();
                                    if (persona != null) {
                                        starshipsAndVehicles.add(persona);
                                    }
                                }
                            }
                        }
                    }
                } catch (IllegalArgumentException exp) {
                }
            }
        }
    }

    /**
     * Avoid duplicates and keep systems in parsec order.
     * @param layout the layout
     */
    private void addToLayouts(AbstractLocationLayout layout) {
        for (int i=0; i<_locationLayouts.size(); i++) {
            LocationLayout curLayout = _locationLayouts.get(i);
            // Do not add duplicates
            if (curLayout.getParentSystemTitle()!=null && layout.getParentSystemTitle()!=null
                    && curLayout.getParentSystemTitle().equals(layout.getParentSystemTitle()))
                return;
            if (curLayout.getParentStarshipOrVehiclePersona()!=null && layout.getParentStarshipOrVehiclePersona()!=null
                    && curLayout.getParentStarshipOrVehiclePersona()==layout.getParentStarshipOrVehiclePersona())
                return;
            if (curLayout.getParentStarshipOrVehicleCard()!=null && layout.getParentStarshipOrVehicleCard()!=null
                    && curLayout.getParentStarshipOrVehicleCard()==layout.getParentStarshipOrVehicleCard())
                return;

            // Add here if lower parsec number
            if (curLayout.getOrderIndex() > layout.getOrderIndex()) {
                _locationLayouts.add(i, layout);
                return;
            }
        }
        // Add to end
        _locationLayouts.add(layout);
    }

    /**
     * Gets the places in the location layout where the location can be deployed. Optionally, a specific system to deploy
     * to can be specified.
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param targetSystem the target system, or null
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @return the places in the location layout where the location can be deployed
     */
    public List<LocationPlacement> getPlacesToDeployLocation(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, String targetSystem, Filter specialLocationConditions) {
        GameState gameState = game.getGameState();

        // If a specific system was specified, verify that this location is a location can deploy there at all.
        if (targetSystem != null) {
            if (cardToDeploy.getPartOfSystem() == null) {
                if (!cardToDeploy.getBlueprint().getUniqueness().isPerSystem())
                    return Collections.emptyList();
                else if (cardToDeploy.getBlueprint().mayNotBePartOfSystem(targetSystem))
                    return Collections.emptyList();
            }
            else if (!cardToDeploy.getPartOfSystem().equals(targetSystem)) {
                return Collections.emptyList();
            }
        }

        List<LocationPlacement> placesToDeploy = new LinkedList<LocationPlacement>();

        // Find where location can be deployed based on location type
        //
        // Holosite locations
        if (Filters.holosite.accepts(gameState, modifiersQuerying, cardToDeploy)) {
            placesToDeploy.addAll(_holositeLayout.getPlacesToDeployLocation(game, modifiersQuerying, cardToDeploy, specialLocationConditions, false));
        }
        // Generic locations
        else if (cardToDeploy.getBlueprint().getUniqueness().isPerSystem()) {

            Filter partOfSystemFilter = targetSystem != null ? Filters.partOfSystem(targetSystem) : Filters.any;
            Filter isOrbitingFilter = targetSystem != null ? Filters.isOrbiting(targetSystem) : Filters.any;

            // For each planet system represented on the table, get the possible places in the
            // location layout where the location could be deployed as part of that planet system.
            Filter filter = Filters.and(Filters.planet_system, partOfSystemFilter);
            // Special case for asteroid cave (only related to Big One)
            if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(game, cardToDeploy)) {
                filter = Filters.and(Filters.Big_One, isOrbitingFilter);
            }
            else if (Filters.site.accepts(game, cardToDeploy)) {
                filter = Filters.or(filter, Filters.and(Filters.planet_site, Filters.not(Filters.perSystemUniqueness), partOfSystemFilter));
            }
            else if (Filters.asteroid_sector.accepts(game, cardToDeploy)) {
                filter = Filters.or(filter, Filters.and(Filters.asteroid_sector, isOrbitingFilter));
            }
            else if (Filters.cloud_sector.accepts(game, cardToDeploy)) {
                filter = Filters.or(filter, Filters.and(Filters.cloud_sector, partOfSystemFilter));
            }

            Collection<PhysicalCard> locationsRelatedToPlanets = Filters.filterAllOnTable(game, filter);
            Set<String> systemNames = new HashSet<String>();
            for (PhysicalCard relatedLocation : locationsRelatedToPlanets) {
                if (relatedLocation.getPartOfSystem() != null)
                    systemNames.add(relatedLocation.getPartOfSystem());
                else
                    systemNames.add(relatedLocation.getSystemOrbited());
            }

            // Try to find places to deploy using each system to be a part of
            for (String systemName : systemNames) {
                // Check if location is not allowed to be part of system
                if (!cardToDeploy.getBlueprint().mayNotBePartOfSystem(systemName)) {
                    // Set "part of system" or "is orbiting system" while looking for valid places to deploy, then set it back to null
                    if (Filters.or(Filters.asteroid_sector, Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly).accepts(game, cardToDeploy))
                        cardToDeploy.setSystemOrbited(systemName);
                    else
                        cardToDeploy.setPartOfSystem(systemName);

                    for (LocationLayout locationLayout : _locationLayouts) {
                        // Since there must already be locations on the table that are part of the system to deploy a generic
                        // location, only look at the layout if the system name matches.
                        if (systemName.equals(locationLayout.getParentSystemTitle())) {
                            placesToDeploy.addAll(locationLayout.getPlacesToDeployLocation(game, modifiersQuerying, cardToDeploy, specialLocationConditions, false));
                            break;
                        }
                    }
                    cardToDeploy.setSystemOrbited(null);
                    cardToDeploy.setPartOfSystem(null);
                }
            }
        }
        // Non-unique starship or vehicle sites
        else if (Filters.and(Filters.non_unique, Filters.or(Filters.starship_site, Filters.vehicle_site)).accepts(game, cardToDeploy)) {

            // Determine each starship or vehicle on table that the site could be related to.
            Filter filter = Filters.and(Filters.owner(cardToDeploy.getOwner()), cardToDeploy.getBlueprint().getRelatedStarshipOrVehicleFilter());
            Collection<PhysicalCard> starshipsAndVehicles = Filters.filterAllOnTable(game, filter);

            // Try to find places to deploy using each starship or vehicle to be a part of
            for (PhysicalCard starshipOrVehicle : starshipsAndVehicles) {

                // Make sure there is a location layout for this vehicle or starship (since they are added/removed as needed)
                addLocationLayoutForStarshipOrVehicle(starshipOrVehicle);

                // Set "related starship or vehicle" while looking for valid places to deploy, then set it back to null
                cardToDeploy.setRelatedStarshipOrVehicle(starshipOrVehicle);

                for (LocationLayout locationLayout : _locationLayouts) {
                    placesToDeploy.addAll(locationLayout.getPlacesToDeployLocation(game, modifiersQuerying, cardToDeploy, specialLocationConditions, false));
                }
                cardToDeploy.setRelatedStarshipOrVehicle(null);
            }
        }
        // All other locations
        else {
            for (LocationLayout locationLayout : _locationLayouts) {
                placesToDeploy.addAll(locationLayout.getPlacesToDeployLocation(game, modifiersQuerying, cardToDeploy, specialLocationConditions, false));
            }
        }

        return placesToDeploy;
    }

    /**
     * Deploy location based on LocationPlacement
     * @param game the game
     * @param modifiersQuerying the modifiers querying
     * @param cardToDeploy the location
     * @param placement the LocationPlacement
     * @param forCheckingOnly true if only temporarily placing location on table to check conditions, otherwise false
     */
    public void deployLocationToLayout(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, LocationPlacement placement, boolean forCheckingOnly) {
        // Holosite
        if (Filters.holosite.accepts(game.getGameState(), modifiersQuerying, cardToDeploy)) {
            _holositeLayout.deployToLayout(game, modifiersQuerying, cardToDeploy, placement, forCheckingOnly);
            return;
        }

        // Other sites
        for (LocationLayout layout : _locationLayouts) {
            // Check if this is the layout to deploy to
            if ((layout.getParentSystemTitle() != null && placement.getParentSystem() != null && layout.getParentSystemTitle().equals(placement.getParentSystem()))
                    || (layout.getParentStarshipOrVehiclePersona() != null
                        && ((placement.getParentStarshipOrVehiclePersona() != null && layout.getParentStarshipOrVehiclePersona() == placement.getParentStarshipOrVehiclePersona())
                            || (placement.getParentStarshipOrVehicleCard() != null && placement.getParentStarshipOrVehicleCard().getBlueprint().hasPersona(layout.getParentStarshipOrVehiclePersona()))))
                    || (layout.getParentStarshipOrVehicleCard() != null && placement.getParentStarshipOrVehicleCard() != null && layout.getParentStarshipOrVehicleCard() == placement.getParentStarshipOrVehicleCard())) {
                layout.deployToLayout(game, modifiersQuerying, cardToDeploy, placement, forCheckingOnly);
                return;
            }
        }

        throw new UnsupportedOperationException("Could not find a layout to deploy " + GameUtils.getFullName(cardToDeploy) + " to.");
    }

    /**
     * Remove location from layout.
     * @param game the game
     * @param modifiersQuerying modifiers querying
     * @param location the location
     * @param forCheckingOnly true if only removing temporarily placed location on table to check conditions, otherwise false
     */
    public void removeLocationFromLayout(SwccgGame game, ModifiersQuerying modifiersQuerying, PhysicalCard location, boolean forCheckingOnly) {
        // Holosite
        if (Filters.holosite.accepts(game.getGameState(), modifiersQuerying, location)) {
            _holositeLayout.removeLocation(location, forCheckingOnly);
            return;
        }

        // Other locations
        for (LocationLayout layout : _locationLayouts) {
            // Find and remove the location
            if (layout.removeLocation(location, forCheckingOnly))
                return;
        }
    }

    /**
     * Add a location layout for the specified starship or vehicle (if not there is not already one for this starship)
     * @param starshipOrVehicle starship or vehicle
     */
    public void addLocationLayoutForStarshipOrVehicle(PhysicalCard starshipOrVehicle) {
        // Check if there is already a location layout for this starship or vehicle
        for (LocationLayout layout : _locationLayouts) {
            if (layout.getParentStarshipOrVehicleCard() == starshipOrVehicle)
                return;

            if (layout.getParentStarshipOrVehiclePersona() != null
                    && starshipOrVehicle.getBlueprint().hasPersona(layout.getParentStarshipOrVehiclePersona()))
                return;
        }

        // Got this far. So need to add a layout for this starship or vehicle.
        addToLayouts(new DefaultOtherStarshipOrVehicleSitesLayout(starshipOrVehicle));
    }

    /**
     * Gets the top locations in order (left to right).
     * @return the top locations
     */
    public List<PhysicalCard> getTopLocationsInOrder() {
        List<PhysicalCard> locationsInOrder = new LinkedList<PhysicalCard>();
        locationsInOrder.addAll(_holositeLayout.getTopLocationsInOrder());

        for (LocationLayout layout : _locationLayouts) {
            locationsInOrder.addAll(layout.getTopLocationsInOrder());
        }

        return locationsInOrder;
    }

    /**
     * Gets the top location for the specified converted location.
     * @return the top location
     */
    public PhysicalCard getTopLocationOfConvertedLocation(PhysicalCard covertedLocation) {
        int index = covertedLocation.getLocationZoneIndex();

        for (PhysicalCard topLocation : getTopLocationsInOrder()) {
            if (topLocation.getLocationZoneIndex()==index)
                return topLocation;
        }
        return null;
    }

    /**
     * Gets the converted locations under the specified top location.
     * @return the converted locations
     */
    public List<PhysicalCard> getConvertedLocationsOfTopLocation(PhysicalCard topLocation) {
        int index = topLocation.getLocationZoneIndex();

        for (List<PhysicalCard> locations : getConvertedLocationsInOrder()) {
            if (!locations.isEmpty() && locations.get(0).getLocationZoneIndex()==index)
                return locations;
        }
        return Collections.emptyList();
    }

    /**
     * Gets all the locations (including converted locations) in order (left to right).
     * @return the top locations
     */
    public List<List<PhysicalCard>> getLocationsInOrder() {
        List<List<PhysicalCard>> locationsInOrder = new LinkedList<List<PhysicalCard>>();
        locationsInOrder.addAll(_holositeLayout.getLocationsInOrder());

        for (LocationLayout layout : _locationLayouts) {
            locationsInOrder.addAll(layout.getLocationsInOrder());
        }

        return locationsInOrder;
    }

    /**
     * Gets the converted locations in order (left to right).
     * @return the converted locations
     */
    public List<List<PhysicalCard>> getConvertedLocationsInOrder() {
        List<List<PhysicalCard>> convertedLocationsInOrder = new LinkedList<List<PhysicalCard>>();
        convertedLocationsInOrder.addAll(_holositeLayout.getConvertedLocationsInOrder());

        for (LocationLayout layout : _locationLayouts) {
            convertedLocationsInOrder.addAll(layout.getConvertedLocationsInOrder());
        }

        return convertedLocationsInOrder;
    }

    /**
     * Updates the location index of each location. This is needed whenever a location is added or removed
     * from the table, since the index is used to tell the user interface the left to right order of the locations.
     */
    public void refreshLocationIndexes() {
        List<List<PhysicalCard>> locationsInOrder = getLocationsInOrder();
        for (int i=0; i<locationsInOrder.size(); i++) {
            for (PhysicalCard card : locationsInOrder.get(i)) {
                card.setLocationZoneIndex(i);
            }
        }
    }

    /**
     * Remove any empty starship or vehicle location layouts that are related to a persona.
     */
    public void removeEmptyStarshipOrVehicleLayouts() {
        for (Iterator<LocationLayout> iterator = _locationLayouts.iterator(); iterator.hasNext(); ) {
            LocationLayout layout = iterator.next();
            if (layout.getParentStarshipOrVehicleCard() != null) {
                if (layout.getLocationsInOrder().isEmpty()) {
                    // Remove the current element from the iterator and the list.
                    iterator.remove();
                }
            }
        }
    }
}
