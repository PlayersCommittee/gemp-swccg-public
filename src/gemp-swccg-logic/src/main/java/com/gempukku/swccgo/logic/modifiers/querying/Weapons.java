package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredAtCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

public interface Weapons extends BaseQuery, Icons {

	/**
	 * Gets the permanent weapon built into the card, not including if card is disarmed or game text canceled.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the permanent weapon
	 */
	default SwccgBuiltInCardBlueprint getPermanentWeapon(GameState gameState, PhysicalCard physicalCard) {
		if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return null;

		SwccgBuiltInCardBlueprint permWeapon = physicalCard.getBlueprint().getPermanentWeapon(physicalCard);
		if (permWeapon == null)
			return null;

		if (physicalCard.isDisarmed())
			return null;

		return permWeapon;
	}

	/**
	 * Gets the amount of extra Force required to fire the specified weapon (or permanent weapon).
	 * @param gameState the game state
	 * @param weaponCard the weapon card, or null if permanent weapon
	 * @return the amount of Force
	 */
	default int getExtraForceRequiredToFireWeapon(GameState gameState, PhysicalCard weaponCard) {
		int result = 0;
		for (Modifier modifier : getModifiers(gameState, ModifierType.EXTRA_FORCE_COST_TO_FIRE_WEAPON)) {
			if (weaponCard != null && modifier.isAffectedTarget(gameState, query(), weaponCard)) {
				result += modifier.getValue(gameState, query(), weaponCard);
			}
		}
		result = Math.max(0, result);
		return result;
	}

	default int numDevicesAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded) {
		SwccgCardBlueprint blueprint = card.getBlueprint();
		CardCategory cardCategory = blueprint.getCardCategory();
		if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE)
			return 0;

		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_USE_DEVICES, card).isEmpty()) {
			return 0;
		}

		if (allowLanded) {
			if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.not(Filters.pilotedForTakeOff)).accepts(gameState, query(), card)) {
				return 0;
			}
		}
		else {
			if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.unpiloted).accepts(gameState, query(), card)) {
				return 0;
			}
		}

		if (Filters.capital_starship.accepts(gameState, query(), card)) {
			return Integer.MAX_VALUE;
		}

		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_USE_ANY_NUMBER_OF_DEVICES, card).isEmpty()) {
			return Integer.MAX_VALUE;
		}

		int numDeviceUsedLimit = 1;

		if (Filters.squadron.accepts(gameState, query(), card)) {
			numDeviceUsedLimit = 3;
		}

		return Math.max(0, numDeviceUsedLimit);
	}

	default int numWeaponsAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded) {
		SwccgCardBlueprint blueprint = card.getBlueprint();
		CardCategory cardCategory = blueprint.getCardCategory();
		if (cardCategory!=CardCategory.CHARACTER && cardCategory!=CardCategory.STARSHIP && cardCategory!=CardCategory.VEHICLE && cardCategory!=CardCategory.LOCATION)
			return 0;

		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_USE_WEAPONS, card).isEmpty()) {
			return 0;
		}

		if (allowLanded) {
			if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.not(Filters.pilotedForTakeOff)).accepts(gameState, query(), card)) {
				return 0;
			}
		}
		else {
			if (Filters.and(Filters.or(Filters.starship, Filters.vehicle), Filters.unpiloted).accepts(gameState, query(), card)) {
				return 0;
			}
		}

		if (Filters.capital_starship.accepts(gameState, query(), card)) {
			return Integer.MAX_VALUE;
		}

		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_USE_ANY_NUMBER_OF_WEAPONS, card).isEmpty()) {
			return Integer.MAX_VALUE;
		}

		int numWeaponUsedLimit = 1;

		if (Filters.squadron.accepts(gameState, query(), card)) {
			numWeaponUsedLimit = 3;
		}
		else if (cardCategory == CardCategory.CHARACTER) {
			numWeaponUsedLimit = Math.max(numWeaponUsedLimit, getIconCount(gameState, card, Icon.WARRIOR));
		}

		return Math.max(0, numWeaponUsedLimit);
	}

	/**
	 * Determines if the specified card is not prohibited from firing weapons.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean notProhibitedFromFiringWeapons(GameState gameState, PhysicalCard card) {
		return (getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_FIRE_WEAPONS, card).isEmpty());
	}

	/**
	 * Determines if the specified card is allowed to fire any number of weapons.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean mayFireAnyNumberOfWeapons(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_ANY_NUMBER_OF_WEAPONS, card).isEmpty());
	}

	/**
	 * Determines if the specified weapon is granted ability to fire twice per battle.
	 * @param gameState the game state
	 * @param weapon the weapon
	 * @return true or false
	 */
	default boolean mayBeFiredTwicePerBattle(GameState gameState, PhysicalCard weapon) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_TWICE_PER_BATTLE, weapon).isEmpty();
	}

	/**
	 * Determines if the specified card is granted ability to fire the specified weapon twice per battle.
	 * @param gameState the game state
	 * @param weaponUser the weapon user
	 * @param weapon the weapon
	 * @return true or false
	 */
	default boolean mayFireWeaponTwicePerBattle(GameState gameState, PhysicalCard weaponUser, PhysicalCard weapon) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_A_WEAPON_TWICE_PER_BATTLE, weaponUser)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified artillery weapon is powered.
	 * @param gameState the game state
	 * @param artilleryWeapon the artillery weapon
	 * @return true or false
	 */
	default boolean isPowered(GameState gameState, PhysicalCard artilleryWeapon) {
		if (!getModifiersAffectingCard(gameState, ModifierType.IS_POWERED, artilleryWeapon).isEmpty())
			return true;

		if (Filters.presentWith(null, Filters.and(Filters.owner(artilleryWeapon.getOwner()),
				Filters.or(Filters.power_droid, Filters.fusion_generator))).accepts(gameState, query(), artilleryWeapon))
			return true;

		return false;
	}

	/**
	 * Determines if the specified artillery weapon is does not require a power source.
	 * @param gameState the game state
	 * @param artilleryWeapon the artillery weapon
	 * @return true or false
	 */
	default boolean doesNotRequirePowerSource(GameState gameState, PhysicalCard artilleryWeapon) {
		return !getModifiersAffectingCard(gameState, ModifierType.DOES_NOT_REQUIRE_POWER_SOURCE, artilleryWeapon).isEmpty();
	}

	/**
	 * Gets the cost to fire the weapon.
	 * @param gameState the game state
	 * @param weapon the weapon
	 * @param cardFiringWeapon the card firing the weapon, or null if weapon is not fired by another card
	 * @param target the card targeted by the weapon, or null if no target specified
	 * @param baseCost the base cost (as defined by the weapon game text)
	 * @return the cost to fire the weapon
	 */
	default float getFireWeaponCost(GameState gameState, PhysicalCard weapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost) {
		float result = baseCost;

		// Check if fires for free
		if (!getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_FREE, weapon).isEmpty()) {
			return 0;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE, cardFiringWeapon)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return 0;
			}
		}

		// Check if printed firing cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_FIRE_WEAPON_COST, weapon)) {
			if (modifier.isDefinedFireWeaponCost(gameState, query(), cardFiringWeapon)) {
				result = modifier.getDefinedFireWeaponCost(gameState, query(), cardFiringWeapon);
			}
		}

		// Check if fire weapon cost is changed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_COST, weapon)) {
			result += modifier.getValue(gameState, query(), weapon);
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_COST, cardFiringWeapon)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				result += modifier.getValue(gameState, query(), cardFiringWeapon);
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_AT_COST, weapon)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)
					&& ((FireWeaponFiredAtCostModifier)modifier).isAffectedFiredAtTarget(gameState, query(), target)) {
				result += modifier.getValue(gameState, query(), cardFiringWeapon);
			}
		}

		// Check if fires for double
		if (!getModifiersAffectingCard(gameState, ModifierType.FIRES_FOR_DOUBLE, weapon).isEmpty()) {
			result = result * 2;
		}

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Gets the cost to fire the permanent weapon.
	 * @param gameState the game state
	 * @param permanentWeapon the permanent weapon
	 * @param cardFiringWeapon the card firing the permanent weapon
	 * @param target the card targeted by the permanent weapon, or null if no target specified
	 * @param baseCost the base cost (as defined by the permanent weapon game text)
	 * @return the cost to fire the weapon
	 */
	default float getFireWeaponCost(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost) {
		float result = baseCost;

		// Check if fires for free
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE, cardFiringWeapon)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return 0;
			}
		}

		// Check if fire weapon cost is changed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FIRE_WEAPON_FIRED_BY_COST, cardFiringWeapon)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				result += modifier.getValue(gameState, query(), permanentWeapon);
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.FIRE_WEAPON_FIRED_AT_COST)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)
					&& ((FireWeaponFiredAtCostModifier)modifier).isAffectedFiredAtTarget(gameState, query(), target)) {
				result += modifier.getValue(gameState, query(), cardFiringWeapon);
			}
		}

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Determines if the specified weapon may be fired repeatedly.
	 * @param gameState the game state
	 * @param weapon the weapon
	 * @return true or false
	 */
	default boolean mayFireWeaponRepeatedly(GameState gameState, PhysicalCard weapon) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_REPEATEDLY_FOR_COST, weapon).isEmpty());
	}

	/**
	 * Gets the cost to fire the weapon repeatedly.
	 * @param gameState the game state
	 * @param weapon the weapon
	 * @return the cost for fire the weapon repeatedly
	 */
	default float getFireWeaponRepeatedlyCost(GameState gameState, PhysicalCard weapon) {
		float result = Float.MAX_VALUE;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_REPEATEDLY_FOR_COST, weapon)) {
			result = Math.min(result, modifier.getValue(gameState, query(), weapon));
		}

		result = Math.max(0, result);
		return result;
	}

	/**
	 * Determines if the specified artillery weapon may be fired without a warrior present.
	 * @param gameState the game state
	 * @param artilleryWeapon the artillery weapon
	 * @return true or false
	 */
	default boolean mayFireArtilleryWeaponWithoutWarriorPresent(GameState gameState, PhysicalCard artilleryWeapon) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_FIRE_ARTILLERY_WEAPON_WITHOUT_WARRIOR_PRESENT, artilleryWeapon).isEmpty());
	}
}
