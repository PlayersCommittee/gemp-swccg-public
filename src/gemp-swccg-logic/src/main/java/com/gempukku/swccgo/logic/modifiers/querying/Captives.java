package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;

public interface Captives extends BaseQuery, MovementCosts {
	/**
	 * Determines if the specified captive is prohibited from being transferred.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if card is prohibited from being transferred, otherwise false
	 */
	default boolean mayNotBeTransferred(GameState gameState, PhysicalCard card) {
		if (!card.isCaptive() || mayNotMove(gameState, card))
			return true;

		PhysicalCard escort = Filters.escortedCaptive.accepts(gameState, query(), card) ? card.getAttachedTo() : null;
		if (escort != null && mayNotMove(gameState, escort)) {
			return true;
		}

		return (!getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_TRANSFERRED, card).isEmpty());
	}

	/**
	 * Determines if the specified captive is to be treated as active as it participates in a battle.
	 * @param gameState the game state
	 * @param card the captive
	 * @return true if card should be treated as active, otherwise false
	 */
	default boolean captiveMayParticipateInBattle(GameState gameState, PhysicalCard card) {
		if (!card.isCaptive())
			return false;

		if(card.getAttachedTo() == card.getEscort())
			return false;

		return !getModifiersAffectingCard(gameState, ModifierType.CAPTIVE_MAY_PARTICIPATE_IN_BATTLE, card).isEmpty();
	}

	/**
	 * Gets the number of captives a character may escort.
	 * @param gameState the game state
	 * @param escort the escort
	 * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
	 * @return true or false
	 */
	default int getNumCaptivesAllowedToEscort(GameState gameState, PhysicalCard escort, boolean skipWarriorCheck) {
		if (!escort.getOwner().equals(gameState.getDarkPlayer())
				|| escort.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
			return 0;
		}

		// Check if this type of character is allowed to escort
		if (skipWarriorCheck
				|| Filters.bounty_hunter.accepts(gameState, query(), escort)
				|| Filters.warrior.accepts(gameState, query(), escort)
				|| Filters.battle_droid.accepts(gameState, query(), escort)
				|| !getModifiersAffectingCard(gameState, ModifierType.MAY_ESCORT_A_CAPTIVE, escort).isEmpty()) {

			int maxCaptives = 1;
			if (!getModifiersAffectingCard(gameState, ModifierType.MAY_ESCORT_ANY_NUMBER_OF_CAPTIVES, escort).isEmpty()) {
				maxCaptives = Integer.MAX_VALUE;
			}
			return maxCaptives;
		}

		return 0;
	}

	/**
	 * Determines if a specified card can escort another specified card as a captive.
	 * @param gameState the game state
	 * @param escort the escort
	 * @param captive the captive
	 * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
	 * @return true or false
	 */
	default boolean canEscortCaptive(GameState gameState, PhysicalCard escort, PhysicalCard captive, boolean skipWarriorCheck) {
		// Need to allow if character is already a captive since Jabba's Prize is a Dark Side card
		if ((!captive.isCaptive() && !captive.getOwner().equals(gameState.getLightPlayer()))
				|| captive.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
			return false;
		}

		int maxCaptives = getNumCaptivesAllowedToEscort(gameState, escort, skipWarriorCheck);
		if (maxCaptives == 0) {
			return false;
		}

		Collection<PhysicalCard> captives = gameState.getCaptivesOfEscort(escort);
		if (!captives.contains(captive)) {

			if (captives.size() >= maxCaptives) {
				return false;
			}

			// Check that new escort is allowed to move (if card to be captive is already a captive)
			if (captive.isCaptive() && mayNotMove(gameState, escort)) {
				return false;
			}

			// If character is aboard a vehicle or starship (except vehicle/starship sites), unless captive already aboard that vehicle or starship,
			// check if there is at least one available passenger slot, since the captive takes up a passenger slot
			PhysicalCard attachedTo = escort.getAttachedTo();
			if (attachedTo != null && (escort.isPilotOf() || escort.isPassengerOf())
					&& (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP || attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE)) {
				if ((!captive.isCaptive() || captive.getAttachedTo() == null || !Filters.or(Filters.piloting(attachedTo), Filters.aboardAsPassenger(attachedTo)).accepts(gameState, query(), captive.getAttachedTo()))
						&& gameState.getAvailablePassengerCapacity(query(), attachedTo, captive) < 1) {
					return false;
				}
			}
		}

		return true;
	}
}
