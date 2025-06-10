package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Ferocity extends BaseQuery, Attributes {
	/**
	 * Gets a card's current ferocity.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param ferocityDestinyTotal the ferocity destiny total, or null
	 * @return the card's ferocity
	 */
	default float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal) {
		return getFerocity(gameState, physicalCard, ferocityDestinyTotal, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's current ferocity.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param ferocityDestinyTotal the ferocity destiny total, or null
	 * @param modifierCollector collector of affecting modifiers
	 * @return the card's ferocity
	 */
	default float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal, ModifierCollector modifierCollector) {
		Float result;

		if (!physicalCard.getBlueprint().hasFerocityAttribute())
			return 0;

		result = physicalCard.getBlueprint().getFerocity();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FEROCITY, physicalCard)) {
			result = modifier.getBaseFerocityDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		// If value if undefined, then return 0
		if (result == null)
			return 0;

		if (ferocityDestinyTotal != null) {
			result += ferocityDestinyTotal;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FEROCITY, physicalCard)) {
			result += modifier.getFerocityModifier(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FEROCITY, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			modifierCollector.addModifier(modifier);
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the card's number of ferocity destiny to draw.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the card's number of ferocity destiny
	 */
	default int getNumFerocityDestiny(GameState gameState, PhysicalCard physicalCard) {
		Integer result = null;

		if (!physicalCard.getBlueprint().hasFerocityAttribute())
			return 0;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FEROCITY, physicalCard)) {
			result = modifier.getNumFerocityDestinyDefinedByGameText(gameState, query(), physicalCard);
		}

		// If value if undefined, then return 0
		if (result == null)
			return 0;

		return Math.max(0, result);
	}


}
