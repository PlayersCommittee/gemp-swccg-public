package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collections;
import java.util.Set;

public interface Targeting extends BaseQuery, Weapons, Captives, CardTraits, Piloting, Prohibited {

	default boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting) {
		return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, null, Collections.singleton(TargetingReason.OTHER));
	}

	default boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, Set<TargetingReason> targetingReasons) {
		return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, null, targetingReasons);
	}

	default boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting) {
		return canBeTargetedBy(gameState, cardToTarget, permanentWeaponDoingTargeting.getPhysicalCard(game()), permanentWeaponDoingTargeting, Collections.singleton(TargetingReason.OTHER));
	}

	default boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting, Set<TargetingReason> targetingReasons) {
		return canBeTargetedBy(gameState, cardToTarget, permanentWeaponDoingTargeting.getPhysicalCard(game()), permanentWeaponDoingTargeting, targetingReasons);
	}

	private boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting) {
		return canBeTargetedBy(gameState, cardToTarget, cardDoingTargeting, permanentWeaponDoingTargeting, Collections.singleton(TargetingReason.OTHER));
	}

	private boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, SwccgBuiltInCardBlueprint permanentWeaponDoingTargeting, Set<TargetingReason> targetingReasons) {
		if (cardDoingTargeting != null) {
			for (String title : cardDoingTargeting.getTitles()) {
				if (cardToTarget.getBlueprint().isImmuneToCardTitle(title))
					return false;

				if (cardDoingTargeting.getOwner().equals(cardToTarget.getOwner())
						&& cardToTarget.getBlueprint().isImmuneToOwnersCardTitle(title))
					return false;

				if (cardToTarget.getBlueprint().isImmuneToOpponentsObjective()
						&& Filters.and(Filters.opponents(cardToTarget), Filters.Objective).accepts(gameState, query(), cardDoingTargeting))
					return false;
			}

			if (permanentWeaponDoingTargeting != null) {
				if (cardToTarget.getBlueprint().isImmuneToCardTitle(permanentWeaponDoingTargeting.getTitle(game())))
					return false;

				if (cardDoingTargeting.getOwner().equals(cardToTarget.getOwner())
						&& cardToTarget.getBlueprint().isImmuneToOwnersCardTitle(permanentWeaponDoingTargeting.getTitle(game())))
					return false;
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.IMMUNE_TO_TITLE, cardToTarget))
				if (modifier.isImmuneToCardModifier(gameState, query(), cardDoingTargeting, permanentWeaponDoingTargeting))
					return false;

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TARGETED_BY, cardToTarget))
				if (modifier.mayNotBeTargetedBy(gameState, query(), cardToTarget, cardDoingTargeting, permanentWeaponDoingTargeting))
					return false;
		}

		if (targetingReasons.contains(TargetingReason.TO_BE_CANCELED)
				&& mayNotBeCanceled(gameState, cardToTarget))
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_DISARMED)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_DISARMED, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_CAPTURED)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_FROZEN)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_FROZEN, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PLACED_OUT_OF_PLAY, cardToTarget).isEmpty()) {
				return false;
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_PLACED_OUT_OF_PLAY_BY, cardToTarget)) {
				if (modifier.isAffectedTarget(gameState, query(), cardDoingTargeting)) {
					return false;
				}
			}
		}

		if (targetingReasons.contains(TargetingReason.TO_BE_TORTURED)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_TORTURED, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_HIT)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_HIT, cardToTarget).isEmpty())
				return false;

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_HIT_BY, cardToTarget)) {
				if (modifier.isAffectedTarget(gameState, query(), cardDoingTargeting))
					return false;
				if (modifier.isAffectedTarget(gameState, query(), permanentWeaponDoingTargeting))
					return false;
			}
		}

		if (targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
				|| targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)
				|| targetingReasons.contains(TargetingReason.TO_BE_PURCHASED)) {

			if ((targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
					|| targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD))
					&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_STOLEN, cardToTarget).isEmpty()) {
				return false;
			}

			if (targetingReasons.contains(TargetingReason.TO_BE_PURCHASED)
					&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PURCHASED, cardToTarget).isEmpty()) {
				return false;
			}

			if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
					&& mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
				return false;
			}

			if (cardDoingTargeting != null) {
				if (cardToTarget.getOwner().equals(cardDoingTargeting.getOwner()))
					return false;

				if ((targetingReasons.contains(TargetingReason.TO_BE_STOLEN)
						|| targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD))
						&& cannotSteal(gameState, cardDoingTargeting))
					return false;
			}

			if (Filters.in_play.accepts(gameState, query(), cardToTarget)) {
				CardCategory cardCategory = cardToTarget.getBlueprint().getCardCategory();
				// An opponent's device, starship, or vehicle may not be stolen if the opponent has characters aboard
				if (cardCategory == CardCategory.STARSHIP || cardCategory == CardCategory.VEHICLE) {
					if (!targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)) {
						boolean foundCharacterAboard = Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.attachedToWithRecursiveChecking(cardToTarget), CardCategory.CHARACTER));
						return !foundCharacterAboard;
					}
					return true;
				}
				if (cardCategory == CardCategory.DEVICE) {
					if (!targetingReasons.contains(TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD)) {
						if (Filters.canSpot(gameState.getGame(), null, Filters.and(Filters.attachedToWithRecursiveChecking(cardToTarget), CardCategory.CHARACTER))) {
							return false;
						}
					}
				}

				if (cardDoingTargeting != null) {
					// Characters may only steal weapons or devices that say they can be
					// deployed on (or moved by) characters.
					if (cardCategory == CardCategory.DEVICE || cardCategory == CardCategory.WEAPON) {
						if (cardDoingTargeting.getBlueprint().getCardCategory() == CardCategory.CHARACTER)
							return cardToTarget.getBlueprint().canBeDeployedOnCharacter();
					}
				}
			}
		}

		if (targetingReasons.contains(TargetingReason.TO_BE_SUSPENDED)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_SUSPENDED, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_CHOKED)
				&& !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CHOKED, cardToTarget).isEmpty())
			return false;

		if (targetingReasons.contains(TargetingReason.TO_BE_LOST) || targetingReasons.contains(TargetingReason.TO_BE_CHOKED)) {
			if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_TARGET_TO_BE_LOST, cardToTarget).isEmpty()) {
				return false;
			}

			if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
					&& mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
				return false;
			}
		}

		if (targetingReasons.contains(TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)) {
			if (cardToTarget.getZone() == Zone.OUT_OF_PLAY
					|| (cardToTarget.getStackedOn() != null && Filters.grabber.accepts(gameState, query(), cardToTarget.getStackedOn()))) {
				return false;
			}

			if (cardToTarget.getBlueprint().getCardCategory() == CardCategory.DEVICE
					&& mayNotRemoveDeviceUnlessDisarmed(gameState, cardToTarget)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines if the affected cards is prohibited from existing at (deploying or moving to) the specified targeted.
	 * @param gameState the game state
	 * @param card the card
	 * @param target the target card
	 * @return true if card may not exist at target, otherwise false
	 */
	default boolean isProhibitedFromTarget(GameState gameState, PhysicalCard card, PhysicalCard target) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_EXIST_AT_TARGET, card))
			if (modifier.isProhibitedFromExistingAt(gameState, query(), target))
				return true;

		// Undercover spies may only exist physically at sites
		if (card.isUndercover() && target.getBlueprint().getCardSubtype() != CardSubtype.SITE)
			return true;

		// If card has "per system" uniqueness, then it cannot exist at same system as another card of same title (per uniqueness limit)
		Uniqueness uniqueness = getUniqueness(gameState, card);
		if (uniqueness != null && uniqueness.isPerSystem()) {
			PhysicalCard location = getLocationHere(gameState, target);
			if (location != null) {
				String systemName = location.getPartOfSystem() != null ? location.getPartOfSystem() : location.getSystemOrbited();
				if (systemName != null) {
					if (Filters.canSpotFromAllOnTable(gameState.getGame(), uniqueness.getValue(), Filters.and(Filters.sameTitleAs(card),
							Filters.locationAndCardsAtLocation(Filters.or(Filters.partOfSystem(systemName),
									Filters.and(Filters.not(Filters.system), Filters.isOrbiting(systemName))))))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	default String getPlayerToChooseCardTargetAtLocation(GameState gameState, PhysicalCard card, PhysicalCard location, String defaultPlayerId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PLAYER_TO_SELECT_CARD_TARGET_AT_LOCATION, card)) {
			String playerId = modifier.getPlayerToSelectCardTargetAtLocation(gameState, query(), location);
			if (playerId!=null)
				return playerId;
		}

		return defaultPlayerId;
	}

	default boolean canWeaponTargetAdjacentSite(GameState gameState, PhysicalCard weapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_ADJACENT_SITE)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean canWeaponTargetAdjacentSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_ADJACENT_SITE)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean canWeaponTargetTwoSitesAway(GameState gameState, PhysicalCard weapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_TWO_SITE_AWAY)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean canWeaponTargetTwoSitesAway(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.TARGET_TWO_SITE_AWAY)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, PhysicalCard weapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_TARGET_AT_NEAREST_RELATED_EXTERIOR_SITE)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_TARGET_AT_NEAREST_RELATED_EXTERIOR_SITE)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return true;
			}
		}
		return false;
	}
}
