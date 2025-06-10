package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;

public interface MovementCosts extends BaseQuery, MovementRestrictions {

	/**
	 * Gets the amount of Force needed to move the card using landspeed.
	 * @param gameState the game state
	 * @param card the card to move using landspeed
	 * @param fromSite the site to move from
	 * @param toSite the site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveUsingLandspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if moves for free using landspeed
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_USING_LANDSPEED, card).isEmpty())
			return 0;

		// Check if moves for free from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromSite)) {
				return 0;
			}
		}

		// Check if moves for free from location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_USING_LANDSPEED, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromSite)) {
				return 0;
			}
		}

		// Check if moves for free from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSite, toSite)) {
				return 0;
			}
		}

		// Check if moves for free from location to location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSite, toSite)) {
				return 0;
			}
		}

		// Check if moves for free to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toSite)) {
				return 0;
			}
		}

		// Check if moves for free to location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION_USING_LANDSPEED, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toSite)) {
				return 0;
			}
		}

		// Check if moves for free when moving toward a location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TOWARD_TARGET, card)) {
			if (modifier.isMovingTowardTarget(gameState, query(), card, toSite)) {
				return 0;
			}
		}

		// Default move using landspeed cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Check for modifiers to move cost using hyperspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_USING_LANDSPEED, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Moving from collapsed site requires 1 additional Force
		if (fromSite.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSite);
		}

		// Check for modifiers to move cost when moving from location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_USING_LANDSPEED, fromSite)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSite);
		}

		// Check for modifiers to move cost when moving from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSite, toSite);
		}

		// Check for modifiers to move cost when moving from location to location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION_USING_LANDSPEED, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSite, toSite);
		}

		// Moving to collapsed site requires 1 additional Force
		if (toSite.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSite);
		}

		// Check for modifiers to move cost when moving to location using landspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION_USING_LANDSPEED, toSite)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSite);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed to move the card using hyperspeed.
	 * @param gameState the game state
	 * @param card the card to move using hyperspeed
	 * @param fromSystem the system to move from
	 * @param toSystem the system to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if moves for free using hyperspeed
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_USING_LANDSPEED, card).isEmpty())
			return 0;

		if (fromSystem != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromSystem)) {
					return 0;
				}
			}

			// Check if moves for free from location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_USING_HYPERSPEED, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromSystem)) {
					return 0;
				}
			}
		}

		if (fromSystem != null && toSystem != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSystem, toSystem)) {
					return 0;
				}
			}

			// Check if moves for free from location to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSystem, toSystem)) {
					return 0;
				}
			}
		}

		if (toSystem != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toSystem)) {
					return 0;
				}
			}

			// Check if moves for free to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION_USING_HYPERSPEED, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toSystem)) {
					return 0;
				}
			}
		}

		// Default move using hyperspeed cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Check for modifiers to move cost using hyperspeed
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_USING_HYPERSPEED, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromSystem != null) {
			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSystem)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSystem);
			}

			// Check for modifiers to move cost when moving from location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_USING_HYPERSPEED, fromSystem)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSystem);
			}
		}

		if (fromSystem != null && toSystem != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSystem, toSystem);
			}

			// Check for modifiers to move cost when moving from location to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION_USING_HYPERSPEED, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSystem, toSystem);
			}
		}

		if (toSystem != null) {
			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSystem)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSystem);
			}

			// Check for modifiers to move cost when moving to location using hyperspeed
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION_USING_HYPERSPEED, toSystem)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSystem);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed to move the card without using hyperspeed.
	 * @param gameState the game state
	 * @param card the card to move without using hyperspeed
	 * @param fromSystem the system to move from
	 * @param toSystem the system to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveWithoutUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		if (fromSystem != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromSystem)) {
					return 0;
				}
			}
		}

		if (fromSystem != null && toSystem != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSystem, toSystem)) {
					return 0;
				}
			}
		}

		if (toSystem != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toSystem)) {
					return 0;
				}
			}
		}

		// Default move without using hyperspeed cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromSystem != null) {
			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSystem)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSystem);
			}
		}

		if (fromSystem != null && toSystem != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSystem, toSystem);
			}
		}

		if (toSystem != null) {
			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSystem)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSystem);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed to move the card using sector movement.
	 * @param gameState the game state
	 * @param card the card to move using sector movement
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveUsingSectorMovementCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if moves for free from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
				return 0;
			}
		}

		// Check if moves for free from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
				return 0;
			}
		}

		// Check if moves for free to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
				return 0;
			}
		}

		// Dark side starfighters moving (as first regular move) from Death Star II sector during move phase is free
		// when there are no Light side starfighters at Death Star II sectors.
		if (card.getOwner().equals(gameState.getDarkPlayer())
				&& Filters.starfighter.accepts(gameState, query(), card)
				&& Filters.Death_Star_II_sector.accepts(gameState, query(), fromLocation)) {

			// Check if no Light side starfighters at Death Star II sectors.
			if (gameState.getCurrentPlayerId().equals(card.getOwner())
					&& gameState.getCurrentPhase() == Phase.MOVE
					&& !hasPerformedRegularMoveThisTurn(card)
					&& !Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.owner(gameState.getLightPlayer()), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)))) {
				return 0;
			}
		}

		// Default move using sector movement cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Check for modifiers to move cost when moving from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
		}

		// Check for modifiers to move cost when moving from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
		}

		// Check for modifiers to move cost when moving to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed to land the card.
	 * @param gameState the game state
	 * @param card the card to land
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getLandingCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

		// Check if landing to docking bay, which is free
		if (Filters.docking_bay.accepts(gameState, query(), toLocation))
			return 0;

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if lands for free
		if (!getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE, card).isEmpty())
			return 0;

		if (fromLocation != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if lands for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if lands for free from location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
				return 0;
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}

			// Check if lands for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}
		}

		if (toLocation != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if lands for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.LANDS_FOR_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if lands for free to location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
				return 0;
			}
		}

		// Default landing cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromLocation != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
			}
		}

		if (toLocation != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to take off.
	 * @param gameState the game state
	 * @param card the card to take off
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getTakeOffCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost) {

		// Check if taking off from docking bay, which is free
		if (Filters.docking_bay.accepts(gameState, query(), fromLocation))
			return 0;

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if takes off for free
		if (!getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE, card).isEmpty())
			return 0;

		if (fromLocation != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if takes off for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if take off for free from location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
				return 0;
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}

			// Check if takes off for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}
		}

		if (toLocation != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if takes off for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_FOR_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if take off for free to location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
				return 0;
			}
		}

		// Default taking off cost
		float result = 1 + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromLocation != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
			}
		}

		if (toLocation != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to enter the starship/vehicle site.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromSite the site to move from
	 * @param toSite the starship/vehicle site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getEnterStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		if (fromSite != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromSite)) {
					return 0;
				}
			}
		}

		if (fromSite != null && toSite != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSite, toSite)) {
					return 0;
				}
			}
		}

		if (toSite != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toSite)) {
					return 0;
				}
			}
		}

		// Default enter starship/vehicle site cost
		float result = 0 + changeInCost;

		// Check for defined enter starship/vehicle site cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ENTER_EXIT_COST, toSite)) {
			if (modifier.isForPlayer(card.getOwner())) {
				result = modifier.getPrintedValueDefinedByGameText(gameState, query(), toSite);
			}
		}

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromSite != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromSite.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSite);
			}
		}

		if (fromSite != null && toSite != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSite, toSite);
			}
		}

		if (toSite != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toSite.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSite);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to exit the starship/vehicle site.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromSite the starship/vehicle site to move from
	 * @param toSite the site to move to
	 * @param asReact true if the movement is for a 'react' movement, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getExitStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		if (fromSite != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromSite)) {
					return 0;
				}
			}
		}

		if (fromSite != null && toSite != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromSite, toSite)) {
					return 0;
				}
			}
		}

		if (toSite != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toSite)) {
					return 0;
				}
			}
		}

		// Default exit starship/vehicle site cost
		float result = 0 + changeInCost;

		// Check for defined exit starship/vehicle site cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ENTER_EXIT_COST, fromSite)) {
			if (modifier.isForPlayer(card.getOwner())) {
				result = modifier.getPrintedValueDefinedByGameText(gameState, query(), fromSite);
			}
		}

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromSite != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromSite.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromSite)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromSite);
			}
		}

		if (fromSite != null && toSite != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromSite, toSite);
			}
		}

		if (toSite != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toSite.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toSite)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toSite);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to move to start a Bombing Run.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromLocation the location to move from
	 * @param toLocation the location to move to
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveToStartBombingRunCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		if (fromLocation != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}
		}

		if (toLocation != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}
		}

		// Default move to start Bombing Run cost
		float result = 1 + changeInCost;

		// Add 1 to move to start Bombing Run cost for each cloud sector between locations
		result += Filters.filter(getSectorsBetween(gameState, fromLocation, toLocation), gameState.getGame(), Filters.cloud_sector).size();

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromLocation != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
			}
		}

		if (toLocation != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to be shuttled.
	 * @param gameState the game state
	 * @param card the card to be shuttled
	 * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
	 * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getShuttleCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost) {

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if shuttles for free
		if (!getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE, card).isEmpty())
			return 0;

		if (fromLocation != null) {
			// Check if moves for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if shuttles for free from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_FROM_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
					return 0;
				}
			}

			// Check if shuttle for free from location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), fromLocation)) {
				return 0;
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check if moves for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}

			// Check if shuttles for free from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_FROM_LOCATION_TO_LOCATION, card)) {
				if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
					return 0;
				}
			}
		}

		if (toLocation != null) {
			// Check if moves for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if shuttles for free to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SHUTTLES_FOR_FREE_TO_LOCATION, card)) {
				if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
					return 0;
				}
			}

			// Check if shuttle for free to location (instead of related starship)
			if (mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(gameState, card.getOwner(), toLocation)) {
				return 0;
			}
		}

		// Default shuttling cost
		float result = 1 + changeInCost;

		// Add 1 to shuttling cost for each cloud sector between locations
		result += Filters.filter(getSectorsBetween(gameState, fromLocation, toLocation), gameState.getGame(), Filters.cloud_sector).size();

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		if (fromLocation != null) {
			// Moving from collapsed site requires 1 additional Force
			if (fromLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving from location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
				result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
			}
		}

		if (fromLocation != null && toLocation != null) {
			// Check for modifiers to move cost when moving from location to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
				result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
			}
		}

		if (toLocation != null) {
			// Moving to collapsed site requires 1 additional Force
			if (toLocation.isCollapsed()) {
				result += 1;
			}

			// Check for modifiers to move cost when moving to location
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
				result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to embark.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param moveTo the card to embark on
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getEmbarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost) {

		// Check if this is a embark on 'crashed' enclosed vehicle
		if (moveTo.isCrashed() && moveTo.getBlueprint().hasKeyword(Keyword.ENCLOSED)) {

			// Check if moves for free
			if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
				return 0;

			return Math.max(1, changeInCost);
		}

		return Math.max(0, changeInCost);
	}

	/**
	 * Gets the amount of Force needed for the card to disembark.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param moveTo the card to disembark to
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getDisembarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost) {

		// Check if this is a disembark from a 'crashed' enclosed vehicle
		if (card.getAttachedTo() != null
				&& card.getAttachedTo().isCrashed() && card.getAttachedTo().getBlueprint().hasKeyword(Keyword.ENCLOSED)) {

			// Check if moves for free
			if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
				return 0;

			return Math.max(1, changeInCost);
		}

		return Math.max(0, changeInCost);
	}

	/**
	 * Determines if characters aboard vehicle may "jump off" when vehicle is about to be lost.
	 * @param gameState the game state
	 * @param card the vehicle
	 * @return true if characters may "jump off", otherwise false
	 */
	default boolean allowsCharactersAboardToJumpOff(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.CHARACTERS_ABOARD_MAY_JUMP_OFF, card).isEmpty());
	}

	/**
	 * Gets the amount of Force needed for the cards to ship-dock.
	 * @param gameState the game state
	 * @param starship1 a starship to ship-dock
	 * @param starship2 a starship to ship-dock
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the ship-docking cost
	 */
	default float getShipdockingCost(GameState gameState, PhysicalCard starship1, PhysicalCard starship2, float changeInCost) {
		// Check if either starship moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, starship1).isEmpty())
			return 0;

		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, starship2).isEmpty())
			return 0;

		// Check if either starship ship-docks for free
		if (!getModifiersAffectingCard(gameState, ModifierType.SHIPDOCKS_FOR_FREE, starship1).isEmpty())
			return 0;

		if (!getModifiersAffectingCard(gameState, ModifierType.SHIPDOCKS_FOR_FREE, starship2).isEmpty())
			return 0;

		return Math.max(1, changeInCost);
	}

	/**
	 * Gets the amount of Force needed for the card to move using location text.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromLocation the location to move from (or location the starship/vehicle is at if move from a starship/vehicle)
	 * @param toLocation the location to move to (or location the starship/vehicle is at if move to a starship/vehicle)
	 * @param baseCost base cost in amount of Force required to perform the movement
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getMoveUsingLocationTextCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost, float changeInCost) {
		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if moves for free from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
				return 0;
			}
		}

		// Check if moves for free from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
				return 0;
			}
		}

		// Check if moves for free to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
				return 0;
			}
		}

		// Default move using location text movement cost
		float result = baseCost + changeInCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Moving from collapsed site requires 1 additional Force
		if (fromLocation.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
		}

		// Check for modifiers to move cost when moving from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
		}

		// Moving to collapsed site requires 1 additional Force
		if (toLocation.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to move using docking bay transit.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromDockingBay the docking bay to move from
	 * @param toDockingBay the docking bay to move to
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @return the move cost
	 */
	default float getDockingBayTransitCost(GameState gameState, PhysicalCard card, PhysicalCard fromDockingBay, PhysicalCard toDockingBay, float changeInCost) {
		String playerId = card.getOwner();

		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty()) {
			return 0;
		}

		// Check if moves for free from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromDockingBay)) {
				return 0;
			}
		}

		// Check if moves for free from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromDockingBay, toDockingBay)) {
				return 0;
			}
		}

		// Check if moves for free to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toDockingBay)) {
				return 0;
			}
		}

		// Check if docking bay transiting to ignores cost of other docking bay
		boolean ignoreFromDockingBayCost = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORE_OTHER_DOCKING_BAY_TRANSIT_COST, toDockingBay)) {
			if (modifier.isForPlayer(playerId) && modifier.getSource(gameState).getCardId() == toDockingBay.getCardId()) {
				ignoreFromDockingBayCost = true;
				break;
			}
		}

		// Default move using docking bay transit cost
		float result = 0 + changeInCost;

		// Check for docking bay transits free from docking bay
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_FROM_FOR_FREE, fromDockingBay)) {
			if (modifier.isForPlayer(playerId)
					&& (!ignoreFromDockingBayCost || modifier.getSource(gameState).getCardId() != fromDockingBay.getCardId())) {
				return 0;
			}
		}

		// Check docking bay transits cost from docking bay
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_FROM_COST, fromDockingBay)) {
			if (modifier.isForPlayer(playerId)
					&& (!ignoreFromDockingBayCost || modifier.getSource(gameState).getCardId() != fromDockingBay.getCardId())) {
				result += modifier.getValue(gameState, query(), fromDockingBay);
				break;
			}
		}

		// Check if docking bay transiting to ignores cost of other docking bay
		boolean ignoreToDockingBayCost = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORE_OTHER_DOCKING_BAY_TRANSIT_COST, fromDockingBay)) {
			if (modifier.isForPlayer(playerId) && modifier.getSource(gameState).getCardId() == fromDockingBay.getCardId()) {
				ignoreToDockingBayCost = true;
				break;
			}
		}

		// Check for docking bay transits free to docking bay
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_TO_FOR_FREE, toDockingBay)) {
			if (modifier.isForPlayer(playerId)
					&& (!ignoreToDockingBayCost || modifier.getSource(gameState).getCardId() != toDockingBay.getCardId())) {
				return 0;
			}
		}

		// Check docking bay transits cost to docking bay
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DOCKING_BAY_TRANSIT_TO_COST, toDockingBay)) {
			if (modifier.isForPlayer(playerId)
					&& (!ignoreToDockingBayCost || modifier.getSource(gameState).getCardId() != toDockingBay.getCardId())) {
				result += modifier.getValue(gameState, query(), toDockingBay);
				break;
			}
		}

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Moving from collapsed site requires 1 additional Force
		if (fromDockingBay.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromDockingBay)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromDockingBay);
		}

		// Check for modifiers to move cost when moving from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromDockingBay, toDockingBay);
		}

		// Moving to collapsed site requires 1 additional Force
		if (toDockingBay.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toDockingBay)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toDockingBay);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the card to relocate between locations.
	 * @param gameState the game state
	 * @param card the card to move
	 * @param fromLocation the location to relocate from
	 * @param toLocation the location to relocate to
	 * @param baseCost the base cost (as defined by the card performing the relocation)
	 * @return the move cost
	 */
	default float getRelocateBetweenLocationsCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost) {
		// Check if moves for free
		if (!getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE, card).isEmpty())
			return 0;

		// Check if moves for free from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocation(gameState, query(), fromLocation)) {
				return 0;
			}
		}

		// Check if moves for free from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_FROM_LOCATION_TO_LOCATION, card)) {
			if (modifier.isMoveFreeFromLocationToLocation(gameState, query(), fromLocation, toLocation)) {
				return 0;
			}
		}

		// Check if moves for free to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVES_FREE_TO_LOCATION, card)) {
			if (modifier.isMoveFreeToLocation(gameState, query(), toLocation)) {
				return 0;
			}
		}

		// Default relocation cost
		float result = baseCost;

		// Check for modifiers to move cost
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST, card)) {
			result += modifier.getMoveCostModifier(gameState, query(), card);
		}

		// Moving from collapsed site requires 1 additional Force
		if (fromLocation.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving from location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION, fromLocation)) {
			result += modifier.getMoveCostFromLocationModifier(gameState, query(), card, fromLocation);
		}

		// Check for modifiers to move cost when moving from location to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_FROM_LOCATION_TO_LOCATION, card)) {
			result += modifier.getMoveCostFromLocationToLocationModifier(gameState, query(), card, fromLocation, toLocation);
		}

		// Moving to collapsed site requires 1 additional Force
		if (toLocation.isCollapsed()) {
			result += 1;
		}

		// Check for modifiers to move cost when moving to location
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MOVE_COST_TO_LOCATION, toLocation)) {
			result += modifier.getMoveCostToLocationModifier(gameState, query(), card, toLocation);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the amount of Force needed for the cards to relocate between locations.
	 * @param gameState the game state
	 * @param cards the cards to move
	 * @param fromLocation the location to relocate from
	 * @param toLocation the location to relocate to
	 * @param baseCost the base cost (as defined by the card performing the relocation)
	 */
	default float getRelocateBetweenLocationsCost(GameState gameState, Collection<PhysicalCard> cards, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost) {
		float maxCost = 0;

		for (PhysicalCard cardToMove : cards) {
			maxCost = Math.max(maxCost, getRelocateBetweenLocationsCost(gameState, cardToMove, fromLocation, toLocation, baseCost));
		}

		return maxCost;
	}
}
