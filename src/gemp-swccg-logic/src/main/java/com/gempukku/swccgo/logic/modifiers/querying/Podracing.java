package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Podracing extends BaseQuery {

	/**
	 * Determines if the player may not draw race destiny.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean mayNotDrawRaceDestiny(GameState gameState, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_DRAW_RACE_DESTINY)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the Podracer's number of race destiny to draw.
	 * @param gameState the game state
	 * @param playerId the player to draw race destiny
	 * @param physicalCard a card
	 * @return the Podracer's number of race destiny to draw
	 */
	default int getNumRaceDestinyToDraw(GameState gameState, String playerId, PhysicalCard physicalCard) {
		int result = 1;

		if (mayNotDrawRaceDestiny(gameState, playerId)) {
			return 0;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAWS, physicalCard)) {
			int numDestinies = (int) modifier.getValue(gameState, query(), physicalCard);
			result = Math.max(result, numDestinies);
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAW_AND_CHOOSE, physicalCard)) {
			int numDestinies = modifier.getNumToDraw(gameState, query(), physicalCard);
			result = Math.max(result, numDestinies);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the Podracer's number of race destiny to choose (in case of draw X and choose Y).
	 * @param gameState the game state
	 * @param playerId the player to draw race destiny
	 * @param physicalCard a card
	 * @return the Podracer's number of race destiny to choose (in case of draw X and choose Y), otherwise 0
	 */
	default int getNumRaceDestinyToChoose(GameState gameState, String playerId, PhysicalCard physicalCard) {
		int result = 0;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_RACE_DESTINY_DRAW_AND_CHOOSE, physicalCard)) {
			int numDestinies = modifier.getNumToChoose(gameState, query(), physicalCard);
			result = Math.max(result, numDestinies);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the value of a race destiny.
	 * @param gameState the game state
	 * @param physicalCard the race destiny card
	 * @return the race destiny value
	 */
	default float getRaceDestiny(GameState gameState, PhysicalCard physicalCard) {
		return getRaceDestiny(gameState, physicalCard, new ModifierCollectorImpl());
	}

	/**
	 * Gets the value of a race destiny.
	 * @param gameState the game state
	 * @param physicalCard the race destiny card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the race destiny value
	 */
	default float getRaceDestiny(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		Float result = physicalCard.getDestinyValueToUse();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard, modifierCollector)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
			result += modifier.getDestinyModifier(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.RACE_DESTINY, physicalCard)) {
			result += modifier.getValue(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		return result;
	}

	/**
	 * Gets the race total for the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the race total
	 */
	default float getHighestRaceTotal(GameState gameState, String playerId) {
		SwccgGame game = gameState.getGame();
		float result = 0;

		// Check for race destinies on Podracer Arena
		PhysicalCard podracerArena = Filters.findFirstFromTopLocationsOnTable(game, Filters.Podrace_Arena);
		if (podracerArena != null) {
			Collection<PhysicalCard> raceDestinies = Filters.filterStacked(gameState.getGame(), Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(podracerArena)));
			for (PhysicalCard raceDestiny : raceDestinies) {
				result += getRaceDestiny(gameState, raceDestiny);
			}
		}

		// Check for race destinies on Podracers
		Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.and(Filters.owner(playerId), Filters.Podracer));
		for (PhysicalCard podracer : podracers) {
			float podracerRaceTotal = getPodracerRaceTotal(gameState, podracer);
			result = Math.max(result, podracerRaceTotal);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the race total for the specified Podracer.
	 * @param gameState the game state
	 * @param podracer the Podracer
	 * @return the race total
	 */
	default float getPodracerRaceTotal(GameState gameState, PhysicalCard podracer) {
		SwccgGame game = gameState.getGame();
		String playerId = podracer.getOwner();
		float result = 0;

		Collection<PhysicalCard> raceDestinies = Filters.filterStacked(game, Filters.and(Filters.raceDestinyForPlayer(playerId), Filters.stackedOn(podracer)));
		for (PhysicalCard raceDestiny : raceDestinies) {
			result += getRaceDestiny(gameState, raceDestiny);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the Podracer cards that are leading the Podrace.
	 * @param gameState the game state
	 * @return the Podracer cards leading the Podrace
	 */
	default Collection<PhysicalCard> getPodracersLeadingPodrace(GameState gameState) {
		SwccgGame game = gameState.getGame();
		List<PhysicalCard> leadingPodracers = new ArrayList<>();

		float leadingRaceTotal = Math.max(getHighestRaceTotal(gameState, gameState.getDarkPlayer()), getHighestRaceTotal(gameState, gameState.getLightPlayer()));

		Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.Podracer);
		for (PhysicalCard podracer : podracers) {
			if (getPodracerRaceTotal(gameState, podracer) >= leadingRaceTotal) {
				leadingPodracers.add(podracer);
			}
		}

		return leadingPodracers;
	}

	/**
	 * Gets the Podracer cards that are behind in the Podrace.
	 * @param gameState the game state
	 * @return the Podracer cards behind in the Podrace
	 */
	default Collection<PhysicalCard> getPodracersBehindInPodrace(GameState gameState) {
		SwccgGame game = gameState.getGame();
		List<PhysicalCard> podracersBehind = new ArrayList<>();

		float leadingRaceTotal = Math.max(getHighestRaceTotal(gameState, gameState.getDarkPlayer()), getHighestRaceTotal(gameState, gameState.getLightPlayer()));

		Collection<PhysicalCard> podracers = Filters.filterActive(game, null, Filters.Podracer);
		for (PhysicalCard podracer : podracers) {
			if (getPodracerRaceTotal(gameState, podracer) < leadingRaceTotal) {
				podracersBehind.add(podracer);
			}
		}

		return podracersBehind;
	}

	/**
	 * Gets the Force retrieval amount for winning a Podrace.
	 * @param gameState the game state
	 * @param baseForceRetrieval the base Force retrieval amount
	 * @return the amount of Force
	 */
	default float getPodraceForceRetrieval(GameState gameState, float baseForceRetrieval) {
		float total = baseForceRetrieval;

		if (gameState.isDuringPodrace()) {
			String winner = gameState.getPodraceWinner();
			if (winner != null) {
				for (Modifier modifier : getModifiers(gameState, ModifierType.PODRACE_FORCE_RETRIEVAL)) {
					if (modifier.isForPlayer(winner)) {
						total += modifier.getValue(gameState, query(), (PhysicalCard) null);
					}
				}
			}
		}

		return Math.max(0, total);
	}

	/**
	 * Gets the Force loss amount for losing a Podrace.
	 * @param gameState the game state
	 * @param baseForceLoss the base Force loss amount
	 * @return the amount of Force
	 */
	default float getPodraceForceLoss(GameState gameState, float baseForceLoss) {
		float total = baseForceLoss;

		if (gameState.isDuringPodrace()) {
			String loser = gameState.getPodraceLoser();
			if (loser != null) {
				for (Modifier modifier : getModifiers(gameState, ModifierType.PODRACE_FORCE_LOSS)) {
					if (modifier.isForPlayer(loser)) {
						total += modifier.getValue(gameState, query(), (PhysicalCard) null);
					}
				}
			}
		}

		return Math.max(0, total);
	}
}
