package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.*;

/**
 * This particular subinterface only really exists because of awkward cyclical inheritance issues.
 */
public interface LocationControl extends BaseQuery, Ability, Battle, Ferocity, Flags, Power, Prohibited {

	/**
	 * Gets the total ability the specified player has present at the specified location.
	 * @param gameState the game state
	 * @param player the player
	 * @param location the location
	 * @return the total ability present
	 */
	default float getTotalAbilityPresentAtLocation(GameState gameState, String player, PhysicalCard location) {
		float result = 0;
		Collection<PhysicalCard> cardsPresentAt = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(player), Filters.present(location)));
		for (PhysicalCard presentCard : cardsPresentAt) {
			// If card is a character or vehicle, add ability of the character or vehicle
			CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
			if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE) {
				result += getAbility(gameState, presentCard, false);
			}
		}

		return Math.max(0, result);
	}
	default float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard) {
		return getTotalAbilityAtLocation(gameState, player, physicalCard, false, false, false, null, false, false, null);
	}

	default float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard, boolean forPresence, boolean forControl, boolean forBattle,
			String playerInitiatingBattle, boolean forBattleDestiny, boolean onlyPiloting, Map<InactiveReason, Boolean> spotOverrides) {
		float result = 0;

		Filter cardsPresentFilter = Filters.and(Filters.owner(player), Filters.present(physicalCard));
		// If checking if control location, skip matching operatives
		if (forControl) {
			cardsPresentFilter = Filters.and(cardsPresentFilter, Filters.not(Filters.operativeOnMatchingPlanet));
		}
		Collection<PhysicalCard> cardsPresentAt = Filters.filterActive(gameState.getGame(), null, spotOverrides, cardsPresentFilter);
		for (PhysicalCard presentCard : cardsPresentAt) {
			// If this is for battle, skip cards that cannot participate in battle (or already have)
			if (forBattle && (isProhibitedFromParticipatingInBattle(gameState, presentCard, playerInitiatingBattle) || hasParticipatedInBattleAtOtherLocation(presentCard, physicalCard)))
				continue;

			// If card is a character or vehicle, add ability of the character or vehicle,
			// and include permanent pilot ability for vehicles and starships
			CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
			if (presentCard.isDejarikHologramAtHolosite()
					|| (cardCategory == CardCategory.CHARACTER && !onlyPiloting) || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
				// If this is for battle destiny, skip cards not participating in battle
				if (!forBattleDestiny || gameState.isParticipatingInBattle(presentCard)) {
					if (forBattleDestiny) {
						result += getAbilityForBattleDestiny(gameState, presentCard);
					}
					else {
						if (!forBattle || player.equals(playerInitiatingBattle) || !Filters.mayNotBeBattled.accepts(gameState, query(), presentCard)) {
							result += getAbility(gameState, presentCard, true);
						}
					}
				}
			}

			// If card is a starship or enclosed vehicle, add ability of cards present aboard that starship or vehicle
			if (cardCategory == CardCategory.STARSHIP || Filters.enclosed_vehicle.accepts(gameState, query(), presentCard)) {

				// Check if unpiloted starship or vehicle
				boolean isUnpiloted = (forBattleDestiny || onlyPiloting) && !isPiloted(gameState, presentCard, false);

				Filter cardsPresentAboardFilter = Filters.and(Filters.owner(player), Filters.aboardExceptRelatedSites(presentCard));
				// If checking if control location, skip matching operatives
				if (forControl) {
					cardsPresentAboardFilter = Filters.and(cardsPresentAboardFilter, Filters.not(Filters.operativeOnMatchingPlanet));
				}
				Collection<PhysicalCard> cardsPresentAboard = Filters.filterActive(gameState.getGame(), null, spotOverrides, cardsPresentAboardFilter);
				for (PhysicalCard presentCardAboard : cardsPresentAboard) {
					// If this is for battle, skip cards that cannot participate in battle
					if (forBattle && (isProhibitedFromParticipatingInBattle(gameState, presentCardAboard, playerInitiatingBattle) || hasParticipatedInBattleAtOtherLocation(presentCardAboard, physicalCard)))
						continue;

					// If only counting ability piloting, only include pilots (if piloted)
					boolean isPiloting = !isUnpiloted && Filters.or(Filters.piloting(presentCard), Filters.driving(presentCard)).accepts(gameState, query(), presentCardAboard);
					if (onlyPiloting && !isPiloting)
						continue;

					CardCategory presentAboardCardCategory = presentCardAboard.getBlueprint().getCardCategory();
					if (presentAboardCardCategory == CardCategory.STARSHIP || presentAboardCardCategory == CardCategory.VEHICLE || presentAboardCardCategory == CardCategory.CHARACTER) {
						// If this is for battle destiny, skip cards not participating in battle, and only count pilots (or passengers that add ability toward battle destiny)
						if (!forBattleDestiny || gameState.isParticipatingInBattle(presentCardAboard)) {
							if (forBattleDestiny) {
								if (isPiloting || passengerAppliesAbilityForBattleDestiny(gameState, presentCardAboard)) {
									result += getAbilityForBattleDestiny(gameState, presentCardAboard);
								}
							}
							else {
								if (!forBattle || player.equals(playerInitiatingBattle) || !Filters.mayNotBeBattled.accepts(gameState, query(), presentCardAboard)) {
									result += getAbility(gameState, presentCardAboard, true);
								}
							}
						}
					}
				}
			}
		}

		// Apply modifiers to total ability at location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_AT_LOCATION, physicalCard)) {
			if (modifier.isForPlayer(player)) {
				float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
				if (modifierAmount >= 0 || !isProhibitedFromHavingTotalAbilityReduced(gameState, physicalCard, player)) {
					result += modifierAmount;
				}
			}
		}

		// Apply modifiers to total ability that can be used for drawing battle destiny
		if (forBattleDestiny) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_FOR_BATTLE_DESTINY, physicalCard)) {
				if (modifier.isForPlayer(player)) {
					result += modifier.getValue(gameState, query(), physicalCard);
				}
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_ABILITY_AT_LOCATION, physicalCard)) {
			if (modifier.isForPlayer(player)) {
				float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
				if (modifierAmount >= result || !isProhibitedFromHavingTotalAbilityReduced(gameState, physicalCard, player)) {
					lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				}
			}
		}
		if (lowestResetValue != null) {
			return Math.max(0, lowestResetValue);
		}

		return Math.max(0, result);
	}



	/**
	 * Gets the total power (or ferocity) in the attack.
	 * @param gameState the game state
	 * @param defender true if total for defender, otherwise total for attacker
	 * @return the total power (or ferocity)
	 */
	default float getAttackTotalPowerOrFerocity(GameState gameState, boolean defender) {
		AttackState attackState = gameState.getAttackState();
		if (attackState == null) {
			return 0;
		}
		float result = 0;

		Collection<PhysicalCard> cardsInAttack = defender ? attackState.getCardsDefending() : attackState.getCardsAttacking();
		for (PhysicalCard cardInAttack : Filters.filter(cardsInAttack, game(), Filters.present(attackState.getAttackLocation()))) {
			// If card is a creature, character, vehicle, or starship, add power (or ferocity) of these cards
			CardCategory cardCategory = cardInAttack.getBlueprint().getCardCategory();
			if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
				result += getPower(gameState, cardInAttack);
			}
			else if (cardCategory == CardCategory.CREATURE) {
				Float ferocityDestinyTotal = attackState.getFerocityDestinyTotal(cardInAttack);
				result += getFerocity(gameState, cardInAttack, ferocityDestinyTotal);
			}
		}

		// Apply modifiers to total power at location if non-creature
		if ((attackState.isNonCreatureAttackingCreature() && !defender)
				|| (attackState.isCreatureAttackingNonCreature() && defender)) {
			String playerWithTotalPower = defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner();
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_POWER_AT_LOCATION, attackState.getAttackLocation())) {
				result += modifier.getTotalPowerModifier(playerWithTotalPower, gameState, query(), attackState.getAttackLocation());
			}
		}

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Gets the total ability in the attack.
	 * @param gameState the game state
	 * @param playerId which player
	 * @return the total ability
	 */
	default float getAttackTotalAbility(GameState gameState, String playerId) {
		AttackState attackState = gameState.getAttackState();
		if (attackState == null) {
			return 0;
		}

		float result = 0;

		PhysicalCard attackLocation = attackState.getAttackLocation();

		Collection<PhysicalCard> cardsInAttack = Filters.filter(attackState.getAllCardsParticipating(), gameState.getGame(), Filters.your(playerId));

		for (PhysicalCard presentCard : Filters.filter(cardsInAttack, game(), Filters.present(attackState.getAttackLocation()))) {

			// If card is a character or vehicle, add ability of the character or vehicle,
			// and include permanent pilot ability for vehicles and starships
			CardCategory cardCategory = presentCard.getBlueprint().getCardCategory();
			if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.VEHICLE || cardCategory == CardCategory.STARSHIP) {
				result += getAbility(gameState, presentCard, true);
			}

			// If card is a starship or enclosed vehicle, add ability of cards present aboard that starship or vehicle
			if (cardCategory == CardCategory.STARSHIP || Filters.enclosed_vehicle.accepts(gameState, query(), presentCard)) {

				// Check if unpiloted starship or vehicle
				boolean isUnpiloted = !isPiloted(gameState, presentCard, false);
				Collection<PhysicalCard> cardsPresentAboard = Filters.filter(cardsInAttack, game(), Filters.aboardExceptRelatedSites(presentCard));
				for (PhysicalCard presentCardAboard : cardsPresentAboard) {
					boolean isPiloting = !isUnpiloted && Filters.or(Filters.piloting(presentCard), Filters.driving(presentCard)).accepts(gameState, query(), presentCardAboard);

					CardCategory presentAboardCardCategory = presentCardAboard.getBlueprint().getCardCategory();
					if (presentAboardCardCategory == CardCategory.STARSHIP || presentAboardCardCategory == CardCategory.VEHICLE || presentAboardCardCategory == CardCategory.CHARACTER) {
						// Only count pilots
						if (isPiloting) {
							result += getAbility(gameState, presentCardAboard, true);
						}
					}
				}
			}
		}

		// Apply modifiers to total ability at location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_ABILITY_AT_LOCATION, attackLocation)) {
			if (modifier.isForPlayer(playerId)) {
				float modifierAmount = modifier.getValue(gameState, query(), attackLocation);
				if (modifierAmount >= 0 || !isProhibitedFromHavingTotalAbilityReduced(gameState, attackLocation, playerId)) {
					result += modifierAmount;
				}
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_ABILITY_AT_LOCATION, attackLocation)) {
			if (modifier.isForPlayer(playerId)) {
				float modifierAmount = modifier.getValue(gameState, query(), attackLocation);
				if (modifierAmount >= result || !isProhibitedFromHavingTotalAbilityReduced(gameState, attackLocation, playerId)) {
					lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				}
			}
		}
		if (lowestResetValue != null) {
			return Math.max(0, lowestResetValue);
		}

		return Math.max(0, result);
	}

	default boolean hasPresenceAt(GameState gameState, String player, PhysicalCard physicalCard, boolean forBattle, String playerInitiatingBattle, Map<InactiveReason, Boolean> spotOverrides) {
		Filter presenceIconFilter = Filters.and(Filters.owner(player), Icon.PRESENCE);
		if (forBattle) {
			presenceIconFilter = Filters.and(presenceIconFilter, Filters.canParticipateInBattleAt(physicalCard, playerInitiatingBattle));
			if (!player.equals(playerInitiatingBattle)) {
				presenceIconFilter = Filters.and(presenceIconFilter, Filters.not(Filters.mayNotBeBattled));
			}
		}
		else {
			presenceIconFilter = Filters.and(presenceIconFilter, Filters.at(physicalCard));
		}

		// Check for [Presence icon] character.
		if (Filters.canSpot(gameState.getGame(), null, spotOverrides, presenceIconFilter))
			return true;

		// Having presence at a location is defined as
		// (1) having total ability of 1 or higher present at that location or
		// (2) having a vehicle or starship present at that location that has total ability of 1 or higher at its bridge, cockpit or cargo bay.

		return getTotalAbilityAtLocation(gameState, player, physicalCard, true, false, forBattle, playerInitiatingBattle, false, false, spotOverrides) >= 1;
	}

	/**
	 * Determines if the specified player occupies the specified location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @return true if the player occupies the location, otherwise false
	 */
	default boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId) {
		return occupiesLocation(gameState, location, playerId, null);
	}

	/**
	 * Determines if the specified player occupies the specified location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @param spotOverrides overrides for which inactive cards are visible to the query
	 * @return true if the player occupies the location, otherwise false
	 */
	default boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId, Map<InactiveReason, Boolean> spotOverrides) {
		if (location.getBlueprint().getCardCategory() != CardCategory.LOCATION)
			return false;

		return hasPresenceAt(gameState, playerId, location, false, null, spotOverrides);
	}

	/**
	 * Determines if the specified player controls the specified location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @return true if the player controls the location, otherwise false
	 */
	default boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId) {
		return controlsLocation(gameState, location, playerId, null);
	}

	/**
	 * Determines if the specified player controls the specified location.
	 *
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @param spotOverrides overrides for which inactive cards are visible to the query
	 * @return true if the player controls the location, otherwise false
	 */
	default boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId, Map<InactiveReason, Boolean> spotOverrides) {
		if (!occupiesLocation(gameState, location, playerId, spotOverrides))
			return false;

		if (occupiesLocation(gameState, location, gameState.getOpponent(playerId), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE))
			return false;

		if (!meetsExtraRequirementsToControlLocation(gameState, location, playerId, spotOverrides))
			return false;

		return true;
	}

	private boolean meetsExtraRequirementsToControlLocation(GameState gameState, PhysicalCard location, String player, Map<InactiveReason, Boolean> spotOverrides) {
		Float minAbility = null;

		// Check if a minimum amount of ability is required to control location.
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_REQUIRED_TO_CONTROL_LOCATION, location)) {
			if (modifier.isForPlayer(player)) {
				float value = modifier.getValue(gameState, query(), location);
				if (minAbility == null || minAbility < value) {
					minAbility = value;
				}
			}
		}

		if (minAbility != null) {
			if (getTotalAbilityAtLocation(gameState, player, location, false, true, false, null, false, false, spotOverrides) < minAbility) {
				return false;
			}
		}

		// Check for operative on matching planet (there needs to be a [Presence] Icon or ability>=1 from other cards)
		if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.owner(player), Filters.operativeOnMatchingPlanet, Filters.at(location)))) {
			// Check for [Presence icon] character that is not a operative on matching planet
			if (Filters.canSpot(gameState.getGame(), null, spotOverrides, Filters.and(Filters.owner(player), Icon.PRESENCE, Filters.not(Filters.operativeOnMatchingPlanet), Filters.at(location)))) {
				return true;
			}

			if (getTotalAbilityAtLocation(gameState, player, location, false, true, false, null, false, false, spotOverrides) < 1) {
				return false;
			}
		}

		return true;
	}
}
