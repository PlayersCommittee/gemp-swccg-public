package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Duels extends BaseQuery {
	/**
	 * Gets actions that the specified Interrupt is currently able to perform to initiate an epic duel.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true or false
	 */
	default List<PlayInterruptAction> getInitiateEpicDuelActions(final GameState gameState, final PhysicalCard card) {
		List<PlayInterruptAction> actionList = new ArrayList<PlayInterruptAction>();

		// Check if the card is granted the ability to initiate an epic duel
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_INITIATE_EPIC_DUEL, card)) {

			// Get valid participants
			Filter validDarkSideParticipantFilter = getValidDuelParticipant(gameState, card, Side.DARK);
			Filter validLightSideParticipantFilter = getValidDuelParticipant(gameState, card, Side.LIGHT);
			final PhysicalCard source = modifier.getSource(gameState);

			// Determine if the epic duel can be initiated with the allowed participants
			final Map<PhysicalCard, Collection<PhysicalCard>> duelMatchups = source.getBlueprint().getInitiateEpicDuelMatchup(gameState.getGame(), source, validDarkSideParticipantFilter, validLightSideParticipantFilter);
			if (!duelMatchups.isEmpty()) {

				final PlayInterruptAction action = new PlayInterruptAction(gameState.getGame(), card);
				action.setText("Initiate epic duel with " + GameUtils.getFullName(source));
				action.appendUsage(
						new PassthruEffect(action) {
							@Override
							protected void doPlayEffect(SwccgGame game) {
								game.getGameState().activatedCard(card.getOwner(), source);
							}
						}
				);

				// Choose target(s)
				action.appendTargeting(
						new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose Dark Side character", TargetingReason.TO_BE_DUELED, Filters.in(duelMatchups.keySet())) {
							@Override
							protected void cardTargeted(final int targetGroupId1, final PhysicalCard darkSideCharacter) {
								action.appendTargeting(
										new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose Light Side character", TargetingReason.TO_BE_DUELED, Filters.in(duelMatchups.get(darkSideCharacter))) {
											@Override
											protected void cardTargeted(final int targetGroupId2, PhysicalCard lightSideCharacter) {
												// Allow response(s)
												action.allowResponses("Initiate epic duel between " + GameUtils.getCardLink(darkSideCharacter) + " and " + GameUtils.getCardLink(lightSideCharacter),
														new RespondablePlayCardEffect(action) {
															@Override
															protected void performActionResults(Action targetingAction) {
																// Get the targeted card(s) from the action using the targetGroupId.
																// This needs to be done in case the target(s) were changed during the responses.
																PhysicalCard darkCharacter = targetingAction.getPrimaryTargetCard(targetGroupId1);
																PhysicalCard lightCharacter = targetingAction.getPrimaryTargetCard(targetGroupId2);

																// Perform result(s)
																action.appendEffect(
																		new DuelEffect(action, darkCharacter, lightCharacter, source.getBlueprint().getDuelDirections(gameState.getGame())));
															}
														});
											}
										}
								);
							}
						}
				);
				actionList.add(action);
			}
		}

		return actionList;
	}

	/**
	 * Gets a filter that accepts cards that can be participants for the specified side of the Force in a duel initiated
	 * by the specified card.
	 *
	 * @param gameState the game state
	 * @param card the card initiating the duel
	 * @param side the side of the Force of the participant
	 * @return the filter
	 */
	default Filter getValidDuelParticipant(GameState gameState, PhysicalCard card, Side side) {
		// Gets filter for cards that are valid dark side participants for a duel initiated by the specified card
		return Filters.and(Filters.character, card.getBlueprint().getValidDuelParticipant(side, gameState.getGame(), card));
	}

	/**
	 * Gets the duel total for the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the duel total
	 */
	default float getDuelTotal(GameState gameState, String playerId) {
		DuelState duelState = gameState.getDuelState();
		if (duelState == null) {
			return 0;
		}

		// Check if duel total is final
		if (duelState.isReachedResults())
			return duelState.getFinalDuelTotal(playerId);

		float result = duelState.getBaseDuelTotal(playerId);
		PhysicalCard character = duelState.getCharacter(playerId);

		// Check modifiers to "duel total"
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DUEL_TOTAL, character)) {
			result += modifier.getValue(gameState, query(), character);
		}

		// Check if this is an attempt to cross over to dark side
		if (duelState.isCrossOverToDarkSideAttempt() && playerId.equals(gameState.getDarkPlayer())) {
			result = getCrossoverAttemptTotal(gameState, duelState.getCharacter(gameState.getLightPlayer()), result);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the lightsaber combat total for the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the lightsaber combat total
	 */
	default float getLightsaberCombatTotal(GameState gameState, String playerId) {
		LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
		if (lightsaberCombatState == null)
			return 0;

		// Check if lightsaber combat total is final
		if (lightsaberCombatState.isReachedResults())
			return lightsaberCombatState.getFinalLightsaberCombatTotal(playerId);

		// If player did not draw any lightsaber combat destiny, then total is 0
		if (lightsaberCombatState.getNumLightsaberCombatDestinyDrawn(playerId) == 0)
			return 0;

		float result = lightsaberCombatState.getBaseLightsaberCombatTotal(playerId);
		PhysicalCard character = lightsaberCombatState.getCharacter(playerId);

		// Check modifiers to "lightsaber combat total"
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LIGHTSABER_COMBAT_TOTAL, character)) {
			result += modifier.getValue(gameState, query(), character);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the crossover attempt total when attempting to cross over the specified character.
	 * @param gameState the game state
	 * @param character the character to attempt to cross over
	 * @param baseValue the initial value of the cross over attempt total
	 * @return the duel total
	 */
	default float getCrossoverAttemptTotal(GameState gameState, PhysicalCard character, float baseValue) {
		float result = baseValue;

		// Check modifiers to "cross over attempt total"
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CROSS_OVER_ATTEMPT_TOTAL, character)) {
			result += modifier.getValue(gameState, query(), character);
		}

		return Math.max(0, result);
	}
}
