package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;

public interface Battle extends BaseQuery, Ability, Attributes, CardTraits, Flags {

	default boolean mayInitiateBattle(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
				&& card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
				&& card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE, card).isEmpty();
	}

	default boolean mayNotCancelBattle(GameState gameState, String playerId, PhysicalCard battleLocation) {
		if (playerId == null)
			return false;

		for(Modifier m:getModifiers(gameState, ModifierType.MAY_NOT_CANCEL_BATTLE)) {
			if (((MayNotCancelBattleModifier) m).mayNotCancelBattle(gameState.getGame(), playerId, battleLocation))
				return true;
		}

		return false;
	}

	default boolean mayBeBattled(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
				&& card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
				&& card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_BATTLED, card).isEmpty();
	}

	default boolean mayNotBeBattled(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
				&& card.getBlueprint().getCardCategory() != CardCategory.STARSHIP
				&& card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_BATTLED, card).isEmpty();
	}

	/**
	 * Determines if the specified card may not be excluded from battle.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may not be excluded from battle, otherwise false
	 */
	default boolean mayNotBeExcludedFromBattle(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_EXCLUDED_FROM_BATTLE, card).isEmpty();
	}

	/**
	 * Determines if the specified card is prohibited from participating in battle.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerInitiatingBattle the player initiating battle
	 * @return true is prohibited from participating in battle, otherwise false
	 */
	default boolean isProhibitedFromParticipatingInBattle(GameState gameState, PhysicalCard card, String playerInitiatingBattle) {
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE, card).isEmpty()) {
			return true;
		}
		if (card.getOwner().equals(playerInitiatingBattle)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE_INITIATED_BY_OWNER, card).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card is currently excluded from battle.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if excluded from battle, otherwise false
	 */
	default boolean isExcludedFromBattle(GameState gameState, PhysicalCard card) {
		CardCategory cardCategory = card.getBlueprint().getCardCategory();
		if (cardCategory == CardCategory.CHARACTER || cardCategory==CardCategory.DEVICE || cardCategory == CardCategory.VEHICLE
				|| cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.WEAPON) {
			return (getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_EXCLUDED_FROM_BATTLE, card).isEmpty() && !getModifiersAffectingCard(gameState, ModifierType.EXCLUDED_FROM_BATTLE, card).isEmpty());
		}
		return false;
	}

	/**
	 * Gets the card causes the specified card to be excluded from battle, or null if the card is either not excluded from
	 * battle or is excluded from battle by rule.
	 * @param gameState the game state
	 * @param card the card
	 * @return the card causing the exclusion from battle, or null
	 */
	default PhysicalCard getCardCausingExclusionFromBattle(GameState gameState, PhysicalCard card) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE, card)) {
			if (modifier.getSource(gameState) != null) {
				return modifier.getSource(gameState);
			}
		}
		if (gameState.getBattleState() != null && card.getOwner().equals(gameState.getBattleState().getPlayerInitiatedBattle())) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE_INITIATED_BY_OWNER, card)) {
				if (modifier.getSource(gameState) != null) {
					return modifier.getSource(gameState);
				}
			}
		}
		return null;
	}

	/**
	 * Determines if the specified card may use the specified combat card.
	 * @param gameState the game state
	 * @param character the character
	 * @param combatCard the combat card
	 * @return true or false
	 */
	default boolean mayUseCombatCard(GameState gameState, PhysicalCard character, PhysicalCard combatCard) {
		PhysicalCard stackedOn = combatCard.getStackedOn();
		if (stackedOn == null)  {
			return false;
		}

		if (Filters.sameCardId(stackedOn).accepts(gameState, query(), character)) {
			return true;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_OTHER_CHARACTERS_COMBAT_CARDS, character)) {
			if (modifier.isAffectedTarget(gameState, query(), stackedOn)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified player is prohibited from initiating battle at the specified location.
	 *
	 * @param gameState the game state
	 * @param location  the location
	 * @param playerId  the player
	 * @return true if player is not allowed to initiate battle at location, otherwise false
	 */
	default boolean mayNotInitiateBattleAtLocation(GameState gameState, PhysicalCard location, String playerId) {
		// Neither player may Force drain at a Death Star II sector
		if (Filters.Death_Star_II_sector.accepts(gameState, query(), location)) {
			return true;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE_AT_LOCATION, location)) {
			if (modifier.isForPlayer(playerId)) {
				return false;
			}
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_INITIATE_BATTLE_AT_LOCATION, location)) {
			boolean ignoresRestrictions = ignoresObjectiveRestrictionsWhenInitiatingBattleAtLocation(gameState, location, modifier.getSource(gameState), playerId);
			if (modifier.isForPlayer(playerId)) {
				if (!ignoresRestrictions) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card ignores objective restrictions when initiating battle at the specified target.
	 * @param gameState the game state
	 * @param location the target card (location)
	 * @param sourceCard the source of the modifier
	 * @param playerId the player
	 * @return true if card ignores objective restrictions when initiating battle at target
	 */
	default boolean ignoresObjectiveRestrictionsWhenInitiatingBattleAtLocation(GameState gameState, PhysicalCard location, PhysicalCard sourceCard, String playerId) {

		if (location != null && getCardTypes(gameState, sourceCard).contains(CardType.OBJECTIVE)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_OBJECTIVE_RESTRICTIONS_WHEN_INITIATING_BATTLE_AT_LOCATION, location)) {
				if (modifier.isForPlayer(playerId)) {
					//if (modifier.isAffectedTarget(gameState, query(), location)) {
					return true;
					//}
				}
			}
		}

		return false;
	}

	/**
	 * Determines if the specified player always initiates battle for free at the specified location.
	 *
	 * @param gameState the game state
	 * @param location  the location
	 * @param playerId  the player
	 * @return true if the player always initiates battle for free at the specified location
	 */
	default boolean alwaysInitiateBattleForFreeAtLocation(GameState gameState, PhysicalCard location, String playerId) {
		// Check if initiates battle for free
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_FOR_FREE, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified player can choose to initiate battle for free at the specified location.
	 *
	 * @param gameState the game state
	 * @param location  the location
	 * @param playerId  the player
	 * @return true if the specified player can choose to initiate battle for free at the specified location
	 */
	default boolean mayInitiateBattleForFreeAtLocation(GameState gameState, PhysicalCard location, String playerId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE_FOR_FREE, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Gets the cost for the specified player to initiate a battle at the specified location.
	 *
	 * @param gameState the game state
	 * @param location  the location
	 * @param playerId  the player
	 * @return the cost
	 */
	default float getInitiateBattleCost(GameState gameState, PhysicalCard location, String playerId, boolean checkFree) {
		if (checkFree) {

			// Check if player always initiates battle for free
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_FOR_FREE, location)) {
				if (modifier.isForPlayer(playerId)) {
					return 0;
				}
			}

			// Check if player has the option to initiate battle for free
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_INITIATE_BATTLE_FOR_FREE, location)) {
				if (modifier.isForPlayer(playerId)) {
					return 0;
				}
			}
		}

		float result = 1;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_COST, location)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), location);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_INITIATE_BATTLE_COST, location)) {
			if (modifier.isForPlayer(playerId)) {
				float modifierAmount = modifier.getValue(gameState, query(), location);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		result = Math.max(0, result);
		return result;
	}

	default float getInitiateBattleCostAsLoseForce(GameState gameState, PhysicalCard location, String playerId) {
		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_BATTLE_COST_AS_LOSE_FORCE, location)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), location);
			}
		}
		return Math.max(0, result);
	}

	/**
	 * Determines the player to take the first weapons segment action in the current battle.
	 * @param gameState the game state
	 * @return the player to take the first weapons segment action
	 */
	default String getPlayerToTakeFirstBattleWeaponsSegmentAction(GameState gameState) {
		if (!gameState.isDuringBattle())
			return null;

		String playerInitiatedBattle = gameState.getBattleState().getPlayerInitiatedBattle();
		String opponent = gameState.getOpponent(playerInitiatedBattle);

		if (hasFlagActive(gameState, ModifierFlag.TAKES_FIRST_BATTLE_WEAPONS_SEGMENT_ACTION, opponent)) {
			return opponent;
		}

		return playerInitiatedBattle;
	}
}
