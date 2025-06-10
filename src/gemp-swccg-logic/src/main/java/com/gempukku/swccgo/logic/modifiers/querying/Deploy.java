package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

public interface Deploy extends BaseQuery, Attributes, CardTraits, Destiny, Locations, Captives, Piloting, Prohibited {

	/**
	 * Determines if the card is deployable.
	 * @param gameState the game state
	 * @param sourceCard the card to initiate the deployment
	 * @param cardToDeploy the card
	 * @param includePlayable true if includes playable cards, false if only deployable cards
	 * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
	 * @param forFree true if playing the card for free, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
	 * @param deploymentOption specifies special deployment options, or null
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @param reactActionOption a 'react' action option, or null if not a 'react'
	 * @param cardToDeployWith the card to deploy with simultaneously
	 * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
	 * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
	 * @return true if card can be played or deployed, otherwise false
	 */
	default boolean isDeployable(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
		if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
			return false;
		}

		float changeInCostToUse = (changeInCost != 0 && (changeInCostCardFilter == null || changeInCostCardFilter.accepts(gameState, query(), cardToDeploy))) ? changeInCost : 0;

		if (cardToDeploy.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
			return cardToDeploy.getBlueprint().getPlayCardAction(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, Filters.any, specialLocationConditions) != null;
		}
		else {
			return !cardToDeploy.getBlueprint().getPlayCardActions(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, Filters.any, null).isEmpty();
		}
	}

	/**
	 * Determines if the card is deployable to target.
	 * @param gameState the game state
	 * @param sourceCard the card to initiate the deployment
	 * @param cardToDeploy the card to deploy
	 * @param includePlayable true if includes playable cards, false if only deployable cards
	 * @param targetFilter the target filter
	 * @param forFree true if playing the card for free, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
	 * @param deploymentOption specifies special deployment options, or null
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
	 * @param reactActionOption a 'react' action option, or null if not a 'react'
	 * @param cardToDeployWith the card to deploy with simultaneously
	 * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
	 * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
	 * @return true if card can be played or deployed, otherwise false
	 */
	default boolean isDeployableToTarget(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, Filter targetFilter, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
		if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
			return false;
		}

		float changeInCostToUse = (changeInCost != 0 && (changeInCostCardFilter == null || changeInCostCardFilter.accepts(gameState, query(), cardToDeploy))) ? changeInCost : 0;

		return !cardToDeploy.getBlueprint().getPlayCardActions(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, forFree, changeInCostToUse, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, targetFilter, null).isEmpty();
	}

	/**
	 * Determines if the card is deployable to the system.
	 * @param gameState the game state
	 * @param sourceCard the card to initiate the deployment
	 * @param cardToDeploy the card to deploy
	 * @param includePlayable true if includes playable cards, false if only deployable cards
	 * @param systemName the system name
	 * @param targetFilter the target filter
	 * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
	 * @param forFree true if playing the card for free, otherwise false
	 * @param changeInCost change in amount of Force (can be positive or negative) required
	 * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
	 * @param deploymentOption specifies special deployment options, or null
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @param reactActionOption a 'react' action option, or null if not a 'react'
	 * @param cardToDeployWith the card to deploy with simultaneously
	 * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
	 * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
	 * @return true if card can be played or deployed, otherwise false
	 */
	default boolean isDeployableToSystem(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean includePlayable, String systemName, Filter targetFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost) {
		if (!includePlayable && !cardToDeploy.getBlueprint().isCardTypeDeployed()) {
			return false;
		}

		if (cardToDeploy.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
			return cardToDeploy.getBlueprint().getPlayLocationToSystemAction(cardToDeploy.getOwner(), gameState.getGame(), cardToDeploy, sourceCard, systemName, specialLocationConditions) != null;
		}
		else {
			return isDeployableToTarget(gameState, sourceCard, cardToDeploy, includePlayable, Filters.and(Filters.locationAndCardsAtLocation(Filters.partOfSystem(systemName)), targetFilter), forFree, changeInCost, changeInCostCardFilter, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost);
		}
	}

	// The deploy cost not specific to deploying to a specific target
	default float getDeployCost(GameState gameState, PhysicalCard cardToDeploy) {
		return getDeployCost(gameState, cardToDeploy, cardToDeploy, false, false);
	}

	// The deploy cost not specific to deploying to a specific target
	default float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, boolean isDejarikRules, boolean includeExtraCost) {
		return getDeployCost(gameState, sourceCard, cardToDeploy, null, false, isDejarikRules, null, false, 0, null, null, includeExtraCost, new ModifierCollectorImpl());
	}

	// If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
	default float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost) {
		return getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, isDejarikRules, playCardOption, forFree, changeInCost, reactActionOption, null, includeExtraCost, new ModifierCollectorImpl());
	}

	// If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
	default float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost, ModifierCollector modifierCollector) {
		return getDeployCost(gameState, sourceCard, cardToDeploy, targetCard, false, isDejarikRules, playCardOption, forFree, changeInCost, reactActionOption, null, includeExtraCost, modifierCollector);
	}

	// If targetCard is null, then it is the deploy cost not specific to deploying to a specific target
	// Optionally can skip free check since other local methods may have already checked that
	private float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard cardToDeploy, PhysicalCard targetCard, boolean skipFreeCheck, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, PhysicalCard withPilot, boolean includeExtraCost, ModifierCollector modifierCollector) {
		if (!forFree && reactActionOption != null) {
			forFree = reactActionOption.isForFree();
			if (!forFree) {
				forFree = reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(gameState, query(), cardToDeploy);
			}
		}

		float extraCost = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, cardToDeploy, targetCard, null, sourceCard, forFree) : 0;

		if (forFree) {
			return extraCost;
		}

		String owner = cardToDeploy.getOwner();
		String opponent = gameState.getOpponent(owner);
		boolean deployCostMayNotBeModified = isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, null, modifierCollector);
		boolean deployCostMayNotBeModifiedByOwner = deployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, owner, modifierCollector);
		boolean deployCostMayNotBeModifiedByOpponent = deployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, cardToDeploy, opponent, modifierCollector);
		boolean deployCostMayNotBeIncreased = deployCostMayNotBeModified || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, null, modifierCollector);
		boolean deployCostMayNotBeIncreasedByOwner = deployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, owner, modifierCollector);
		boolean deployCostMayNotBeIncreasedByOpponent = deployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, cardToDeploy, opponent, modifierCollector);

		// Use destiny number instead if "Dejarik Rules"
		if (isDejarikRules || cardToDeploy.isDejarikHologramAtHolosite()) {
			Float result = getDestiny(gameState, cardToDeploy) + extraCost;

			// Check if deploy cost using dejarik rules is modified by something else already in play
			if (!deployCostMayNotBeModified) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_USING_DEJARIK_RULES, cardToDeploy)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
						float modifierValue = modifier.getDeployCostModifier(gameState, query(), cardToDeploy);
						if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
							result += modifierValue;
						}
					}
				}
			}

			result = Math.max(0, result);
			return result;
		}

		if (!skipFreeCheck) {
			// Check if the card deploys for free
			if (grantedDeployForFree(gameState, cardToDeploy, targetCard, modifierCollector)) {
				return extraCost;
			}
		}

		// Check if deploy cost is determined by a calculation, instead of normally
		Float deployCostViaCalculation = getDeployCostFromCalculation(gameState, cardToDeploy, modifierCollector);
		if (deployCostViaCalculation != null) {
			return deployCostViaCalculation + extraCost;
		}

		Float result = cardToDeploy.getBlueprint().getDeployCost();

		// Check if deploy cost is specified by game text
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST, cardToDeploy)) {
			if (modifier.isPlayCardOption(playCardOption)) {
				result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getPrintedValueDefinedByGameText(gameState, query(), cardToDeploy));
				modifierCollector.addModifier(modifier);
			}
		}

		// Check if deploy cost to specific targets is specified by game text
		if (targetCard != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
				if (modifier.isDefinedDeployCostToTarget(gameState, query(), targetCard)) {
					result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getDefinedDeployCostToTarget(gameState, query(), targetCard));
					modifierCollector.addModifier(modifier);
				}
			}
		}

		// If value if undefined, then return 0
		if (result == null)
			return extraCost;

		// If card is a character and it is "doubled", then double the printed number
		if (cardToDeploy.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& isDoubled(gameState, cardToDeploy, modifierCollector)) {
			result *= 2;
		}

		float totalReduceCostModifiers = 0;

		// Check for change in cost
		if (!deployCostMayNotBeModified) {
			boolean modifiedByOwner = sourceCard == null || sourceCard.getOwner().equals(owner);

			if ((modifiedByOwner && !deployCostMayNotBeModifiedByOwner)
					|| (!modifiedByOwner && !deployCostMayNotBeModifiedByOpponent)) {
				if (reactActionOption != null) {
					if (reactActionOption.getChangeInCost() < 0
							|| (modifiedByOwner && !deployCostMayNotBeIncreasedByOwner)
							|| (!modifiedByOwner && !deployCostMayNotBeIncreasedByOpponent)) {
						result += reactActionOption.getChangeInCost();
						if (reactActionOption.getChangeInCost() < 0) {
							totalReduceCostModifiers -= reactActionOption.getChangeInCost();
						}
					}
				} else {
					if (changeInCost < 0
							|| (modifiedByOwner && !deployCostMayNotBeIncreasedByOwner)
							|| (!modifiedByOwner && !deployCostMayNotBeIncreasedByOpponent)) {
						result += changeInCost;
						if (changeInCost < 0) {
							totalReduceCostModifiers -= changeInCost;
						}
					}
				}
			}
		}

		// Check if deploy cost is modified by something else already in play
		if (!deployCostMayNotBeModified) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST, cardToDeploy)) {
				String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
				if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
					if (targetCard == null || !isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
						float modifierValue = modifier.getDeployCostModifier(gameState, query(), cardToDeploy);
						if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
							result += modifierValue;
							if (modifierValue < 0) {
								totalReduceCostModifiers -= modifierValue;
								modifierCollector.addModifier(modifier);
							}
						}
					}
				}
			}

			// Check if deploy cost of starship is modified when simultaneously deployed with pilot
			if (withPilot != null) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_WITH_PILOT, cardToDeploy)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
						if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
							float modifierValue = modifier.getDeployCostWithPilotModifier(gameState, query(), withPilot);
							if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									totalReduceCostModifiers -= modifierValue;
									modifierCollector.addModifier(modifier);
								}
							}
						}
					}
				}
			}
		}

		// Check if deploy cost is affected when deployed to specific targets
		if (targetCard != null) {

			if (!deployCostMayNotBeModified) {

				// Deploying to collapsed site requires 1 additional Force
				if (!deployCostMayNotBeIncreased) {
					PhysicalCard location = getLocationHere(gameState, targetCard);
					if (location != null && location.isCollapsed()) {
						result += 1;
					}
				}

				// From something else already in play
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, targetCard)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeModifiedByOpponent : deployCostMayNotBeModifiedByOwner)) {
						if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
							float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), cardToDeploy, targetCard);
							if (modifierValue < 0 || (!deployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? deployCostMayNotBeIncreasedByOpponent : deployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									totalReduceCostModifiers -= modifierValue;
									modifierCollector.addModifier(modifier);
								}
							}
						}
					}
				}

				// From self
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SELF_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
					if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, cardToDeploy, targetCard, modifier.getSource(gameState))) {
						float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), cardToDeploy, targetCard);
						if (modifierValue < 0 || (!deployCostMayNotBeIncreased && !deployCostMayNotBeIncreasedByOwner)) {
							result += modifierValue;
							if (modifierValue < 0) {
								totalReduceCostModifiers -= modifierValue;
								modifierCollector.addModifier(modifier);
							}
						}
					}
				}
			}
		}

		// Check the most that the deploy cost can be modified (reduced) by
		float maxToReduceCostBy = Float.MAX_VALUE;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY, cardToDeploy)) {
			maxToReduceCostBy = Math.max(0, Math.min(maxToReduceCostBy, modifier.getMaximumToReduceDeployCostBy(gameState, query(), cardToDeploy)));
			modifierCollector.addModifier(modifier);
		}
		if (maxToReduceCostBy != Float.MAX_VALUE) {
			result += Math.max(0, totalReduceCostModifiers - maxToReduceCostBy);
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		if (targetCard != null) {
			Float lowestResetValue = null;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST_TO_TARGET, cardToDeploy)) {
				if (modifier.isAffectedTarget(gameState, query(), targetCard)) {
					float modifierAmount = modifier.getValue(gameState, query(), cardToDeploy);
					lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
					modifierCollector.addModifier(modifier);
				}
			}
			if (lowestResetValue != null) {
				result = lowestResetValue;
			}
		}
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST, cardToDeploy)) {
			float modifierAmount = modifier.getValue(gameState, query(), cardToDeploy);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			modifierCollector.addModifier(modifier);
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result) + extraCost;
	}

	default float getSimultaneousDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard starship, boolean starshipForFree, float starshipChangeInCost, PhysicalCard pilot, boolean pilotForFree, float pilotChangeInCost, PhysicalCard targetCard, ReactActionOption reactActionOption, boolean includeExtraCost) {
		float extraCostForStarship = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, starship, targetCard, null, sourceCard, starshipForFree) : 0;
		float extraCostForPilot = includeExtraCost ? getExtraForceRequiredToDeployToTarget(gameState, pilot, starship, targetCard, sourceCard, pilotForFree) : 0;

		// Check if 'react' and 'react' is free
		if (reactActionOption != null && (reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null
				&& reactActionOption.getForFreeCardFilter().accepts(gameState, query(), starship) && reactActionOption.getForFreeCardFilter().accepts(gameState, query(), pilot)))) {
			return extraCostForStarship + extraCostForPilot;
		}

		float starshipDeployCost = 0;
		if (!starshipForFree) {
			starshipDeployCost = getDeployCost(gameState, sourceCard, starship, targetCard, false, false, null, false, starshipChangeInCost, reactActionOption, pilot, false, new ModifierCollectorImpl());
		}
		starshipDeployCost = (starshipDeployCost + extraCostForStarship);

		// Check if pilot deploys for free
		if (pilotForFree || grantedDeployForFree(gameState, pilot, starship) || grantedDeployForFree(gameState, pilot, targetCard)) {
			return starshipDeployCost + extraCostForPilot;
		}

		// Check if pilot deploys for free when simultaneously deployed with ship
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOYS_FOR_FREE, starship)) {
			if (modifier.isAffectedPilot(gameState, query(), pilot)
					&& modifier.isAffectedTarget(gameState, query(), targetCard)) {
				return starshipDeployCost + extraCostForPilot;
			}
		}

		float pilotCost = 0;

		Float result = pilot.getBlueprint().getDeployCost();

		// Check if deploy cost is specified by game text
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST, pilot)) {
			result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getPrintedValueDefinedByGameText(gameState, query(), pilot));
		}

		// Check if deploy cost to specific targets is specified by game text
		if (targetCard != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DEPLOY_COST_TO_TARGET, pilot)) {
				if (modifier.isDefinedDeployCostToTarget(gameState, query(), targetCard)) {
					result = Math.min(result != null ? result : Float.MAX_VALUE, modifier.getDefinedDeployCostToTarget(gameState, query(), targetCard));
				}
			}
		}

		// If value if undefined, then return 0
		if (result != null) {

			// If card is a character and it is "doubled", then double the printed number
			if (pilot.getBlueprint().getCardCategory() == CardCategory.CHARACTER
					&& isDoubled(gameState, pilot)) {
				result *= 2;
			}

			String owner = starship.getOwner();
			String opponent = gameState.getOpponent(owner);
			boolean pilotDeployCostMayNotBeModified = isDeployCostNotAllowedToBeModified(gameState, starship, null);
			boolean pilotDeployCostMayNotBeModifiedByOwner = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, pilot, owner);
			boolean pilotDeployCostMayNotBeModifiedByOpponent = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeModified(gameState, pilot, opponent);
			boolean pilotDeployCostMayNotBeIncreased = pilotDeployCostMayNotBeModified || isDeployCostNotAllowedToBeIncreased(gameState, pilot, null);
			boolean pilotDeployCostMayNotBeIncreasedByOwner = pilotDeployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, pilot, owner);
			boolean pilotDeployCostMayNotBeIncreasedByOpponent = pilotDeployCostMayNotBeIncreased || isDeployCostNotAllowedToBeIncreased(gameState, pilot, opponent);

			float pilotTotalReduceCostModifiers = 0;

			if (!pilotDeployCostMayNotBeModified) {

				// Check for change in cost
				if (pilotChangeInCost < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
					result += pilotChangeInCost;
					if (pilotChangeInCost < 0) {
						pilotTotalReduceCostModifiers -= pilotChangeInCost;
					}
				}

				// Deploying to collapsed site requires 1 additional Force
				if (!pilotDeployCostMayNotBeIncreased) {
					PhysicalCard location = getLocationHere(gameState, targetCard);
					if (location != null && location.isCollapsed()) {
						result += 1;
					}
				}

				// Check if deploy cost is modified by something else already in play
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST, pilot)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
						if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, starship, modifier.getSource(gameState))
								&& !isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState))) {
							float modifierValue = modifier.getDeployCostModifier(gameState, query(), pilot);
							if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									pilotTotalReduceCostModifiers -= modifierValue;
								}
							}
						}
					}
				}
				// Check if deploy cost is affected when deployed to specific targets
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, starship)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
						if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, starship, modifier.getSource(gameState))) {
							float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), pilot, starship);
							if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									pilotTotalReduceCostModifiers -= modifierValue;
								}
							}
						}
					}
				}
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_COST_TO_TARGET, targetCard)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
						if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState))) {
							float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), pilot, targetCard);
							if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									pilotTotalReduceCostModifiers -= modifierValue;
								}
							}
						}
					}
				}
				// Check if deploy cost is affected when simultaneously deployed with ship
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOY_COST, starship)) {
					String modifierOwner = modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null;
					if (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeModifiedByOpponent : pilotDeployCostMayNotBeModifiedByOwner)) {
						if (modifier.isAffectedPilot(gameState, query(), pilot)
								&& modifier.isAffectedTarget(gameState, query(), targetCard)
								&& (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState)))) {
							float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), pilot, targetCard);
							if (modifierValue < 0 || (!pilotDeployCostMayNotBeIncreased && (modifierOwner == null || !(opponent.equals(modifierOwner) ? pilotDeployCostMayNotBeIncreasedByOpponent : pilotDeployCostMayNotBeIncreasedByOwner)))) {
								result += modifierValue;
								if (modifierValue < 0) {
									pilotTotalReduceCostModifiers -= modifierValue;
								}
							}
						}
					}
				}
				// From self
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SELF_DEPLOY_COST_TO_TARGET, pilot)) {
					if (!isImmuneToDeployCostToTargetModifierFromCard(gameState, pilot, targetCard, modifier.getSource(gameState))) {
						float modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), pilot, starship);
						if (modifierValue < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
							result += modifierValue;
							if (modifierValue < 0) {
								pilotTotalReduceCostModifiers -= modifierValue;
							}
						}
						modifierValue = modifier.getDeployCostToTargetModifier(gameState, query(), pilot, targetCard);
						if (modifierValue < 0 || !pilotDeployCostMayNotBeIncreasedByOwner) {
							result += modifierValue;
							if (modifierValue < 0) {
								pilotTotalReduceCostModifiers -= modifierValue;
							}
						}
					}
				}
			}

			// Check the most that the deploy cost can be modified (reduced) by
			float pilotMaxToReduceCostBy = Float.MAX_VALUE;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_AMOUNT_TO_REDUCE_DEPLOY_COST_BY, pilot)) {
				pilotMaxToReduceCostBy = Math.max(0, Math.min(pilotMaxToReduceCostBy, modifier.getMaximumToReduceDeployCostBy(gameState, query(), pilot)));
			}
			if (pilotMaxToReduceCostBy != Float.MAX_VALUE) {
				result += Math.max(0, pilotTotalReduceCostModifiers - pilotMaxToReduceCostBy);
			}

			// Check if value was reset to an "unmodifiable value", and use lowest found
			Float lowestResetValue = null;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST_TO_TARGET, pilot)) {
				if (modifier.isAffectedTarget(gameState, query(), starship)
						|| modifier.isAffectedTarget(gameState, query(), targetCard)) {
					float modifierAmount = modifier.getValue(gameState, query(), pilot);
					lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				}
			}
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DEPLOY_COST, pilot)) {
				float modifierAmount = modifier.getValue(gameState, query(), pilot);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			}
			if (lowestResetValue != null) {
				result = lowestResetValue;
			}

			pilotCost = result;
		}

		return starshipDeployCost + Math.max(0, pilotCost) + extraCostForPilot;
	}

	default boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_DEPLOY_COST_MODIFIERS_TO_TARGET, cardToDeploy)) {
			if (modifier.isImmuneToDeployCostToTargetModifierFromCard(gameState, query(), cardToDeploy, deployToTarget, sourceOfModifier)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified interrupt plays for free.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if interrupt plays for free, otherwise false
	 */
	default boolean isInterruptPlayForFree(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.INTERRUPT_PLAYS_FOR_FREE, card).isEmpty();
	}

	/**
	 * Gets the amount of extra Force required to play the specified Interrupt.
	 * @param gameState the game state
	 * @param card the Interrupt card
	 * @return the amount of Force
	 */
	default int getExtraForceRequiredToPlayInterrupt(GameState gameState, PhysicalCard card) {
		int result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_PLAY_INTERRUPT, card)) {
			result += modifier.getValue(gameState, query(), card);
		}
		result = Math.max(0, result);
		return result;
	}

	/**
	 * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
	 * being deployed to may be specified.
	 * @param gameState the game state
	 * @param card a card
	 * @param targetCard the target, or null if not deploying to specific target
	 * @return true if card is granted the ability to deploy for free to target, otherwise false
	 */
	default boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard targetCard) {
		return grantedDeployForFree(gameState, card, targetCard, new ModifierCollectorImpl());
	}

	/**
	 * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
	 * being deployed to may be specified.
	 * @param gameState the game state
	 * @param card a card
	 * @param targetCard the target, or null if not deploying to specific target
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if card is granted the ability to deploy for free to target, otherwise false
	 */
	default boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard targetCard, ModifierCollector modifierCollector) {
		boolean isAlwaysFree = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE, card)) {
			isAlwaysFree = true;
			modifierCollector.addModifier(modifier);
		}
		if (isAlwaysFree) {
			return true;
		}

		// Check if card deploys for free to specified target
		if (targetCard != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE_TO_TARGET, card)) {
				if (modifier.isDeployFreeToTarget(gameState, query(), targetCard)) {
					return true;
				}
				// Check if self deployment modifier is applied at any location
				if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == card.getCardId()
						&& appliesOwnDeploymentModifiersAtAnyLocation(gameState, card)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Determines if a card deploys for free.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card deploys for free, otherwise false
	 */
	default boolean deploysForFree(GameState gameState, PhysicalCard card) {
		if (!hasDeployCostAttribute(card))
			return false;

		/*
		 * The AR 1-20 defines "free" as always being free and no deploy cost at all.
		 *
		 * We need to ignore things that may have allowed it to be free
		 * such as General McQuarrie with Hoth Sentry.
		 *
		 * That's why the 'grantedDeployForFree' has been removed below
		 *
		 * if (grantedDeployForFree(gameState, card, null))
		 *   return true;
		 */

		if (card.getBlueprint().getSpecialDeployCostEffect(null, card.getOwner(), gameState.getGame(), card, null, null) != null)
			return false;

		return getDeployCost(gameState, card, card, null, true, false, null, false, 0, null, null, false, new ModifierCollectorImpl()) == 0;
	}

	/**
	 * Determines if a card's deploy cost is less than or equal to a specified cost.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the cost
	 * @return true if card's deploy cost is less than or equal to the specified cost, otherwise false
	 */
	default boolean hasDeployCostLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value) {
		if (!hasDeployCostAttribute(card))
			return false;

		return getDeployCost(gameState, card) <= value;
	}

	/**
	 * Determines if the specified card is explicitly granted the ability to be deployed during the current phase.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card is granted the ability to deploy during the current phase, otherwise false
	 */
	default boolean grantedDeployDuringCurrentPhase(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_DURING_CURRENT_PHASE, card).isEmpty());
	}

	/**
	 * Gets the amount of extra Force required to deploy the specified card to the specified target.
	 * @param gameState the game state
	 * @param cardToDeploy the card to deploy
	 * @param target the deploy target, or null
	 * @param targetOfAttachedTo if deploying simultaneously on another card, the target for the card this card will be attached to, otherwise null
	 * @param sourceCard the card to initiate the deployment
	 * @param forFree true if deploy card explicitly for free, otherwise false
	 * @return the amount of Force
	 */
	default int getExtraForceRequiredToDeployToTarget(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard target, PhysicalCard targetOfAttachedTo, PhysicalCard sourceCard, boolean forFree) {
		int result = 0;
		if (target != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_TO_TARGET, cardToDeploy)) {
				if (modifier.isAffectedTarget(gameState, query(), target)) {
					result += modifier.getValue(gameState, query(), cardToDeploy, target);
				}
			}
		}

		// Check if card is deploying for free, but not due to its own game text
		boolean forFreeOwnGameText = forFree && sourceCard != null && sourceCard.getCardId() == cardToDeploy.getCardId();
		if (!forFreeOwnGameText) {
			boolean forFreeNotFromOwnGameText = forFree;

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE, cardToDeploy)) {
				if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()) {
					forFreeOwnGameText = true;
					break;
				}
				else {
					forFreeNotFromOwnGameText = true;
				}
			}

			if (!forFreeOwnGameText) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_FREE_TO_TARGET, cardToDeploy)) {
					if (modifier.isDeployFreeToTarget(gameState, query(), target)) {
						if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()) {
							forFreeOwnGameText = true;
							break;
						}
						else {
							forFreeNotFromOwnGameText = true;
						}
					}
					// Check if self deployment modifier is applied at any location
					if (modifier.getSource(gameState) != null && modifier.getSource(gameState).getCardId() == cardToDeploy.getCardId()
							&& appliesOwnDeploymentModifiersAtAnyLocation(gameState, cardToDeploy)) {
						forFreeOwnGameText = true;
						break;
					}
				}

				if (!forFreeNotFromOwnGameText) {
					if (targetOfAttachedTo != null) {
						// Check if pilot deploys for free when simultaneously deployed with ship
						for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SIMULTANEOUS_PILOT_DEPLOYS_FOR_FREE, target)) {
							if (modifier.isAffectedPilot(gameState, query(), cardToDeploy)
									&& modifier.isAffectedTarget(gameState, query(), targetOfAttachedTo)) {
								forFreeNotFromOwnGameText = true;
								break;
							}
						}
					}
				}

				if (!forFreeOwnGameText && forFreeNotFromOwnGameText) {
					for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EXTRA_FORCE_COST_TO_DEPLOY_FOR_FREE_EXCEPT_BY_OWN_GAME_TEXT, cardToDeploy)) {
						result += modifier.getValue(gameState, query(), cardToDeploy);
					}
				}
			}
		}

		result = Math.max(0, result);
		return result;
	}



	/**
	 * Determines if the specified card's deploy cost is not allowed to be modified.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerModifyingCost the player to modify the deploy cost
	 * @return true if deploy cost may not be modified, otherwise false
	 */
	default boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost) {
		return isDeployCostNotAllowedToBeModified(gameState, card, playerModifyingCost, new ModifierCollectorImpl());
	}

	/**
	 * Determines if the specified card's deploy cost is not allowed to be modified.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerModifyingCost the player to modify the deploy cost
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if deploy cost may not be modified, otherwise false
	 */
	default boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_MODIFIED, card)) {
			if (modifier.isForPlayer(playerModifyingCost)) {
				retVal = true;
				modifierCollector.addModifier(modifier);
			}
		}
		return retVal;
	}

	/**
	 * Determines if the specified card's deploy cost is not allowed to be increased.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerIncreasingCost the player to increase the deploy cost
	 * @return true if deploy cost may not be increased, otherwise false
	 */
	default boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost) {
		return isDeployCostNotAllowedToBeIncreased(gameState, card, playerIncreasingCost, new ModifierCollectorImpl());
	}

	/**
	 * Determines if the specified card's deploy cost is not allowed to be increased.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerIncreasingCost the player to increase the deploy cost
	 * @param modifierCollector collector of affecting modifiers
	 * @return true if deploy cost may not be increased, otherwise false
	 */
	default boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost, ModifierCollector modifierCollector) {
		boolean retVal = false;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_INCREASED, card)) {
			if (modifier.isForPlayer(playerIncreasingCost)) {
				retVal = true;
				modifierCollector.addModifier(modifier);
			}
		}
		return retVal;
	}

	/**
	 * Gets the amount of Force needed for the card to be transferred to the target.
	 * @param gameState the game state
	 * @param cardToTransfer the card to be transferred
	 * @param target the target
	 * @param playCardOption the play card option chosen
	 * @return the transfer cost
	 */
	default float getTransferCost(GameState gameState, PhysicalCard cardToTransfer, PhysicalCard target, PlayCardOption playCardOption) {
		// Check if transfers for free to target
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TRANSFERS_FREE_TO_TARGET, cardToTransfer)) {
			if (modifier.isAffectedTarget(gameState, query(), target)) {
				return 0;
			}
		}
		return getDeployCost(gameState, cardToTransfer, cardToTransfer, target, false, playCardOption, false, 0, null, false);
	}

	default boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard) {
		return physicalCard.getBlueprint().isDeployUsingBothForcePiles();
	}

	default boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard, PhysicalCard targetCard) {
		boolean result = physicalCard.getBlueprint().isDeployUsingBothForcePiles();
		if (result) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DEPLOY_ONLY_USING_OWN_FORCE_TO_TARGET, physicalCard)) {
				if (modifier.isAffectedTarget(gameState, query(), targetCard))
					return false;
			}
		}
		return result;
	}

	/**
	 * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
	 * @param gameState the game state
	 * @param card a card
	 * @return the deploy cost as determined by a calculation, otherwise null
	 */
	default Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card) {
		return getDeployCostFromCalculation(gameState, card, new ModifierCollectorImpl());
	}

	/**
	 * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
	 * @param gameState the game state
	 * @param card a card
	 * @return the deploy cost as determined by a calculation, otherwise null
	 */
	default Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector) {
		Float value = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.USE_CALCULATION_FOR_DEPLOY_COST, card)) {
			float curValue = modifier.getValue(gameState, query(), card);
			if (value == null || curValue < value) {
				value = curValue;
				modifierCollector.addModifier(modifier);
			}
		}
		if (value != null) {
			return Math.max(0, value);
		}
		return null;
	}

	/**
	 * Determines if the specified card's own deployment modifiers are applied at any location.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean appliesOwnDeploymentModifiersAtAnyLocation(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.APPLIES_OWN_DEPLOYMENT_MODIFIERS_AT_ANY_LOCATION, card).isEmpty());
	}



	/**
	 * Determines if the card may deploy to the target without presence or Force icons.
	 * @param gameState the game state
	 * @param target the target
	 * @param cardToDeploy the card to deploy
	 * @return true if card can be deployed to the target without presence or Force icons, otherwise false
	 */
	default boolean mayDeployToTargetWithoutPresenceOrForceIcons(GameState gameState, PhysicalCard target, PhysicalCard cardToDeploy) {
		// Do not need presence to deploy a Spy (or card that deploys without presence or Force icons) or "moves like character"
		if (hasKeyword(gameState, cardToDeploy, Keyword.SPY) || cardToDeploy.getBlueprint().isMovesLikeCharacter())
			return true;

		// Check for deploy without presence or Force icons modifier
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITHOUT_PRESENCE_OR_FORCE_ICONS, cardToDeploy))
			if (modifier.isAffectedTarget(gameState, query(), target))
				return true;

		return false;
	}

	/**
	 * Determines if a pilot may deploy simultaneously with the card to the target without presence or Force icons.
	 * @param gameState the game state
	 * @param target the target
	 * @param starshipOrVehicle the card to deploy
	 * @return true if card can be deployed to the target without presence or Force icons, otherwise false
	 */
	default boolean mayDeployPilotSimultaneouslyToTargetWithoutPresenceOrForceIcons(GameState gameState, PhysicalCard target, PhysicalCard starshipOrVehicle) {
		// Only check starships or vehicles
		if (!getCardTypes(gameState, starshipOrVehicle).contains(CardType.STARSHIP)
				&& !getCardTypes(gameState, starshipOrVehicle).contains(CardType.VEHICLE))
			return false;

		// Check for deploy without presence or Force icons modifier
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_PILOT_SIMULTANEOUSLY_WITHOUT_PRESENCE_OR_FORCE_ICONS, starshipOrVehicle))
			if (modifier.isAffectedTarget(gameState, query(), target))
				return true;

		return false;
	}

	default boolean mayDeployAsIfFromHand(GameState gameState, PhysicalCard card) {
		if (card.getZone() != Zone.STACKED)
			return false;

		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_IF_FROM_HAND, card).isEmpty());
	}

	/**
	 * Determines if a card is allowed to be deployed instead of a starfighter using Combat Response.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if allowed, otherwise false
	 */
	default boolean mayDeployInsteadOfStarfighterUsingCombatResponse(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_INSTEAD_OF_STARFIGHTER_USING_COMBAT_RESPONSE, card).isEmpty());
	}

	/**
	 * Determines if a card is allowed to be deployed with the specified pilot instead of a matching starfighter using Combat Response.
	 * @param gameState the game state
	 * @param pilot the pilot
	 * @param card the card
	 * @return true if allowed, otherwise false
	 */
	default boolean mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(GameState gameState, PhysicalCard pilot, PhysicalCard card) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_WITH_INSTEAD_OF_MATCHING_STARFIGHTER_USING_COMBAT_RESPONSE, pilot)) {
			if (modifier.isAffectedTarget(gameState, query(), card)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if a card may be replaced (character converted) by opponent.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if allowed, otherwise false
	 */
	default boolean mayBeReplacedByOpponent(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_REPLACED_BY_OPPONENT, card).isEmpty()
				&& getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_REPLACED_BY_OPPONENT, card).isEmpty();
	}

	/**
	 * Determines if the affected cards is prohibited from deploying to the specified targeted.
	 * @param gameState the game state
	 * @param playedCard the card
	 * @param target the target card
	 * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
	 * @return true if card may not be deployed to target, otherwise false
	 */
	default boolean isProhibitedFromDeployingTo(GameState gameState, PhysicalCard playedCard, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption) {
		CardCategory cardCategory = playedCard.getBlueprint().getCardCategory();
		PhysicalCard location = getLocationHere(gameState, target);

		// Only Epic Events may deploy at a Death Star II sector
		if (cardCategory != CardCategory.EPIC_EVENT) {
			if (location != null && Filters.Death_Star_II_sector.accepts(gameState, query(), location)) {
				return true;
			}
		}

		// Only may deploy to Death Star: Trench if explicitly allowed
		if (deploymentRestrictionsOption == null || !deploymentRestrictionsOption.isAllowTrench()) {
			if (location != null && Filters.Death_Star_Trench.accepts(gameState, query(), location)) {
				if (!isGrantedToDeployTo(gameState, playedCard, target, null)) {
					return true;
				}
			}
		}

		// Check if card has (limit 1 per location)
		if (location != null
				&& (isOperativePreventedFromDeployingToOrMovingToLocation(gameState, playedCard, location)
				|| isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(gameState, playedCard, location))) {
			return true;
		}


		// Check if card has may not deploy restriction that is only ignored at certain locations and check if the restrictions should be ignored at this location
		if (location != null
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, playedCard).isEmpty()
				&& !getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION, playedCard).isEmpty()) {

			for (Modifier mayNotPlayModifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, playedCard)) {
				boolean mayNotPlay = true;

				if (mayNotPlayModifier.getSource(gameState) != null) {
					for (Modifier ignoreModifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION, playedCard)) {
						Filter cardFilter = ((IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier) ignoreModifier).getCardFilter();
						Filter locationFilter = ((IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier) ignoreModifier).getLocationFilter();

						if (locationFilter.accepts(gameState.getGame(), location)
								&& cardFilter.accepts(gameState.getGame(), mayNotPlayModifier.getSource(gameState))) {
							mayNotPlay = false;
						}
					}
				}

				if (mayNotPlay)
					return true;
			}
		}


		// Check if location deployment restrictions are ignored when deploying to specified target
		boolean ignoresLocationDeploymentRestrictions = ignoresLocationDeploymentRestrictions(gameState, playedCard, target, deploymentRestrictionsOption, false);
		boolean ignoresLocationDeploymentRestrictionsInGameText = ignoresLocationDeploymentRestrictions || ignoresGameTextLocationDeploymentRestrictions(gameState, playedCard);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_DEPLOY_TO_TARGET, playedCard)) {
			PhysicalCard sourceCard = modifier.getSource(gameState);
			if ((modifier.isAlwaysInEffect() || sourceCard == null
					|| ((!ignoresLocationDeploymentRestrictionsInGameText || !playedCard.equals(sourceCard))
					&& !ignoresLocationDeploymentRestrictionsFromSource(gameState, playedCard, sourceCard)))
					&& (location == null || !ignoresLocationDeploymentRestrictionsFromSourceWhenDeployingToTarget(gameState, playedCard, sourceCard, location))
					&& modifier.isAffectedTarget(gameState, query(), target)) {
				return true;
			}
		}

		if (!ignoresLocationDeploymentRestrictions) {

			// Check for "Hoth Energy Shield"
			if (playedCard.getOwner().equals(gameState.getDarkPlayer())
					&& (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE)) {
				if (location != null && isLocationUnderHothEnergyShield(gameState, location)) {
					return true;
				}
			}

			// Check for "Dagobah"
			if (cardCategory == CardCategory.CHARACTER || cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
				if (location != null && Filters.Dagobah_location.accepts(gameState, query(), location)) {
					// Check if allowed to deploy to Dagobah location (or target at Dagobah location)
					if (!grantedToDeployToDagobahTarget(gameState, playedCard, target)) {
						return true;
					}
				}
				// Check for Ahch-To
				if (location != null && Filters.AhchTo_location.accepts(gameState, query(), location)) {
					// Check if allowed to deploy to Ahch-To location (or target at Ahch-To location)
					if (!grantedToDeployToAhchToTarget(gameState, playedCard, target)) {
						return true;
					}
				}
			}
			else if (cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.WEAPON) {
				if (Filters.Dagobah_location.accepts(gameState, query(), target) || Filters.AhchTo_location.accepts(gameState, query(), target)) {
					return true;
				}
			}
		}

		return false;
	}


}
