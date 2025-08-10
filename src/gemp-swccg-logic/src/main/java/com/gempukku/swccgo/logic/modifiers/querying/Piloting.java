package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ChangeTractorBeamDestinationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.*;

public interface Piloting extends BaseQuery, Icons, Locations, Prohibited {
	/**
	 * Gets the permanent pilots and astromechs aboard the card, not including any that are removed/suspended.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the permanent pilots and astromechs
	 */
	default List<SwccgBuiltInCardBlueprint> getPermanentsAboard(GameState gameState, PhysicalCard physicalCard) {
		List<SwccgBuiltInCardBlueprint> permanentsAboard = new ArrayList<SwccgBuiltInCardBlueprint>();
		permanentsAboard.addAll(getPermanentPilotsAboard(gameState, physicalCard));
		permanentsAboard.addAll(getPermanentAstromechsAboard(gameState, physicalCard));
		return permanentsAboard;
	}

	/**
	 * Gets the permanent pilots aboard the card, not including any that are removed/suspended.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the permanent pilots
	 */
	default List<SwccgBuiltInCardBlueprint> getPermanentPilotsAboard(GameState gameState, PhysicalCard physicalCard) {
		List<SwccgBuiltInCardBlueprint> permanentsAboard = physicalCard.getBlueprint().getPermanentsAboard(physicalCard);
		if (permanentsAboard == null || permanentPilotsSuspended(gameState, physicalCard))
			return Collections.emptyList();

		List<SwccgBuiltInCardBlueprint> permanentPilots = new ArrayList<SwccgBuiltInCardBlueprint>();
		for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
			if (permanentAboard.isPilot()) {
				permanentPilots.add(permanentAboard);
			}
		}

		return permanentPilots;
	}

	/**
	 * Gets the permanent astromechs aboard the card, not including any that are removed/suspended.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the permanent astromechs
	 */
	default List<SwccgBuiltInCardBlueprint> getPermanentAstromechsAboard(GameState gameState, PhysicalCard physicalCard) {
		List<SwccgBuiltInCardBlueprint> permanentsAboard = physicalCard.getBlueprint().getPermanentsAboard(physicalCard);
		if (permanentsAboard == null || permanentAstromechsSuspended(gameState, physicalCard))
			return Collections.emptyList();

		List<SwccgBuiltInCardBlueprint> permanentAstromechs = new ArrayList<SwccgBuiltInCardBlueprint>();
		for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
			if (permanentAboard.isAstromech()) {
				permanentAstromechs.add(permanentAboard);
			}
		}

		return permanentAstromechs;
	}

	/**
	 * Gets the modifiers from the specified permanent built-in.
	 * @param gameState the game state
	 * @param permanentBuiltIn the permanent built-in
	 * @return the modifiers
	 */
	default List<Modifier> getModifiersFromPermanentBuiltIn(GameState gameState, SwccgBuiltInCardBlueprint permanentBuiltIn) {
		PhysicalCard card = permanentBuiltIn.getPhysicalCard(game());
		return permanentBuiltIn.getGameTextModifiers(card);
	}

	default boolean permanentPilotsSuspended(GameState gameState, PhysicalCard physicalCard) {
		return (!getModifiersAffectingCard(gameState, ModifierType.SUSPEND_PERMANENT_PILOT, physicalCard).isEmpty());
	}

	default boolean permanentAstromechsSuspended(GameState gameState, PhysicalCard physicalCard) {
		return (!getModifiersAffectingCard(gameState, ModifierType.SUSPEND_PERMANENT_ASTROMECH, physicalCard).isEmpty());
	}

	default int getPilotCapacity(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory()!= CardCategory.STARSHIP && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
			return 0;

		int result = physicalCard.getBlueprint().getPilotCapacity();
		if (result==Integer.MAX_VALUE)
			return result;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PILOT_CAPACITY, physicalCard)) {
			result += modifier.getPilotCapacityModifier(gameState, query(), physicalCard);
		}
		return Math.max(0, result);
	}

	default int getAstromechCapacity(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP && physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
			return 0;

		int result = physicalCard.getBlueprint().getAstromechCapacity();
		if (result==Integer.MAX_VALUE)
			return result;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ASTROMECH_CAPACITY, physicalCard)) {
			result += modifier.getAstromechCapacityModifier(gameState, query(), physicalCard);
		}
		return Math.max(0, result);
	}

	default boolean canCarryPassengerAsIfCreatureVehicle(GameState gameState, PhysicalCard physicalCard, PhysicalCard passenger) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_CARRY_PASSENGER_AS_IF_CREATURE_VEHICLE, physicalCard)) {
			if (passenger == null || modifier.isAffectedTarget(gameState, query(), passenger)) {
				return true;
			}
		}
		return false;
	}

	default boolean hasCapacityForCardsToRelocate(GameState gameState, PhysicalCard physicalCard, Collection<PhysicalCard> cardsToRelocate) {
		// Only allow this for capital starships
		if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP && physicalCard.getBlueprint().getCardSubtype()== CardSubtype.CAPITAL)
			return false;

		// Get capacities available
		int availablePilotSlots = gameState.getAvailablePilotCapacity(query(), physicalCard, null);
		int availablePassengerSlots = gameState.getAvailablePassengerCapacity(query(), physicalCard, null);
		int availableAstromechOnlySlots = gameState.getAvailablePassengerCapacityForAstromech(query(), physicalCard, null) - availablePassengerSlots;

		// Check if characters could fit in the available slots (fill passenger slots last since any character)
		for (PhysicalCard cardToRelocate : cardsToRelocate) {
			if (Filters.astromech_droid.accepts(gameState, query(), cardToRelocate) && availableAstromechOnlySlots > 0)
				availableAstromechOnlySlots--;
			else if (hasIcon(gameState, cardToRelocate, Icon.PILOT) && availablePilotSlots>0)
				availablePilotSlots--;
			else if (availablePassengerSlots>0)
				availablePassengerSlots--;
			else
				return false;
		}

		return true;
	}

	/**
	 * Determines if the card is piloted.
	 * @param gameState the game state
	 * @param card the card
	 * @param forStarshipTakeoff true if checking if starship is piloted for takeoff, otherwise false
	 * @return true if piloted, otherwise false
	 */
	default boolean isPiloted(GameState gameState, PhysicalCard card, boolean forStarshipTakeoff) {
		SwccgCardBlueprint blueprint = card.getBlueprint();

		// Crashed vehicle is not piloted
		if (card.isCrashed())
			return false;

		// Pilots of landed vehicles are considered passengers.
		if (blueprint.getCardCategory() == CardCategory.VEHICLE && card.isInCargoHoldAsVehicle())
			return false;

		// Creature vehicles and Lift Tubes are piloted
		if (blueprint.getCardSubtype()==CardSubtype.CREATURE || card.getTitle().equals(Title.Lift_Tube))
			return true;

		// Landed starships are not piloted (except for takeoff)
		if (blueprint.getCardCategory() == CardCategory.STARSHIP
				&& !card.isMakingBombingRun()
				&& ((card.getAttachedTo() != null && (!forStarshipTakeoff || !card.isConcealed()))
				|| (card.getAtLocation() != null && !forStarshipTakeoff && Filters.and(Filters.site, Filters.not(Filters.Death_Star_Trench)).accepts(gameState, query(), card.getAtLocation()))))
			return false;

		// Make sure at least one active pilot fits the valid pilot filter (see "Mist Hunter" from Dagobah for example)
		for (PhysicalCard pilotCard : gameState.getPilotCardsAboard(query(), card, true)) {
			if (gameState.isCardInPlayActive(pilotCard, false, false, false, forStarshipTakeoff, false, false, false, false)
					&& card.getBlueprint().getValidPilotFilter(card.getOwner(), gameState.getGame(), card, false).accepts(gameState, gameState.getGame().getModifiersQuerying(), pilotCard)) {
				return true;
			}
		}

		// Check for permanent pilots
		int permanentPilotCount = getIconCount(gameState, card, Icon.PILOT);
		if (permanentPilotCount > 0) {
			if (Filters.squadron.accepts(gameState, query(), card)) {
				return permanentPilotCount >= blueprint.getModelTypes().size();
			}
			else {
				return true;
			}
		}

		return false;
	}

	default boolean isLanded(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
				&& physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE) {
			return false;
		}

		if (physicalCard.isMakingBombingRun())
			return false;

		if (physicalCard.isInCargoHoldAsVehicle() || physicalCard.isInCargoHoldAsStarfighterOrTIE())
			return true;

		if (physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {
			if (physicalCard.getAtLocation() != null) {
				return Filters.and(Filters.site, Filters.not(Filters.Death_Star_Trench)).accepts(gameState, query(), physicalCard.getAtLocation());
			}
		}

		return false;
	}

	default boolean cannotAddToPowerOfPilotedBy(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.DOES_NOT_ADD_TO_POWER_WHEN_PILOTING, physicalCard).isEmpty();
	}

	default boolean hasAstromech(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
			return false;

		List<PhysicalCard> aboardCards = gameState.getPassengerCardsAboard(physicalCard);
		for (PhysicalCard aboardCard : aboardCards) {
			if (Filters.astromech_droid.acceptsCount(gameState, query(), aboardCard) >= 1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the starship has an astromech or nav computer.
	 * @param gameState the game state
	 * @param card the starship
	 * @return true or false
	 */
	default boolean hasAstromechOrNavComputer(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
			return false;

		if (hasIcon(gameState, card, Icon.NAV_COMPUTER))
			return true;

		return hasAstromech(gameState, card);
	}

	default boolean isAboard(GameState gameState, PhysicalCard physicalCard, PhysicalCard starshipOrVehicle, boolean includeAboardCargoOf, boolean includeRelatedSites) {
		if (starshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.STARSHIP && starshipOrVehicle.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
			return false;

		// A character, starship, vehicle, weapon or device is "aboard" (or "on") a starship or vehicle if it is:
		// (1) Present at any site related to that starship or vehicle.
		// (2) At the bridge, cockpit or cargo bay of that starship or vehicle.
		CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
		if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE && cardCategory!=CardCategory.WEAPON && cardCategory!=CardCategory.DEVICE)
			return false;


		List<PhysicalCard> physicalCardList = gameState.getAboardCards(starshipOrVehicle, includeAboardCargoOf);
		if (!Filters.filter(physicalCardList, gameState.getGame(), physicalCard).isEmpty())
			return true;

		if (!includeRelatedSites)
			return false;

		PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
		if (location==null)
			return false;

		if (location.getBlueprint().hasIcon(Icon.STARSHIP_SITE) || location.getBlueprint().hasIcon(Icon.VEHICLE_SITE)) {
			if (location.getBlueprint().getRelatedStarshipOrVehiclePersona() != null
					&& starshipOrVehicle.getBlueprint().hasPersona(location.getBlueprint().getRelatedStarshipOrVehiclePersona())) {
				return true;
			}

			if (location.getRelatedStarshipOrVehicle() != null
					&& starshipOrVehicle.getCardId() == location.getRelatedStarshipOrVehicle().getCardId()) {
				return true;
			}
		}

		return false;
	}

	default boolean hasPermanentPilotAlone(GameState gameState, PhysicalCard physicalCard) {
		// Your character or permanent pilot is alone at a location if it is active
		// and you have no other cards at that location that are active characters
		// or active cards with ability. Combo Cards (such as Artoo & Threepio or Tonnika Sisters),
		// TODO: Handle the rest...
		// and a permanent pilot of a starship or vehicle that has multiple permanent pilots
		// (such as Executor or a TIE Squadron), are not considered to be alone.
		// Your starship or vehicle is alone at a location if the only active characters,
		// vehicles and starships you have at that location are aboard that starship or vehicle.
		if ((physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE
				&& physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP)
				|| physicalCard.getBlueprint().isComboCard())
			return false;

		if (getPermanentsAboard(gameState, physicalCard).size() > 1)
			return false;

		PhysicalCard location = getLocationThatCardIsAt(gameState, physicalCard);
		if (location==null)
			return false;

		if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.not(physicalCard), Filters.at(location),
				Filters.owner(physicalCard.getOwner()), Filters.or(Filters.character, Filters.abilityMoreThan(0, true)))))
			return false;

		return true;
	}

	default boolean hasPermanentPilot(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP
				&& physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
			return false;

		return !getPermanentPilotsAboard(gameState, physicalCard).isEmpty();
	}

	default boolean hasPermanentAstromech(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory()!=CardCategory.STARSHIP
				&& physicalCard.getBlueprint().getCardCategory()!=CardCategory.VEHICLE)
			return false;

		return !getPermanentAstromechsAboard(gameState, physicalCard).isEmpty();
	}

	default PhysicalCard getIsPilotOf(GameState gameState, PhysicalCard card) {
		PhysicalCard attachedTo = card.getAttachedTo();
		if (attachedTo != null
				&& card.isPilotOf()
				&& Filters.not(Filters.transport_vehicle).accepts(gameState, query(), attachedTo)
				&& !cannotDriveOrPilot(gameState, card)) {
			return attachedTo;
		}
		return null;
	}

	default PhysicalCard getIsDriverOf(GameState gameState, PhysicalCard card) {
		PhysicalCard attachedTo = card.getAttachedTo();
		if (attachedTo != null
				&& card.isPilotOf()
				&& Filters.transport_vehicle.accepts(gameState, query(), attachedTo)
				&& !cannotDriveOrPilot(gameState, card)) {
			return attachedTo;
		}
		return null;
	}

	default Collection<PhysicalCard> getDestinationForCapturedStarships(GameState gameState, PhysicalCard tractorBeam) {
		Collection<PhysicalCard> result = new LinkedList<PhysicalCard>();

		for(Modifier m:getModifiersAffectingCard(gameState, ModifierType.TRACTOR_BEAM_DESTINATION, tractorBeam)) {
			Filter destinationFilter = Filters.and(((ChangeTractorBeamDestinationModifier)m).getDestination());
			result.addAll(Filters.filterAllOnTable(gameState.getGame(), destinationFilter));
		}

		if(result.isEmpty())
			return Collections.singletonList(tractorBeam.getAttachedTo());

		return result;
	}

	/**
	 * Determines if a card has its ability-1 permanent pilot replaced.
	 * @param gameState the game state
	 * @param card a card
	 * @return true or false
	 */
	default boolean isAbility1PermanentPilotReplaced(GameState gameState, PhysicalCard card) {
		List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, card);
		if (permPilots != null) {
			for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
				float permPilotAbility = permPilot.getAbility();
				if (permPilotAbility == 1) {
					return !getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, card).isEmpty();
				}
			}
		}
		return false;
	}

}
