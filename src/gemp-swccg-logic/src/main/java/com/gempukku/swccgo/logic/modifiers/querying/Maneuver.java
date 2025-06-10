package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Maneuver extends BaseQuery, Piloting {
	/**
	 * Determines if the card has maneuver.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card has maneuver, otherwise false
	 */
	default boolean hasManeuver(GameState gameState, PhysicalCard card) {
		return hasManeuver(gameState, card, true);
	}

	private boolean hasManeuver(GameState gameState, PhysicalCard card, boolean skipManeuverValueCheck) {
		if (!card.getBlueprint().hasManeuverAttribute())
			return false;

		if (!skipManeuverValueCheck) {
			return getManeuver(gameState, card) > 0;
		}

		return true;
	}

	/**
	 * Determines if a card's maneuver is more than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the maneuver value
	 * @return true if card's maneuver is more than the specified value, otherwise false
	 */
	default boolean hasManeuverMoreThan(GameState gameState, PhysicalCard card, float value) {
		return getManeuver(gameState, card) > value;
	}

	default float getManeuver(GameState gameState, PhysicalCard physicalCard) {
		return getManeuver(gameState, physicalCard, new ModifierCollectorImpl());
	}

	default float getManeuver(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		if (!hasManeuver(gameState, physicalCard, true))
			return 0;

		if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
				&& !isPiloted(gameState, physicalCard, false))
			return 0;

		Float result = physicalCard.getBlueprint().getManeuver();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_MANEUVER, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		if (result != null) {
			// If card is a character and it is "doubled", then double the printed number
			if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
					&& isDoubled(gameState, physicalCard, modifierCollector)) {
				result *= 2;
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MANEUVER, physicalCard)) {
				result += modifier.getManeuverModifier(gameState, query(), physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_MANEUVER, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			modifierCollector.addModifier(modifier);
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		if (result == null)
			return 0;

		return Math.max(0, result);
	}
}
