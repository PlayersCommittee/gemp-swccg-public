package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.List;

public interface Landspeed extends BaseQuery, Locations {
	/**
	 * Determines if the card has landspeed.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card has landspeed, otherwise false
	 */
	default boolean hasLandspeed(GameState gameState, PhysicalCard card) {
		return hasLandspeed(gameState, card, false);
	}

	private boolean hasLandspeed(GameState gameState, PhysicalCard card, boolean skipLandspeedValueCheck) {
		if (!card.getBlueprint().hasLandspeedAttribute() && !card.getBlueprint().isMovesLikeCharacter())
			return false;

		if (!skipLandspeedValueCheck) {
			return getLandspeed(gameState, card) > 0;
		}

		return true;
	}

	/**
	 * Determines if a card's landspeed is more than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the landspeed value
	 * @return true if card's landspeed is more than the specified value, otherwise false
	 */
	default boolean hasLandspeedMoreThan(GameState gameState, PhysicalCard card, float value) {
		return getLandspeed(gameState, card) > value;
	}

	default float getLandspeed(GameState gameState, PhysicalCard physicalCard) {
		return getLandspeed(gameState, physicalCard, new ModifierCollectorImpl());
	}

	default float getLandspeed(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		if (!hasLandspeed(gameState, physicalCard, true))
			return 0;

		if (physicalCard.getBlueprint().isMovesLikeCharacter()) {
			return 1;
		}

		Float result = physicalCard.getBlueprint().getLandspeed();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_LANDSPEED, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		boolean canLandspeedBeIncreased = !isProhibitedFromHavingLandspeedIncreased(gameState, physicalCard);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDSPEED, physicalCard)) {
			float modifyAmount = modifier.getValue(gameState, query(), physicalCard);
			if (modifyAmount < 0 || canLandspeedBeIncreased) {
				result += modifyAmount;
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_LANDSPEED, physicalCard)) {
			float resetValue = modifier.getValue(gameState, query(), physicalCard);
			if (resetValue < result || canLandspeedBeIncreased) {
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, resetValue) : resetValue;
				modifierCollector.addModifier(modifier);
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if a card's landspeed may not be increased.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card's landspeed may not be increased, otherwise false
	 */
	default boolean isProhibitedFromHavingLandspeedIncreased(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_LANDSPEED_INCREASED, card).isEmpty();
	}

	/**
	 * Gets landspeed required to move the card to the specified site using landspeed.
	 * @param gameState the game state
	 * @param card a card
	 * @param toSite the site to move to
	 * @return the landspeed required to move, or null if not valid to calculate
	 */
	default Integer getLandspeedRequired(GameState gameState, PhysicalCard card, PhysicalCard toSite) {
		PhysicalCard fromSite = getLocationThatCardIsAt(gameState, card);
		if (fromSite == null) {
			return null;
		}

		int landspeedRequiredForSite = 1;

		// Moving from initial site
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_FROM_LOCATION_LANDSPEED_REQUIREMENT, fromSite)) {
			if (modifier.isAffectedTarget(gameState, query(), card)
					&& !isImmuneToLandspeedRequirementsFromCard(gameState, card, modifier.getSource(gameState))) {
				landspeedRequiredForSite += modifier.getValue(gameState, query(), card);
			}
		}
		int totalLandspeedRequired = Math.max(0, landspeedRequiredForSite);

		List<PhysicalCard> sitesBetween = getSitesBetween(gameState, fromSite, toSite);
		if (sitesBetween == null) {
			return null;
		}

		// Moving from sites in between
		for (PhysicalCard siteBetween : sitesBetween) {
			landspeedRequiredForSite = 1;

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_FROM_LOCATION_LANDSPEED_REQUIREMENT, siteBetween)) {
				if (modifier.isAffectedTarget(gameState, query(), card)
						&& !isImmuneToLandspeedRequirementsFromCard(gameState, card, modifier.getSource(gameState))) {
					landspeedRequiredForSite += modifier.getValue(gameState, query(), card);
				}
			}

			totalLandspeedRequired += Math.max(0, landspeedRequiredForSite);
		}

		return totalLandspeedRequired;
	}

	/**
	 * Determines if the specified card is immune to landspeed requirements from the specified source card.
	 * @param gameState the game state
	 * @param card the card
	 * @param sourceCard the source of the modifier
	 * @return true if card is immune, otherwise false
	 */
	default boolean isImmuneToLandspeedRequirementsFromCard(GameState gameState, PhysicalCard card, PhysicalCard sourceCard) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_LANDSPEED_REQUIREMENTS, card)) {
			if (modifier.isImmuneToLandspeedRequirementModifierFromCard(gameState, query(), sourceCard)) {
				return true;
			}
		}
		return false;
	}



}
