package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Armor extends BaseQuery, Piloting {
	/**
	 * Determines if the card has armor.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card has armor, otherwise false
	 */
	default boolean hasArmor(GameState gameState, PhysicalCard card) {
		return hasArmor(gameState, card, false);
	}

	private boolean hasArmor(GameState gameState, PhysicalCard card, boolean skipArmorValueCheck) {
		if (!card.getBlueprint().hasArmorAttribute())
			return false;

		if (!skipArmorValueCheck) {
			return getArmor(gameState, card) > 0;
		}

		return true;
	}

	default float getArmor(GameState gameState, PhysicalCard physicalCard) {
		return getArmor(gameState, physicalCard, new ModifierCollectorImpl());
	}

	default float getArmor(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		if (!hasArmor(gameState, physicalCard, true))
			return 0;

		Float result = physicalCard.getBlueprint().getArmor();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_ARMOR, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		if (result != null) {
			// If card is a starship or vehicle and it is unpiloted, armor = 2
			if ((physicalCard.getBlueprint().getCardCategory() == CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory() == CardCategory.VEHICLE)
					&& !isPiloted(gameState, physicalCard, false)) {
				return 2;
			}

			// If card is a character and it is "doubled", then double the printed number
			if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
					&& isDoubled(gameState, physicalCard, modifierCollector)) {
				result *= 2;
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ARMOR, physicalCard)) {
				result += modifier.getArmorModifier(gameState, query(), physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ARMOR, physicalCard)) {
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
