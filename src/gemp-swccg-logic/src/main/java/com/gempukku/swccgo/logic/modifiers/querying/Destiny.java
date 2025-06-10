package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.*;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collection;

public interface Destiny extends BaseQuery {


	/**
	 * Gets a card's current destiny value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the destiny value
	 */
	default float getDestiny(GameState gameState, PhysicalCard physicalCard) {
		return getDestiny(gameState, physicalCard, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's current destiny value.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the destiny value
	 */
	default float getDestiny(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		Float result = physicalCard.getDestinyValueToUse();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, physicalCard)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
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
			float modifierValue = modifier.getDestinyModifier(gameState, query(), physicalCard);
			result += modifierValue;
			modifierCollector.addModifier(modifier);
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_DESTINY, physicalCard)) {
			result = modifier.getValue(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		return result;
	}

	default float getDestinyModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
		float result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_INCREASE_MODIFIER_LIMIT, physicalCard)) {
			result = modifier.getDestinyModifierLimit(gameState, query(), physicalCard);
		}
		return result;
	}

	/**
	 * Determines if a card's destiny value is less than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the destiny value
	 * @return true if card's destiny value is less than the specified value, otherwise false
	 */
	default boolean hasDestinyLessThan(GameState gameState, PhysicalCard card, float value) {
		return getDestiny(gameState, card) < value;
	}

	/**
	 * Determines if a card's destiny value is less than or equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the destiny value
	 * @return true if card's destiny value is less than or equal to the specified value, otherwise false
	 */
	default boolean hasDestinyLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value) {
		return getDestiny(gameState, card) <= value;
	}

	/**
	 * Determines if a card's destiny value is equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the destiny value
	 * @return true if card's destiny value is equal to the specified value, otherwise false
	 */
	default boolean hasDestinyEqualTo(GameState gameState, PhysicalCard card, float value) {
		return getDestiny(gameState, card) == value;
	}

	/**
	 * Gets the destiny value when a card is drawn for destiny.
	 * @param gameState the game state
	 * @param card the card drawn for destiny
	 * @param destinyDrawActionSource the source card for the draw destiny action
	 * @return the destiny value
	 */
	default float getDestinyForDestinyDraw(GameState gameState, PhysicalCard card, PhysicalCard destinyDrawActionSource) {
		Float result = card.getDestinyValueToUse();

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_DESTINY, card)) {
			result = modifier.getPrintedValueDefinedByGameText(gameState, query(), card);
		}
		// If value if undefined, then return 0
		if (result == null)
			return 0;

		// If card is a character and it is "doubled", then double the printed number
		if (card.getBlueprint().getCardCategory()==CardCategory.CHARACTER
				&& isDoubled(gameState, card)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, card)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyModifier(gameState, query(), card);
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, card)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), card);
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		if (destinyDrawActionSource != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_DESTINY_DRAW_FOR_ACTION_SOURCE, destinyDrawActionSource)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getDestinyDrawFromSourceCardModifier(card.getOwner(), gameState, query(), destinyDrawActionSource);
				}
			}
		}

		return result;
	}

	/**
	 * Determines if the specified player may not add destiny draws to power.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if destinies draws to power may not be added, otherwise false
	 */
	default boolean mayNotAddDestinyDrawsToPower(GameState gameState, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_POWER)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the specified player may not add destiny draws to attrition.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if destinies draws to attrition may not be added, otherwise false
	 */
	default boolean mayNotAddDestinyDrawsToAttrition(GameState gameState, String playerId) {
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_ADD_DESTINY_DRAWS_TO_ATTRITION)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if battle destiny draws by a specified player may not be canceled by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player drawing battle destiny
	 * @param playerToCancel the player to cancel battle destiny
	 * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
	 * @return true if battle destinies may not be canceled, otherwise false
	 */
	default boolean mayNotCancelBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToCancel, boolean isCancelAndRedraw) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY, battleLocation)) {
			if (modifier.mayNotCancelBattleDestiny(playerDrawingDestiny, playerToCancel)) {
				return true;
			}
		}
		if (!isCancelAndRedraw) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_CANCEL_BATTLE_DESTINY_UNLESS_BEING_REDRAWN, battleLocation)) {
				if (modifier.mayNotCancelBattleDestiny(playerDrawingDestiny, playerToCancel)) {
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * Determines if the current destiny draw may not be canceled by the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
	 * @return true if destiny may not be canceled, otherwise false
	 */
	default boolean mayNotCancelDestinyDraw(GameState gameState, String playerId, boolean isCancelAndRedraw) {
		DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
		if (drawDestinyState == null)
			return true;

		DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

		// See if we have a global "cannot cancel destiny" rule in effect
		for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS)) {
			if (modifier.mayNotCancelDestiny(drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
				return true;
			}
		}

		// If it is not a cancel and redraw, see if we have a global "cannot cancel destiny unless redrawn" rule in effect
		// or if the specific destiny effect may not be canceled unless being redrawn
		if (!isCancelAndRedraw) {
			for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS_UNLESS_BEING_REDRAWN)) {
				if (modifier.mayNotCancelDestiny(drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
					return true;
				}
			}

			if (drawDestinyEffect.mayNotBeCanceledUnlessBeingRedrawn())
				return true;
		}


		if (drawDestinyEffect.isDestinyCanceled()
				|| drawDestinyEffect.getSubstituteDestiny() != null
				|| drawDestinyEffect.mayNotBeCanceledByPlayer(playerId))
			return true;

		if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
			if (mayNotCancelBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId, isCancelAndRedraw)) {
				return true;
			}
		}
		else if (drawDestinyEffect.getDestinyType() == DestinyType.WEAPON_DESTINY) {
			// Get from WeaponFiringState
			WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
			if (weaponFiringState != null) {
				PhysicalCard weapon = weaponFiringState.getCardFiring();
				SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
				PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

				for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_CANCEL_WEAPON_DESTINY)) {
					if (modifier.mayNotCancelWeaponDestiny(gameState, query(), drawDestinyEffect.getPlayerDrawingDestiny(), playerId, weapon, permanentWeapon, cardFiringWeapon)) {
						return true;
					}
				}
			}
		}
		else if(drawDestinyEffect.getDestinyType() == DestinyType.CHOKE_DESTINY
				&& gameState.getGame().getModifiersQuerying().hasFlagActive(gameState.getGame().getGameState(), ModifierFlag.CHOKE_DESTINIES_MAY_NOT_BE_CANCELLED, drawDestinyEffect.getPlayerDrawingDestiny())){
			return true;
		}

		return false;
	}

	/**
	 * Determines if total battle destiny for a specified player may not be modified by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player with total battle destiny
	 * @param playerToModify the player to modify total battle destiny
	 * @return true if total battle destiny may not be modified, otherwise false
	 */
	default boolean mayNotModifyTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToModify) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_TOTAL_BATTLE_DESTINY, battleLocation)) {
			if (modifier.mayNotModifyBattleDestiny(playerDrawingDestiny, playerToModify)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Determines if total battle destiny for a specified player may not be increased by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player with total battle destiny
	 * @param playerToModify the player to increase total battle destiny
	 * @return true if total battle destiny may not be increased, otherwise false
	 */
	default boolean mayNotIncreaseTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToModify) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_INCREASE_TOTAL_BATTLE_DESTINY, battleLocation)) {
			if (((MayNotIncreaseTotalBattleDestinyModifier)modifier).mayNotIncreaseBattleDestiny(playerDrawingDestiny, playerToModify)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if battle destiny draws by a specified player may not be modified by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player drawing battle destiny
	 * @param playerToModify the player to modify battle destiny
	 * @return true if battle destinies may not be modified, otherwise false
	 */
	default boolean mayNotModifyBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToModify) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_MODIFY_BATTLE_DESTINY, battleLocation)) {
			if (modifier.mayNotModifyBattleDestiny(playerDrawingDestiny, playerToModify)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the current destiny draw may not be modified by the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if destiny may not be modified, otherwise false
	 */
	default boolean mayNotModifyDestinyDraw(GameState gameState, String playerId) {
		DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
		if (drawDestinyState == null)
			return true;

		DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

		if (drawDestinyEffect.isDestinyCanceled()
				|| drawDestinyEffect.getSubstituteDestiny() != null)
			return true;

		if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
			if (mayNotModifyBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
				return true;
			}
		}
		else if (drawDestinyEffect.getDestinyType() == DestinyType.WEAPON_DESTINY) {
			// Get from WeaponFiringState
			WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
			if (weaponFiringState != null) {
				PhysicalCard weapon = weaponFiringState.getCardFiring();
				SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
				PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

				for (Modifier modifier : getModifiers(gameState, ModifierType.MAY_NOT_MODIFY_WEAPON_DESTINY)) {
					if (modifier.mayNotModifyWeaponDestiny(gameState, query(), drawDestinyEffect.getPlayerDrawingDestiny(), playerId, weapon, permanentWeapon, cardFiringWeapon)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Determines if battle destiny draws by a specified player may not be substituted.
	 * @param gameState the game state
	 * @param playerId the player drawing battle destiny
	 * @return true if battle destinies may not be substituted, otherwise false
	 */
	default boolean mayNotSubstituteBattleDestinyDraws(GameState gameState, String playerId) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_SUBSTITUTE_BATTLE_DESTINY, battleLocation)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the current destiny draw may not be substituted.
	 * @param gameState the game state
	 * @return true if destiny may not be substituted, otherwise false
	 */
	default boolean mayNotSubstituteDestinyDraw(GameState gameState) {
		DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
		if (drawDestinyState == null)
			return true;

		DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

		if (drawDestinyEffect.isDestinyCanceled()
				|| drawDestinyEffect.getSubstituteDestiny() != null)
			return true;

		if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
			if (mayNotSubstituteBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny())) {
				return true;
			}
		}

		return false;
	}

	default int getNumDestinyDrawsToTotalPowerOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
		BattleState battleState = gameState.getBattleState();
		if (battleState ==null)
			return 0;

		int result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_DESTINY_DRAWS_TO_POWER_ONLY, battleState.getBattleLocation()))
			result += modifier.getNumDestinyDrawsToPowerOnlyModifier(player, gameState, query());

		if (isGetLimit) {
			// TODO: See getNumBattleDestinyDraws() for what to do here.
			return Integer.MAX_VALUE;
		}

		return Math.max(0, result);
	}

	default int getNumDestinyDrawsToAttritionOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
		BattleState battleState = gameState.getBattleState();
		if (battleState ==null)
			return 0;

		int result = 0;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_DESTINY_DRAWS_TO_ATTRITION_ONLY, battleState.getBattleLocation()))
			result += modifier.getNumDestinyDrawsToAttritionOnlyModifier(player, gameState, query());

		if (isGetLimit) {
			// TODO: See getNumBattleDestinyDraws() for what to do here.
			return Integer.MAX_VALUE;
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn weapon destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for weapon destiny
	 * @param playerId the player drawing weapon destiny
	 * @return the weapon destiny value
	 */
	default float getWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
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

	/**
	 * Gets the value of a drawn destiny to power.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for destiny to power
	 * @param playerId the player drawing destiny to power
	 * @return the destiny to power value
	 */
	default float getDestinyToPower(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		return result;
	}


	/**
	 * Checks to see if we should be drawing destiny from the bottom of the deck instead of top
	 * @param gameState
	 * @param playerId
	 * @return
	 */
	default boolean shouldDrawDestinyFromBottomOfDeck(GameState gameState, String playerId) {
		boolean isDrawFromBottom = false;
		for (Modifier modifier: getModifiers(gameState, ModifierType.DRAW_DESTINY_FROM_BOTTOM_OF_DECK)) {
			isDrawFromBottom = true;
		}
		return isDrawFromBottom;
	}

	/**
	 * Checks to see if we should be drawing destiny from the bottom of the deck instead of top
	 * @param gameState
	 * @param playerId
	 * @return
	 */
	default PhysicalCard getDrawsDestinyFromBottomOfDeckModiferSource(GameState gameState, String playerId) {
		for (Modifier modifier: getModifiers(gameState, ModifierType.DRAW_DESTINY_FROM_BOTTOM_OF_DECK)) {
			return modifier.getSource(gameState);
		}
		return null;
	}

	/**
	 * Gets the value of a drawn destiny to attrition.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for destiny to attrition
	 * @param playerId the player drawing destiny to attrition
	 * @return the destiny to attrition value
	 */
	default float getDestinyToAttrition(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		return result;
	}

	/**
	 * Gets the value of a drawn carbon-freezing destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for carbon-freezing destiny
	 * @return the total battle destiny
	 */
	default float getCarbonFreezingDestiny(GameState gameState, PhysicalCard physicalCard) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_CARBON_FREEZING_DESTINY)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getValue(gameState, query(), physicalCard);
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		return result;
	}

	default float getTotalCarbonFreezingDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		float result = baseTotalDestiny;
		for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_CARBON_FREEZING_DESTINY)) {
			if (modifier.isForPlayer(playerId)) {
				result += modifier.getValue(gameState, query(), (PhysicalCard) null);
			}
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn asteroid destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for asteroid destiny
	 * @param playerId the player drawing asteroid destiny
	 * @return the asteroid destiny value
	 */
	default float getAsteroidDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		PhysicalCard targetedStarship = gameState.getStarshipDrawingAsteroidDestinyAgainst();
		if (targetedStarship != null) {
			for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_ASTEROID_DESTINY)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					if (modifier.isAffectedTarget(gameState, query(), targetedStarship)) {
						result += modifier.getValue(gameState, query(), physicalCard);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Gets the total asteroid destiny value after applying modifiers to the base total asteroid destiny.
	 * @param gameState the game state
	 * @param playerId the player with the asteroid destiny
	 * @param baseTotalDestiny the base total asteroid destiny
	 * @return the total asteroid destiny
	 */
	default float getTotalAsteroidDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		float result = baseTotalDestiny;

		PhysicalCard location = gameState.getLocationDrawingAsteroidDestinyAt();
		if (location == null)
			return result;

		// Add 1 for each additional sector at that system that has 'Asteroid Rules' in effect.
		int additionalSectors = Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.asteroidRulesInEffect, Filters.relatedAsteroidSector(location)));
		result += additionalSectors;

		PhysicalCard targetedStarship = gameState.getStarshipDrawingAsteroidDestinyAgainst();
		if (targetedStarship != null) {
			for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_ASTEROID_DESTINY)) {
				if (modifier.isAffectedTarget(gameState, query(), targetedStarship)) {
					result += modifier.getValue(gameState, query(), targetedStarship);
				}
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn search party destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for search party destiny
	 * @param playerId the player with the search party destiny
	 * @return the total battle destiny
	 */
	default float getSearchPartyDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_SEARCH_PARTY_DESTINY_AT_LOCATION, gameState.getSearchPartyLocation())) {
			if (modifier.isForPlayer(playerId)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), physicalCard);
				}
			}
		}

		// Add 1 for each character (or 2 if a scout) in search party.
		for (PhysicalCard searchPartyMember : gameState.getSearchParty()) {
			result += (Filters.scout.accepts(gameState, query(), searchPartyMember) ? 2 : 1);
		}

		return result;
	}

	/**
	 * Gets the total search party destiny value after applying modifiers to the base search party destiny.
	 * @param gameState the game state
	 * @param playerId the player with the search party destiny
	 * @param baseTotalDestiny the base total search party destiny
	 * @return the total battle destiny
	 */
	default float getTotalSearchPartyDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		return Math.max(0, baseTotalDestiny);
	}

	/**
	 * Gets the value of a drawn tractor beam destiny.
	 * @param gameState the game state
	 * @param tractorBeam the tractor beam
	 * @param physicalCard the card drawn for tractor beam destiny
	 * @param playerId the player with the tractor beam destiny
	 * @return the tractor beam destiny draw value
	 */
	default float getTractorBeamDestiny(GameState gameState, PhysicalCard tractorBeam, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_TRACTOR_BEAM_DESTINY, tractorBeam)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getValue(gameState, query(), physicalCard);
			}
		}

		return result;
	}

	/**
	 * Gets the value of a drawn training destiny.
	 * @param gameState the game state
	 * @param jediTest the Jedi Test
	 * @param physicalCard the card drawn for training destiny
	 * @param playerId the player with the training destiny
	 * @return the training destiny draw value
	 */
	default float getTrainingDestiny(GameState gameState, PhysicalCard jediTest, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_TRAINING_DESTINY, jediTest)) {
			if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
				result += modifier.getValue(gameState, query(), physicalCard);
			}
		}

		return result;
	}

	/**
	 * Gets the total training destiny value after applying modifiers to the base training destiny.
	 * @param gameState the game state
	 * @param jediTest the Jedi Test
	 * @param baseTotalDestiny the base total training destiny
	 * @return the total battle destiny
	 */
	default float getTotalTrainingDestiny(GameState gameState, PhysicalCard jediTest, float baseTotalDestiny) {
		float result = baseTotalDestiny;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_TRAINING_DESTINY, jediTest)) {
			result += modifier.getValue(gameState, query(), jediTest);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the total training destiny value after applying modifiers to the base tractor beam destiny.
	 * @param gameState the game state
	 * @param tractorBeam the tractor beam
	 * @param baseTotalDestiny the base total tractor beam destiny
	 * @return the total battle destiny
	 */
	default float getTotalTractorBeamDestiny(GameState gameState, PhysicalCard tractorBeam, float baseTotalDestiny) {
		float result = baseTotalDestiny;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_TRACTOR_BEAM_DESTINY, tractorBeam)) {
			result += modifier.getValue(gameState, query(), tractorBeam);
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn duel destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for duel destiny
	 * @param playerId the player drawing duel destiny
	 * @return the duel destiny value
	 */
	default float getDuelDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DUEL_DESTINY)) {
			if (modifier.isForPlayer(playerId)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), physicalCard);
				}
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		return result;
	}

	/**
	 * Gets the value of a drawn lightsaber combat destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for lightsaber combat destiny
	 * @param playerId the player drawing lightsaber combat destiny
	 * @return the lightsaber combat destiny value
	 */
	default float getLightsaberCombatDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
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
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_LIGHTSABER_COMBAT_DESTINY)) {
			if (modifier.isForPlayer(playerId)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), physicalCard);
				}
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getValue(gameState, query(), (PhysicalCard) null);
				}
			}
		}

		return result;
	}

	/**
	 * Gets the total destiny value after applying modifiers to the base total destiny.
	 * @param gameState the game state
	 * @param playerId the player with the destiny
	 * @param baseTotalDestiny the base total destiny
	 * @return the total destiny
	 */
	default float getTotalDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		float result = baseTotalDestiny;
		for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_DESTINY)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				result += modifier.getValue(gameState, query(), (PhysicalCard) null);
			}
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the total weapon destiny value after applying modifiers to the base total weapon destiny.
	 * @param gameState the game state
	 * @param playerId the player with the weapon destiny
	 * @param baseTotalDestiny the base total weapon destiny
	 * @return the total weapon destiny
	 */
	default float getTotalWeaponDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		float result = baseTotalDestiny;

		// Get from WeaponFiringState
		WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
		if (weaponFiringState != null) {
			PhysicalCard weapon = weaponFiringState.getCardFiring();
			SwccgBuiltInCardBlueprint permanentWeapon = weaponFiringState.getPermanentWeaponFiring();
			Collection<PhysicalCard> weaponTargets = weaponFiringState.getTargets();
			PhysicalCard cardFiringWeapon = weaponFiringState.getCardFiringWeapon();

			for (Modifier modifier : getModifiers(gameState, ModifierType.TOTAL_WEAPON_DESTINY)) {
				result += modifier.getTotalWeaponDestinyModifier(gameState, query(), cardFiringWeapon, weapon, permanentWeapon, weaponTargets);
			}
		}

		return Math.max(0, result);
	}

	default float getTotalWeaponDestinyForCombinedFiring(GameState gameState, String playerId, PhysicalCard weaponTarget, float baseTotalDestiny) {
		float result = baseTotalDestiny;
		//if (!gameState.getWeaponFiringState().isCombinedFiring())
		//    return result;
        /* TODO: Fix this

        Collection<PhysicalCard> differentCardTitlesFiring = gameState.getWeaponFiringState().getCardsWithDifferentTitlesFiring();
        for (PhysicalCard weapon : differentCardTitlesFiring) {
            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_WEAPON_DESTINY_FOR_WEAPON, weapon)) {
                result += modifier.getTotalWeaponDestinyModifier(gameState, query(), weapon.getAttachedTo(), weapon, weaponTarget);
            }

            for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_WEAPON_DESTINY_FOR_WEAPON_FIRED_BY, weapon.getAttachedTo())) {
                result += modifier.getTotalWeaponDestinyModifier(gameState, query(), weapon.getAttachedTo(), weapon, weaponTarget);
            }
        } */
		return Math.max(0, result);
	}

	/**
	 * Gets the total destiny to power value after applying modifiers to the base total destiny to power.
	 * @param gameState the game state
	 * @param playerId the player with the destiny to power
	 * @param baseTotalDestiny the base total destiny to power
	 * @return the total destiny to power
	 */
	default float getTotalDestinyToPower(GameState gameState, String playerId, float baseTotalDestiny) {
		return Math.max(0, baseTotalDestiny);
	}

	/**
	 * Gets the number of duel destiny draws for the specified player.
	 * @param gameState the game state
	 * @param player the player
	 * @return the number duel destiny draws
	 */
	default int getNumDuelDestinyDraws(GameState gameState, String player) {
		DuelState duelState = gameState.getDuelState();
		if (duelState ==null)
			return 0;

		int result = duelState.getBaseNumDuelDestinyDraws(player);
		// Check modifiers to "number of duel destiny draws"
		for (Modifier modifier : getModifiers(gameState, ModifierType.NUM_DUEL_DESTINY_DRAWS))
			result += modifier.getNumDuelDestinyDrawsModifier(player, gameState, query());

		return Math.max(0, result);
	}

	/**
	 * Gets the number of lightsaber combat destiny draws for the specified player.
	 * @param gameState the game state
	 * @param player the player
	 * @return the number lightsaber combat destiny draws
	 */
	default int getNumLightsaberCombatDestinyDraws(GameState gameState, String player) {
		LightsaberCombatState lightsaberCombatState = gameState.getLightsaberCombatState();
		if (lightsaberCombatState == null)
			return 0;

		int result = lightsaberCombatState.getBaseNumDuelDestinyDraws(player);
		for (Modifier modifier : getModifiers(gameState, ModifierType.NUM_LIGHTSABER_COMBAT_DESTINY_DRAWS)) {
			result += modifier.getNumLightsaberCombatDestinyDrawsModifier(player, gameState, query());
		}
		return Math.max(0, result);
	}

	/**
	 * Gets the total movement destiny value after applying modifiers to the base total movement destiny.
	 * @param gameState the game state
	 * @param playerId the player with the movement destiny
	 * @param baseTotalDestiny the base total movement destiny
	 * @return the total movement destiny
	 */
	default float getTotalMovementDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		float result = baseTotalDestiny;

		PhysicalCard starship = gameState.getStarshipDrawingMovementDestinyAgainst();
		if (starship == null)
			return result;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_MOVEMENT_DESTINY, starship)) {
			result += modifier.getValue(gameState, query(), starship);
		}

		return Math.max(0, result);
	}
}
