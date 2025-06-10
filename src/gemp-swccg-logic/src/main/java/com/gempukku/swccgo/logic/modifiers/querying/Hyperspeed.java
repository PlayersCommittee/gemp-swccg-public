package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Hyperspeed extends BaseQuery, Keywords {
	/**
	 * Determines if the card does not have a hyperdrive.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card does not have a hyperdrive, otherwise false
	 */
	default boolean hasNoHyperdrive(GameState gameState, PhysicalCard card) {
		if (!card.getBlueprint().hasHyperspeedAttribute())
			return true;

		return hasKeyword(gameState, card, Keyword.NO_HYPERDRIVE);
	}

	/**
	 * Gets a card's hyperspeed value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the hyperspeed value
	 */
	default float getHyperspeed(GameState gameState, PhysicalCard physicalCard) {
		return getHyperspeed(gameState, physicalCard, null, null, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's hyperspeed value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the hyperspeed value
	 */
	default float getHyperspeed(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		return getHyperspeed(gameState, physicalCard, null, null, modifierCollector);
	}

	/**
	 * Gets a card's hyperspeed value when moving to the specified system.
	 * @param gameState the game state
	 * @param card a card
	 * @param fromSystem the system to move from
	 * @param toSystem the system to move to
	 * @return the hyperspeed value
	 */
	default float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem) {
		return getHyperspeed(gameState, card, fromSystem, toSystem, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's hyperspeed value when moving to the specified system.
	 * @param gameState the game state
	 * @param card a card
	 * @param fromSystem the system to move from
	 * @param toSystem the system to move to
	 * @param modifierCollector collector of affecting modifiers
	 * @return the hyperspeed value
	 */
	default float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, ModifierCollector modifierCollector) {
		if (hasNoHyperdrive(gameState, card))
			return 0;

		Float result = card.getBlueprint().getHyperspeed();
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// Check if hyperspeed is modified
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED, card)) {
			result += modifier.getHyperspeedModifier(gameState, query(), card);
			modifierCollector.addModifier(modifier);
		}

		// Check if hyperspeed is affected when moving from specific systems
		if (fromSystem != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED_WHEN_MOVING_FROM_LOCATION, card)) {
				if (modifier.isAffectedTarget(gameState, query(), fromSystem)) {
					result += modifier.getHyperspeedModifier(gameState, query(), card);
					modifierCollector.addModifier(modifier);
				}
			}
		}

		// Check if hyperspeed is affected when moving to specific systems
		if (toSystem != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.HYPERSPEED_WHEN_MOVING_TO_LOCATION, card)) {
				if (modifier.isAffectedTarget(gameState, query(), toSystem)) {
					result += modifier.getHyperspeedModifier(gameState, query(), card);
					modifierCollector.addModifier(modifier);
				}
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_HYPERSPEED, card)) {
			float modifierAmount = modifier.getValue(gameState, query(), card);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			modifierCollector.addModifier(modifier);
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}
}
