package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Power extends BaseQuery, Attributes, Destiny, Piloting, Politics {
	/**
	 * Gets a card's current power.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the card's power
	 */
	default float getPower(GameState gameState, PhysicalCard physicalCard) {
		return getPower(gameState, physicalCard, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's current power.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the card's power
	 */
	default float getPower(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		Float result;

		// Use destiny number instead if "Dejarik Rules"
		if (physicalCard.isDejarikHologramAtHolosite()) {
			result = getDestiny(gameState, physicalCard);
		}
		else if (isPoliticsUsedForPower(gameState, physicalCard, modifierCollector)) {
			return getPolitics(gameState, physicalCard);
		}
		else {
			if (!physicalCard.getBlueprint().hasPowerAttribute())
				return 0;

			if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
					&& !isPiloted(gameState, physicalCard, false))
				return 0;

			result = physicalCard.getBlueprint().getPower();

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_POWER, physicalCard)) {
				result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard, modifierCollector)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.POWER, physicalCard)) {
			result *= modifier.getPowerMultiplierModifier(gameState, query(), physicalCard);
			PhysicalCard sourceCard = modifier.getSource(gameState);
			String playerId = sourceCard != null ? sourceCard.getOwner() : null;
			float modifierAmount = modifier.getPowerModifier(gameState, query(), physicalCard);
			if (modifierAmount <= 0 || !isProhibitedFromHavingPowerIncreasedByCard(gameState, physicalCard, playerId, sourceCard, modifierCollector)) {
				if (modifierAmount >= 0 || !isProhibitedFromHavingPowerReduced(gameState, physicalCard, playerId, modifierCollector)) {
					result += modifierAmount;
					modifierCollector.addModifier(modifier);
				}
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_POWER, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
			if (modifierAmount >= result || !isProhibitedFromHavingPowerReduced(gameState, physicalCard, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null, modifierCollector)) {
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				modifierCollector.addModifier(modifier);
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	default float getPowerModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.POWER_INCREASE_MODIFIER_LIMIT, physicalCard)) {
			result = modifier.getPowerModifierLimit(gameState, query(), physicalCard);
		}
		return result;
	}

	/**
	 * Determines if a card's power is less than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the power value
	 * @return true if card's power is less than the specified value, otherwise false
	 */
	default boolean hasPowerLessThan(GameState gameState, PhysicalCard card, float value) {
		if (!hasPowerAttribute(card))
			return false;

		return getPower(gameState, card) < value;
	}

	/**
	 * Determines if a card's power is equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the power value
	 * @return true if card's power is equal to the specified value, otherwise false
	 */
	default boolean hasPowerEqualTo(GameState gameState, PhysicalCard card, float value) {
		if (!hasPowerAttribute(card))
			return false;

		return getPower(gameState, card) == value;
	}

	/**
	 * Determines if a card's power may not be reduced by the specified player.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @return true if card's power may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId) {
		return isProhibitedFromHavingPowerReduced(gameState, card, playerId, new ModifierCollectorImpl());
	}

	/**
	 * Determines if a card's power may not be reduced by the specified player.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's power may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_POWER_REDUCED, card)) {
			if (modifier.isForPlayer(playerId)) {
				retVal = true;
				modifierCollector.addModifier(modifier);
			}
		}
		return retVal;
	}

	/**
	 * Determines if a card's power may not be increased by certain cards.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @param increasedByCard the card to check if its ability to increase power is being restricted
	 * @return true if card's power may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard) {
		return isProhibitedFromHavingPowerIncreasedByCard(gameState, card, playerId, increasedByCard, new ModifierCollectorImpl());
	}

	/**
	 * Determines if a card's power may not be increased by certain cards.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @param increasedByCard the card to check if its ability to increase power is being restricted
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's power may not be increased by certain cards, otherwise false
	 */
	default boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_POWER_INCREASED_BY_CARD, card)) {
			if (modifier.isForPlayer(playerId)) {
				Filter restrictedCardsFilter = modifier.getCardsRestrictedFromIncreasingPowerFilter();
				if (restrictedCardsFilter.accepts(gameState, query(), increasedByCard)) {
					retVal = true;
					modifierCollector.addModifier(modifier);
				}
			}
		}
		return retVal;
	}

	/**
	 * Determines if a character's politics used for that card's power.
	 * @param gameState the game state
	 * @param card a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if character's politics is used for power, otherwise false
	 */
	default boolean isPoliticsUsedForPower(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.USE_POLITICS_FOR_POWER, card)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}

	default float getTotalPowerAtLocation(GameState gameState, PhysicalCard location, String playerId, boolean inBattle, boolean onlyPresent) {
		float result = 0;
		Filter participantFilter = Filters.any;
		if (inBattle)
			participantFilter = Filters.participatingInBattle;

		// Figure out cards present at the location
		Collection<PhysicalCard> presentCards = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.present(location), participantFilter));
		for (PhysicalCard presentCard : presentCards) {
			// If card is a character, vehicle, or starship, add power of these cards
			CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
			if (presentCard.isDejarikHologramAtHolosite() || cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP)
				result += getPower(gameState, presentCard);
		}

		Map<PhysicalCard, Float> modifierSourceMap = new HashMap<>(); // for cumulative rule
		if (!onlyPresent) {
			// Apply modifiers to total power at location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_POWER_AT_LOCATION, location)) {
				PhysicalCard source = modifier.getSource(gameState);
				float modifierAmount = modifier.getTotalPowerModifier(playerId, gameState, query(), location);
				if (!modifierSourceMap.containsKey(source))  {
					modifierSourceMap.put(source, modifierAmount);
					result += modifierAmount;
				} else if (modifier.isCumulative()) {
					result += modifierAmount;
				} else if (modifierSourceMap.get(source)<modifierAmount) {
					result += modifierAmount - modifierSourceMap.get(source);
				}
			}
		}

		result = Math.max(0, result);
		return result;
	}

	default float getTotalPowerDuringBattle(GameState gameState, String playerId, PhysicalCard battleLocation) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_POWER_DURING_BATTLE, battleLocation)) {
			if (modifier.isForPlayer(playerId)) {
				return modifier.getTotalPowerDuringBattleModifier(playerId, gameState, query(), battleLocation);
			}
		}

		return 0;
	}
}
