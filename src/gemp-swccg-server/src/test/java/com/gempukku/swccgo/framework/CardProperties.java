package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.timing.results.VehicleCrashedResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertIsInt;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Functions for checking aspects of a card, such as numeric stats and other state-based properties.
 *
 * If you are just checking the printed stats, then you can retrieve the blueprint of a card and check those values
 * directly.  These helper functions are more for live cards which may be affected by modifiers sourced from other cards.
 */
public interface CardProperties extends TestBase {

	/**
	 * Checks whether a given card is considered active and in play for purposes of game text and usage.
	 * @param card The card to check.
	 * @return True if the card is in play and active, false otherwise.
	 */
	default boolean IsCardActive(PhysicalCardImpl card) { return gameState().isCardInPlayActive(card); }


	/**
	 * @param card The target card
	 * @return All cards currently attached to the given card.
	 */
    default List<PhysicalCardImpl> GetAttachedCards(PhysicalCardImpl card) {
        return (List<PhysicalCardImpl>)(List<?>)gameState().getAttachedCards(card);
    }

	/**
	 * @param card The target card
	 * @return All cards currently stacked on the given card.
	 */
    default List<PhysicalCardImpl> GetStackedCards(PhysicalCardImpl card) {
        return (List<PhysicalCardImpl>)(List<?>)gameState().getStackedCards(card);
    }


	/**
	 * Determines if a given card is currently attached to another.  Checks both that the current zone of the card is
	 * correct, and also that it currently records the bearer as its attachment point.
	 * @param card The card which may or may not be attached
	 * @param bearer The card which supposedly bears the other card
	 * @return True if card is in the ATTACHED zone and records bearer as its AttachedTo.
	 */
	default boolean IsAttachedTo(PhysicalCardImpl bearer, PhysicalCardImpl card) {
		if(card.getZone() != Zone.ATTACHED) {
			return false;
		}

		return bearer == card.getAttachedTo();
	}

	/**
	 * Determines if a given card is currently stacked on another.  Checks both that the current zone of the card is
	 * correct, and also that it currently records the bearer as its attachment point.
	 * @param card The card which may or may not be attached
	 * @param on The card which is supposedly under the other card
	 * @return True if card is in the ATTACHED zone and records bearer as its AttachedTo.
	 */
	default boolean IsStackedOn(PhysicalCardImpl on, PhysicalCardImpl card) {
		if((card.getZone() != Zone.STACKED) && (card.getZone() != Zone.STACKED_FACE_DOWN)) {
			return false;
		}

		return on == card.getStackedOn();
	}

	/**
	 * Determines if given cards are currently riding another as a passenger or pilot.
	 * @param cards The card which may or may not be riding
	 * @param in The card which is supposedly holding the other cards
	 * @return True if the list of cards are all passengers or pilots of the given card.
	 */
	default boolean IsAboard(PhysicalCardImpl in, PhysicalCardImpl...cards) {
		return gameState().getAboardCards(in, false).containsAll(Arrays.stream(cards).toList());
	}

	/**
	 * Determines if given cards are currently riding another as a passenger.
	 * @param cards The card which may or may not be riding
	 * @param in The card which is supposedly holding the other cards
	 * @return True if the list of cards are all passengers of the given card.
	 */
	default boolean IsAboardAsPassenger(PhysicalCardImpl in, PhysicalCardImpl...cards) {
		return gameState().getPassengerCardsAboard(in).containsAll(Arrays.stream(cards).toList());
	}

	/**
	 * Determines if given cards are currently riding another as a pilot.
	 * @param cards The card which may or may not be riding
	 * @param in The card which is supposedly holding the other cards
	 * @return True if the list of cards are all pilots of the given card.
	 */
	default boolean IsAboardAsPilot(PhysicalCardImpl in, PhysicalCardImpl...cards) {
		return gameState().getPilotCardsAboard(game().getModifiersQuerying(), in, true).containsAll(Arrays.stream(cards).toList());
	}

	/**
	 * Retrieves the total unused passenger capacity for a ship or vehicle which could be used to accommodate additional
	 * passengers.
	 * @param vehicle The vehicle (or ship) to check
	 * @return The total unused passenger capacity of the given card.  0 if this card cannot hold any more passengers.
	 */
	default int GetPassengerCapacity(PhysicalCardImpl vehicle) {
		return gameState().getAvailablePassengerCapacity(game().getModifiersQuerying(), vehicle, null);
	}


	/**
	 * @param card The card to inspect.
	 * @return The modified current destiny of the card, as altered by all current in-game effects.
	 */
	default int GetDestiny(PhysicalCardImpl card)
	{
        float destiny = game().getModifiersQuerying().getDestiny(gameState(), card);
        assertIsInt(destiny);
        return Math.round(destiny);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current power of the card, as altered by all current in-game effects.
	 */
	default int GetPower(PhysicalCardImpl card)
	{
        float power = game().getModifiersQuerying().getPower(gameState(), card);
        assertIsInt(power);
		return Math.round(power);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current maneuver of the card, as altered by all current in-game effects.
	 */
	default int GetManeuver(PhysicalCardImpl card)
	{
        float maneuver = game().getModifiersQuerying().getManeuver(gameState(), card);
        assertIsInt(maneuver);
        return Math.round(maneuver);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current ability of the card, as altered by all current in-game effects.
	 */
	default int GetAbility(PhysicalCardImpl card)
	{
        float ability = game().getModifiersQuerying().getAbility(gameState(), card);
        assertIsInt(ability);
        return Math.round(ability);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current defense of the card, as altered by all current in-game effects.
	 */
	default int GetDefense(PhysicalCardImpl card) {
        float defense = game().getModifiersQuerying().getDefenseValue(gameState(), card);
        assertIsInt(defense);
        return Math.round(defense);
    }

	/**
	 * @param card The card to inspect.
	 * @return The modified current battle destiny ability of the card, as altered by all current in-game effects.
	 */
	default int GetBattleDestinyAbility(PhysicalCardImpl card)
	{
        float battlDestinyAbility = game().getModifiersQuerying().getAbilityForBattleDestiny(gameState(), card);
        assertIsInt(battlDestinyAbility);
        return Math.round(battlDestinyAbility);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current armor of the card, as altered by all current in-game effects.
	 */
	default int GetArmor(PhysicalCardImpl card)
	{
        float armor = game().getModifiersQuerying().getArmor(gameState(), card);
        assertIsInt(armor);
        return Math.round(armor);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current deploy cost of the card, as altered by all current in-game effects.
	 */
	default int GetDeployCost(PhysicalCardImpl card)
	{
        float deployCost = game().getModifiersQuerying().getDeployCost(gameState(), card);
        assertIsInt(deployCost);
        return Math.round(deployCost);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current forfeit value of the card, as altered by all current in-game effects.
	 */
	default int GetForfeit(PhysicalCardImpl card)
	{
        float forfeit = game().getModifiersQuerying().getForfeit(gameState(), card);
        assertIsInt(forfeit);
        return Math.round(forfeit);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current landspeed of the card, as altered by all current in-game effects.
	 */
	default int GetLandspeed(PhysicalCardImpl card)
	{
        float landspeed = game().getModifiersQuerying().getLandspeed(gameState(), card);
        assertIsInt(landspeed);
        return Math.round(landspeed);
	}

	/**
	 * @param card The card to inspect.
	 * @return The modified current hyperspeed of the card, as altered by all current in-game effects.
	 */
	default int GetHyperspeed(PhysicalCardImpl card)
	{
        float hyperspeed = game().getModifiersQuerying().getHyperspeed(gameState(), card);
        assertIsInt(hyperspeed);
        return Math.round(hyperspeed);
	}

	/**
	 * @param card The card to inspect.
	 * @param icon The icon to check for.
	 * @return The modified current icon count of the card, as altered by all current in-game effects.
	 */
	default int GetIconCount(PhysicalCardImpl card, Icon icon)
	{
        float iconCount = game().getModifiersQuerying().getIconCount(gameState(), card, icon);
        assertIsInt(iconCount);
        return Math.round(iconCount);
	}

	/**
	 * @param card The card to inspect.
	 * @param keyword The keyword to check for.
	 * @return Whether the current card has the given keyword, either printed on it or added (or removed) by a game effect.
	 */
    default boolean HasKeyword(PhysicalCardImpl card, Keyword keyword)
    {
        return game().getModifiersQuerying().hasKeyword(gameState(), card, keyword);
    }

	/**
	 * @param card The card to inspect.
	 * @param type The card type to check for.
	 * @return Whether the current card has the given type, either printed on it or added (or removed) by a game effect.
	 */
    default boolean HasType(PhysicalCardImpl card, CardType type)
    {
        return game().getModifiersQuerying().getCardTypes(gameState(), card).contains(type);
    }

	/**
	 * Checks whether the given cards are participating in the current battle or not.
	 * @param cards The cards to check.
	 * @return True if all provided cards are participating in a battle, false if any are not (or if there is not any
	 * currently running battle).
	 */
	default boolean IsParticipatingInBattle(PhysicalCardImpl...cards) {
		return Arrays.stream(cards).allMatch(card -> gameState().isParticipatingInBattle(card));
	}


	/**
	 * @param location The location to inspect for Dark Side saber icons printed on the card.
	 * @return The total number of Dark Side saber icons printed on the card (does not take modifiers into account).
	 */
	default int GetDSIconsOnLocation(PhysicalCardImpl location) {
		return location.getBlueprint().getIconCount(Icon.DARK_FORCE);
	}

	/**
	 * @param location The location to inspect for Light Side saber icons printed on the card.
	 * @return The total number of Light Side saber icons printed on the card (does not take modifiers into account).
	 */
	default int GetLSIconsOnLocation(PhysicalCardImpl location) {
		return location.getBlueprint().getIconCount(Icon.LIGHT_FORCE);
	}

	/**
	 * Checks whether one site is considered adjacent to another.
	 * @param site The site whose adjacency is in question.
	 * @param other The site the first site should be adjacent to.
	 * @return True if the two sites are considered adjacent, false otherwise.
	 */
	default boolean IsAdjacentTo(PhysicalCardImpl site, PhysicalCardImpl other) {
		return Filters.adjacentSite(site).accepts(game(), other);
	}

	/**
	 * Checks whether one site is considered related to another.
	 * @param site The site whose relationship is in question.
	 * @param other The site the first site should be related to.
	 * @return True if the two sites are considered related, false otherwise.
	 */
	default boolean IsRelatedTo(PhysicalCardImpl site, PhysicalCardImpl other) {
		return Filters.relatedSite(site).accepts(game(), other);
	}

	/**
	 * Checks whether a card is wholly immune to attrition
	 * @param card The card to check
	 * @return True if no amount of attrition can affect this card, false otherwise.
	 */
	default boolean IsImmuneToAttrition(PhysicalCardImpl card) {
		return game().getModifiersQuerying().getImmunityToAttritionOfExactly(gameState(), card) == Float.MAX_VALUE;
	}

	/**
	 * Checks for 'nighttime conditions' at a particular site
	 * @param site The site to check
	 * @return True if site is affected by nighttime conditions, false otherwise
	 */
	default boolean IsNighttimeAt(PhysicalCardImpl site) {
		return Filters.under_nighttime_conditions.accepts(game(), site);
	}
	
	default boolean IsMatchingPilot(PhysicalCardImpl ship, PhysicalCardImpl character) {
		return game().getModifiersQuerying().isMatchingPair(gameState(), character, ship);
	}


	/**
	 * Checks that all Icons in list are on card blueprint and that all Icons (in enum) not in the list are not on card blueprint
	 * @param bp The card blueprint to check
	 * @param allowedIcons The list of expected Icons to test against
	 */
	default void BlueprintIconCheck(SwccgCardBlueprint bp, List<Icon> allowedIcons) {
		for(var icon : Icon.values()) {
			if(allowedIcons.contains(icon)) {
				assertTrue("Card '" + bp.getTitle() + "' missing required icon:  " + icon, bp.hasIcon(icon));
			}
			else {
				assertFalse("Card '" + bp.getTitle() + "' included disallowed icon: " + icon, bp.hasIcon(icon));
			}
		}
	}

	/**
	 * Checks that all Personas in list are on card blueprint and that all Personas (in enum) not in the list are not on card blueprint
	 * @param bp The card blueprint to check
	 * @param allowedPersonas The list of expected Personas to test against
	 */
	default void BlueprintPersonaCheck(SwccgCardBlueprint bp, List<Persona> allowedPersonas) {
		for(var persona : Persona.values()) {
			if(allowedPersonas.contains(persona)) {
				assertTrue("Card '" + bp.getTitle() + "' missing required Persona: " + persona, bp.hasPersona(persona));
			}
			else {
				assertFalse("Card '" + bp.getTitle() + "' included disallowed Persona: " + persona,bp.hasPersona(persona));
			}
		}
	}

	/**
	 * Checks that all Keywords in list are on card blueprint and that all Keywords (in enum) not in the list are not on card blueprint
	 * @param bp The card blueprint to check
	 * @param allowedKeywords The list of expected Keywords to test against
	 */
	default void BlueprintKeywordCheck(SwccgCardBlueprint bp, List<Keyword> allowedKeywords) {
		for(var keyword : Keyword.values()) {
			if(allowedKeywords.contains(keyword)) {
				assertTrue("Card '" + bp.getTitle() + "' missing required Keyword: " + keyword, bp.hasKeyword(keyword));
			}
			else {
				assertFalse("Card '" + bp.getTitle() + "' included disallowed Keyword: " + keyword,bp.hasKeyword(keyword));
			}
		}
	}

	/**
	 * Checks that all CardTypes in list are on card blueprint and that all CardTypes (in enum) not in the list are not on card blueprint
	 * @param bp The card blueprint to check
	 * @param allowedCardTypes The list of expected CardTypes to test against
	 */
	default void BlueprintCardTypeCheck(SwccgCardBlueprint bp, List<CardType> allowedCardTypes) {
		for(var cardtype : CardType.values()) {
			if(allowedCardTypes.contains(cardtype)) {
				assertTrue("Card '" + bp.getTitle() + "' missing required CardType: " + cardtype,bp.isCardType(cardtype));
			}
			else {
				assertFalse("Card '" + bp.getTitle() + "' included disallowed CardType: " + cardtype,bp.isCardType(cardtype));
			}
		}
	}

	/**
	 * Checks that all ModelTypes in list are on card blueprint and that all ModelTypes (in enum) not in the list are not on card blueprint
	 * @param bp The card blueprint to check
	 * @param allowedModelTypes The list of expected ModelTypes to test against
	 */
	default void BlueprintModelTypeCheck(SwccgCardBlueprint bp, List<ModelType> allowedModelTypes) {
		for(var modeltype : ModelType.values()) {
			if(allowedModelTypes.contains(modeltype)) {
				assertTrue("Card '" + bp.getTitle() + "' missing required ModelType: " + modeltype,bp.getModelTypes().contains(modeltype));
			}
			else {
				assertFalse("Card '" + bp.getTitle() + "' included disallowed ModelType: " + modeltype,bp.getModelTypes().contains(modeltype));
			}
		}
	}

}


