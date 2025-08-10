package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;

public interface EpicEvents extends BaseQuery, Destiny {

	default boolean ignoreDuringEpicEventCalculation(GameState gameState, PhysicalCard card, boolean isForBlownAway) {
		if (isForBlownAway && Filters.and(Filters.generic, Filters.location).accepts(gameState, query(), card)) {
			return true;
		}
		return (!getModifiersAffectingCard(gameState, ModifierType.IGNORE_DURING_EPIC_EVENT_CALCULATION, card).isEmpty());
	}

	default float getEpicEventCalculationTotal(GameState gameState, PhysicalCard physicalCard, float baseTotal) {
		float result = baseTotal;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EPIC_EVENT_CALCULATION_TOTAL, physicalCard)) {
			result += modifier.getEpicEventCalculationTotalModifier(gameState, query(), physicalCard);
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the 'blow away' Blockade Flagship attempt total.
	 * @param gameState the game state
	 * @param baseTotal the base total
	 * @return the total
	 */
	default float getBlowAwayBlockadeFlagshipAttemptTotal(GameState gameState, float baseTotal) {
		float result = baseTotal;
		for (Modifier modifier : getModifiers(gameState, ModifierType.BLOW_AWAY_BLOCKADE_FLAGSHIP_ATTEMPT_TOTAL)) {
			result += modifier.getValue(gameState, query(), (PhysicalCard) null);
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the 'blow away' Shield Gate attempt total.
	 * @param gameState the game state
	 * @param baseTotal the base total
	 * @return the total
	 */
	default float getBlowAwayShieldGateAttemptTotal(GameState gameState, float baseTotal) {
		float result = baseTotal;
		for (Modifier modifier : getModifiers(gameState, ModifierType.BLOW_AWAY_SHIELD_GATE_ATTEMPT_TOTAL)) {
			result += modifier.getValue(gameState, query(), (PhysicalCard) null);
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn epic event destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for epic event destiny
	 * @param playerId the player drawing epic event destiny
	 * @return the epic event destiny value
	 */
	default float getEpicEventDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
		Float result = physicalCard.getDestinyValueToUse();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyModifier(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		// Get from EpicEventState
		EpicEventState epicEventState = gameState.getEpicEventState();
		if (epicEventState != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_EPIC_EVENT_DESTINY_DRAW, epicEventState.getEpicEvent())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getEpicEventDestinyModifier(playerId, gameState, query(), epicEventState.getEpicEvent());
				}
			}
		}

		return result;
	}

	/**
	 * Gets the value of a drawn epic event and weapon destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for epic event and weapon destiny
	 * @param playerId the player drawing epic event and weapon destiny
	 * @return the epic event and weapon destiny value
	 */
	default float getEpicEventAndWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
		Float result = physicalCard.getDestinyValueToUse();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()==CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyModifier(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_WEAPON_DESTINY, physicalCard)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		// Get from EpicEventState
		EpicEventState epicEventState = gameState.getEpicEventState();
		if (epicEventState != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_EPIC_EVENT_DESTINY_DRAW, epicEventState.getEpicEvent())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getEpicEventDestinyModifier(playerId, gameState, query(), epicEventState.getEpicEvent());
				}
			}
		}

		// Get from WeaponFiringState
		WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
		if (weaponFiringState != null) {
			PhysicalCard weapon = weaponFiringState.getCardFiring();
			SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
			Collection<PhysicalCard> weaponTargets = weaponFiringState.getTargets();
			PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

			for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_WEAPON_DESTINY)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getWeaponDestinyModifier(gameState, query(), cardFiringWeapon, weapon, permanentWeapon, weaponTargets);
				}
			}
		}

		return result;
	}
}
