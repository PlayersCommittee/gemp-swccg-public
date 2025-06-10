package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsDuringAttackModifier;

import java.util.*;

public interface BattleDestiny extends BaseQuery, Flags, LocationControl {
	/**
	 * Gets the number of destinies that the specified player can draw for battle destiny.
	 * @param gameState the game state
	 * @param player the player
	 * @param isGetLimit if true, gets the limit to the number of draws, otherwise gets then the number that can be attempted
	 *                   when ignoring limit.
	 * @param isForGui if true, gets the number of destiny draws to show on user interface that can be attempted
	 * @return the number of battle destinies
	 */
	default int getNumBattleDestinyDraws(GameState gameState, String player, boolean isGetLimit, boolean isForGui) {
		BattleState battleState = gameState.getBattleState();
		if (battleState == null)
			return 0;

		int result = 0;
		float abilityRequired = 4;
		boolean abilityRequiredWasChanged = false;
		boolean moreThanAbilityRequired = false;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_REQUIRED_FOR_BATTLE_DESTINY_MODIFIER, battleState.getBattleLocation())) {
			if (modifier.isForPlayer(player)) {
				float value = modifier.getAbilityRequiredToDrawBattleDestinyModifier(player, gameState, query());
				abilityRequiredWasChanged = true;
				abilityRequired += value;
			}
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY_REQUIRED_FOR_BATTLE_DESTINY, battleState.getBattleLocation())) {
			if (modifier.isForPlayer(player)) {
				float value = modifier.getUnmodifiableAbilityRequiredToDrawBattleDestiny(player, gameState, query());
				abilityRequiredWasChanged = true;
				abilityRequired = Math.max(abilityRequired, value);
			}
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY_MORE_THAN_REQUIRED_FOR_BATTLE_DESTINY, battleState.getBattleLocation())) {
			if (modifier.isForPlayer(player)) {
				float value = modifier.getUnmodifiableAbilityRequiredToDrawBattleDestiny(player, gameState, query());
				if (!abilityRequiredWasChanged || value >= abilityRequired) {
					abilityRequiredWasChanged = true;
					moreThanAbilityRequired = true;
					abilityRequired = Math.max(abilityRequired, value);
				}
			}
		}

		float abilityForBattle = getTotalAbilityAtLocation(gameState, player, battleState.getBattleLocation(), false, false, true, battleState.getPlayerInitiatedBattle(), true, false, null);
		if ((!moreThanAbilityRequired && (abilityForBattle >= abilityRequired)) || (abilityForBattle > abilityRequired))
			result = 1;

		// Skip destiny draw adders if ability required was changed and requirement is not met
		if (!abilityRequiredWasChanged
				|| (!moreThanAbilityRequired && (abilityForBattle >= abilityRequired))
				|| (abilityForBattle > abilityRequired)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.NUM_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
				if (modifier.isForPlayer(player)) {
					result += modifier.getValue(gameState, query(), battleState.getBattleLocation());
				}
			}
		}

		result = Math.max(0, result);

		Collection<PhysicalCard> battleParticipants = battleState.getCardsParticipating(player);

		// If getting limit, then limit is MAX_BATTLE_DESTINY_DRAWS (or MIN_BATTLE_DESTINY_DRAWS if MIN_BATTLE_DESTINY_DRAWS is larger than the number
		// of destinies determined at query() point and larger than MAX_BATTLE_DESTINY_DRAWS).
		if (isGetLimit) {
			Integer curMinLimit = null;
			Integer curMaxLimit = null;

			boolean destiniesMayBeLimited = true;

			// check if the number of battle destiny draws for either player can't be limited
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DESTINY_DRAWS_MAY_NOT_BE_LIMITED_FOR_EITHER_PLAYER, battleState.getBattleLocation())) {
				if (modifier != null) {
					destiniesMayBeLimited = false;
				}
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
				if (destiniesMayBeLimited && modifier.isForPlayer(player)) {
					int limit = modifier.getMaximumBattleDestinyDrawsModifier(player, gameState, query());
					if (curMaxLimit == null || limit < curMaxLimit) {
						curMaxLimit = limit;
					}
				}
			}

			for (PhysicalCard battleParticipant : battleParticipants) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
					int limit = modifier.getMinimumBattleDestinyDrawsModifier(gameState, query());
					if (curMinLimit == null || limit > curMinLimit) {
						curMinLimit = limit;
					}
				}
			}

			if (curMinLimit != null && curMinLimit > result)
				return curMinLimit;

			if (curMinLimit != null && curMaxLimit != null)
				return Math.max(curMaxLimit, curMinLimit);

			if (curMinLimit == null && curMaxLimit != null)
				return curMaxLimit;

			return Integer.MAX_VALUE;
		}
		else {
			// Do not check MAX_BATTLE_DESTINY_DRAWS if not checking drawing limit or not for showing on user interface

			if (isForGui) {
				boolean destiniesMayBeLimited = true;

				// check if the number of battle destiny draws for a player can't be limited by the opponent
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.BATTLE_DESTINY_DRAWS_MAY_NOT_BE_LIMITED_FOR_EITHER_PLAYER, battleState.getBattleLocation())) {
					if (modifier != null) {
						destiniesMayBeLimited = false;
					}
				}

				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAX_BATTLE_DESTINY_DRAWS, battleState.getBattleLocation())) {
					if (destiniesMayBeLimited && modifier.isForPlayer(player)) {
						result = Math.min(result, modifier.getMaximumBattleDestinyDrawsModifier(player, gameState, query()));
					}
				}
			}

			for (PhysicalCard battleParticipant : battleParticipants) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
					result = Math.max(result, modifier.getMinimumBattleDestinyDrawsModifier(gameState, query()));
				}
			}
		}

		return Math.max(0, result);
	}

	/**
	 * Gets the value of a drawn battle destiny.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for battle destiny
	 * @param playerId the player drawing battle destiny
	 * @return the battle destiny value
	 */
	default float getBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
		PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();
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

		// Check if player's battle destiny modifiers affect total battle destiny instead
		boolean affectTotalBdInstead = hasFlagActive(gameState, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
			if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getDestinyModifier(gameState, query(), physicalCard);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
			if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_BATTLE_DESTINY, physicalCard)) {
			if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard);
				}
			}
		}
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
			if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
				if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					result += modifier.getBattleDestinyAtLocationModifier(playerId, gameState, query(), battleLocation);
				}
			}
		}
		for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
			if (modifier.isForTopDrawDestinyEffect(gameState)) {
				if (!affectTotalBdInstead || modifier.getSource(gameState) == null || !playerId.equals(modifier.getSource(gameState).getOwner())) {
					if (!mayNotModifyDestinyDraw(gameState, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
						result += modifier.getValue(gameState, query(), (PhysicalCard) null);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Gets the player's battle destiny modifiers and the modifier amount for the specified card drawn for battle destiny,
	 * even if the battle destiny cannot be modified. This is used when having the battle destiny modifiers affect total
	 * battle destiny instead.
	 * @param gameState the game state
	 * @param physicalCard the card drawn for battle destiny
	 * @param playerId the player drawing battle destiny
	 * @return the list of source card to modifier amount
	 */
	default List<Map<PhysicalCard, Float>> getPlayersBattleDestinyModifiersToApplyToTotalBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId) {
		List<Map<PhysicalCard, Float>> list = new LinkedList<Map<PhysicalCard, Float>>();

		// Check if player's battle destiny modifiers affect total battle destiny instead
		if (hasFlagActive(gameState, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId)) {

			PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();

			if (physicalCard != null) {
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY, physicalCard)) {
					if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
						list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyModifier(gameState, query(), physicalCard)));
					}
				}
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, physicalCard)) {
					if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
						list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard)));
					}
				}
				for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.DESTINY_WHEN_DRAWN_FOR_BATTLE_DESTINY, physicalCard)) {
					if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
						list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getDestinyWhenDrawnForDestinyModifier(gameState, query(), physicalCard)));
					}
				}
			}
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.EACH_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
				if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
					list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getBattleDestinyAtLocationModifier(playerId, gameState, query(), battleLocation)));
				}
			}
			for (Modifier modifier : getModifiers(gameState, ModifierType.EACH_DESTINY_DRAW)) {
				if (modifier.isForTopDrawDestinyEffect(gameState)) {
					if (modifier.getSource(gameState) != null && playerId.equals(modifier.getSource(gameState).getOwner())) {
						list.add(Collections.singletonMap(modifier.getSource(gameState), modifier.getValue(gameState, query(), (PhysicalCard) null)));
					}
				}
			}
		}
		return list;
	}

	/**
	 * Gets the total battle destiny value after applying modifiers to the base total battle destiny.
	 * @param gameState the game state
	 * @param playerId the player with the battle destiny
	 * @param baseTotalDestiny the base total battle destiny
	 * @return the total battle destiny
	 */
	default float getTotalBattleDestiny(GameState gameState, String playerId, float baseTotalDestiny) {
		PhysicalCard battleLocation = gameState.getBattleState().getBattleLocation();
		if (battleLocation == null)
			return 0;

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_TOTAL_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
			if (modifier.isForPlayer(playerId)) {
				if (!mayNotResetTotalBattleDestiny(gameState, playerId, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					float modifierAmount = modifier.getValue(gameState, query(), battleLocation);
					lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				}
			}
		}
		if (lowestResetValue != null) {
			return Math.max(0, lowestResetValue);
		}

		float result = baseTotalDestiny;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_BATTLE_DESTINY_AT_LOCATION, battleLocation)) {
			if (modifier.isForPlayer(playerId)) {
				if (!mayNotModifyTotalBattleDestiny(gameState, playerId, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null)) {
					boolean mayNotIncrease = mayNotIncreaseTotalBattleDestiny(gameState, playerId, modifier.getSource(gameState) != null ? modifier.getSource(gameState).getOwner() : null);

					float multiplyValue = modifier.getMultiplierValue(gameState, query(), battleLocation);
					float addValue = modifier.getValue(gameState, query(), battleLocation);
					if (!mayNotIncrease || multiplyValue < 1)
						result *= multiplyValue;
					if (!mayNotIncrease || addValue < 0)
						result += addValue;
				}
			}
		}
		return Math.max(0, result);
	}

	default boolean mayNotApplyAbilityForSenseOrAlterDestiny(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_SENSE_ALTER_DESTINY, card).isEmpty();
	}

	/**
	 * Determines if the specified card may not add battle destiny draws.
	 * @param gameState the game state
	 * @param card the card
	 * @return true if battle destinies draws may not be added, otherwise false
	 */
	default boolean mayNotAddBattleDestinyDraws(GameState gameState, PhysicalCard card) {
		return !getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_ADD_BATTLE_DESTINY_DRAWS, card).isEmpty();
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
	 * Determines if total battle destiny for a specified player may not be reset by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player with total battle destiny
	 * @param playerToReset the player to reset total battle destiny
	 * @return true if total battle destiny may not be reset, otherwise false
	 */
	default boolean mayNotResetTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToReset) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_RESET_TOTAL_BATTLE_DESTINY, battleLocation)) {
			if (modifier.mayNotResetBattleDestiny(playerDrawingDestiny, playerToReset)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if battle destiny draws by a specified player may not be reset by the other specified player.
	 * @param gameState the game state
	 * @param playerDrawingDestiny the player drawing battle destiny
	 * @param playerToReset the player to reset battle destiny
	 * @return true if battle destinies may not be reset, otherwise false
	 */
	default boolean mayNotResetBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToReset) {
		PhysicalCard battleLocation = gameState.getBattleLocation();
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_RESET_BATTLE_DESTINY, battleLocation)) {
			if (modifier.mayNotResetBattleDestiny(playerDrawingDestiny, playerToReset)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the current destiny draw may not be reset by the specified player.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return true if destiny may not be reset, otherwise false
	 */
	default boolean mayNotResetDestinyDraw(GameState gameState, String playerId) {
		DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
		if (drawDestinyState == null)
			return true;

		DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();

		if (drawDestinyEffect.isDestinyCanceled()
				|| drawDestinyEffect.getSubstituteDestiny() != null)
			return true;

		if (drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY) {
			if (mayNotResetBattleDestinyDraws(gameState, drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Determines if the number of battle destinies the specified player may draw if unable to otherwise.
	 * @param gameState the game state
	 * @param player the player
	 * @return the number of battle destinies
	 */
	default int getNumBattleDestinyDrawsIfUnableToOtherwise(GameState gameState, String player) {
		BattleState battleState = gameState.getBattleState();
		if (battleState == null)
			return 0;

		int result = 0;
		for (PhysicalCard battleParticipant : battleState.getCardsParticipating(player)) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MIN_BATTLE_DESTINY_DRAWS, battleParticipant)) {
				result = Math.max(result, modifier.getMinimumBattleDestinyDrawsModifier(gameState, query()));
			}
		}

		return result;
	}


}
