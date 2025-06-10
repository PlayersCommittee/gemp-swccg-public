package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;

public interface Attacks extends BaseQuery, Defense, LocationControl {
	/**
	 * Determines if the specified player is prohibited from initiating attacks at the specified location.
	 *
	 * @param gameState the game state
	 * @param location  the location
	 * @param playerId  the player
	 * @return true if player is not allowed to initiate attacks at location, otherwise false
	 */
	default boolean mayNotInitiateAttacksAtLocation(GameState gameState, PhysicalCard location, String playerId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_ATTACKS_AT_LOCATION, location)) {
			if (modifier.isForPlayer(playerId)) {
				return false;
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_INITIATE_ATTACKS_AT_LOCATION, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card is prohibited from attacking the specified target.
	 * @param gameState the game state
	 * @param card the card
	 * @param target the target
	 * @return true is prohibited from attacking the specified target, otherwise false
	 */
	default boolean isProhibitedFromAttackingTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACK, card).isEmpty()) {
			return true;
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACK_TARGET, card)) {
			if (modifier.isAffectedTarget(gameState, query(), target)) {
				return true;
			}
		}
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_ATTACKED, target).isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if the specified card is granted to attack the specified target.
	 * @param gameState the game state
	 * @param card the card
	 * @param target the target
	 * @return true is granted to attack the specified target, otherwise false
	 */
	default boolean grantedToAttackTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
		if (Filters.parasite.accepts(gameState, query(), card)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PARASITE_TARGET, card)) {
				if (modifier.isAffectedTarget(gameState, query(), target)) {
					return true;
				}
			}
			return false;
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_ATTACK_TARGET, card)) {
			if (modifier.isAffectedTarget(gameState, query(), target)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the attack total.
	 * @param gameState the game state
	 * @param defender true if total for defender, otherwise total for attacker
	 * @return the attack total
	 */
	default float getAttackTotal(GameState gameState, boolean defender) {
		AttackState attackState = gameState.getAttackState();
		if (attackState == null) {
			return 0;
		}

		// Check if attack total is final
		if (attackState.isFinalTotalsSet()) {
			return defender ? attackState.getFinalDefenderTotal() : attackState.getFinalAttackerTotal();
		}

		float result = getAttackTotalPowerOrFerocity(gameState, defender);
		if ((defender && attackState.isCreatureAttackingNonCreature()) || (!defender && attackState.isNonCreatureAttackingCreature())) {
			Float attackDestinyTotal = attackState.getAttackDestinyTotal(defender ? attackState.getDefenderOwner() : attackState.getAttackerOwner());
			if (attackDestinyTotal != null) {
				result += attackDestinyTotal;
			}
		}
		if (defender && attackState.isNonCreatureAttackingCreature()) {
			for (PhysicalCard cardDefending : attackState.getCardsDefending()) {
				result += getDefenseValue(gameState, cardDefending);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if the card is placed out of play when eaten by the specified card.
	 * @param gameState the game state
	 * @param cardEaten the card eaten
	 * @param cardEatenBy the card that ate the card
	 * @return true or false
	 */
	default boolean isEatenByPlacedOutOfPlay(GameState gameState, PhysicalCard cardEaten, PhysicalCard cardEatenBy) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EATEN_BY_IS_PLACED_OUT_OF_PLAY, cardEatenBy)) {
			if (modifier.isAffectedTarget(gameState, query(), cardEaten)) {
				return true;
			}
		}
		return false;
	}

	default int getNumAttackDestinyDraws(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
		AttackState attackState = gameState.getAttackState();
		if (attackState == null)
			return 0;

		float abilityRequired = 4;
		float totalAbility = getAttackTotalAbility(gameState, player);

		int result = 0;
		if (Float.compare(totalAbility, abilityRequired) >= 0) {
			result++;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_ATTACK_DESTINY_DRAWS, attackState.getAttackLocation())) {
			int num = ((NumDestinyDrawsDuringAttackModifier)modifier).getNumAttackDestinyDraws(player, gameState, gameState.getGame().getModifiersQuerying());
			result += num;
		}

		return Math.max(0, result);
	}
}
