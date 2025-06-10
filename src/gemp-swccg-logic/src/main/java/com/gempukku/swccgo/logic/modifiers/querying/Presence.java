package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

public interface Presence extends BaseQuery, Keywords {
	default PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard physicalCard) {
		return getCardIsPresentAt(gameState, physicalCard, false, false);
	}

	default PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard physicalCard, boolean includeMovesLikeCharacter, boolean includeEnclosedInPrison) {
		// Only a character, creature, starship, vehicle, weapon or device can be "present" somewhere (include effects)
		// If includeMovesLikeCharacter is set to true, then also allow cards that "move like characters"

		// Card is "present" at a location if the card is physically at that location or is
		// attached to another card (except a starship or "enclosed" vehicle) that is "present at" that location.

		// Card is "present" on/in a starship, "enclosed" vehicle, or "prison" location if the card
		// is attached to that starship, vehicle, or location, or is attached to another card that
		// is "present" on/in that starship, vehicle, or location.
		CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
		if (cardCategory != CardCategory.CHARACTER && cardCategory != CardCategory.STARSHIP && cardCategory != CardCategory.VEHICLE && cardCategory != CardCategory.EFFECT
				&& cardCategory != CardCategory.WEAPON && cardCategory != CardCategory.DEVICE && cardCategory != CardCategory.JEDI_TEST && cardCategory != CardCategory.CREATURE
				&& !physicalCard.isDejarikHologramAtHolosite()
				&& (!includeMovesLikeCharacter || !physicalCard.getBlueprint().isMovesLikeCharacter()))
			return null;

		PhysicalCard atLocation = physicalCard.getAtLocation();
		if (atLocation != null)
			return atLocation;

		PhysicalCard attachedTo = physicalCard.getAttachedTo();
		if (attachedTo == null)
			return null;

		if ((attachedTo.getBlueprint().getCardCategory() == CardCategory.LOCATION && (!physicalCard.isImprisoned() || includeEnclosedInPrison))
				|| (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP && physicalCard.getBlueprint().getCardSubtype() != CardSubtype.STARSHIP)
				|| (attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE && hasKeyword(gameState, attachedTo, Keyword.ENCLOSED) && physicalCard.getBlueprint().getCardSubtype() != CardSubtype.VEHICLE))
			return attachedTo;

		return getCardIsPresentAt(gameState, attachedTo);
	}

	default boolean isPresentAt(GameState gameState, PhysicalCard physicalCard, PhysicalCard atTarget) {
		PhysicalCard presentAt = getCardIsPresentAt(gameState, physicalCard);
		return (presentAt!=null) && (presentAt.getCardId() == atTarget.getCardId());
	}



	/**
	 * Gets the location that the specified card is "present" at.
	 *
	 * @param gameState the game state
	 * @param card      the card
	 * @return location that the specified card is "present" at, otherwise null
	 */
	default PhysicalCard getLocationThatCardIsPresentAt(GameState gameState, PhysicalCard card) {
		PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, false);
		if (presentAt == null || presentAt.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return null;

		return presentAt;
	}

	/**
	 * Determines if the two cards are "with" each other.
	 *
	 * @param gameState the game state
	 * @param physicalCard1 a card
	 * @param physicalCard2 a card
	 * @return true if cards are "with" each other, otherwise false
	 */
	default boolean isWith(GameState gameState, PhysicalCard physicalCard1, PhysicalCard physicalCard2) {
		// A card is not "with" itself
		if (physicalCard1.getCardId() == physicalCard2.getCardId())
			return false;

		// Two cards are "with" each other if they are both "at" the same location
		PhysicalCard at1 = getLocationThatCardIsAt(gameState, physicalCard1);
		PhysicalCard at2 = getLocationThatCardIsAt(gameState, physicalCard2);
		return (at1 != null) && (at2 != null) && (at1.getCardId() == at2.getCardId());
	}

	/**
	 * Determines if the two cards are "present with" each other.
	 *
	 * @param gameState the game state
	 * @param card1 a card
	 * @param card2 a card
	 * @return true if cards are "present with" each other, otherwise false
	 */
	default boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2) {
		return isPresentWith(gameState, card1, card2, false);
	}

	/**
	 * Determines if the two cards are "present with" each other. Optionally includes cards that "move like a character".
	 *
	 * @param gameState the game state
	 * @param card1 a card
	 * @param card2 a card
	 * @param includeMovesLikeCharacter true if including cards that "move like a character", otherwise false
	 * @return true if cards are "present with" each other, otherwise false
	 */
	default boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2, boolean includeMovesLikeCharacter) {
		// A card is not "present with" itself
		if (card1.getCardId() == card2.getCardId())
			return false;

		// Imprisoned characters are not "present with" anything else
		if (Filters.imprisoned.accepts(gameState, query(), card1)
				|| Filters.imprisoned.accepts(gameState, query(), card2)) {
			return false;
		}

		// Two cards are "present with" each other if they are both "present" at/on/in the same place
		PhysicalCard presentAt1 = getCardIsPresentAt(gameState, card1, includeMovesLikeCharacter, false);
		PhysicalCard presentAt2 = getCardIsPresentAt(gameState, card2, includeMovesLikeCharacter, false);
		return (presentAt1 != null) && (presentAt2 != null) && (presentAt1.getCardId() == presentAt2.getCardId());
	}
	/**
	 * Gets the location that the specified card is "at".
	 *
	 * @param gameState the game state
	 * @param card the card
	 * @return location that the specified card is "at", otherwise null
	 */
	default PhysicalCard getLocationThatCardIsAt(GameState gameState, PhysicalCard card) {
		// An Effect or Epic Event is "at" a location if:
		// (1) Its "atLocation" or "attachedTo" is that location
		// (2) It is attached to a card that is at that location
		if (card.getBlueprint().getCardCategory()==CardCategory.DEFENSIVE_SHIELD
				|| card.getBlueprint().getCardCategory()==CardCategory.EFFECT
				|| card.getBlueprint().getCardCategory()==CardCategory.EPIC_EVENT
				|| card.getBlueprint().getCardCategory()==CardCategory.JEDI_TEST) {
			PhysicalCard atLocation = card.getAtLocation();
			if (atLocation!=null) {
				if (atLocation.getBlueprint().getCardCategory()==CardCategory.LOCATION)
					return atLocation;
				else
					return getLocationThatCardIsAt(gameState, atLocation);
			}

			PhysicalCard attachedTo = card.getAttachedTo();
			if (attachedTo!=null) {
				if (attachedTo.getBlueprint().getCardCategory()==CardCategory.LOCATION)
					return attachedTo;
				else
					return getLocationThatCardIsAt(gameState, attachedTo);
			}

			return null;
		}

		// A character, starship, vehicle, weapon or device is "at" a location if it is:
		// (1) Present at that location
		// (2) Abort a starship or vehicle at that location.
		PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, true);
		if (presentAt==null)
			return null;

		CardCategory cardCategory = presentAt.getBlueprint().getCardCategory();
		if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE)
			return getLocationThatCardIsAt(gameState, presentAt);

		if (cardCategory == CardCategory.LOCATION)
			return presentAt;

		return null;
	}

	/**
	 * Gets the system that the specified card is "at".
	 *
	 * @param gameState the game state
	 * @param card the card
	 * @return name of the planet the specified card is "at", otherwise null
	 */
	default String getSystemThatCardIsAt(GameState gameState, PhysicalCard card) {
		// A character, starship, vehicle, weapon or device is "at" a planet if
		// (1) it is on that planet
		// (2) At the bridge, cockpit or cargo bay of a starship that is present at (orbiting) that system.
		String onPlanet = getSystemThatCardIsOn(gameState, card);
		if (onPlanet != null)
			return onPlanet;

		PhysicalCard location = getLocationThatCardIsAt(gameState, card);
		if (location == null)
			return null;

		return location.getPartOfSystem();
	}

	/**
	 * Gets the system that the specified card is "on".
	 *
	 * @param gameState the game state
	 * @param card the card
	 * @return name of the planet the specified card is "on", otherwise null
	 */
	default String getSystemThatCardIsOn(GameState gameState, PhysicalCard card) {
		// A character, starship, vehicle, weapon or device is "on" a planet if it is:
		// (1) Present at any site, cloud or Death Star II sector related to that planet name.
		// (2) At the bridge, cockpit or cargo bay of a starship or vehicle that is present at
		// any site, cloud or Death Star II sector related to that planet name.
		if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
			if (card.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM)
				return null;
			else
				return card.getPartOfSystem();
		}

		PhysicalCard presentAt = getCardIsPresentAt(gameState, card, true, true);
		if (presentAt == null)
			return null;

		return getSystemThatCardIsOn(gameState, presentAt);
	}

	default boolean isPlaceToBePresentOnPlanet(GameState gameState, PhysicalCard physicalCard, String planet) {

		CardCategory cardCategory = physicalCard.getBlueprint().getCardCategory();
		if (cardCategory == CardCategory.LOCATION)
			return (physicalCard.getBlueprint().getCardSubtype()!=CardSubtype.SYSTEM
					&& physicalCard.getPartOfSystem()!=null && physicalCard.getPartOfSystem().equals(planet));

		PhysicalCard presentAt = getCardIsPresentAt(gameState, physicalCard);
		if (presentAt==null)
			return false;

		return isPlaceToBePresentOnPlanet(gameState, presentAt, planet);
	}
}
