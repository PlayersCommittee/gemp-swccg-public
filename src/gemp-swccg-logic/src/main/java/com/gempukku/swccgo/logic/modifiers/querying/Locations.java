package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.*;

public interface Locations extends BaseQuery, Flags, Icons, Presence, Podracing {

	/**
	 * Determines if the two specified cards are adjacent sectors.
	 *
	 * @param gameState the game state
	 * @param sector1     a card
	 * @param sector2     a card
	 * @return true if specified cards are adjacent sectors, otherwise false
	 */
	default boolean isAdjacentSectors(GameState gameState, PhysicalCard sector1, PhysicalCard sector2) {
		Integer distance = getDistanceBetweenSectors(gameState, sector1, sector2);
		return distance != null && distance == 1;
	}

	/**
	 * Gets the distance between the sector (or the sectors the cards are "at"), or null if determining a distance is not valid.
	 *
	 * @param gameState the game state
	 * @param card1     a card
	 * @param card2     a card
	 * @return the distance between the sectors, or null
	 */
	default Integer getDistanceBetweenSectors(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		PhysicalCard sector1 = getLocationHere(gameState, card1);
		PhysicalCard sector2 = getLocationHere(gameState, card2);

		if (sector1 == null || sector1.getBlueprint().getCardSubtype() != CardSubtype.SECTOR
				|| sector2 == null || sector2.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
			return null;

		int site1Index = sector1.getLocationZoneIndex();
		int site2Index = sector2.getLocationZoneIndex();
		int indexDistance = Math.abs(site1Index - site2Index);

		// Check if sectors are part of same system (or orbiting the same system)
		if ((sector1.getPartOfSystem() != null && sector1.getPartOfSystem().equals(sector2.getPartOfSystem()))
				|| (sector1.getSystemOrbited() != null && sector1.getSystemOrbited().equals(sector2.getSystemOrbited()))) {

			return indexDistance;
		}

		return null;
	}

	/**
	 * Gets the sectors in order between the cards (or between the locations the cards are "at"), or null if determining
	 * sectors between is not valid.
	 *
	 * @param gameState the game state
	 * @param card1     a card
	 * @param card2     a card
	 * @return the sectors in order between card1 and card2, or null
	 */
	default List<PhysicalCard> getSectorsBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		PhysicalCard location1 = getLocationHere(gameState, card1);
		PhysicalCard location2 = getLocationHere(gameState, card2);

		if (location1 == null || location2 == null) {
			return null;
		}

		boolean related = false;

		if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
			if (location1.getTitle().equals(location2.getPartOfSystem())
					|| (location2.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
					&& location1.getTitle().equals(location2.getSystemOrbited()))) {
				related = true;
			}
		}
		else if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
			if (location2.getTitle().equals(location1.getPartOfSystem())
					|| (location1.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM
					&& location2.getTitle().equals(location1.getSystemOrbited()))) {
				related = true;
			}
		}
		else if (location1.getPartOfSystem() != null && location1.getPartOfSystem().equals(location2.getPartOfSystem())) {
			related = true;
		}
		else if (location1.getSystemOrbited() != null && location1.getSystemOrbited().equals(location2.getSystemOrbited())) {
			related = true;
		}

		if (!related) {
			return null;
		}

		List<PhysicalCard> sectors = new ArrayList<PhysicalCard>();

		int location1Index = location1.getLocationZoneIndex();
		int location2Index = location2.getLocationZoneIndex();
		boolean leftToRight = location1Index < location2Index;

		// Add sectors in between (in correct order)
		List<PhysicalCard> locationsInOrder = gameState.getLocationsInOrder();
		for (int i=location1Index; i!=location2Index;) {
			if (i != location1Index
					&& locationsInOrder.get(i).getBlueprint().getCardSubtype() == CardSubtype.SECTOR) {
				sectors.add(locationsInOrder.get(i));
			}

			if (leftToRight)
				i++;
			else
				i--;
		}

		return sectors;
	}

	/**
	 * Determines if the two specified cards are adjacent sites.
	 *
	 * @param gameState the game state
	 * @param site1     a card
	 * @param site2     a card
	 * @return true if specified cards are adjacent sites, otherwise false
	 */
	default boolean isAdjacentSites(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
		Integer distance = getDistanceBetweenSites(gameState, site1, site2);
		return distance != null && distance == 1;
	}

	/**
	 * Determines if the sites are part of the same system, starship, or vehicle.
	 *
	 * @param gameState the game state
	 * @param site1     a site
	 * @param site2     a site
	 * @return the sites in order between card1 and card2
	 */
	default boolean isSitesWithSameParent(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
		if (site1 == null || site1.getBlueprint().getCardSubtype() != CardSubtype.SITE
				|| site2 == null || site2.getBlueprint().getCardSubtype() != CardSubtype.SITE) {
			return false;
		}

		// Check if sites are part of the same system
		if (site1.getPartOfSystem() != null
				&& site1.getPartOfSystem().equals(site2.getPartOfSystem())) {

			return true;
		}
		// Check if sites are part of the same starship/vehicle
		else if ((site1.getBlueprint().hasIcon(Icon.STARSHIP_SITE) && site2.getBlueprint().hasIcon(Icon.STARSHIP_SITE))
				|| (site1.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && site2.getBlueprint().hasIcon(Icon.VEHICLE_SITE))) {

			if (site1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
				return Filters.siteOfStarshipOrVehicle(site1.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, query(), site2);
			}
			else if (site2.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
				return Filters.siteOfStarshipOrVehicle(site2.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, query(), site1);
			}

			return site1.getRelatedStarshipOrVehicle() != null
					&& site2.getRelatedStarshipOrVehicle() != null
					&& site1.getRelatedStarshipOrVehicle().getCardId() == site2.getRelatedStarshipOrVehicle().getCardId();
		}

		return false;
	}

	/**
	 * Gets the distance between the sites (or the sites the cards are "at"), or null if determining a distance is not
	 * valid.
	 *
	 * @param gameState the game state
	 * @param card1     a card
	 * @param card2     a card
	 * @return the distance between the sites, or null
	 */
	default Integer getDistanceBetweenSites(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		PhysicalCard site1 = getLocationHere(gameState, card1);
		PhysicalCard site2 = getLocationHere(gameState, card2);

		if (!isSitesWithSameParent(gameState, site1, site2)
				|| Filters.Death_Star_Trench.accepts(gameState, query(), site1)
				|| Filters.Death_Star_Trench.accepts(gameState, query(), site2)) {
			return null;
		}

		int site1Index = site1.getLocationZoneIndex();
		int site2Index = site2.getLocationZoneIndex();

		return Math.abs(site1Index - site2Index);
	}

	/**
	 * Gets the sites in order between the cards (or between the locations the cards are "at"), or null if determining
	 * sites between is not valid.
	 *
	 * @param gameState the game state
	 * @param card1     a card
	 * @param card2     a card
	 * @return the sites in order between card1 and card2, or null
	 */
	default List<PhysicalCard> getSitesBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		PhysicalCard site1 = getLocationHere(gameState, card1);
		PhysicalCard site2 = getLocationHere(gameState, card2);

		if (!isSitesWithSameParent(gameState, site1, site2)
				|| Filters.Death_Star_Trench.accepts(gameState, query(), site1)
				|| Filters.Death_Star_Trench.accepts(gameState, query(), site2)) {
			return null;
		}

		int location1Index = site1.getLocationZoneIndex();
		int location2Index = site2.getLocationZoneIndex();
		boolean leftToRight = location1Index < location2Index;

		List<PhysicalCard> sites = new ArrayList<PhysicalCard>();

		// Add sites in between (in correct order)
		List<PhysicalCard> locationsInOrder = gameState.getLocationsInOrder();
		for (int i=location1Index; i!=location2Index;) {
			if (i != location1Index
					&& locationsInOrder.get(i).getBlueprint().getCardSubtype() == CardSubtype.SITE) {
				sites.add(locationsInOrder.get(i));
			}

			if (leftToRight)
				i++;
			else
				i--;
		}

		return sites;
	}



	/**
	 * Gets the locations that the specified cards are "at".
	 *
	 * @param gameState the game state
	 * @param cards     the cards
	 * @return locations that the specified cards are "at"
	 */
	default Collection<PhysicalCard> getLocationsThatCardsAreAt(GameState gameState, Collection<PhysicalCard> cards) {
		Set<PhysicalCard> locations = new HashSet<PhysicalCard>();
		for (PhysicalCard card : cards) {
			PhysicalCard location = getLocationThatCardIsAt(gameState, card);
			if (location != null) {
				locations.add(location);
			}
		}

		return locations;
	}

	/**
	 * Gets the locations that the specified cards are "present" at.
	 *
	 * @param gameState the game state
	 * @param cards     the cards
	 * @return locations that the specified cards are "present" at
	 */
	default Collection<PhysicalCard> getLocationsThatCardsArePresentAt(GameState gameState, Collection<PhysicalCard> cards) {
		Set<PhysicalCard> locations = new HashSet<PhysicalCard>();
		for (PhysicalCard card : cards) {
			PhysicalCard location = getLocationThatCardIsPresentAt(gameState, card);
			if (location != null) {
				locations.add(location);
			}
		}

		return locations;
	}

	

	

	default int getParsecNumber(GameState gameState, PhysicalCard physicalCard) {
		SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
		if (blueprint.getCardCategory()!=CardCategory.LOCATION
				|| blueprint.getCardSubtype()!=CardSubtype.SYSTEM)
			return 0;

		return physicalCard.getParsec();
	}

	default boolean isAtSite(GameState gameState, PhysicalCard physicalCard, PhysicalCard site) {
		// A character, starship, vehicle, weapon or device is "at" a site if it is:
		// (1) Present at that site
		// (2) Abort a starship or vehicle at that site.

		PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
		if (location==null || Filters.site.accepts(gameState, query(), location))
			return false;

		return (location.getCardId()==site.getCardId());
	}

	default boolean isAtStarshipSite(GameState gameState, PhysicalCard physicalCard) {

		PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
		return (site!=null && site.getBlueprint().hasIcon(Icon.STARSHIP_SITE));
	}

	default boolean isAtVehicleSite(GameState gameState, PhysicalCard physicalCard) {

		PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
		return (site!=null && site.getBlueprint().hasIcon(Icon.VEHICLE_SITE));
	}

	default boolean isAtStarshipSiteOrVehicleSiteOfPersona(GameState gameState, PhysicalCard physicalCard, Persona starshipOrVehicle) {

		PhysicalCard site = getLocationThatCardIsAt(gameState, physicalCard);
		if (site==null)
			return false;

		// TODO: Since non-unique starship/vehicle sites will not have the starship/vehicle persona in the blueprint, need to check this a different way
		// TODO: when those cards are added

		if (site.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || site.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {
			return site.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
					&& site.getBlueprint().getRelatedStarshipOrVehiclePersona()==starshipOrVehicle;
		}

		return false;
	}

	default boolean isAlone(GameState gameState, PhysicalCard physicalCard) {
		return isCharacterAlone(gameState, physicalCard) || isStarshipOrVehicleAlone(gameState, physicalCard);
	}

	default boolean isAlone(GameState gameState, PhysicalCard physicalCard, Map<InactiveReason, Boolean> spotOverrides) {
		return isCharacterAlone(gameState, physicalCard, spotOverrides) || isStarshipOrVehicleAlone(gameState, physicalCard, spotOverrides);
	}

	default boolean isCharacterAlone(GameState gameState, PhysicalCard physicalCard) {
		return isCharacterAlone(gameState, physicalCard, null);
	}

	default boolean isCharacterAlone(GameState gameState, PhysicalCard physicalCard, Map<InactiveReason, Boolean> spotOverrides) {
		// Your character or permanent pilot is alone at a location if it is active
		// and you have no other cards at that location that are active characters
		// or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
		// TODO: Handle the rest...
		// and a permanent pilot of a starship or vehicle that has multiple permanent pilots
		// (such as Executor or a TIE Squadron), are not considered to be alone.
		// Your starship or vehicle is alone at a location if the only active characters,
		// vehicles and starships you have at that location are aboard that starship or vehicle.
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
				|| physicalCard.getBlueprint().isComboCard())
			return false;

		PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
		if (location==null)
			return false;

		if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.not(physicalCard), Filters.at(location),
				Filters.owner(physicalCard.getOwner()), Filters.or(CardCategory.CHARACTER, Filters.abilityMoreThan(0, true)))))
			return false;

		return true;
	}

	default boolean isStarshipOrVehicleAlone(GameState gameState, PhysicalCard physicalCard) {
		return isStarshipOrVehicleAlone(gameState, physicalCard, null);
	}

	default boolean isStarshipOrVehicleAlone(GameState gameState, PhysicalCard physicalCard, Map<InactiveReason, Boolean> spotOverrides) {
		// Your character or permanent pilot is alone at a location if it is active
		// and you have no other cards at that location that are active characters
		// or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
		// TODO: Handle the rest...
		// and a permanent pilot of a starship or vehicle that has multiple permanent pilots
		// (such as Executor or a TIE Squadron), are not considered to be alone.
		// Your starship or vehicle is alone at a location if the only active characters,
		// vehicles and starships you have at that location are aboard that starship or vehicle.
		if ((physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
				&& physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP))
			return false;

		PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
		if (location==null)
			return false;

		if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.at(location), Filters.owner(physicalCard.getOwner()),
				Filters.or(CardCategory.CHARACTER, CardCategory.STARSHIP, CardCategory.VEHICLE),
				Filters.not(Filters.or(physicalCard, Filters.aboardOrAboardCargoOf(physicalCard))))))
			return false;

		return true;
	}

	default PhysicalCard getLocationHere(GameState gameState, PhysicalCard card) {
		if (card == null)
			return null;

		if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION)
			return card;

		return getLocationThatCardIsAt(gameState, card);
	}

	default Collection<PhysicalCard> getLocationsHere(GameState gameState, Collection<PhysicalCard> cards) {
		Collection<PhysicalCard> locations = new HashSet<PhysicalCard>();

		for (PhysicalCard card : cards) {
			if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
				locations.add(card);
			}
			else {
				PhysicalCard atLocation = getLocationThatCardIsAt(gameState, card);
				if (atLocation != null)
					locations.add(atLocation);
			}
		}

		return locations;
	}

	/**
	 * Determines if the specified location is a battleground.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
	 * @return true if location is a battleground, otherwise false
	 */
	default boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard) {
		return isBattleground(gameState, location, ignoreForceIconsFromCard, new ModifierCollectorImpl());
	}

	/**
	 * Determines if the specified location is a battleground.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if location is a battleground, otherwise false
	 */
	default boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard, ModifierCollector modifierCollector) {
		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		// Holosites are never battlegrounds
		if (Filters.holosite.accepts(gameState, query(), location))
			return false;

		// Coruscant: Galactic Senate is never a battleground
		if (Filters.Galactic_Senate.accepts(gameState, query(), location))
			return false;

		// Dagobah locations are never battlegrounds
		if (Filters.Dagobah_location.accepts(gameState, query(), location))
			return false;

		// Ahch-To locations are never battlegrounds
		if (Filters.AhchTo_location.accepts(gameState, query(), location))
			return false;

		// Audience Chamber when Bo Shuda is deployed there is never a battleground
		if (Filters.and(Filters.Audience_Chamber, Filters.hasAttached(Filters.Bo_Shuda)).accepts(gameState, query(), location))
			return false;

		// ◇ Desert where a Sandwhirl is present is never a battleground.
		if (Filters.and(Filters.generic, Filters.desert, Filters.hasAttached(Filters.Sandwhirl)).accepts(gameState, query(), location))
			return false;

		// Tatooine: Podrace Arena while either player has a race total > 0. While Expand The Empire is deployed
		// on the Tatooine: Podrace Arena, the adjacent sites are also prohibited from being battlegrounds while
		// either player has a race total > 0
		if (gameState.isDuringPodrace()) {
			if (getHighestRaceTotal(gameState, gameState.getDarkPlayer()) > 0 || getHighestRaceTotal(gameState, gameState.getLightPlayer()) > 0) {
				if (Filters.or(Filters.Podrace_Arena, Filters.adjacentSiteTo(null, Filters.and(Filters.Podrace_Arena,
						Filters.hasAttached(Filters.Expand_The_Empire)))).accepts(gameState, query(), location)) {
					return false;
				}
			}
		}

		// Shielded Hoth locations are never battlegrounds
		if (isLocationUnderHothEnergyShield(gameState, location))
			return false;

		// Check if considered a non-battleground regardless of Force icons. Note that NONBATTLEGROUND modifier overrides BATTLEGROUND modifier so we process it first
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NONBATTLEGROUND, location)) {
			modifierCollector.addModifier(modifier);
			return false;
		}

		// Must have both Light and Dark icons
		if (hasLightAndDarkForceIcons(gameState, location, ignoreForceIconsFromCard))
			return true;

		// Check if considered a battleground regardless of Force icons
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLEGROUND, location)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}

		return retVal;
	}

	/**
	 * Determines if the specified starship can deploy as landed to the specified location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param starship the starship
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @return true if starship may deploy as landed to the location, otherwise false
	 */
	default boolean isLocationStarshipMayDeployToAsLanded(GameState gameState, PhysicalCard location, PhysicalCard starship, DeploymentRestrictionsOption deploymentRestrictionsOption) {
		if (starship.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
			return false;

		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		if (isDockingBay(gameState, location))
			return true;

		if (Filters.launch_bay.accepts(gameState, query(), location))
			return true;

		if (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isAllowDeployLandedToExteriorSites() && Filters.exterior_site.accepts(gameState, query(), location))
			return true;

		if (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isAllowDeployUnpilotedToSystemOrSector() && Filters.or(Filters.system, Filters.sector).accepts(gameState, query(), location))
			return true;

		return grantedToDeployToAsLanded(gameState, starship, location);
	}

	/**
	 * Determines if the specified location is a docking bay.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @return true if location is a docking bay, otherwise false
	 */
	default boolean isDockingBay(GameState gameState, PhysicalCard location) {
		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		return location.getBlueprint().hasKeyword(Keyword.DOCKING_BAY) && !location.isBlownAway();
	}

	/**
	 * Determines if the specified location is the battle location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @return true if location is the battle location, otherwise false
	 */
	default boolean isBattleLocation(GameState gameState, PhysicalCard location) {
		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		PhysicalCard battleLocation = gameState.getBattleLocation();

		return battleLocation != null
				&& battleLocation.getCardId() == location.getCardId();
	}

	/**
	 * Determines if the specified location is the Force drain location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @return true if location is the Force drain location, otherwise false
	 */
	default boolean isForceDrainLocation(GameState gameState, PhysicalCard location) {
		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		PhysicalCard forceDrainLocation = gameState.getForceDrainLocation();

		return forceDrainLocation != null
				&& forceDrainLocation.getCardId() == location.getCardId();
	}

	/**
	 * Determines if the specified locations are related locations.
	 *
	 * @param gameState the game state
	 * @param location1 a location
	 * @param location2 a location
	 * @return true if the locations are related locations, otherwise false
	 */
	default boolean isRelatedLocations(GameState gameState, PhysicalCard location1, PhysicalCard location2) {
		if (location1.getBlueprint().getCardCategory() != CardCategory.LOCATION
				|| location2.getBlueprint().getCardCategory() != CardCategory.LOCATION
				|| location1.getZone() != Zone.LOCATIONS || location2.getZone() != Zone.LOCATIONS
				|| location1.getCardId() == location2.getCardId()) {
			return false;
		}

		// Two asteroid sectors are related if they are orbiting the same system
		if (location1.getBlueprint().hasKeyword(Keyword.ASTEROID) && location2.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
			String relatedSystemName = location1.getSystemOrbited();
			return (relatedSystemName != null && relatedSystemName.equals(location2.getSystemOrbited()));
		}

		// System and asteroid sector are related if the sector is orbiting the system
		if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && location2.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
			return location1.getTitle().equals(location2.getSystemOrbited());
		}
		if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM && location1.getBlueprint().hasKeyword(Keyword.ASTEROID)) {
			return location2.getTitle().equals(location1.getSystemOrbited());
		}

		// Two sites or sectors are related if they are part of the same system
		if ((location1.getBlueprint().getCardSubtype() == CardSubtype.SITE || location1.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)
				&& (location2.getBlueprint().getCardSubtype() == CardSubtype.SITE || location2.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
			String relatedSystemName = location1.getPartOfSystem();
			if (relatedSystemName != null && relatedSystemName.equals(location2.getPartOfSystem())) {
				return true;
			}
		}

		// System and site or sector are related if sector is part of the system
		if (location1.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM &&
				(location2.getBlueprint().getCardSubtype() == CardSubtype.SITE || location2.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
			// Check if site or sector belongs to same system
			return (location2.getPartOfSystem() != null && location2.getPartOfSystem().equals(location1.getTitle()));
		}
		if (location2.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM &&
				(location1.getBlueprint().getCardSubtype() == CardSubtype.SITE || location1.getBlueprint().getCardSubtype() == CardSubtype.SECTOR)) {
			// Check if site or sector belongs to same system
			return (location1.getPartOfSystem() != null && location1.getPartOfSystem().equals(location2.getTitle()));
		}

		// Check if starship and vehicle sites are related
		if ((location1.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location1.getBlueprint().hasIcon(Icon.VEHICLE_SITE))
				&& location2.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location2.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {

			// Check if persona matches
			if (location1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
				return Filters.siteOfStarshipOrVehicle(location1.getBlueprint().getRelatedStarshipOrVehiclePersona(), false).accepts(gameState, query(), location2);
			}

			// Check if starship/vehicle card matches
			if (location1.getRelatedStarshipOrVehicle() != null) {
				return Filters.siteOfStarshipOrVehicle(location1.getRelatedStarshipOrVehicle()).accepts(gameState, query(), location2);
			}

			return false;
		}

		// If site is a vehicle site, then it is related to sites on the system it is at a site of
		if (location1.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && location2.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
			PhysicalCard vehicle = location1.getRelatedStarshipOrVehicle();
			if (vehicle == null && location1.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
				vehicle = Filters.findFirstFromAllOnTable(gameState.getGame(), location1.getBlueprint().getRelatedStarshipOrVehiclePersona());
			}
			if (vehicle != null) {
				PhysicalCard locationVehicleIsAt = getLocationThatCardIsAt(gameState, vehicle);
				if (locationVehicleIsAt != null && locationVehicleIsAt.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
					String partOfSystem = locationVehicleIsAt.getPartOfSystem();

					if (partOfSystem != null && partOfSystem.equals(location2.getPartOfSystem())) {
						return true;
					}
				}
			}

			return false;
		}
		if (location2.getBlueprint().hasIcon(Icon.VEHICLE_SITE) && location1.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
			PhysicalCard vehicle = location2.getRelatedStarshipOrVehicle();
			if (vehicle == null && location2.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
				vehicle = Filters.findFirstFromAllOnTable(gameState.getGame(), location2.getBlueprint().getRelatedStarshipOrVehiclePersona());
			}
			PhysicalCard locationVehicleIsAt = getLocationThatCardIsAt(gameState, vehicle);
			if (locationVehicleIsAt != null && locationVehicleIsAt.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
				String partOfSystem = locationVehicleIsAt.getPartOfSystem();

				if (partOfSystem != null && partOfSystem.equals(location1.getPartOfSystem())) {
					return true;
				}
			}

			return false;
		}

		// Big One and its Asteroid Cave (Space Slug Belly) are related
		if (Filters.Big_One.accepts(gameState, query(), location1) && Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, query(), location2)) {
			String relatedSystemName = location1.getSystemOrbited();
			return (relatedSystemName != null && relatedSystemName.equals(location2.getSystemOrbited()));
		}
		if (Filters.Big_One.accepts(gameState, query(), location2) && Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, query(), location1)) {
			String relatedSystemName = location2.getSystemOrbited();
			return (relatedSystemName != null && relatedSystemName.equals(location1.getSystemOrbited()));
		}

		return false;
	}

	/**
	 * Determines if the specified location is a starship or vehicle site of the specified starship or vehicle.
	 *
	 * @param gameState the game state
	 * @param starshipOrVehicle a starship or vehicle
	 * @param location a location
	 * @return true if the locations are related locations, otherwise false
	 */
	default boolean isRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard starshipOrVehicle, PhysicalCard location) {
		if (!Filters.or(Filters.starship_site, Filters.vehicle_site).accepts(gameState, query(), location))
			return false;

		if (location.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
				&& starshipOrVehicle.getBlueprint().hasPersona(location.getBlueprint().getRelatedStarshipOrVehiclePersona()))
			return true;

		if (location.getRelatedStarshipOrVehicle() != null
				&& location.getRelatedStarshipOrVehicle().getCardId() == starshipOrVehicle.getCardId())
			return true;

		return false;
	}

	/**
	 * Determines if the specified card ignores location deployment restrictions when deploying to the specified target.
	 * @param gameState the game state
	 * @param card the card
	 * @param target the target card
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @param skipForceIconsOrPresenceCheck the skip checking sufficent Force icons or presence
	 * @return true if card ignores location deployment restrictions when deploying to target
	 */
	default boolean ignoresLocationDeploymentRestrictions(GameState gameState, PhysicalCard card, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean skipForceIconsOrPresenceCheck) {
		if (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions()) {
			return true;
		}

		PhysicalCard location = getLocationHere(gameState, target);
		if (location != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_WHEN_DEPLOYING_TO_LOCATION, card)) {
				if (!skipForceIconsOrPresenceCheck || !modifier.isExceptForceIconOrPresenceRequirement()) {
					if (modifier.isAffectedTarget(gameState, query(), location)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	default boolean onlyDeploysAdjacentToSpecificLocations(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardSubtype() != CardSubtype.SITE && card.getBlueprint().getCardSubtype() != CardSubtype.SECTOR)
			return false;

		if (!getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_ADJACENT_TO_SPECIFIC_LOCATION, card).isEmpty()) {
			for (Modifier modifier: getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_ADJACENT_TO_SPECIFIC_LOCATION, card)) {
				DeploysAdjacentToLocationModifier m = (DeploysAdjacentToLocationModifier)modifier;

				if (Filters.canSpot(gameState.getGame(), card, m.getAdjacentToFilter()))
					return true;

				// can't spot a valid location and it doesn't say "if possible"
				if (!m.onlyIfPossible())
					return true;
			}
		}

		return false;
	}

	default Filter getFilterForOnlyDeploysAdjacentToSpecificLocations(GameState gameState, PhysicalCard card) {
		Filter filter = Filters.any;
		if (!getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_ADJACENT_TO_SPECIFIC_LOCATION, card).isEmpty()) {
			for (Modifier modifier: getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_ADJACENT_TO_SPECIFIC_LOCATION, card)) {
				DeploysAdjacentToLocationModifier m = (DeploysAdjacentToLocationModifier)modifier;

				if (Filters.canSpot(gameState.getGame(), card, m.getAdjacentToFilter()) || !m.onlyIfPossible()) {
					filter = Filters.and(filter, m.getAdjacentToFilter());
				}
			}
		}

		return filter;
	}
}
