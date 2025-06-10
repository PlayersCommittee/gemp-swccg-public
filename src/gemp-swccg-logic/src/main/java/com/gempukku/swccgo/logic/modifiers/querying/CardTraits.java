package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.*;

/**
 * A sort of "miscellaneous" subinterface for handling anything relating to cards that isn't better placed on a
 * more specific subinterface.
 */
public interface CardTraits extends BaseQuery, Limits, Piloting, Weapons {

	/**
	 * Determines if the limit of how may times a card with any titles of the specified card can be played per turn has
	 * been reached.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if the limit has been reached, otherwise false
	 */
	default boolean isPlayingCardTitleTurnLimitReached(GameState gameState, PhysicalCard card) {
		if (gameState.getCurrentPhase()== Phase.PLAY_STARTING_CARDS)
			return false;

		Uniqueness uniqueness = getUniqueness(gameState, card);
		if (uniqueness==null || uniqueness == Uniqueness.UNRESTRICTED || uniqueness.isPerSystem())
			return false;

		for (String title : card.getTitles()) {
			if (uniqueness.getValue() <= getCardTitlePlayedTurnLimitCounter(title).getUsedLimit()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the limit of the number of cards on table (or out of play) with same title or persona as the specified
	 * card has been reached.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if the limit has been reached, otherwise false
	 */
	default boolean isUniquenessOnTableLimitReached(GameState gameState, PhysicalCard card) {
		SwccgCardBlueprint blueprint = card.getBlueprint();
		SwccgBuiltInCardBlueprint permanentWeapon = null;

		// Get any personas (including any permanents aboard).
		Set<Persona> personas = new HashSet<Persona>(getPersonas(gameState, card));
		List<SwccgBuiltInCardBlueprint> permanentsAboard = getPermanentsAboard(gameState, card);
		for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
			personas.addAll(permanentAboard.getPersonas(game()));
		}

		// Only check "out of play" for characters, starships, and vehicles owned by the same player
		if (blueprint.getCardCategory() == CardCategory.CHARACTER || blueprint.getCardCategory() == CardCategory.STARSHIP || blueprint.getCardCategory() == CardCategory.VEHICLE) {
			for (Persona persona : personas) {
				if (!Filters.filterCount(gameState.getOutOfPlayPile(card.getOwner()), gameState.getGame(), 1, Filters.and(Filters.persona(persona), Filters.your(card))).isEmpty()) {
					return true;
				}
				if (!Filters.filterCount(getCardsConsideredOutOfPlay(gameState), gameState.getGame(), 1, Filters.and(Filters.persona(persona), Filters.your(card))).isEmpty()) {
					return true;
				}
			}
			// Add any permanent weapon personas
			permanentWeapon = getPermanentWeapon(gameState, card);
			if (permanentWeapon != null) {
				personas.addAll(permanentWeapon.getPersonas(game()));
			}
		}

		// Check uniqueness of any personas (within the card) on table
		for (Persona persona : personas) {
			// cards owned by the same player
			if (Filters.canSpotForUniquenessChecking(gameState.getGame(), Filters.and(Filters.your(card), Filters.not(card), Filters.or(Filters.persona(persona), Filters.hasPermanentAboard(Filters.persona(persona)), Filters.hasPermanentWeapon(Filters.persona(persona)))))) {
				return true;
			}

			// opponent's cards that are stolen
			if (Filters.canSpotForUniquenessChecking(gameState.getGame(), Filters.and(Filters.opponents(card), Filters.stolen, Filters.not(card), Filters.or(Filters.persona(persona), Filters.hasPermanentAboard(Filters.persona(persona)), Filters.hasPermanentWeapon(Filters.persona(persona)))))) {
				return true;
			}

			// any captives (check for the light side player only)
			if (game().getLightPlayer().equals(card.getOwner())
					&& Filters.canSpotForUniquenessChecking(gameState.getGame(), Filters.and(Filters.captive, Filters.not(card), Filters.or(Filters.persona(persona), Filters.hasPermanentAboard(Filters.persona(persona)), Filters.hasPermanentWeapon(Filters.persona(persona)))))) {
				return true;
			}
		}

		// Otherwise, check based on uniqueness of card title.
		Uniqueness uniqueness = getUniqueness(gameState, card);
		if (uniqueness != null && !uniqueness.isPerSystem()) {

			Filter filterForUniqueness = Filters.sameTitleAs(card, false);
			if (blueprint.getCardCategory() == CardCategory.LOCATION) {
				filterForUniqueness = Filters.and(filterForUniqueness, Filters.owner(card.getOwner()), Filters.not(Filters.collapsed), Filters.not(Filters.perSystemUniqueness));
			}
			else {
				filterForUniqueness = Filters.or(filterForUniqueness, Filters.and(Filters.your(card), Filters.hasPermanentAboard(filterForUniqueness)), Filters.and(Filters.your(card), Filters.hasPermanentWeapon(filterForUniqueness)));
			}
			int count = Filters.countForUniquenessChecking(gameState.getGame(), filterForUniqueness);

			if (uniqueness == Uniqueness.UNIQUE) {
				if (count > 0) {
					return true;
				}

				// Only check out of play for characters, starships, and vehicles (except Jabba's Prize)
				if ((blueprint.getCardCategory() == CardCategory.CHARACTER || blueprint.getCardCategory() == CardCategory.STARSHIP || blueprint.getCardCategory() == CardCategory.VEHICLE)
						&& !Filters.Jabbas_Prize.accepts(gameState, query(), card)) {
					if (!Filters.filterCount(gameState.getAllOutOfPlayCards(), gameState.getGame(), 1, Filters.and(Filters.your(card), Filters.sameTitleAs(card, false))).isEmpty()) {
						return true;
					}
				}
			}

			if (uniqueness.getValue() <= count) {
				return true;
			}
		}

		// Check its permanent weapon by title (if any)
		if (permanentWeapon != null) {
			String weaponTitle = permanentWeapon.getTitle(game());
			Uniqueness weaponUniqueness = permanentWeapon.getUniqueness();

			if (weaponUniqueness != null) {

				int count = Filters.countForUniquenessChecking(gameState.getGame(), Filters.and(Filters.not(card), Filters.or(Filters.title(weaponTitle), Filters.hasPermanentAboard(Filters.title(weaponTitle)), Filters.hasPermanentWeapon(Filters.title(weaponTitle)))));

				if (weaponUniqueness == Uniqueness.UNIQUE
						&& count > 0) {
					return true;
				}

				if (weaponUniqueness.getValue() <= count) {
					return true;
				}
			}
		}

		return false;
	}

	default Collection<PhysicalCard> getCardsConsideredOutOfPlay(GameState gameState) {
		Collection<PhysicalCard> cards = new HashSet<PhysicalCard>();
		for(PhysicalCard card: Filters.filterStacked(gameState.getGame(), Filters.any)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.CONSIDERED_OUT_OF_PLAY, card).isEmpty())
				cards.add(card);
		}
		for(PhysicalCard card: Filters.filterActive(gameState.getGame(), null, SpotOverride.INCLUDE_ALL, Filters.any)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.CONSIDERED_OUT_OF_PLAY, card).isEmpty())
				cards.add(card);
		}

		return cards;
	}

	default Collection<PhysicalCard> getCardsForPersonaChecking(String playerId) {
		Collection<PhysicalCard> cards = new LinkedList<>();
		//my cards on table
		cards.addAll(Filters.filterAllOnTable(game(), Filters.your(playerId)));

		//cards in my out of play pile
		cards.addAll(game().getGameState().getOutOfPlayPile(playerId));

		//my cards that are considered out of play
		cards.addAll(Filters.filter(game().getModifiersQuerying().getCardsConsideredOutOfPlay(game().getGameState()), game(), Filters.your(playerId)));

		//opponent's cards on table that are stolen
		cards.addAll(Filters.filterAllOnTable(game(), Filters.and(Filters.opponents(playerId), Filters.stolen)));

		//captives on table (only for the light side player)
		if (game().getLightPlayer().equals(playerId)) {
			cards.addAll(Filters.filterAllOnTable(game(), Filters.captive));
		}

		return cards;
	}

	/**
	 * Determines if the card is placed in Used Pile (instead of Lost Pile) when canceled by the specified player and card.
	 * @param gameState the game state
	 * @param card the card being canceled
	 * @param canceledByPlayerId the player canceling the card
	 * @param canceledByCard the card canceling the card
	 * @return true if card is to be placed in Used Pile, otherwise false
	 */
	default boolean isPlacedInUsedPileWhenCanceled(GameState gameState, PhysicalCard card, String canceledByPlayerId, PhysicalCard canceledByCard) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PLACE_IN_USED_PILE_WHEN_CANCELED, card)) {
			if (modifier.isPlacedInUsedPileWhenCanceled(gameState, query(), canceledByPlayerId, canceledByCard)) {
				return true;
			}
		}
		return false;
	}

	default boolean isPlacedOutOfPlayWhenPlayedAsSubtype(GameState gameState, PhysicalCard card, CardSubtype subtype) {
		for (Modifier m:getModifiersAffectingCard(gameState, ModifierType.PLACED_OUT_OF_PLAY_WHEN_COMPLETED, card)) {
			if (((InterruptPlacedOutOfPlayWhenCompletedModifier)m).affectsSubtype(subtype))
				return true;
		}
		return false;
	}

	default boolean isForfeitedToUsedPile(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.FORFEITED_TO_USED_PILE, card).isEmpty();
	}

	/**
	 * Determines if a card is lost anytime it is about to be stolen.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card is lost anytime it is about to be stolen, otherwise false
	 */
	default boolean isLostIfAboutToBeStolen(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.LOST_IF_ABOUT_TO_BE_STOLEN, card).isEmpty();
	}

	default boolean isMatchingPair(GameState gameState, PhysicalCard character, PhysicalCard starshipVehicleOrWeapon) {
		if (character.getBlueprint().getCardCategory()==CardCategory.CHARACTER) {
			SwccgCardBlueprint characterBlueprint = character.getBlueprint();

			// Do checking based on if other card is starship, vehicle, or weapon
			if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.STARSHIP) {
				SwccgCardBlueprint starshipBlueprint = starshipVehicleOrWeapon.getBlueprint();

				// Check if the character has the starship as matching (or vice versa)
				if (characterBlueprint.getMatchingStarshipFilter().accepts(gameState, query(), starshipVehicleOrWeapon)
						|| starshipBlueprint.getMatchingPilotFilter().accepts(gameState, query(), character)) {

					// Further check that the character is a valid pilot for the starship
					if (Filters.pilot.accepts(gameState, query(), character)
							&& starshipBlueprint.getValidPilotFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon, false).accepts(gameState, query(), character)) {
						return true;
					}
				}
			}
			else if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.VEHICLE) {
				SwccgCardBlueprint vehicleBlueprint = starshipVehicleOrWeapon.getBlueprint();
				// Creature and transport vehicles are not 'piloted'
				if (vehicleBlueprint.getCardSubtype()==CardSubtype.CREATURE
						|| vehicleBlueprint.getCardSubtype()==CardSubtype.TRANSPORT) {
					return false;
				}

				// Check if the character has the vehicle as matching (or vice versa)
				if (characterBlueprint.getMatchingVehicleFilter().accepts(gameState, query(), starshipVehicleOrWeapon)
						|| vehicleBlueprint.getMatchingPilotFilter().accepts(gameState, query(), character)) {

					// Further check that the character is a valid pilot for the vehicle
					if (Filters.pilot.accepts(gameState, query(), character)
							&& vehicleBlueprint.getValidPilotFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon, false).accepts(gameState, query(), character)) {
						return true;
					}
				}
			}
			else if (starshipVehicleOrWeapon.getBlueprint().getCardCategory()==CardCategory.WEAPON) {
				SwccgCardBlueprint weaponBlueprint = starshipVehicleOrWeapon.getBlueprint();
				// Only character weapons can be 'matching'
				if (weaponBlueprint.getCardSubtype()!=CardSubtype.CHARACTER) {
					return false;
				}

				// Check if the character has the weapon as matching (or vice versa)
				if (characterBlueprint.getMatchingWeaponFilter().accepts(gameState, query(), starshipVehicleOrWeapon)
						|| weaponBlueprint.getMatchingCharacterFilter().accepts(gameState, query(), character)) {

					// Further check that the character is a valid user for the weapon
					if (weaponBlueprint.getValidToUseWeaponFilter(starshipVehicleOrWeapon.getOwner(), gameState.getGame(), starshipVehicleOrWeapon).accepts(gameState, query(), character)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	default boolean hasMindscannedCharacter(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MINDSCANNED_CHARACTER, card).isEmpty();
	}

	default SwccgCardBlueprint getMindscannedCharacterBlueprint(GameState gameState, PhysicalCard card) {
		//there shouldn't be more than one
		for(Modifier m:getModifiersAffectingCard(gameState, ModifierType.MINDSCANNED_CHARACTER, card)) {
			return ((MindscannedCharacterModifier) m).getmindScannedCharacter().getBlueprint();
		}
		return null;
	}

	default boolean mindscannedCharacterGameTextWasCanceled(GameState gameState, PhysicalCard card) {
		//there shouldn't be more than one
		for(Modifier m:getModifiersAffectingCard(gameState, ModifierType.MINDSCANNED_CHARACTER, card)) {
			return ((MindscannedCharacterModifier) m).wasGameTextCanceled();
		}
		return true;
	}

	default boolean mayBeRevealedAsResistanceAgent(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_REVEALED_AS_RESISTANCE_AGENT, card).isEmpty();
	}

	default boolean isCommuning(GameState gameState, Filterable filter) {
		Collection<PhysicalCard> stackedCards = Filters.filterStacked(gameState.getGame(), Filters.and(filter));
		for (PhysicalCard card:stackedCards) {
			if(!getModifiersAffectingCard(gameState, ModifierType.COMMUNING, card).isEmpty())
				return true;
		}
		return false;
	}

	/**
	 * Determines if the two cards have the same card title. For combo cards, each title is checked.
	 *
	 * @param gameState the game state
	 * @param card1 a card
	 * @param card2 a card
	 * @return true if cards have same card title, otherwise false
	 */
	default boolean cardTitlesMatch(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		// Check each title
		for (String cardTitle : card1.getTitles()) {
			if (Filters.title(cardTitle).accepts(gameState, query(), card2)) {
				return true;
			}
		}

		return false;
	}

	default boolean isSpecies(GameState gameState, PhysicalCard physicalCard, Species species) {

		boolean retVal =  physicalCard.getBlueprint().hasSpeciesAttribute() && physicalCard.getBlueprint().getSpecies() == species;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_SPECIES, physicalCard)) {
			if (modifier.hasSpecies(gameState, query(), physicalCard, species)) {
				retVal = true;
			}
		}

		return retVal;

	}

	/**
	 * Gets the personas of the specified card.
	 * @param gameState the game state
	 * @param card the card
	 * @return personas
	 */
	default Set<Persona> getPersonas(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().hasCharacterPersonaOnlyWhileOnTable() && !Filters.onTable.accepts(gameState, query(), card)) {
			return Collections.emptySet();
		}
		Set<Persona> personas = new HashSet<Persona>();
		personas.addAll(card.getBlueprint().getPersonas());
		if (card.isCrossedOver()) {
			Set<Persona> crossedOverPersonas = new HashSet<Persona>();
			for (Persona persona : personas) {
				crossedOverPersonas.add(persona.getCrossedOverPersona());
			}
			return crossedOverPersonas;
		}
		if (!card.isStolen()) {
			List<SwccgBuiltInCardBlueprint> permanentsAboard = getPermanentsAboard(gameState, card);
			for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
				Set<Persona> personasAboard = permanentAboard.getPersonas(gameState.getGame());
				if (!permanentsAboard.isEmpty()) {
					personas.addAll(personasAboard);
				}
			}
		}
		return personas;
	}

	/**
	 * Determines if the specified card has the specified persona.
	 * @param gameState the game state
	 * @param card the card
	 * @param persona the persona
	 * @return true or false
	 */
	default boolean hasPersona(GameState gameState, PhysicalCard card, Persona persona) {
		return getPersonas(gameState, card).contains(persona);
	}

	default CardSubtype getModifiedSubtype(GameState gameState, PhysicalCard card) {
		for(Modifier m:getModifiersAffectingCard(gameState, ModifierType.MODIFY_CARD_SUBTYPE, card)) {
			return ((ChangeCardSubtypeModifier)m).getSubtype();
		}
		return null;
	}

	default Set<CardType> getCardTypes(GameState gameState, PhysicalCard card) {
		Set<CardType> types = new HashSet<>();
		if (card.isDejarikHologramAtHolosite())
			return types;

		types.addAll(card.getBlueprint().getCardTypes());
		for(Modifier m:getModifiersAffectingCard(gameState, ModifierType.ADD_CARD_TYPE, card)) {
			types.add(((AddCardTypeModifier)m).getType());
		}
		return types;
	}

	/**
	 * Determines if the specified card ignores objective restrictions when force draining at the specified target.
	 * @param gameState the game state
	 * @param location the target card (location)
	 * @param sourceCard the source of the modifier
	 * @param playerId the player
	 * @return true if card ignores objective restrictions when force draining at target
	 */
	default boolean ignoresObjectiveRestrictionsWhenForceDrainingAtLocation(GameState gameState, PhysicalCard location, PhysicalCard sourceCard, String playerId) {

		if (location != null && getCardTypes(gameState, sourceCard).contains(CardType.OBJECTIVE)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_OBJECTIVE_RESTRICTIONS_WHEN_FORCE_DRAINING_AT_LOCATION, location)) {
				if (modifier.isForPlayer(playerId)) {
					//if (modifier.isAffectedTarget(gameState, query(), location)) {
					return true;
					//}
				}
			}
		}

		return false;
	}



	default Uniqueness getUniqueness(GameState gameState, PhysicalCard card) {
		if (!getModifiersAffectingCard(gameState, ModifierType.NOT_UNIQUE, card).isEmpty())
			return null;

		if (!getModifiersAffectingCard(gameState, ModifierType.UNIQUE, card).isEmpty())
			return Uniqueness.UNIQUE;

		return card.getBlueprint().getUniqueness();
	}

	default CardSubtype getInterruptType(GameState gameState, PhysicalCard card) {
		if (!getModifiersAffectingCard(gameState, ModifierType.LOST_INTERRUPT, card).isEmpty())
			return CardSubtype.LOST;

		if (!getModifiersAffectingCard(gameState, ModifierType.USED_INTERRUPT, card).isEmpty())
			return CardSubtype.USED;

		return card.getBlueprint().getCardSubtype();
	}
}
