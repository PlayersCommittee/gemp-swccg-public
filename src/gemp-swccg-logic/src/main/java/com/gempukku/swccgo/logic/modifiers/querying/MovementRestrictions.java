package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.TIEsMayLandAtExteriorSiteModifier;

import java.util.ArrayList;
import java.util.List;

public interface MovementRestrictions extends BaseQuery, Locations {

	default boolean tieAllowedToLand(GameState gameState, PhysicalCard card, PhysicalCard toLocation) {
		if (!Filters.exterior_site.accepts(gameState, query(), toLocation))
			return false;
		if (Filters.docking_bay.accepts(gameState, query(), toLocation))
			return true;
		for (Modifier modifier: getModifiersAffectingCard(gameState, ModifierType.TIE_MAY_LAND_AT_EXTERIOR_SITE, card)) {
			if (((TIEsMayLandAtExteriorSiteModifier)modifier).allowedToLandAt(gameState, query(), toLocation))
				return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMove(GameState gameState, PhysicalCard card) {
		if (card.isMissing() || card.isConcealed() || card.isCrashed())
			return true;

		if (card.getAttachedTo() != null
				&& Filters.aboard(Filters.makingBombingRun).accepts(gameState, query(), card)) {
			return true;
		}

		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE, card).isEmpty());
	}

	/**
	 * Determines if the specified card is prohibited from moving except using landspeed.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is prohibited from moving except using landspeed, otherwise false
	 */
	default boolean mayOnlyMoveUsingLandspeed(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_ONLY_MOVE_USING_LANDSPEED, card).isEmpty());
	}

	/**
	 * Determines if the specified card is prohibited from moving from site to site using hyperspeed.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromSite the site to move from
	 * @param toSite the site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationUsingLandspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
		List<PhysicalCard> sitesBetween = getSitesBetween(gameState, fromSite, toSite);
		if (sitesBetween == null) {
			return true;
		}

		List<PhysicalCard> sitesAlongPath = new ArrayList<PhysicalCard>();
		sitesAlongPath.add(fromSite);
		sitesAlongPath.addAll(sitesBetween);
		sitesAlongPath.add(toSite);

		for (int i=0; i<sitesAlongPath.size()-1; ++i) {
			PhysicalCard curFromSite = sitesAlongPath.get(i);
			PhysicalCard curToSite = sitesAlongPath.get(i+1);

			// Check if not a valid location for card to move to
			if (!card.getBlueprint().getValidMoveTargetFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, query(), curToSite)) {
				return true;
			}

			// Check if may not move from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, curFromSite)) {
				if (modifier.prohibitedFromMovingFromLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if may not move from location using landspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_USING_LANDSPEED, curFromSite)) {
				if (modifier.prohibitedFromMovingFromLocation(gameState, query(), card)) {
					return true;
				}
			}

			if (asReact) {
				// Check if player may not 'react' from the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, curFromSite)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}

			// Check if may not move from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, curFromSite)) {
				if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, query(), card, curToSite)) {
					return true;
				}
			}

			// Check if may not move from location to location using landspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, curFromSite)) {
				if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, query(), card, curToSite)) {
					return true;
				}
			}

			// Check if may not move away from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
				if (modifier.prohibitedFromMovingAwayFromLocation(gameState, query(), curFromSite, curToSite)) {
					return true;
				}
			}

			// Check if may not move to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, curToSite)) {
				if (modifier.prohibitedFromMovingToLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if may not move to location using landspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION_USING_LANDSPEED, curToSite)) {
				if (modifier.prohibitedFromMovingToLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if card has (limit 1 per location)
			if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, curToSite)
					||isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(gameState, card, curToSite)) {
				return true;
			}

			if (asReact) {
				// Check if player may not 'react' to the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, curToSite)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving using hyperspeed.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is prohibited from moving using hyperspeed, otherwise false
	 */
	default boolean mayNotMoveUsingHyperspeed(GameState gameState, PhysicalCard card) {
		if (mayNotMove(gameState, card))
			return true;

		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_USING_HYPERSPEED, card).isEmpty());
	}

	/**
	 * Determine if the specified card is prohibited from moving to or from specified locations.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	private boolean mayNotMoveFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {

		if (fromLocation != null) {
			// Check if may not move from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, fromLocation)) {
				if (modifier.prohibitedFromMovingFromLocation(gameState, query(), card)) {
					return true;
				}
			}

			if (asReact) {
				// Check if player may not 'react' from the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, fromLocation)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if may not move from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, fromLocation)) {
				if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, query(), card, toLocation)) {
					return true;
				}
			}

			// Check if may not move away from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
				if (modifier.prohibitedFromMovingAwayFromLocation(gameState, query(), fromLocation, toLocation)) {
					return true;
				}
			}
		}

		if (toLocation != null) {
			// Check if may not move to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, toLocation)) {
				if (modifier.prohibitedFromMovingToLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if card has (limit 1 per location)
			if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)
					||isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)) {
				return true;
			}

			if (asReact) {
				// Check if player may not 'react' to the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, toLocation)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location using hyperspeed.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact))
			return true;

		if (fromLocation != null) {
			// Check if may not move from location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_USING_HYPERSPEED, fromLocation)) {
				if (modifier.prohibitedFromMovingFromLocation(gameState, query(), card)) {
					return true;
				}
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if may not move from location to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, fromLocation)) {
				if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, query(), card, toLocation)) {
					return true;
				}
			}
		}

		if (toLocation != null) {
			// Check if may not move to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION_USING_HYPERSPEED, toLocation)) {
				if (modifier.prohibitedFromMovingToLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if card has (limit 1 per location)
			if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)
					||isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location without using hyperspeed.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationWithoutUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact);
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location using sector movement.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationUsingSectorMovement(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
		List<PhysicalCard> sectorsBetween = getSectorsBetween(gameState, fromLocation, toLocation);

		List<PhysicalCard> locationsAlongPath = new ArrayList<PhysicalCard>();
		locationsAlongPath.add(fromLocation);
		locationsAlongPath.addAll(sectorsBetween);
		locationsAlongPath.add(toLocation);

		for (int i=0; i<locationsAlongPath.size()-1; ++i) {
			PhysicalCard curFromLocation = locationsAlongPath.get(i);
			PhysicalCard curToLocation = locationsAlongPath.get(i+1);

			// Check if not a valid location for card to move to
			if (!card.getBlueprint().getValidMoveTargetFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, query(), curToLocation)) {
				return true;
			}

			// Check if may not move from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION, curFromLocation)) {
				if (modifier.prohibitedFromMovingFromLocation(gameState, query(), card)) {
					return true;
				}
			}

			if (asReact) {
				// Check if player may not 'react' from the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_FROM_LOCATION, curFromLocation)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}

			// Check if may not move from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_FROM_LOCATION_TO_LOCATION, curFromLocation)) {
				if (modifier.prohibitedFromMovingFromLocationToLocation(gameState, query(), card, curToLocation)) {
					return true;
				}
			}

			// Check if may not move away from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_AWAY_FROM_LOCATION, card)) {
				if (modifier.prohibitedFromMovingAwayFromLocation(gameState, query(), curFromLocation, curToLocation)) {
					return true;
				}
			}

			// Check if may not move to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MOVE_TO_LOCATION, curToLocation)) {
				if (modifier.prohibitedFromMovingToLocation(gameState, query(), card)) {
					return true;
				}
			}

			// Check if card has (limit 1 per location)
			if (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, card, curToLocation)
					||isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(gameState, card, toLocation)) {
				return true;
			}

			if (asReact) {
				// Check if player may not 'react' to the location
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REACT_TO_LOCATION, curToLocation)) {
					if (modifier.isForPlayer(card.getOwner())) {
						return true;
					}
				}
			}
		}

		// If during attempt to 'blow away' Death Star II, then starfighters may only move from a Death Star II sector toward the Death Star II system
		if (gameState.getEpicEventState() != null && gameState.getEpicEventState().getEpicEventType() == EpicEventState.Type.ATTEMPT_TO_BLOW_AWAY_DEATH_STAR_II) {
			PhysicalCard deathStarII = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Death_Star_II_system);
			if (deathStarII == null) {
				return true;
			}
			if (!Filters.starfighter.accepts(gameState, query(), card)
					|| !Filters.Death_Star_II_sector.accepts(gameState, query(), fromLocation)
					|| !Filters.toward(card, deathStarII).accepts(gameState, query(), toLocation)) {
				return true;
			}
		}
		// Dark side starfighters may not move to a Death Star II sector that is not toward the Death Star II system if
		// there are no Light side starfighters at Death Star II sectors.
		else if (card.getOwner().equals(gameState.getDarkPlayer())
				&& Filters.starfighter.accepts(gameState, query(), card)
				&& Filters.Death_Star_II_sector.accepts(gameState, query(), toLocation)) {

			// Check if no Light side starfighters at Death Star II sectors.
			if (!Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.owner(gameState.getLightPlayer()), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)))) {
				PhysicalCard deathStarII = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Death_Star_II_system);
				if (deathStarII == null) {
					return true;
				}

				// Check if not moving toward Death Star II system (or not during player's move phase)
				if (!gameState.getCurrentPlayerId().equals(card.getOwner())
						|| gameState.getCurrentPhase() != Phase.MOVE
						|| !Filters.toward(card, deathStarII).accepts(gameState, query(), toLocation)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location using location text.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationUsingLocationText(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location using docking bay transit.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the docking bay to move from
	 * @param toLocation the docking bay to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationUsingDockingBayTransit(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
			return true;
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not transit through.
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& (isLocationUnderHothEnergyShield(gameState, fromLocation)
				|| isLocationUnderHothEnergyShield(gameState, toLocation))) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from landing from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotLandFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact)) {
			return true;
		}

		// Check if TIE, which can only land at docking bay (or starship site that may be landed at instead of embarking on related starship).
		if (Filters.TIE.accepts(gameState, query(), card)
				&& !Filters.docking_bay.accepts(gameState, query(), toLocation)
				&& !Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(card.getOwner()).accepts(gameState, query(), card)
				&& !tieAllowedToLand(gameState, card, toLocation)) {
			return true;
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not land under.
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& isLocationUnderHothEnergyShield(gameState, toLocation)) {
			return true;
		}

		// Check for "Cave Rules". Cards may not land to Space Slug Belly if Space Slug mouth is closed.
		if (Filters.Space_Slug_Belly.accepts(gameState, query(), toLocation)) {
			PhysicalCard spaceSlug = Filters.findFirstFromAllOnTable(gameState.getGame(), Filters.and(Filters.Space_Slug, Filters.at(Filters.relatedBigOne(toLocation))));
			if (spaceSlug != null && spaceSlug.isMouthClosed()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from taking off from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotTakeOffFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, asReact)) {
			return true;
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not take off under
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& isLocationUnderHothEnergyShield(gameState, fromLocation)) {
			return true;
		}

		// Check for "Cave Rules". Cards may not take off from Space Slug Belly if Space Slug mouth is closed.
		if (Filters.Space_Slug_Belly.accepts(gameState, query(), fromLocation)) {
			PhysicalCard spaceSlug = Filters.findFirstFromAllOnTable(gameState.getGame(), Filters.and(Filters.Space_Slug, Filters.at(Filters.relatedBigOne(fromLocation))));
			if (spaceSlug != null && spaceSlug.isMouthClosed()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location to start a Bombing Run.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationToStartBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
			return true;
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not move to make a Bombing Run
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& isLocationUnderHothEnergyShield(gameState, toLocation)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location to end a Bombing Run.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationToEndBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
			return true;
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not move from to end a Bombing Run
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& isLocationUnderHothEnergyShield(gameState, fromLocation)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location at start of an Attack Run.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationAtStartOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
	}

	/**
	 * Determines if the specified card is prohibited from moving from location to location at end of an Attack Run.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromLocationToLocationAtEndOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
	}

	/**
	 * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromSite the starship/vehicle site to move from
	 * @param toStarshipOrVehicle the starship/vehicle to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromSiteToRelatedStarshipOrVehicle(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toStarshipOrVehicle) {
		PhysicalCard locationOfStarshipOrVehicle = getLocationThatCardIsAt(gameState, toStarshipOrVehicle);
		return mayNotMoveFromLocationToLocation(gameState, card, fromSite, locationOfStarshipOrVehicle, false);
	}

	/**
	 * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromStarshipOrVehicle the starship/vehicle to move from
	 * @param toSite the starship/vehicle site to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotMoveFromStarshipOrVehicleToRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromStarshipOrVehicle, PhysicalCard toSite) {
		PhysicalCard locationOfStarshipOrVehicle = getLocationThatCardIsAt(gameState, fromStarshipOrVehicle);
		return mayNotMoveFromLocationToLocation(gameState, card, locationOfStarshipOrVehicle, toSite, false);
	}

	/**
	 * Determines if the specified card is prohibited from entering the starship/vehicle site from a site.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromSite the site to move from
	 * @param toSite the starship/vehicle site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotEnterStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromSite, toSite, asReact);
	}

	/**
	 * Determines if the specified card is prohibited from exit the starship/vehicle site to a site.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromSite the starship/vehicle site to move from
	 * @param toSite the site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotExitStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromSite, toSite, asReact);
	}

	/**
	 * Determines if the specified card is prohibited from shuttling from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
	 * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotShuttleFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
			return true;
		}

		// Check for "Dagobah" or "Ahch-To", which neither player may shuttle at
		if (Filters.Dagobah_location.accepts(gameState, query(), fromLocation) || Filters.Dagobah_location.accepts(gameState, query(), toLocation)
				|| Filters.AhchTo_location.accepts(gameState, query(), fromLocation) || Filters.AhchTo_location.accepts(gameState, query(), toLocation))
			return true;

		// Check for "Hoth Energy Shield", which Dark side cards may not shuttle under
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& (isLocationUnderHothEnergyShield(gameState, fromLocation)
				|| isLocationUnderHothEnergyShield(gameState, toLocation))) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is prohibited from embarking from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotEmbarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
	}

	/**
	 * Determines if the specified card is prohibited from disembarking from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotDisembarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation) {
		return mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false);
	}

	/**
	 * Determines if the specified card is prohibited from relocating from location to location.
	 * @param gameState the game state
	 * @param card the card
	 * @param fromLocation the location to relocate from
	 * @param toLocation the location to relocate to
	 * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
	 * @param allowAhchTo true if relocating from/to Ahch-To locations is allowed, otherwise false
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayNotRelocateFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean allowDagobah, boolean allowAhchTo) {
		if (mayNotMoveFromLocationToLocation(gameState, card, fromLocation, toLocation, false)) {
			return true;
		}

		// Check for Dagobah or Ahch-To, which neither player may relocate from/to unless explicitly allowed
		if (!allowDagobah) {
			if (Filters.Dagobah_location.accepts(gameState, query(), fromLocation) || Filters.Dagobah_location.accepts(gameState, query(), toLocation))
				return true;
		}

		if (!allowAhchTo) {
			if (Filters.AhchTo_location.accepts(gameState, query(), fromLocation) || Filters.AhchTo_location.accepts(gameState, query(), toLocation)) {
				return true;
			}
		}

		// Check for "Hoth Energy Shield", which Dark side cards may not relocate under
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& (isLocationUnderHothEnergyShield(gameState, fromLocation)
				|| isLocationUnderHothEnergyShield(gameState, toLocation))) {
			return true;
		}

		// Check for Death Star II Sector which neither player may relocate from/to
		if (Filters.Death_Star_II_sector.accepts(gameState, query(), fromLocation) || Filters.Death_Star_II_sector.accepts(gameState, query(), toLocation)) {
			return true;
		}

		// Check if may only move using landspeed
		if (mayOnlyMoveUsingLandspeed(gameState, card)) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the specified card is moved by opponent instead of owner.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is moved by opponent instead of owner
	 */
	default boolean isMovedOnlyByOpponent(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MOVED_ONLY_BY_OPPONENT, card).isEmpty();
	}

	/**
	 * Determines if the specified card moves using landspeed only during deploy phase.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is moved by opponent instead of owner
	 */
	default boolean isMovesUsingLandspeedOnlyDuringDeployPhase(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MOVES_USING_LANDSPEED_ONLY_DURING_DEPLOY_PHASE, card).isEmpty();
	}

	/**
	 * Determines if the specified card is a location the player may shuttle, transfer, land, and take off at for
	 * free instead of the related starship. (Example: Star Destroyer: Launch Bay)
	 * @param gameState the game state
	 * @param playerId the player
	 * @param location the card
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(GameState gameState, String playerId, PhysicalCard location) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_LOCATION_TO_SHUTTLE_TRANSFER_LAND_OR_TAKE_OFF_FOR_FREE_INSTEAD_OF_RELATED_STARSHIP, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card can shuttle directly from the specified location to the other specified location.
	 * @param gameState the game state
	 * @param cardToMove the card to move
	 * @param fromLocation the location to shuttle from
	 * @param toLocation the location to shuttle to
	 * @return true if card is prohibited from moving, otherwise false
	 */
	default boolean mayShuttleDirectlyFromLocationToLocation(GameState gameState, PhysicalCard cardToMove, PhysicalCard fromLocation, PhysicalCard toLocation) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_SHUTTLE_DIRECTLY_FROM_LOCATION_TO_LOCATION, cardToMove)) {
			if (modifier.isGrantedToShuttleFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
				return true;
			}
		}
		return false;
	}
}
