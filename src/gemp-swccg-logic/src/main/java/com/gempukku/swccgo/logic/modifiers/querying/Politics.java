package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public interface Politics extends BaseQuery, Attributes {

	/**
	 * Gets the political agendas of the specified card.
	 * @param gameState the game state
	 * @param card the card
	 * @return the political agendas
	 */
	default List<Agenda> getAgendas(GameState gameState, PhysicalCard card) {
		List<Agenda> agendas = new LinkedList<Agenda>();
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return agendas;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_AGENDA, card)) {
			for (Agenda agenda : Agenda.values()) {
				if (!agendas.contains(agenda) && modifier.hasAgenda(agenda)) {
					agendas.add(agenda);
				}
			}
		}
		return agendas;
	}

	/**
	 * Determines if the card has the specified political agenda.
	 * @param gameState the game state
	 * @param card the card
	 * @param agenda the agenda
	 * @return true if card has the agenda, otherwise false
	 */
	default boolean hasAgenda(GameState gameState, PhysicalCard card, Agenda agenda) {
		if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
			return false;

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.GIVE_AGENDA, card)) {
			if (modifier.hasAgenda(agenda)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a card's current politics.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @return the card's politics
	 */
	default float getPolitics(GameState gameState, PhysicalCard physicalCard) {
		return getPolitics(gameState, physicalCard, new ModifierCollectorImpl());
	}

	/**
	 * Gets a card's current politics.
	 * @param gameState the game state
	 * @param physicalCard a card
	 * @param modifierCollector collector of affecting modifiers
	 * @return the card's politics
	 */
	default float getPolitics(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
		float result = physicalCard.getBlueprint().getPolitics();

		// If card is a character and it is "doubled", then double the printed number
		if (physicalCard.getBlueprint().getCardCategory()== CardCategory.CHARACTER
				&& isDoubled(gameState, physicalCard, modifierCollector)) {
			result *= 2;
		}

		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.POLITICS, physicalCard)) {
			result += modifier.getPoliticsModifier(gameState, query(), physicalCard);
			modifierCollector.addModifier(modifier);
		}

		// Check if value was reset to an "unmodifiable value", and use lowest found
		Float lowestResetValue = null;
		for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_POLITICS, physicalCard)) {
			float modifierAmount = modifier.getUnmodifiablePolitics(gameState, query(), physicalCard);
			lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
			modifierCollector.addModifier(modifier);
		}
		if (lowestResetValue != null) {
			result = lowestResetValue;
		}

		return Math.max(0, result);
	}

	/**
	 * Determines if a card's politics is more than a specified value.
	 *
	 * @param gameState the game state
	 * @param card a card
	 * @param value the maneuver value
	 * @return true if card's politics is more than the specified value, otherwise false
	 */
	default boolean hasPoliticsMoreThan(GameState gameState, PhysicalCard card, float value) {
		if (!hasPoliticsAttribute(card))
			return false;

		return getPolitics(gameState, card) > value;
	}

	/**
	 * Determines if a card's politics is equal to a specified value.
	 *
	 * @param gameState the game state
	 * @param card      a card
	 * @param value     the politics value
	 * @return true if card's politics is equal to the specified value, otherwise false
	 */
	default boolean hasPoliticsEqualTo(GameState gameState, PhysicalCard card, float value) {
		if (!hasPoliticsAttribute(card))
			return false;

		return getPolitics(gameState, card) == value;
	}
	
	/**
	 * Gets the player that has a "senate majority", or null if neither player does.
	 * @param gameState the game state
	 * @return the player with a "senate majority"
	 */
	default String getPlayerWithSenateMajority(GameState gameState) {
		PhysicalCard galacticSenate = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Galactic_Senate);
		if (galacticSenate == null)
			return null;

		float dsTotal = 0;
		float lsTotal = 0;

		Collection<PhysicalCard> cardsAtSenate = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.character, Filters.at(galacticSenate)));
		for (PhysicalCard cardAtSenate : cardsAtSenate) {
			if (cardAtSenate.getOwner().equals(gameState.getDarkPlayer()))
				dsTotal += getPolitics(gameState, cardAtSenate);
			else
				lsTotal += getPolitics(gameState, cardAtSenate);
		}

		if (dsTotal > lsTotal)
			return gameState.getDarkPlayer();
		else if (lsTotal > dsTotal)
			return gameState.getLightPlayer();
		else
			return null;
	}

	/**
	 * Gets the player's total politics at Galactic Senate.
	 * @param gameState the game state
	 * @param playerId the player
	 * @return the politics total
	 */
	default float getTotalPoliticsAtGalacticSenate(GameState gameState, String playerId) {
		PhysicalCard galacticSenate = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), Filters.Galactic_Senate);
		float total = 0;

		Collection<PhysicalCard> cardsAtSenate = Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.owner(playerId), Filters.character, Filters.at(galacticSenate)));
		for (PhysicalCard cardAtSenate : cardsAtSenate) {
			total += getPolitics(gameState, cardAtSenate);
		}

		return Math.max(0, total);
	}
}
