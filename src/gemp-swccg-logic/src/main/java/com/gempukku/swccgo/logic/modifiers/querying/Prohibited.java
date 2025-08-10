package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

/**
 * This subinterface is a bit of a catch-all, used to contain "may not" flags that either do not
 * fit any other subinterface or as a way of reducing the total size of some of the mroe bloated
 * subinterfaces.
 */
public interface Prohibited extends BaseQuery, Flags {

	default boolean mayNotBeUsed(GameState gameState, PhysicalCard deviceOrWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_USED)) {
			if (modifier.isAffectedTarget(gameState, query(), deviceOrWeapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean mayNotBeUsed(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_USED)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean mayNotBeFired(GameState gameState, PhysicalCard weapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_FIRED)) {
			if (modifier.isAffectedTarget(gameState, query(), weapon)) {
				return true;
			}
		}
		return false;
	}

	default boolean mayNotBeFired(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_BE_FIRED)) {
			if (modifier.isAffectedTarget(gameState, query(), permanentWeapon)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified device or weapon is allowed to be used by a landed starship.
	 * @param gameState the game state
	 * @param deviceOrWeapon the device or weapon
	 * @return true or false
	 */
	default boolean mayBeUsedByLandedStarship(GameState gameState, PhysicalCard deviceOrWeapon) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_BE_USED_BY_LANDED_STARSHIP, deviceOrWeapon).isEmpty();
	}

	/**
	 * Determines if the specified card is prohibited from allowing the specified player to download cards.
	 * @param gameState the game state
	 * @param card the card
	 * @param playerId the playerId
	 * @return true or false
	 */
	default boolean isProhibitedFromAllowingPlayerToDownloadCards(GameState gameState, PhysicalCard card, String playerId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ALLOW_PLAYER_TO_DOWNLOAD_CARDS, card)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	default boolean cannotDriveOrPilot(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.CANT_DRIVE_OR_PILOT, card).isEmpty();
	}

	/**
	 * Determines if the specified card is not allowed to steal other cards.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is not allowed to steal other cards, otherwise false
	 */
	default boolean cannotSteal(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.CANT_STEAL, card).isEmpty();
	}

	/**
	 * Determines if the specified card is explicitly not allowed to be played due to existence of a "can't play" modifier
	 * affecting the card.
	 * @param gameState the game state
	 * @param card the card
	 * @param isDejarikRules true if playing using Dejarik Rules, otherwise false
	 * @return true if card cannot be played, otherwise false
	 */
	default boolean isPlayingCardProhibited(GameState gameState, PhysicalCard card, boolean isDejarikRules) {
		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, card).isEmpty()) {
			if (!getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD, card).isEmpty()) {
				for (Modifier mayNotPlayModifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, card)) {
					for (Modifier ignoresDeploymentRestrictionFromCardModifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD, card)) {
						Filter cardFilter = ((IgnoresDeploymentRestrictionsFromCardModifier) ignoresDeploymentRestrictionFromCardModifier).getCardFilter();
						return !cardFilter.accepts(gameState.getGame(), mayNotPlayModifier.getSource(gameState));
					}
				}
			}

			if(!getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION, card).isEmpty()) {
				for (Modifier mayNotPlayModifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY, card)) {
					for (Modifier ignoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier : getModifiersAffectingCard(gameState, ModifierType.IGNORES_DEPLOYMENT_RESTRICTIONS_FROM_CARD_WHEN_DEPLOYING_TO_LOCATION, card)) {
						Filter cardFilter = ((IgnoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier) ignoresDeploymentRestrictionsFromCardWhenDeployingToLocationModifier).getCardFilter();
						return !cardFilter.accepts(gameState.getGame(), mayNotPlayModifier.getSource(gameState));
					}
				}
			}
			return true;
		}
		if (isDejarikRules && !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PLAY_USING_DEJARIK_RULES, card).isEmpty()) {
			return true;
		}
		return false;
	}

	default boolean prohibitedFromCarrying(GameState gameState, PhysicalCard character, PhysicalCard cardToBeCarried) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CARRY, character))
			if (modifier.prohibitedFromCarrying(gameState, query(), character, cardToBeCarried))
				return true;
		return false;
	}

	default boolean prohibitedFromPiloting(GameState gameState, PhysicalCard pilot, PhysicalCard starshipOrVehicle) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_PILOT_TARGET, pilot))
			if (modifier.prohibitedFromPiloting(gameState, query(), starshipOrVehicle))
				return true;
		return false;
	}

	/**
	 * Determines if the specified Interrupt is explicitly allowed to be played to cancel a Force drain at the location.
	 * @param gameState the game state
	 * @param card the Interrupt card
	 * @param location the Force drain location
	 * @return true if allowed, otherwise false
	 */
	default boolean mayPlayInterruptToCancelForceDrain(GameState gameState, PhysicalCard card, PhysicalCard location) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_CANCEL_FORCE_DRAIN, card))
			if (modifier.isAffectedTarget(gameState, query(), location))
				return true;
		return false;
	}

	/**
	 * Determines if the specified Interrupt is explicitly allowed to be played to cancel the specified card (being played or
	 * on table).
	 * @param gameState the game state
	 * @param card the Interrupt card
	 * @param targetCard the card being played or on table
	 * @return true if allowed, otherwise false
	 */
	default boolean mayPlayInterruptToCancelCard(GameState gameState, PhysicalCard card, PhysicalCard targetCard) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_PLAY_TO_CANCEL_CARD, card))
			if (modifier.isAffectedTarget(gameState, query(), targetCard))
				return true;
		return false;
	}

	default boolean cannotBeConverted(GameState gameState, PhysicalCard location) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CONVERTED, location).isEmpty());
	}

	default boolean canBeConvertedByDeployment(GameState gameState, PhysicalCard card, String playerId) {
		return false;
	}

	/**
	 * Determines if the specified spy does not 'break cover' during deploy using normal Undercover rules.
	 * @param gameState the game state
	 * @param card the card
	 * @return true or false
	 */
	default boolean mayNotBreakOwnCoverDuringDeployPhase(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BREAK_OWN_COVER_DURING_DEPLOY_PHASE, card).isEmpty());
	}

	default boolean cannotJoinSearchParty(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_JOIN_SEARCH_PARTY, card).isEmpty());
	}

	default boolean cannotBeFlipped(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_FLIPPED, card).isEmpty());
	}

	default boolean notImmediatelyLostIfAsteroidSectorDrawnForAsteroidDestiny(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.NOT_LOST_IF_ASTEROID_SECTOR_DRAWN_FOR_ASTEROID_DESTINY, card).isEmpty());
	}

	default boolean cannotTurnOnBinaryDroid(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TURNED_ON, card).isEmpty());
	}

	default boolean mayNotBeGrabbed(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_GRABBED, card).isEmpty());
	}

	/**
	 * Determines if the specified card may not be canceled.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may not be canceled, otherwise false
	 */
	default boolean mayNotBeCanceled(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().isCardTypeMayNotBeCanceled())
			return false;

		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_CANCELED, card).isEmpty());
	}

	/**
	 * Determines if the specified card may not be placed out of play.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may not be placed out of play, otherwise false
	 */
	default boolean mayNotBePlacedOutOfPlay(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_PLACED_OUT_OF_PLAY, card).isEmpty());
	}

	/**
	 * Determines if the specified card may not be removed from lost pile if just lost
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may not be placed out of play, otherwise false
	 */
	default boolean mayNotRemoveJustLostCardFromLostPile(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_REMOVE_JUST_LOST_CARDS_FROM_LOST_PILE, card).isEmpty());
	}

	/**
	 * Determines if a card may not be targeted by weapons used by the specified card.
	 * @param gameState the game state
	 * @param cardTargeted the card targeted
	 * @param weaponUser the card use
	 * @return true if card may be targeted, otherwise false
	 */
	default boolean mayNotBeTargetedByWeaponUser(GameState gameState, PhysicalCard cardTargeted, PhysicalCard weaponUser) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TARGETED_BY_WEAPON_USER, cardTargeted)) {
			if (modifier.mayNotBeTargetedBy(gameState, query(), cardTargeted, weaponUser, null))
				return true;
		}
		return false;
	}

	/**
	 * Determines if the specified card may not be removed (unless attached to card is Disarmed).
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card may not be removed, otherwise false
	 */
	default boolean mayNotRemoveDeviceUnlessDisarmed(GameState gameState, PhysicalCard card) {
		return (!getModifiersAffectingCard(gameState, ModifierType.DEVICE_MAY_NOT_BE_REMOVED_UNLESS_DISARMED, card).isEmpty());
	}

	default boolean cannotApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		if (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY, card).isEmpty()) {
			return true;
		}

		// Check if attached to "crashed vehicle"
		if (card.getBlueprint().getCardCategory()==CardCategory.CHARACTER
				&& card.getAttachedTo() != null
				&& card.getAttachedTo().isCrashed()) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if characters are explicitly not allowed to be 'revived'.
	 *
	 * @param gameState the game state
	 * @return true if characters cannot be 'revived', otherwise false
	 */
	default boolean isRevivingCharactersProhibited(GameState gameState) {
		return !getModifiers(gameState, ModifierType.MAY_NOT_BE_REVIVED).isEmpty();
	}

	/**
	 * Determines if the specified card is explicitly not allowed to 'cloak'.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card cannot 'cloak', otherwise false
	 */
	default boolean isCloakingCardProhibited(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CLOAK, card).isEmpty();
	}

	/**
	 * Determines if the specified card is explicitly not allowed to 'attach'.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card cannot 'attach', otherwise false
	 */
	default boolean isAttachingCardProhibited(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ATTACH, card).isEmpty();
	}
}
