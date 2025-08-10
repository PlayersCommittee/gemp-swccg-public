package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

public interface ForceDrains extends BaseQuery, CardTraits, Icons, Captives, Piloting, Prohibited {

	default boolean mayForceDrain(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_FORCE_DRAIN, card).isEmpty();
	}

	/**
	 * Determines if the specified player is prohibited from Force draining at the specified location
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @return true if player is not allowed to Force drain at location, otherwise false
	 */
	default boolean isProhibitedFromForceDrainingAtLocation(GameState gameState, PhysicalCard location, String playerId) {
		// Neither player may Force drain at a Death Star II sector
		if (Filters.Death_Star_II_sector.accepts(gameState, query(), location)) {
			return true;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_FORCE_DRAIN_AT_LOCATION, location)) {
			boolean ignoresRestrictions = ignoresObjectiveRestrictionsWhenForceDrainingAtLocation(gameState, location, modifier.getSource(gameState), playerId);
			if (modifier.isForPlayer(playerId)) {
				if (!ignoresRestrictions) {
					return true;
				}
			}
		}
		return false;
	}

	default boolean cantCancelForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard cardCanceling, String playerCanceling, String playerDraining) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_FORCE_DRAIN_AT_LOCATION, location)) {
			if (modifier.cantCancelForceDrain(gameState, query(), playerCanceling, playerDraining))
				return true;
		}

		// Check if source card may not cancel Force drains
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_FORCE_DRAINS_BY_USING_CARD, cardCanceling)) {
			if (modifier.isForPlayer(playerDraining)) {
				if (modifier.isAffectedTarget(gameState, query(), location)) {
					return true;
				}
			}
		}

		return false;
	}

	default boolean cantModifyForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard modifiedByCard, String playerModifying, String playerDraining) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_FORCE_DRAIN_AT_LOCATION, location)) {
			if (modifier.cantModifyForceDrain(gameState, query(), playerModifying, playerDraining))
				return true;
		}
		// Check if source card may not modify Force drains
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_FORCE_DRAINS_BY_USING_CARD, modifiedByCard)) {
			if (modifier.isForPlayer(playerDraining)) {
				if (modifier.isAffectedTarget(gameState, query(), location)) {
					return true;
				}
			}
		}
		return false;
	}

	default boolean cantReduceForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard reducedByCard, String playerReducing, String playerDraining) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REDUCE_FORCE_DRAIN_AT_LOCATION, location)) {
			if (modifier.cantModifyForceDrain(gameState, query(), playerReducing, playerDraining))
				return true;
		}
		return false;
	}

	default boolean cantReduceForceLossFromForceDrainAtLocation(GameState gameState, PhysicalCard location, String playerReducing, String playerDraining) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REDUCE_FORCE_LOSS_FROM_FORCE_DRAIN_AT_LOCATION, location)) {
			if (modifier.cantModifyForceLossFromForceDrain(gameState, query(), playerReducing, playerDraining))
				return true;
		}
		return false;
	}

	/**
	 * Gets the cost for the specified player to initiate a Force drain at the specified location.
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @return the cost
	 */
	default float getInitiateForceDrainCost(GameState gameState, PhysicalCard location, String playerId) {
		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INITIATE_FORCE_DRAIN_COST, location)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), location);
			}
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the amount of the Force drain.
	 * @param gameState the game state
	 * @param location the Force drain location
	 * @param performingPlayerId the player performing the Force drain
	 * @return the amount of the Force drain
	 */
	default float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId) {
		return getForceDrainAmount(gameState, location, performingPlayerId, new ModifierCollectorImpl());
	}

	/**
	 * Gets the amount of the Force drain.
	 * @param gameState the game state
	 * @param location the Force drain location
	 * @param performingPlayerId the player performing the Force drain
	 * @param modifierCollector collector of affecting modifiers
	 * @return the amount of the Force drain
	 */
	default float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId, ModifierCollector modifierCollector) {
		Icon icon = (gameState.getSide(performingPlayerId) == Side.DARK) ? Icon.LIGHT_FORCE : Icon.DARK_FORCE;

		int result = getIconCount(gameState, location, icon);
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_AMOUNT, location)) {
			if (modifier.isForPlayer(performingPlayerId)) {
				result += modifier.getForceDrainModifier(performingPlayerId, gameState, query(), location);
				modifierCollector.addModifier(modifier);
			}
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_FORCE_DRAIN_AMOUNT, location)) {
			if (modifier.isForPlayer(performingPlayerId)) {
				result = modifier.getUnmodifiableForceDrainAmount(performingPlayerId, gameState, query(), location);
				modifierCollector.addModifier(modifier);
			}
		}
		return Math.max(0, result);
	}

	/**
	 * Determines if a Force drain modifier is canceled.
	 * @param gameState the game state
	 * @param location the Force drain location
	 * @param source the source of the modifier
	 * @param playerModifying the owner of the source card
	 * @param playerDraining the player Force draining
	 * @param amount the amount of the modifier
	 * @return true if modifier is canceled, otherwise false
	 */
	default boolean isForceDrainModifierCanceled(GameState gameState, PhysicalCard location, PhysicalCard source, String playerModifying, String playerDraining, float amount) {
		// Check if Force drains at location may not be modified
		if (cantModifyForceDrainAtLocation(gameState, location, source, playerModifying, playerDraining)) {
			return true;
		}

		// Check if Force drain modifier may not be canceled
		for (Modifier modifier:getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_MODIFIERS_MAY_NOT_BE_CANCELED, source)) {
			if (((ForceDrainModifiersMayNotBeCanceledModifier)modifier).isForceDrainAtLocationFilter(gameState.getGame(), location)) {
				return false;
			}
		}

		if (amount > 0) {
			// Check if Force drain bonus may not be canceled
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.FORCE_DRAIN_BONUSES_MAY_NOT_BE_CANCELED, source)) {
				if (((ForceDrainBonusesMayNotBeCanceledModifier) modifier).isForceDrainAtLocationFilter(gameState.getGame(), location)) {
					return false;
				}
			}
		}

		// Check if opponent's Force drain modifiers are canceled
		if (playerModifying.equals(playerDraining)) {
			for (Modifier modifier : getModifiers(gameState, ModifierType.CANCEL_OPPONENTS_FORCE_DRAIN_MODIFIERS)) {
				if (modifier.isForPlayer(gameState.getOpponent(playerDraining))) {
					if (modifier.isAffectedTarget(gameState, query(), location)) {
						return true;
					}
				}
			}
		}

		if (amount > 0) {
			// Check if Force drain bonuses from specific cards are canceled
			if (!getModifiersAffectingCard(gameState, ModifierType.CANCEL_FORCE_DRAIN_BONUSES_FROM_CARD, source).isEmpty()) {
				return true;
			}

			// Check if opponent's Force drain bonuses are canceled
			if (playerModifying.equals(playerDraining)) {
				for (Modifier modifier : getModifiers(gameState, ModifierType.CANCEL_OPPONENTS_FORCE_DRAIN_BONUSES)) {
					if (modifier.isForPlayer(gameState.getOpponent(playerDraining))) {
						if (modifier.isAffectedTarget(gameState, query(), location)) {
							return true;
						}
					}
				}
			}
		}
		else if (amount < 0) {
			// Check if Force drains at location may not be reduced
			if (cantReduceForceDrainAtLocation(gameState, location, source, playerModifying, playerDraining)) {
				return true;
			}
		}

		return false;
	}

}
