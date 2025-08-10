package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public interface Ability extends BaseQuery, Destiny, Icons, JediTests, Piloting, Prohibited {

	/**
	 * Determines if the card has ability.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param includePermPilots true if including permanent pilots, otherwise false
	 * @return true if card has ability, otherwise false
	 */
	default boolean hasAbility(GameState gameState, PhysicalCard card, boolean includePermPilots) {
		return hasAbility(gameState, card, includePermPilots, false);
	}

	private boolean hasAbility(GameState gameState, PhysicalCard card, boolean includePermPilots, boolean skipAbilityValueCheck) {
		if (!includePermPilots && !card.getBlueprint().hasAbilityAttribute() && !card.isDejarikHologramAtHolosite())
			return false;

		if (!skipAbilityValueCheck) {
			return getAbility(gameState, card, includePermPilots) > 0;
		}

		return true;
	}

	/**
	 * Determines if a card's ability is less than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the ability value
	 * @return true if card's ability is less than the specified value, otherwise false
	 */
	default boolean hasAbilityLessThan(GameState gameState, PhysicalCard card, float value) {
		return getAbility(gameState, card) < value;
	}

	/**
	 * Determines if a card's ability is equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the ability value
	 * @return true if card's ability is equal to the specified value, otherwise false
	 */
	default boolean hasAbilityEqualTo(GameState gameState, PhysicalCard card, float value) {
		return getAbility(gameState, card) == value;
	}

	/**
	 * Determines if a card's ability is more than a specified value.
	 *
	 * @param gameState the game state
	 * @param card      a card
	 * @param value     the ability value
	 * @param includePermPilots true if ability of permanent pilots is included, otherwise false
	 * @return true if card's ability is more than the specified value, otherwise false
	 */
	default boolean hasAbilityMoreThan(GameState gameState, PhysicalCard card, float value, boolean includePermPilots) {
		return getAbility(gameState, card, includePermPilots) > value;
	}

	/**
	 * Determines if a card is a player's highest ability character on the the table. If multiple characters have the
	 * highest ability, then all of them are considered to be a highest ability character.
	 *
	 * @param gameState the game state
	 * @param source the card that is performing this query
	 * @param card a card
	 * @return true if the card is the owning player's highest ability character on the table, otherwise false
	 */
	default boolean isPlayersHighestAbilityCharacter(GameState gameState, PhysicalCard source, PhysicalCard card, String playerId) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER
				|| !card.getOwner().equals(playerId))
			return false;

		if (excludedFromBeingHighestAbilityCharacter(gameState, source, card))
			return false;

		float ability = getAbility(gameState, card);
		if (ability == 0)
			return false;

		return !Filters.canSpot(gameState.getGame(), source,
				Filters.and(Filters.owner(playerId), CardCategory.CHARACTER, Filters.abilityMoreThan(ability), Filters.notExcludedFromBeingHighestAbilityCharacter(source), Filters.canBeTargetedBy(source)));
	}

	/**
	 * Determines if the specified card is excluded from being the highest-ability character from the perspective of the
	 * card performing the query.
	 * @param gameState the game state
	 * @param cardPerformingQuery the card performing the query
	 * @param card the card
	 * @return true if not allowed, otherwise false
	 */
	default boolean excludedFromBeingHighestAbilityCharacter(GameState gameState, PhysicalCard cardPerformingQuery, PhysicalCard card) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_HIGHEST_ABILITY_CHARACTER, card)) {
			if (modifier.isAffectedTarget(gameState, query(), cardPerformingQuery)) {
				return true;
			};
		}

		return false;
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard) {
		return getAbility(gameState, physicalCard, false);
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard, PhysicalCard cardTargetingMe) {
		return getAbility(gameState, physicalCard, false, new ModifierCollectorImpl(), cardTargetingMe);
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		return getAbility(gameState, physicalCard, false, modifierCollector);
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots) {
		return getAbility(gameState, physicalCard, includePermPilots, new ModifierCollectorImpl());
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots, ModifierCollector modifierCollector) {
		return getAbility(gameState, physicalCard, includePermPilots, modifierCollector, null);
	}

	default float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots, ModifierCollector modifierCollector, PhysicalCard cardTargetingMe) {

		SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
		float result = 0;

		// Some cards allow for using a specific number vs a particular other card
		if (cardTargetingMe != null) {
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.USE_SPECIFIC_ABILITY_VS_CARD, physicalCard)) {

				UseSpecificAbilityVsCardModifier resetModifier = (UseSpecificAbilityVsCardModifier)modifier;
				if (resetModifier != null) {
					Filterable specificCardFilter = resetModifier.getSpecificCardFilter();
					if (Filters.or(specificCardFilter).accepts(gameState, query(),  cardTargetingMe)) {
						return resetModifier.getValue(gameState, query(),  physicalCard);
					}
				}
			}
		}


		// Use destiny number instead if "Dejarik Rules"
		if (physicalCard.isDejarikHologramAtHolosite()) {
			result = getDestiny(gameState, physicalCard);
		}
		else {
			if (physicalCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER
					&& physicalCard.getBlueprint().getCardCategory() != CardCategory.STARSHIP
					&& physicalCard.getBlueprint().getCardCategory() != CardCategory.VEHICLE)
				return 0;

			if (!hasAbility(gameState, physicalCard, includePermPilots, true))
				return 0;

			// Check if value was reset to an "unmodifiable value", and use lowest found
			Float lowestResetValue = null;
			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_ABILITY, physicalCard)) {
				float modifierAmount = modifier.getUnmodifiableAbility(gameState, query(),  physicalCard);
				lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
				modifierCollector.addModifier(modifier);
			}
			if (lowestResetValue != null) {
				return lowestResetValue;
			}
		}

		if (Filters.or(Filters.character, Filters.creature_vehicle).accepts(gameState, query(),  physicalCard)) {
			Float ability = blueprint.getAbility();

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.PRINTED_ABILITY, physicalCard)) {
				ability = modifier.getPrintedValueDefinedByGameText(gameState, query(),  physicalCard);
				modifierCollector.addModifier(modifier);
			}
			// If value if undefined, then return 0
			if (ability == null)
				return 0;

			result = ability;

			// If card is a character and it is "doubled", then double the printed number
			if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
					&& isDoubled(gameState, physicalCard, modifierCollector)) {
				result *= 2;
			}

			for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY, physicalCard)) {
				result += modifier.getAbilityModifier(gameState, query(),  physicalCard);
				modifierCollector.addModifier(modifier);
			}
		}

		// If character has completed any Jedi Tests, ability is increased to highest Jedi Test number completed
		if (blueprint.getCardCategory() == CardCategory.CHARACTER) {
			Collection<PhysicalCard> completedJediTests = Filters.filterAllOnTable(gameState.getGame(), Filters.and(Filters.completed_Jedi_Test, Filters.jediTestTargetingApprentice(Filters.sameCardId(physicalCard))));
			for (PhysicalCard completedJediTest : completedJediTests) {
				int jediTestNumber = getJediTestNumber(gameState, completedJediTest);
				if (jediTestNumber > result && gameState.isCardInPlayActive(completedJediTest)) {
					result = jediTestNumber;
				}
			}
		}

		if (includePermPilots
				&& (blueprint.getCardCategory()==CardCategory.STARSHIP || blueprint.getCardCategory()==CardCategory.VEHICLE)
				&& !physicalCard.isCrashed()
				&& hasIcon(gameState, physicalCard, Icon.PILOT)) {
			List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
			if (permPilots != null) {
				for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
					float permPilotAbility = permPilot.getAbility();
					if (permPilotAbility == 1) {
						for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
							permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, query(),  physicalCard));
							modifierCollector.addModifier(modifier);
						}
					}
					result += permPilotAbility;
				}
			}
		}

		return Math.max(0, result);
	}




	default float getHighestAbilityPiloting(GameState gameState, PhysicalCard physicalCard, boolean onlyPermanentPilots, boolean excludePermPilots) {
		SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
		if (blueprint.getCardCategory()!=CardCategory.STARSHIP && blueprint.getCardCategory()!=CardCategory.VEHICLE)
			return 0;

		float result = 0;
		List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
		if (!excludePermPilots && permPilots != null) {
			for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
				float permPilotAbility = permPilot.getAbility();
				if (permPilotAbility == 1) {
					for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
						permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, query(),  physicalCard));
					}
				}
				if (permPilotAbility > result) {
					result = permPilotAbility;
				}
			}
		}

		if (!onlyPermanentPilots) {
			List<PhysicalCard> pilots = gameState.getPilotCardsAboard(query(),  physicalCard, true);
			for (PhysicalCard pilot : pilots) {
				float pilotAbility = getAbility(gameState, pilot);
				if (pilotAbility > result) {
					result = pilotAbility;
				}
			}
		}

		return Math.max(0, result);
	}

	default List<Float> getAbilityOfPilotsAboard(GameState gameState, PhysicalCard physicalCard) {
		SwccgCardBlueprint blueprint = physicalCard.getBlueprint();
		if (blueprint.getCardCategory()!=CardCategory.STARSHIP && blueprint.getCardCategory()!=CardCategory.VEHICLE) {
			return Collections.emptyList();
		}

		List<Float> abilities = new LinkedList<Float>();
		List<SwccgBuiltInCardBlueprint> permPilots = getPermanentPilotsAboard(gameState, physicalCard);
		if (permPilots != null) {
			for (SwccgBuiltInCardBlueprint permPilot : permPilots) {
				float permPilotAbility = permPilot.getAbility();
				if (permPilotAbility == 1) {
					for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.REPLACE_ABILITY_1_PERMANENT_PILOTS, physicalCard)) {
						permPilotAbility = Math.max(permPilotAbility, modifier.getReplacementPermanentPilotAbility(gameState, query(),  physicalCard));
					}
				}
				abilities.add(permPilotAbility);
			}
		}

		List<PhysicalCard> pilots = gameState.getPilotCardsAboard(query(),  physicalCard, true);
		for (PhysicalCard pilot : pilots) {
			float pilotAbility = getAbility(gameState, pilot);
			abilities.add(pilotAbility);
		}

		return abilities;
	}

	default float getAbilityForBattleDestiny(GameState gameState, PhysicalCard physicalCard) {
		if (cannotApplyAbilityForBattleDestiny(gameState, physicalCard))
			return 0;

		if (physicalCard.getBlueprint().getCardCategory()==CardCategory.STARSHIP || physicalCard.getBlueprint().getCardCategory()==CardCategory.VEHICLE) {
			if (!isPiloted(gameState, physicalCard, false)
					|| isPermanentPilotsNotAbleToApplyAbilityForBattleDestiny(gameState, physicalCard)) {
				return 0;
			}
		}

		float result = getAbility(gameState, physicalCard, true);

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.ABILITY_FOR_BATTLE_DESTINY, physicalCard)) {
			result += modifier.getValue(gameState, query(),  physicalCard);
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if a specified player's total ability at the specified location may not be reduced.
	 * @param gameState the game state
	 * @param location the location
	 * @param playerId the player
	 * @return true or false
	 */
	default boolean isProhibitedFromHavingTotalAbilityReduced(GameState gameState, PhysicalCard location, String playerId) {
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_HAVE_TOTAL_ABILITY_AT_LOCATION_REDUCED, location)) {
			if (modifier.isForPlayer(playerId)) {
				return true;
			}
		}
		return false;
	}
}
