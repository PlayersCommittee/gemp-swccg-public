package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Defense extends BaseQuery, Ability, Maneuver, Armor {
	/**
	 * Gets a card's defense value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the defense value
	 */
	default float getDefenseValue(GameState gameState, PhysicalCard physicalCard) {
		return getDefenseValue(gameState, physicalCard, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's defense value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the defense value
	 */
	default float getDefenseValue(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		float result = 0;

		// Determine value to use for defense value
		if (physicalCard.isDejarikHologramAtHolosite()
				|| (physicalCard.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& !physicalCard.getBlueprint().hasIcon(Icon.DROID))) {
			result = Math.max(result, getAbility(gameState, physicalCard));
		}
		else {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEFENSE_VALUE, physicalCard)) {
				result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}
		result = Math.max(result, getManeuver(gameState, physicalCard));
		result = Math.max(result, getArmor(gameState, physicalCard));
		if (physicalCard.getBlueprint().hasSpecialDefenseValueAttribute()) {
			result = Math.max(result, getSpecialDefenseValue(gameState, physicalCard));
		}

		float defenseValueBeforeModified = result;
		float positiveBonuses = 0;

		// Check if defense value is modified
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEFENSE_VALUE, physicalCard)) {
			float modifierAmount = modifier.getDefenseValueModifier(gameState, query(), physicalCard);
			if (modifierAmount >= 0 || !isProhibitedFromHavingDefenseValueReduced(gameState, physicalCard, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null, modifierCollector)) {
				result += modifierAmount;
				modifierCollector.addModifier(modifier);
			}
			if (modifierAmount > 0) {
				positiveBonuses += modifierAmount;
			}
		}

		// Check if defense value may not be reduced below a specified value
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_DEFENSE_VALUE_REDUCED_TO, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);

			// The rule for the defense value reduction + minimum is: "if the card + positive modifiers had a value > MIN_VALUE"
			// and a reduction causes it to fall below the minimum, then enforce the minimum.
			// If the card would NOT have been above that minimum (with all positive modifiers) then do NOT enforce the minimum
			if (modifierAmount <= (defenseValueBeforeModified + positiveBonuses)) {
				result = Math.max(modifierAmount, result);
			}
			modifierCollector.addModifier(modifier);
		}

		// Check if defense value may not be increased above a specified value
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_DEFENSE_VALUE_MODIFIED_TO, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);

			if (modifierAmount < (defenseValueBeforeModified + positiveBonuses)) {
				result = Math.min(modifierAmount, result);
			}

			modifierCollector.addModifier(modifier);
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEFENSE_VALUE, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
			if (modifierAmount >= result || !isProhibitedFromHavingDefenseValueReduced(gameState, physicalCard, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null, modifierCollector)) {
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				modifierCollector.addModifier(modifier);
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		boolean defenseValueMayNotIncreaseBeyondPrinted = isProhibitedFromHavingDefenseValueIncreasedBeyondPrinted(gameState, physicalCard, modifierCollector);
		if (defenseValueMayNotIncreaseBeyondPrinted) {
			if (result > defenseValueBeforeModified) {
				result = defenseValueBeforeModified;
			}
		}

		return Math.max(0, result);
	}

	default float getSpecialDefenseValue(GameState gameState, PhysicalCard physicalCard) {
		float result = physicalCard.getBlueprint().getSpecialDefenseValue();
		return Math.max(0, result);
	}

	default Statistic getDefenseValueStatistic(GameState gameState, PhysicalCard physicalCard) {
		Statistic result;
		float highestValue;
		SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
		if (physicalCard.isDejarikHologramAtHolosite()
				|| (blueprint.getCardCategory()==CardCategory.CHARACTER
				&& !blueprint.hasIcon(Icon.DROID))) {
			result = Statistic.ABILITY;
			highestValue = getAbility(gameState, physicalCard, false);
		}
		else {
			result = Statistic.DEFENSE_VALUE;
			highestValue = 0;
		}

		if (blueprint.getCardCategory()==CardCategory.CHARACTER
				|| blueprint.getCardCategory()==CardCategory.VEHICLE
				|| blueprint.getCardCategory()==CardCategory.STARSHIP) {
			float maneuver = getManeuver(gameState, physicalCard);
			if (maneuver > highestValue) {
				result = Statistic.MANEUVER;
				highestValue = maneuver;
			}
			float armor = getArmor(gameState, physicalCard);
			if (armor > highestValue) {
				result = Statistic.ARMOR;
				highestValue = armor;
			}
		}

		return result;
	}

	/**
	 * Determines if a card's defense value may not be increased above printed value
	 *
	 * @param gameState         the game state
	 * @param card              a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's forfeit may not be increased above printed values
	 */
	default boolean isProhibitedFromHavingDefenseValueIncreasedBeyondPrinted(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEFENSE_VALUE_INCREASED_ABOVE_PRINTED, card)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}

	/**
	 * Determines if a card's defense value may not be reduced by the specified player.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @return true if card's defense value may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingDefenseValueReduced(GameState gameState, PhysicalCard card, String playerId) {
		return isProhibitedFromHavingDefenseValueReduced(gameState, card, playerId, new ModifierCollectorImpl());
	}

	/**
	 * Determines if a card's defense value may not be reduced by the specified player.
	 * @param gameState the game state
	 * @param card a card
	 * @param playerId the player
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's defense value may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingDefenseValueReduced(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEFENSE_VALUE_REDUCED, card)) {
			if (modifier.isForPlayer(playerId)) {
				retVal = true;
				modifierCollector.addModifier(modifier);
			}
		}
		return retVal;
	}
}
