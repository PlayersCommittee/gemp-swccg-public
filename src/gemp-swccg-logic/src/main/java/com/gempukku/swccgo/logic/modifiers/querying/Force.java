package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Force extends BaseQuery, PrivateQuery, Flags, Icons, Captives, Piloting, Prohibited {
	/**
	 * Increments the amount of Force that has been activated by the player.
	 * @param playerId the player
	 * @param fromForceGeneration true if Force was activated due to Force generation, otherwise false
	 */
	void forceActivated(String playerId, boolean fromForceGeneration);

	/**
	 * Gets the amount of Force the player has activated this turn.
	 * @param playerId the player
	 * @param onlyFromForceGeneration true if only Force activate due to Force generation is counted
	 * @return the amount of Force
	 */
	int getForceActivatedThisTurn(String playerId, boolean onlyFromForceGeneration);

	/**
	 * Gets the amount of Force the player has activated this phase.
	 * @param playerId the player
	 * @return the amount of Force
	 */
	int getForceActivatedThisPhase(String playerId);

	/**
	 * Gets the amount of Force generation at a location for the player.
	 * @param gameState the game state
	 * @param physicalCard the location
	 * @param playerId the player
	 * @return the amount of Force generation
	 */
	default float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId) {
		return getForceGenerationFromLocation(gameState, physicalCard, playerId, new ModifierCollectorImpl());
	}

	/**
	 * Gets the amount of Force generation at a location for the player.
	 * @param gameState the game state
	 * @param physicalCard the location
	 * @param playerId the player
	 * @param modifierCollector collector of affecting modifiers
	 * @return the amount of Force generation
	 */
	default float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId, ModifierCollector modifierCollector) {
		float result = getIconCount(gameState, physicalCard, gameState.getDarkPlayer().equals(playerId) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_GENERATION_AT_LOCATION, physicalCard)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORCE_GENERATION_AT_LOCATION, physicalCard)) {
			if (modifier.isForPlayer(playerId)) {
				float modifierAmount = modifier.getValue(gameState, query(), physicalCard);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				modifierCollector.addModifier(modifier);
			}
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		// Limit Force Generation
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_GENERATION_AT_LOCATION, physicalCard)) {
			if (modifier.isForPlayer(playerId)) {
				result = Math.min(result, modifier.getValue(gameState, query(), physicalCard));
				modifierCollector.addModifier(modifier);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if the specified player is explicitly not allowed to activate Force due to existence of a "can't activate Force"
	 * modifier affecting the player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if player not allowed to activate Force, otherwise false
	 */
	default boolean isActivatingForceProhibited(GameState gameState, String playerId) {
		return hasFlagActive(gameState, ModifierFlag.MAY_NOT_ACTIVATE_FORCE, playerId);
	}

	/**
	 * Determines if the specified player is explicitly not allowed to activate Force due to existence of a "can't activate Force"
	 * modifier affecting the player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if player not allowed to activate Force, otherwise false
	 */
	default boolean isActivateForceFromForceGenerationLimitReached(GameState gameState, String playerId) {
		return getForceActivatedThisTurn(playerId, true) >= Math.floor(gameState.getPlayersTotalForceGeneration(playerId));
	}

	/**
	 * Gets the total Force generation for the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the total Force generation for the player
	 */
	default float getTotalForceGeneration(GameState gameState, String playerId) {
		float total = 1; // 1 Force from player

		// personal force generation
		if (!getModifiers(gameState, ModifierType.PERSONAL_FORCE_GENERATION).isEmpty()) {
			Float minPersonalGeneration = null;

			// get the smallest value among reset personal generation modifiers
			for(Modifier modifier: getModifiers(gameState, ModifierType.PERSONAL_FORCE_GENERATION)) {
				if (modifier.isForPlayer(playerId)) {
					if (minPersonalGeneration==null)
						minPersonalGeneration = modifier.getTotalForceGenerationModifier(playerId, gameState, query());
					else
						minPersonalGeneration = Math.min(minPersonalGeneration, modifier.getTotalForceGenerationModifier(playerId, gameState, query()));
				}
			}

			if (minPersonalGeneration != null)
				total = minPersonalGeneration;
		}


		// Add Force generation from locations
		List<PhysicalCard> locations = gameState.getTopLocations();
		for (PhysicalCard location : locations) {
			total += getForceGenerationFromLocation(gameState, location, playerId);
		}

		for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_FORCE_GENERATION)) {
			total += modifier.getTotalForceGenerationModifier(playerId, gameState, query());
		}

		// Add 1 for each character with matching Force icon
		Icon icon = playerId.equals(gameState.getDarkPlayer()) ? Icon.DARK_JEDI_MASTER : Icon.JEDI_MASTER;
		total += Filters.countActive(gameState.getGame(), null, Filters.and(CardCategory.CHARACTER, icon));

		return Math.max(0, total);
	}

	/**
	 * Gets the total Force generation for the specified player.
	 *
	 * @param gameState the game state
	 * @param playerId  the player
	 * @return the total Force generation for the player
	 */
	default float getTotalForceIconCount(GameState gameState, String playerId) {
		float total = 0;
		Icon icon = playerId.equals(gameState.getDarkPlayer()) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
		// Add Force generation from locations
		List<PhysicalCard> locations = gameState.getTopLocations();
		for (PhysicalCard location : locations) {
			total += getIconCount(gameState, location, icon);
		}

		// Add 1 for each character with matching Force icon
		icon = playerId.equals(gameState.getDarkPlayer()) ? Icon.DARK_JEDI_MASTER : Icon.JEDI_MASTER;
		total += Filters.countActive(gameState.getGame(), null, Filters.and(CardCategory.CHARACTER, icon));

		return Math.max(0, total);
	}

	/**
	 * Gets the amount of Force the player has available to use.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the amount of Force
	 */
	default int getForceAvailableToUse(GameState gameState, String playerId) {
		int playersForcePileSize = gameState.getForcePile(playerId).size();
		int opponentsForceAvailable = getOpponentsForceAvailableToUse(gameState, playerId);

		return Math.max(0, playersForcePileSize + opponentsForceAvailable);
	}

	/**
	 * Gets the amount of opponent's Force the player can has available to use.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the amount of Force
	 */
	default int getOpponentsForceAvailableToUse(GameState gameState, String playerId) {
		int opponentsForcePileSize = gameState.getForcePile(gameState.getOpponent(playerId)).size();

		// Determine the maximum number of opponent's Force that can be used
		int opponentsForceAvailable = 0;
		for (int forceToUse = opponentsForcePileSize; forceToUse > 0; --forceToUse) {
			if (!getCardsToUseOpponentsForceFirst(gameState, playerId, forceToUse, opponentsForcePileSize).isEmpty()) {
				opponentsForceAvailable = forceToUse;
				break;
			}
		}

		return Math.max(0, opponentsForceAvailable);
	}

	/**
	 * Gets the maximum number of Force the player may use from opponent's Force Pile via the specified card.
	 * @param gameState the game state
	 * @param playerId the player
	 * @param card the card
	 * @param opponentsForceAlreadyToBeUsed the amount of opponent's Force already reserved to be used
	 * @param minOpponentForceToUse the minimum amount of total opponent's Force that must be used
	 * @return the amount of Force
	 */
	default int getMaxOpponentsForceToUseViaCard(GameState gameState, String playerId, PhysicalCard card, int opponentsForceAlreadyToBeUsed, int minOpponentForceToUse) {
		String opponent = gameState.getOpponent(playerId);
		int opponentsForcePileSize = Math.max(0, gameState.getForcePile(opponent).size() - opponentsForceAlreadyToBeUsed);
		int minToUse = Math.max(0, minOpponentForceToUse - opponentsForceAlreadyToBeUsed);

		// Determine the maximum number of opponent's Force that can be used by the card
		for (int forceToUse = opponentsForcePileSize; forceToUse > 0 && forceToUse >= minToUse; --forceToUse) {
			Map<PhysicalCard, Integer> cardMap = getCardsToUseOpponentsForceFirst(gameState, playerId, forceToUse, opponentsForcePileSize);
			if (cardMap.containsKey(card)) {
				return cardMap.get(card);
			}
		}
		return 0;
	}

	/**
	 * Determine the cards that must be used by the player to use opponent's Force first in order to use the specified
	 * amount of opponent's Force and how much those cards can use if used first.
	 * @param gameState the game state
	 * @param playerId the player
	 * @param forceToUse the amount of Force to attempt to use
	 * @param opponentsForcePileSize the size of opponents Force Pile
	 * @return the map containing a card that must be used first and max Force that may be used
	 */
	private Map<PhysicalCard, Integer> getCardsToUseOpponentsForceFirst(GameState gameState, String playerId, int forceToUse, int opponentsForcePileSize) {
		Map<PhysicalCard, Integer> validCardsToUseFirst = new HashMap<PhysicalCard, Integer>();

		// Look at modifiers that allow player to use opponent's Force
		List<PhysicalCard> cardList = new ArrayList<PhysicalCard>();
		Map<PhysicalCard, Integer> maxUsableByCardMap = new HashMap<PhysicalCard, Integer>();
		Map<PhysicalCard, Integer> minForcePileRequiredByCardMap = new HashMap<PhysicalCard, Integer>();
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_USE_OPPONENTS_FORCE)) {
			if (modifier.isForPlayer(playerId)) {
				cardList.add(modifier.getSource(gameState));
				maxUsableByCardMap.put(modifier.getSource(gameState), Math.min(forceToUse, (int) modifier.getValue(gameState, query(), (PhysicalCard) null)));
				minForcePileRequiredByCardMap.put(modifier.getSource(gameState), modifier.getMinForcePileSizeToUseOpponentsForce(gameState, query()));
			}
		}

		if (!cardList.isEmpty()) {
			List<List<PhysicalCard>> cardUsageOrderPermutations = generateCardUsageOrderPermutations(cardList, maxUsableByCardMap);
			for (List<PhysicalCard> cardUsageOrder : cardUsageOrderPermutations) {
				if (!cardUsageOrder.isEmpty() && cardUsageOrder.size() >= forceToUse) {
					PhysicalCard firstCard = cardUsageOrder.get(0);
					int numForceIfUsedFirst = getMaxOpponentsForceFirstCardCanUseInUsageOrder(forceToUse, opponentsForcePileSize, cardUsageOrder, minForcePileRequiredByCardMap);
					if (numForceIfUsedFirst > 0) {
						int valueToSet = validCardsToUseFirst.containsKey(firstCard) ? Math.max(numForceIfUsedFirst, validCardsToUseFirst.get(firstCard)) : numForceIfUsedFirst;
						validCardsToUseFirst.put(firstCard, valueToSet);
					}
				}
			}
		}

		return validCardsToUseFirst;
	}

	/**
	 * Gets the amount of Force loss for the specified player due to the current blown away action.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the amount of Force
	 */
	default float getBlownAwayForceLoss(GameState gameState, String playerId) {
		float total = 0;

		for (Modifier modifier : getModifiers(gameState, ModifierType.BLOWN_AWAY_FORCE_LOSS)) {
			if (modifier.isForTopBlowAwayEffect(gameState)) {
				if (modifier.isForPlayer(playerId)) {
					total += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		for (Modifier modifier : getModifiers(gameState, ModifierType.BLOWN_AWAY_FORCE_MULTIPLIER)) {
			if (modifier.isForTopBlowAwayEffect(gameState)) {
				if (modifier.isForPlayer(playerId)) {
					total *= modifier.getMultiplierValue(gameState, query(), null);
				}
			}
		}

		return Math.max(0, total);
	}

	/**
	 * Gets the Force loss amount for losing lightsaber combat.
	 * @param gameState the game state
	 * @return the lightsaber combat destiny value
	 */
	default float getLightsaberCombatForceLoss(GameState gameState, float baseForceLoss) {
		float total = baseForceLoss;

		LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
		if (lightsaberCombatState != null) {
			PhysicalCard winningCharacter = lightsaberCombatState.getWinningCharacter();
			if (winningCharacter != null) {
				for (Modifier modifier : getModifiers(gameState, ModifierType.LIGHTSABER_COMBAT_FORCE_LOSS)) {
					total += modifier.getLightsaberCombatForceLossModifier(gameState, query(), winningCharacter);
				}
			}
		}

		return Math.max(0, total);
	}

	/**
	 * Determines if a Force generation is immune to limit.
	 * @param gameState the game state
	 * @param playerId the player whose Force generation is being checked
	 * @param location the location
	 * @param source the source of the limit
	 * @return true if Force generation at location for player is immune to limit, otherwise false
	 */
	default boolean isImmuneToForceGenerationLimit(GameState gameState, String playerId, PhysicalCard location, PhysicalCard source) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_GENERATION_AT_LOCATION_IMMUNE_TO, location)) {
			if (modifier.isForPlayer(playerId)) {
				if (modifier.isAffectedTarget(gameState, query(), source)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Determines if a Force generation is immune to cancel.
	 * @param gameState the game state
	 * @param playerId the player whose Force generation is being checked
	 * @param location the location
	 * @param source the source of the cancel
	 * @return true if Force generation at location for player is immune to cancel, otherwise false
	 */
	default boolean isImmuneToForceGenerationCancel(GameState gameState, String playerId, PhysicalCard location, PhysicalCard source) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_GENERATION_AT_LOCATION_IMMUNE_TO_CANCEL, location)) {
			if (modifier.isForPlayer(playerId)) {
				if (modifier.isAffectedTarget(gameState, query(), source)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the amount of Force for the specified player to lose.
	 * @param gameState the game state
	 * @param playerId the player to lose Force
	 * @param isCost true if Force loss is a cost, otherwise false
	 * @param baseValue the initial value of the Force loss
	 * @return the amount of Force to lose
	 */
	default float getForceToLose(GameState gameState, String playerId, boolean isCost, float baseValue) {
		float result = baseValue;

		// If not a cost, check if all Force loss is divided in half (rounding up or down) first, then other modifiers
		if (!isCost) {
			if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_UP_FORCE_LOSS, playerId))
				result = (float) Math.ceil((double) result / 2);
			else if (hasFlagActive(gameState, ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, playerId))
				result = (float) Math.floor((double) result / 2);

			for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_LOSS)) {
				if (modifier.isForPlayer(playerId)) {
					result += modifier.getForceLossModifier(gameState, query());
				}
			}

			for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_LOSS_MINIMUM)) {
				if (modifier.isForPlayer(playerId)) {
					result = Math.max(result, modifier.getForceLossMinimum(gameState, query()));
				}
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the maximum amount of Force that the specified player can lose from the specified source card.
	 * @param gameState the game state
	 * @param playerId the player to lose Force
	 * @param source the source card of the Force loss
	 * @return the maximum amount of Force to lose
	 */
	default float getForceToLoseFromCardLimit(GameState gameState, String playerId, PhysicalCard source) {
		float result = Integer.MAX_VALUE;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_CARD, source)) {
			if (modifier.isForPlayer(playerId)) {
				result = Math.min(result, modifier.getForceLossLimit(gameState, query()));
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the maximum amount of Force that the specified player can lose from a Force drain at the specified location.
	 * @param gameState the game state
	 * @param playerId the player to lose Force
	 * @param location the Force drain location
	 * @return the maximum amount of Force to lose
	 */
	default float getForceToLoseFromForceDrainLimit(GameState gameState, String playerId, PhysicalCard location) {
		float result = Float.MAX_VALUE;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_FORCE_DRAIN, location)) {
			if (modifier.isForPlayer(playerId)) {
				result = Math.min(result, modifier.getForceLossLimit(gameState, query()));
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the maximum amount of Force that the specified player can lose from an 'insert' card.
	 * @param gameState the game state
	 * @param playerId the player to lose Force
	 * @return the maximum amount of Force to lose
	 */
	default float getForceToLoseFromInsertCardLimit(GameState gameState, String playerId) {
		float result = Integer.MAX_VALUE;

		for (Modifier modifier : getModifiers(gameState, ModifierType.LIMIT_FORCE_LOSS_FROM_INSERT_CARD)) {
			if (modifier.isForPlayer(playerId)) {
				result = Math.min(result, modifier.getForceLossLimit(gameState, query()));
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if Force retrieval from the specified card is immune to Secret Plans.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean isForceRetrievalImmuneToSecretPlans(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.FORCE_RETRIEVAL_IMMUNE_TO_SECRET_PLANS, card).isEmpty();
	}

	/**
	 * Gets the initial calculated amount of Force for the specified player to retrieve when collecting a bounty.
	 * @param gameState the game state
	 * @param playerId the player to retrieve Force
	 * @param bountyHunterToCollect the bounty hunter to collect the bounty
	 * @param baseValue the initial value of the Force retrieval
	 * @return the amount of Force to retrieve
	 */
	default float getForceToRetrieveForBounty(GameState gameState, String playerId, PhysicalCard bountyHunterToCollect, float baseValue) {
		float result = baseValue;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_RETRIEVAL_FOR_BOUNTY, bountyHunterToCollect)) {
			result += modifier.getValue(gameState, query(), bountyHunterToCollect);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force for the specified player to retrieve.
	 * @param gameState the game state
	 * @param playerId  the player to retrieve Force
	 * @param source the source card of the Force retrieval
	 * @param baseValue the initial value of the Force retrieval
	 * @return the amount of Force to retrieve
	 */
	default float getForceToRetrieve(GameState gameState, String playerId, PhysicalCard source, float baseValue) {
		float result = baseValue;

		for (Modifier modifier : getModifiers(gameState, ModifierType.FORCE_RETRIEVAL)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), source);
			}
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORCE_RETRIEVAL, source)) {
			float modifierAmount = modifier.getValue(gameState, query(), source);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if the specified player is explicitly not allowed to retrieve Force for initiating a battle.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean mayNotRetrieveForceForInitiatingBattle(GameState gameState, String playerId) {
		return hasFlagActive(gameState, ModifierFlag.MAY_NOT_RETRIEVE_FORCE_FOR_INITIATING_BATTLE, playerId);
	}

	/**
	 * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean mayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL, card).isEmpty();
	}

	/**
	 * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean playersCardsAtLocationMayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card, String playerId) {
		return !getModifiersAffectingCard(gameState, ModifierType.PLAYERS_CARDS_AT_LOCATION_MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL, card).isEmpty();
	}
}
