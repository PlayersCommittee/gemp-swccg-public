package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

public interface Flags extends BaseQuery {
	default boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag) {
		return hasFlagActive(gameState, modifierFlag, null);
	}

	default boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.SPECIAL_FLAG))
			if (modifier.hasFlagActive(gameState, query(), modifierFlag, playerId))
				return true;

		return false;
	}

	default int getFlagActiveCount(GameState gameState, ModifierFlag modifierFlag, String playerId) {
		int count = 0;
		for (Modifier modifier : getModifiers(gameState, ModifierType.SPECIAL_FLAG)) {
			if (modifier.hasFlagActive(gameState, query(), modifierFlag, playerId)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Determines if sites are prevented from deploying between the specified sites.
	 * @param gameState the game state
	 * @param site1 a site on one side
	 * @param site2 a site on the other side
	 * @return true if allowed, otherwise false
	 */
	default boolean isSitePreventedFromDeployingBetweenSites(GameState gameState, PhysicalCard site1, PhysicalCard site2) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_DEPLOY_SITES_BETWEEN_SITES)) {
			if (modifier.mayNotDeploySiteBetweenSites(gameState, query(), site1, site2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if specified card is not allowed to be used to transport to or from specified location.
	 * @param gameState the game state
	 * @param card the card
	 * @param location the location
	 * @return true if not allowed, otherwise false
	 */
	default boolean prohibitedFromUsingCardToTransportToOrFromLocation(GameState gameState, PhysicalCard card, PhysicalCard location) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.CANT_USE_TO_TRANSPORT_TO_OR_FROM_LOCATION, card)) {
			if (modifier.isAffectedTarget(gameState, query(), location)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified Operative is prevented from deploying to or moving to location.
	 * @param gameState the game state
	 * @param card the Operative
	 * @param location the location
	 * @return true if Operative cannot deploy or move to location, otherwise false
	 */
	default boolean isOperativePreventedFromDeployingToOrMovingToLocation(GameState gameState, PhysicalCard card, PhysicalCard location) {
		// Special rule: A player may not move own Operative to same location on matching planet as another of that
		// player's Operatives with same title.
		if (Filters.operative.accepts(gameState, query(), card)) {
			String matchingSystem = card.getBlueprint().getMatchingSystem();
			if (Filters.and(Filters.on(matchingSystem), Filters.sameLocationAs(null, SpotOverride.INCLUDE_ALL,
					Filters.and(Filters.your(card), Filters.operative, Filters.sameTitleAs(card)))).accepts(gameState, query(), location)) {
				return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_DEPLOY_MOVE_OPERATIVE_RULE, card).isEmpty();
			}
		}
		return false;
	}

	/**
	 * Determines if the specified Sith Probe Droid is prevented from deploying to or moving to location.
	 * @param gameState the game state
	 * @param card the Sith Probe Droid
	 * @param location the location
	 * @return true if Sith Probe Droid cannot deploy or move to location, otherwise false
	 */
	default boolean isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(GameState gameState, PhysicalCard card, PhysicalCard location) {
		// Limit 1 Sith Probe Droid per location from Tatooine Sith Probe Droid
		// AR entry: The "limit 1 per location" text on this droid works as per the operative
		// rules (see Characteristics - Operatives, Ap. D). A player may not voluntarily deploy
		// or move a Sith Probe Droid to or across a location where another Sith Probe Droid is
		// located. If this should ever happen accidentally, the owner must choose one to be
		// lost. If they belong to different owners, the droid lost is determined randomly.

		if (Filters.Sith_Probe_Droid.accepts(gameState, query(), card)
				&&Filters.sameLocationAs(null, SpotOverride.INCLUDE_ALL,Filters.Sith_Probe_Droid).accepts(gameState, query(), location)) {
			return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_DEPLOY_MOVE_SITH_PROBE_DROID, card).isEmpty();
		}
		return false;
	}



	/**
	 * Determines if a card is allowed to make a Kessel Run if not a smuggler.
	 * @param gameState the game state
	 * @param card a card
	 * @return true if not allowed, otherwise false
	 */
	default boolean isAllowedToMakeKesselRunWhenNotSmuggler(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_MAKE_KESSEL_RUN_WHEN_NOT_SMUGGLER, card).isEmpty();
	}

	/**
	 * Determines if a location is under the "Hoth Energy Shield"
	 * @param gameState the game state
	 * @param location a location
	 * @return true if under "Hoth Energy Shield", otherwise false
	 */
	default boolean isLocationUnderHothEnergyShield(GameState gameState, PhysicalCard location) {
		return !getModifiersAffectingCard(gameState, ModifierType.UNDER_HOTH_ENERGY_SHIELD, location).isEmpty()
				&& getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_COVERED_BY_HOTH_ENERGY_SHIELD, location).isEmpty();
	}

	/**
	 * Determines if the specified card ignores location deployment restrictions from the source card.
	 * @param gameState the game state
	 * @param cardToDeploy the card to deploy
	 * @param sourceCard the source card of the location deployment restriction
	 * @return true if card ignores location deployment restrictions in its game text
	 */
	default boolean ignoresLocationDeploymentRestrictionsFromSource(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard sourceCard) {
		String playerId = cardToDeploy.getOwner();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_FROM_CARD, sourceCard)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card ignores location deployment restrictions from the source card.
	 * @param gameState the game state
	 * @param cardToDeploy the card to deploy
	 * @param sourceCard the source card of the location deployment restriction
	 * @param target the target it is deploying to
	 * @return true if card ignores location deployment restrictions in its game text
	 */
	default boolean ignoresLocationDeploymentRestrictionsFromSourceWhenDeployingToTarget(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard sourceCard, PhysicalCard target) {
		String playerId = cardToDeploy.getOwner();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION, cardToDeploy)) {
			Filter cardFilter = ((IgnoresLocationDeploymentRestrictionsFromCardWhenDeployingToLocationModifier)modifier).getCardFilter();

			if (target != null
					&& cardFilter.accepts(gameState.getGame(), sourceCard)
					&& modifier.isAffectedTarget(gameState, query(), target)
					&& modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified card ignores location deployment restrictions in its game text.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card ignores location deployment restrictions in its game text
	 */
	default boolean ignoresGameTextLocationDeploymentRestrictions(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_IN_GAME_TEXT, card).isEmpty());
	}

	/**
	 * Determines if the specified card is granted the ability to deploy to the specified target.
	 * @param gameState the game state
	 * @param card the card
	 * @param target the target
	 * @param reactActionOption a 'react' action option, or null if not a 'react'
	 * @return true if card is granted the ability to deploy to the target, otherwise false
	 */
	default boolean isGrantedToDeployTo(GameState gameState, PhysicalCard card, PhysicalCard target, ReactActionOption reactActionOption) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_TARGET, card)) {
			if (modifier.isAffectedTarget(gameState, query(), target)) {
				return true;
			}
		}

		if (reactActionOption != null) {
			if (reactActionOption.isGrantedDeployToTarget()
					&& Filters.and(reactActionOption.getCardToReactFilter()).accepts(gameState, query(), card)
					&& reactActionOption.getTargetFilter().accepts(gameState, query(), target)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the specified card is a squadron that is granted the ability to deploy.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is granted the ability to deploy, otherwise false
	 */
	default boolean isSquadronAllowedToDeploy(GameState gameState, PhysicalCard card) {
		return Filters.squadron.accepts(gameState, query(), card)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_TARGET, card).isEmpty();
	}

	default boolean grantedToDeployToDagobahTarget(GameState gameState, PhysicalCard playedCard, PhysicalCard target) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_DAGOBAH_TARGET, playedCard))
			if (modifier.grantedToDeployToDagobahTarget(gameState, query(), target))
				return true;

		return false;
	}

	default boolean grantedToDeployToAhchToTarget(GameState gameState, PhysicalCard playedCard, PhysicalCard target) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_TO_AHCHTO_TARGET, playedCard))
			if (modifier.grantedToDeployToAhchToTarget(gameState, query(), target))
				return true;

		return false;
	}

	default boolean grantedToDeployToAsLanded(GameState gameState, PhysicalCard playedCard, PhysicalCard target) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_DEPLOY_AS_LANDED_TO_TARGET, playedCard))
			if (modifier.isAffectedTarget(gameState, query(), target))
				return true;

		return false;
	}

	default boolean blownAwayForceLossMayNotBeReduced(GameState gameState) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.BLOWN_AWAY_FORCE_LOSS)) {
			if (modifier.isForTopBlowAwayEffect(gameState)) {
				if (((BlownAwayForceLossModifier)modifier).forceLossMayNotBeReduced())
					return true;
			}
		}
		return false;
	}

	default boolean landsAsUnlimitedMove(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.LANDS_AS_UNLIMITED_MOVE, card).isEmpty();
	}

	default boolean takesOffAsUnlimitedMove(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.TAKES_OFF_AS_UNLIMITED_MOVE, card).isEmpty();
	}

	default boolean isConflictCard(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.CONFLICT_CARD, card).isEmpty();
	}

	default boolean isCreditCard(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.CREDIT_CARD, card).isEmpty();
	}

	/**
	 * Determines if a Revolution card has its effects canceled.
	 * @param gameState the game state
	 * @param card a card
	 * @return true or false
	 */
	default boolean isEffectsOfRevolutionCanceled(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.EFFECTS_OF_REVOLUTION_CANCELED, card).isEmpty();
	}



	/**
	 * Determines if the card is granted the ability to use the device.
	 * @param gameState the game state
	 * @param card a card
	 * @param device a device
	 * @return true if card is granted ability to use the device, otherwise false
	 */
	default boolean grantedToUseDevice(GameState gameState, PhysicalCard card, PhysicalCard device) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_DEVICE, card)) {
			if (modifier.isAffectedTarget(gameState, query(), device)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the card is granted the ability to use the weapon.
	 * @param gameState the game state
	 * @param card a card
	 * @param weapon a weapon
	 * @return true if card is granted ability to use the weapon, otherwise false
	 */
	default boolean grantedToUseWeapon(GameState gameState, PhysicalCard card, PhysicalCard weapon) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_USE_WEAPON, card)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean isImmuneToCardTitle(GameState gameState, PhysicalCard card, String cardTitle) {
		if (card.getBlueprint().isImmuneToCardTitle(cardTitle))
			return true;

		if (card.getBlueprint().isImmuneToOwnersCardTitle(cardTitle))
			return true;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_TITLE, card)) {
			if (modifier.isImmuneToCardTitleModifier(gameState, query(), cardTitle)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the modifiers from the source card are suspended from affecting the specified card.
	 * @param gameState the game state
	 * @param source the source card
	 * @param affectedCard the affected card
	 * @return true if effects from modifier are suspended, otherwise false
	 */
	default boolean isEffectsFromModifierToCardSuspended(GameState gameState, PhysicalCard source, PhysicalCard affectedCard) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.SUSPEND_EFFECTS_FROM_CARD, affectedCard)) {
			if (modifier.isAffectedTarget(gameState, query(), source)) {
				return true;
			}
		}
		return false;
	}



	/**
	 * Determines if the card deploys and moves like an undercover spy (including if card is an undercover spy)).
	 * @param gameState the game state
	 * @param card a card
	 * @return true if card is an undercover spy or deploys and moves like an undercover spy, otherwise false
	 */
	default boolean isDeploysAndMovesLikeUndercoverSpy(GameState gameState, PhysicalCard card) {
		if (card.isUndercover())
			return true;

		return (!getModifiersAffectingCard(gameState, ModifierType.DEPLOYS_AND_MOVES_LIKE_UNDERCOVER_SPY, card).isEmpty());
	}

	/**
	 * Determines if a card was granted to targeted by the specified card.
	 * @param gameState the game state
	 * @param cardTargeted the card targeted
	 * @param cardTargeting the card doing the targeting
	 * @return true if card may be targeted, otherwise false
	 */
	default boolean grantedMayBeTargetedBy(GameState gameState, PhysicalCard cardTargeted, PhysicalCard cardTargeting) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY, cardTargeted)) {
			if (modifier.grantedToBeTargetedByCard(gameState, query(), cardTargeting))
				return true;
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY_WEAPONS, cardTargeted)) {
			if (modifier.grantedToBeTargetedByCard(gameState, query(), cardTargeting))
				return true;
		}
		return false;
	}

	/**
	 * Determines if a card is explicitly allowed to be placed on owner's Political Effect.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may be placed on Political Effects, otherwise false
	 */
	default boolean grantedMayBePlaceOnOwnersPoliticalEffect(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_BE_PLACED_ON_OWNERS_POLITICAL_EFFECT, card).isEmpty());
	}

	default boolean canBeTargetedByWeaponsAsIfPresent(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_BE_TARGETED_BY_WEAPONS_AS_IF_PRESENT, card).isEmpty());
	}

	default boolean canBeTargetedByWeaponsAsStarfighter(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.TARGETED_BY_WEAPONS_LIKE_A_STARFIGHTER, card).isEmpty());
	}

	default boolean isPermanentPilotsNotAbleToApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.STARSHIP &&
				card.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
			return false;

		if (!getModifiersAffectingCard(gameState, ModifierType.PERMANENT_PILOTS_MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty()) {
			return true;
		}

		// Check if attached to "crashed vehicle"
		if (card.isCrashed()) {
			return true;
		}

		return false;
	}

	default boolean passengerAppliesAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.PASSENGER_APPLIES_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty();
	}
}
