package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

public interface Forfeit extends BaseQuery, Attributes, Destiny, Flags, Keywords, Prohibited {

	default boolean cannotSatisfyAttrition(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_SATISFY_ATTRITION, card).isEmpty();
	}

	/**
	 * Determines if a card satisfies all battle damage when forfeited.
	 * @param gameState the game state
	 * @param card a card
	 * @return the forfeit value
	 */
	default boolean isSatisfyAllBattleDamageWhenForfeited(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.SATISFIES_ALL_BATTLE_DAMAGE_WHEN_FORFEITED, card).isEmpty());
	}

	/**
	 * Determines if a card satisfies all attrition when forfeited.
	 * @param gameState the game state
	 * @param card a card
	 * @return the forfeit value
	 */
	default boolean isSatisfyAllAttritionWhenForfeited(GameState gameState, PhysicalCard card) {
		if (cannotSatisfyAttrition(gameState, card))
			return false;

		return (!getModifiersAffectingCard(gameState, ModifierType.SATISFIES_ALL_ATTRITION_WHEN_FORFEITED, card).isEmpty());
	}

	default boolean mayBeForfeitedInBattle(GameState gameState, PhysicalCard physicalCard) {
		return (physicalCard.getBlueprint().hasForfeitAttribute() || physicalCard.isDejarikHologramAtHolosite())
				&& !hasKeyword(gameState, physicalCard, Keyword.MAY_NOT_BE_FORFEITED_IN_BATTLE);
	}

	default boolean mayNotBeForfeitedInBattle(GameState gameState, PhysicalCard physicalCard) {
		return hasKeyword(gameState, physicalCard, Keyword.MAY_NOT_BE_FORFEITED_IN_BATTLE);
	}


	/**
	 * Gets a card's current forfeit value to use when forfeiting card.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the forfeit value to use when forfeiting card
	 */
	default float getForfeitWhenForfeiting(GameState gameState, PhysicalCard physicalCard) {
		// Check if card has another value to be used when forfeited
		Float lowestValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORFEIT_VALUE_TO_USE, physicalCard)) {
			float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
			if (lowestValue == null || modifierAmount < lowestValue) {
				lowestValue = (lowestValue != null) ? Math.min(lowestValue, modifierAmount) : modifierAmount;
			}
		}
		if (lowestValue != null) {
			return Math.max(0, lowestValue);
		}

		return getForfeit(gameState, physicalCard);
	}

	default float getForfeit(GameState gameState, PhysicalCard physicalCard) {
		return getForfeit(gameState, physicalCard, new ModifierCollectorImpl());
	}

	default float getForfeit(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		if (!physicalCard.getBlueprint().hasForfeitAttribute() && !physicalCard.isDejarikHologramAtHolosite())
			return 0;

		Float result;

		// Use destiny number instead if "Dejarik Rules"
		if (physicalCard.isDejarikHologramAtHolosite()) {
			result = getDestiny(gameState, physicalCard);
		}
		else {
			result = physicalCard.getBlueprint().getForfeit();
		}

		Float printedForfeit = result;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FORFEIT_VALUE, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard, modifierCollector)) {
			result *= 2;
		}

		boolean forfeitMayNotBeReduced = isProhibitedFromHavingForfeitReduced(gameState, physicalCard, modifierCollector);
		boolean forfeitMayNotBeIncreased = isProhibitedFromHavingForfeitValueIncreased(gameState, physicalCard, modifierCollector);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORFEIT_VALUE, physicalCard)) {
			float modifierAmount = modifier.getForfeitModifier(gameState, query(), physicalCard);
			if ((modifierAmount >= 0 && !forfeitMayNotBeIncreased) || (modifierAmount <= 0 && !forfeitMayNotBeReduced)) {
				result += modifierAmount;
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORFEIT_VALUE, physicalCard)) {
			float modifierAmount = modifier.getUnmodifiableForfeit(gameState, query(), physicalCard);
			if (modifierAmount >= result || !forfeitMayNotBeReduced) {
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				modifierCollector.addModifier(modifier);
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		boolean forfeitMayNotIncreaseBeyondPrinted = isProhibitedFromHavingForfeitIncreasedBeyondPrinted(gameState, physicalCard, modifierCollector);
		if (forfeitMayNotIncreaseBeyondPrinted) {
			if (result > printedForfeit) {
				result = printedForfeit;
			}
		}

		return Math.max(0, result);
	}

	default float getForfeitModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORFEIT_INCREASE_MODIFIER_LIMIT, physicalCard)) {
			result = modifier.getForfeitModifierLimit(gameState, query(), physicalCard);
		}
		return result;
	}

	/**
	 * Determines if a card remains in play and reduces it's forfeit when 'forfeited'.
	 * @param gameState the game state
	 * @param card a card
	 * @return true or false
	 */
	default boolean isRemainsInPlayAndReducesForfeitWhenForfeited(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.REMAINS_IN_PLAY_WHEN_FORFEITED, card).isEmpty();
	}

	/**
	 * Determines if a card's forfeit may not be reduced.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card's forfeit may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card) {
		return isProhibitedFromHavingForfeitReduced(gameState, card, new ModifierCollectorImpl());
	}

	/**
	 * Determines if a card's forfeit may not be reduced.
	 * @param gameState the game state
	 * @param card a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's forfeit may not be reduced, otherwise false
	 */
	default boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_FORFEIT_VALUE_REDUCED, card)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}

	/**
	 * Gets the total destiny to attrition value after applying modifiers to the base total destiny to attrition.
	 * @param gameState the game state
	 * @param playerId the player with the destiny to attrition
	 * @param baseTotalDestiny the base total destiny to attrition
	 * @return the total destiny to attrition
	 */
	default float getTotalDestinyToAttrition(GameState gameState, String playerId, float baseTotalDestiny) {
		return Math.max(0, baseTotalDestiny);
	}

	/**
	 * Determines if the specified player takes no battle damage.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean isTakesNoBattleDamage(GameState gameState, String playerId) {
		BattleState battleState = gameState.getBattleState();
		if (battleState == null)
			return false;

		PhysicalCard location = battleState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NO_BATTLE_DAMAGE, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	default float getTotalBattleDamage(GameState gameState, String playerId) {
		BattleState battleState = gameState.getBattleState();
		if (battleState == null)
			return 0;

		float result = battleState.getBaseBattleDamage(playerId);
		if (result == 0)
			return 0;

		// Check if all Force loss is divided in half (rounding up or down) first
		if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_UP_FORCE_LOSS, playerId))
			result = (float) Math.ceil((double) result / 2);
		else if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, playerId))
			result = (float) Math.floor((double) result / 2);

		PhysicalCard location = battleState.getBattleLocation();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DAMAGE, location)) {
			if (modifier.isForPlayer(playerId)) {
				result *= modifier.getMultiplierValue(gameState, query(), location);
				result += modifier.getValue(gameState, query(), location);
			}
		}

		// Last, check if battle damage has a limit
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DAMAGE_LIMIT, location)) {
			if (modifier.isForPlayer(playerId)) {
				result = Math.min(result, modifier.getValue(gameState, query(), location));
			}
		}

		return Math.max(0, result);
	}

	default float getTotalAttrition(GameState gameState, String playerId) {
		BattleState battleState = gameState.getBattleState();
		if (battleState == null)
			return 0;

		// If during damage segment of battle, then attrition is already set
		if (battleState.isReachedDamageSegment()) {
			return battleState.getAttritionTotal(gameState.getGame(), playerId);
		}

		// If opponent did not draw any battle destiny, attrition is 0
		if (battleState.getNumBattleDestinyDrawn(gameState.getOpponent(playerId)) == 0)
			return 0;

		PhysicalCard location = battleState.getBattleLocation();

		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ATTRITION, location)) {
			if (modifier.isForPlayer(playerId)) {
				float modifierAmount = modifier.getValue(gameState, query(), location);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			}
		}
		if (lowestResetValue != null) {
			return Math.max(0, lowestResetValue);
		}

		float result = battleState.getBaseAttrition(playerId);
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ATTRITION, location)) {
			result += modifier.getAttritionModifier(playerId, gameState, query(), location);
		}

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Determines if the card has any immunity to attrition.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card has any immunity to attrition, otherwise false
	 */
	default boolean hasAnyImmunityToAttrition(GameState gameState, PhysicalCard card) {
		return hasAnyImmunityToAttrition(gameState, card, false, null, new ModifierCollectorImpl());
	}

	/**
	 * Determines if the card already has immunity to attrition (when ignoring modifiers from specified card).
	 * @param gameState the game state
	 * @param card      a card
	 * @param sourceToIgnore source card to ignore modifiers from
	 * @return true if card already has any immunity to attrition, otherwise false
	 */
	default boolean alreadyHasImmunityToAttrition(GameState gameState, PhysicalCard card, Filterable sourceToIgnore) {
		return hasAnyImmunityToAttrition(gameState, card, false, sourceToIgnore, new ModifierCollectorImpl());
	}

	private boolean hasAnyImmunityToAttrition(GameState gameState, PhysicalCard card, boolean skipImmunityValueCheck, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
		if (!card.getBlueprint().hasImmunityToAttritionAttribute())
			return false;

		boolean mayNotBeCanceled = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_MAY_NOT_BE_CANCELED, card)) {
			mayNotBeCanceled = true;
			modifierCollector.addModifier(modifier);
		}

		if (!mayNotBeCanceled) {
			boolean isCanceled = false;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LOSE_IMMUNITY_TO_ATTRITION, card)) {
				isCanceled = true;
				modifierCollector.addModifier(modifier);
			}
			if (isCanceled) {
				return false;
			}
		}

		if (!skipImmunityValueCheck) {
			return (getImmunityToAttritionLessThan(gameState, card, sourceToIgnore, modifierCollector) > 0 || getImmunityToAttritionOfExactly(gameState, card, sourceToIgnore, modifierCollector) > 0);
		}

		return true;
	}

	/**
	 * Gets the amount of attrition the specified card is immune to less than.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the immunity to attrition less than value
	 */
	default float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard) {
		return getImmunityToAttritionLessThan(gameState, physicalCard, null, new ModifierCollectorImpl());
	}

	/**
	 * Gets the amount of attrition the specified card is immune to less than.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the immunity to attrition less than value
	 */
	default float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		return getImmunityToAttritionLessThan(gameState, physicalCard, null, modifierCollector);
	}

	private float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard physicalCard, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
		Float lockedValue = null;

		// During the damage segment of a battle, immunity to attrition cannot change, so look at the saved value
		if (gameState.isDuringDamageSegmentOfBattle() && Filters.participatingInBattle.accepts(gameState, query(), physicalCard)) {
			float value = physicalCard.getImmunityToAttritionLessThan();
			if (physicalCard.getImmunityToAttritionOfExactly() >= value)
				lockedValue = 0f;
			else
				lockedValue = value;
		}

		if (!hasAnyImmunityToAttrition(gameState, physicalCard, true, sourceToIgnore, modifierCollector)) {
			if (lockedValue != null) {
				return lockedValue;
			}
			return 0;
		}

		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN, physicalCard)) {
			if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
				result = Math.max(result, modifier.getImmunityToAttritionLessThanModifier(gameState, query(), physicalCard));
				modifierCollector.addModifier(modifier);
			}
		}

		if (result > 0) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, physicalCard)) {
				if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
					result += modifier.getImmunityToAttritionChangedModifier(gameState, query(), physicalCard);
					modifierCollector.addModifier(modifier);
				}
			}
		}


		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_OF_EXACTLY, physicalCard)) {
			if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
				if (result <= modifier.getImmunityToAttritionOfExactlyModifier(gameState, query(), physicalCard)) {
					if (lockedValue != null) {
						return lockedValue;
					}
					return 0;
				}
			}
		}

		if (lockedValue != null) {
			return lockedValue;
		}

		// See if we are capped at a value and apply the cap
		float immunityValueCap = Float.MAX_VALUE;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_LIMITED_TO_VALUE, physicalCard)) {
			if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {

				float newCappedValue =  modifier.getImmunityToAttritionCappedAtValue(gameState, query(), physicalCard);
				if (newCappedValue < immunityValueCap) {
					immunityValueCap = newCappedValue;
				}
				modifierCollector.addModifier(modifier);
			}
		}

		result = Math.min(result, immunityValueCap);

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of attrition the specified card is immune to exactly only.
	 * @param gameState the game state
	 * @param physicalCard  a card
	 * @return the immunity to attrition of exactly value
	 */
	default float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard) {
		return getImmunityToAttritionOfExactly(gameState, physicalCard, Filters.none, new ModifierCollectorImpl());
	}

	/**
	 * Gets the amount of attrition the specified card is immune to exactly only.
	 * @param gameState the game state
	 * @param physicalCard  a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the immunity to attrition of exactly value
	 */
	default float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		return getImmunityToAttritionOfExactly(gameState, physicalCard, Filters.none, modifierCollector);
	}

	private float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard physicalCard, Filterable sourceToIgnore, ModifierCollector modifierCollector) {
		Float lockedValue = null;
		// During the damage segment of a battle, immunity to attrition cannot change, so look at the saved value
		if (gameState.isDuringDamageSegmentOfBattle()) {
			float value = physicalCard.getImmunityToAttritionOfExactly();
			if (physicalCard.getImmunityToAttritionLessThan() > value)
				lockedValue = 0f;
			else
				lockedValue = value;
		}

		if (!hasAnyImmunityToAttrition(gameState, physicalCard, true, sourceToIgnore, modifierCollector)) {
			if (lockedValue != null) {
				return lockedValue;
			}
			return 0;
		}

		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_OF_EXACTLY, physicalCard)) {
			if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
				result = Math.max(result, modifier.getImmunityToAttritionOfExactlyModifier(gameState, query(), physicalCard));
				modifierCollector.addModifier(modifier);
			}
		}

		if (result > 0) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, physicalCard)) {
				if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
					result += modifier.getImmunityToAttritionChangedModifier(gameState, query(), physicalCard);
					modifierCollector.addModifier(modifier);
				}
			}
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN, physicalCard)) {
			if (sourceToIgnore == null || modifier.getSource(gameState) == null || !Filters.and(sourceToIgnore).accepts(gameState, query(), modifier.getSource(gameState))) {
				if (result < modifier.getImmunityToAttritionLessThanModifier(gameState, query(), physicalCard)) {
					if (lockedValue != null) {
						return lockedValue;
					}
					return 0;
				}
			}
		}

		if (lockedValue != null) {
			return lockedValue;
		}
		return Math.max(0, result);
	}

	/**
	 * Determines if a card's forfeit value is equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card      a card
	 * @param value     the forfeit value
	 * @return true if card's forfeit value is equal to the specified value, otherwise false
	 */
	default boolean hasForfeitValueEqualTo(GameState gameState, PhysicalCard card, float value) {
		if (!hasForfeitValueAttribute(card))
			return false;

		return getForfeit(gameState, card) == value;
	}

	/**
	 * Determines if a card's forfeit value is more than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the forfeit value
	 * @return true if card's forfeit value is more than the specified value, otherwise false
	 */
	default boolean hasForfeitValueMoreThan(GameState gameState, PhysicalCard card, float value) {
		if (!hasForfeitValueAttribute(card))
			return false;

		return getForfeit(gameState, card) > value;
	}

	/**
	 * Determines if a card's forfeit may not be increased above printed value
	 * @param gameState the game state
	 * @param card a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's forfeit may not be increased above printed values
	 */
	default boolean isProhibitedFromHavingForfeitIncreasedBeyondPrinted(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_FORFEIT_VALUE_INCREASED_ABOVE_PRINTED, card)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}

	/**
	 * Determines if a card's forfeit may not be increased
	 * @param gameState the game state
	 * @param card a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card's forfeit may not be increased
	 */
	default boolean isProhibitedFromHavingForfeitValueIncreased(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_FORFEIT_VALUE_INCREASED, card)) {
			retVal = true;
			modifierCollector.addModifier(modifier);
		}
		return retVal;
	}

}
