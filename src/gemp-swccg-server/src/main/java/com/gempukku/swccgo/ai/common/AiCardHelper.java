package com.gempukku.swccgo.ai.common;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;
import java.util.Set;

/**
 * Utility class providing convenient access to card properties for AI decision-making.
 * Wraps GEMP's SwccgCardBlueprint API with null-safe accessors.
 *
 * All methods are static for easy access without needing an instance.
 */
public class AiCardHelper {

    // =========================================================================
    // Basic Stat Accessors (null-safe, returns 0/0.0 for missing values)
    // =========================================================================

    /**
     * Gets the printed power value of a card.
     * @param card the card to query
     * @return power value, or 0 if not applicable
     */
    public static int getPower(PhysicalCard card) {
        if (card == null) return 0;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0;
        Float power = blueprint.getPower();
        return power != null ? power.intValue() : 0;
    }

    /**
     * Gets the printed ability value of a card.
     * @param card the card to query
     * @return ability value, or 0 if not applicable
     */
    public static int getAbility(PhysicalCard card) {
        if (card == null) return 0;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0;
        Float ability = blueprint.getAbility();
        return ability != null ? ability.intValue() : 0;
    }

    /**
     * Gets the printed deploy cost of a card.
     * @param card the card to query
     * @return deploy cost, or 0 if not applicable
     */
    public static int getDeployCost(PhysicalCard card) {
        if (card == null) return 0;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0;
        Float deploy = blueprint.getDeployCost();
        return deploy != null ? deploy.intValue() : 0;
    }

    /**
     * Gets the printed forfeit value of a card.
     * @param card the card to query
     * @return forfeit value, or 0 if not applicable
     */
    public static int getForfeit(PhysicalCard card) {
        if (card == null) return 0;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0;
        Float forfeit = blueprint.getForfeit();
        return forfeit != null ? forfeit.intValue() : 0;
    }

    /**
     * Gets the printed destiny value of a card.
     * @param card the card to query
     * @return destiny value, or 0.0 if not applicable
     */
    public static float getDestiny(PhysicalCard card) {
        if (card == null) return 0.0f;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0.0f;
        Float destiny = blueprint.getDestiny();
        return destiny != null ? destiny : 0.0f;
    }

    // =========================================================================
    // Icon Checks
    // =========================================================================

    /**
     * Checks if a card has the Pilot skill icon.
     * @param card the card to query
     * @return true if the card has the Pilot icon
     */
    public static boolean isPilot(PhysicalCard card) {
        return hasIcon(card, Icon.PILOT);
    }

    /**
     * Checks if a card has the Warrior skill icon.
     * @param card the card to query
     * @return true if the card has the Warrior icon
     */
    public static boolean isWarrior(PhysicalCard card) {
        return hasIcon(card, Icon.WARRIOR);
    }

    /**
     * Checks if a card has a permanent weapon.
     * @param card the card to query
     * @return true if the card has a permanent weapon
     */
    public static boolean hasPermanentWeapon(PhysicalCard card) {
        return hasIcon(card, Icon.PERMANENT_WEAPON);
    }

    /**
     * Checks if a card has a specific icon.
     * @param card the card to query
     * @param icon the icon to check for
     * @return true if the card has the icon
     */
    public static boolean hasIcon(PhysicalCard card, Icon icon) {
        if (card == null || icon == null) return false;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return false;
        return blueprint.hasIcon(icon);
    }

    /**
     * Gets the count of force icons on a card.
     * @param card the card to query
     * @param side which side's force icons to count (DARK or LIGHT)
     * @return number of force icons, or 0 if none
     */
    public static int getForceIcons(PhysicalCard card, Side side) {
        if (card == null || side == null) return 0;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return 0;
        Icon forceIcon = (side == Side.DARK) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
        return blueprint.getIconCount(forceIcon);
    }

    // =========================================================================
    // Card Type Checks
    // =========================================================================

    /**
     * Checks if a card is a character.
     * @param card the card to query
     * @return true if the card is a character
     */
    public static boolean isCharacter(PhysicalCard card) {
        return hasCategory(card, CardCategory.CHARACTER);
    }

    /**
     * Checks if a card is a starship.
     * @param card the card to query
     * @return true if the card is a starship
     */
    public static boolean isStarship(PhysicalCard card) {
        return hasCategory(card, CardCategory.STARSHIP);
    }

    /**
     * Checks if a card is a vehicle.
     * @param card the card to query
     * @return true if the card is a vehicle
     */
    public static boolean isVehicle(PhysicalCard card) {
        return hasCategory(card, CardCategory.VEHICLE);
    }

    /**
     * Checks if a card is a location.
     * @param card the card to query
     * @return true if the card is a location
     */
    public static boolean isLocation(PhysicalCard card) {
        return hasCategory(card, CardCategory.LOCATION);
    }

    /**
     * Checks if a card is a weapon.
     * @param card the card to query
     * @return true if the card is a weapon
     */
    public static boolean isWeapon(PhysicalCard card) {
        return hasCategory(card, CardCategory.WEAPON);
    }

    /**
     * Checks if a card is an effect.
     * @param card the card to query
     * @return true if the card is an effect
     */
    public static boolean isEffect(PhysicalCard card) {
        return hasCategory(card, CardCategory.EFFECT);
    }

    /**
     * Checks if a card is an interrupt.
     * @param card the card to query
     * @return true if the card is an interrupt
     */
    public static boolean isInterrupt(PhysicalCard card) {
        return hasCategory(card, CardCategory.INTERRUPT);
    }

    /**
     * Checks if a card has a specific card category.
     * @param card the card to query
     * @param category the category to check for
     * @return true if the card has the category
     */
    public static boolean hasCategory(PhysicalCard card, CardCategory category) {
        if (card == null || category == null) return false;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return false;
        return blueprint.getCardCategory() == category;
    }

    // =========================================================================
    // Matching Checks (Pilot/Ship, Weapon/Character)
    // =========================================================================

    /**
     * Checks if a pilot is a matching pilot for a ship.
     * Uses GEMP's built-in matching filter system.
     * @param pilot the pilot card
     * @param ship the ship card
     * @return true if the pilot matches the ship
     */
    public static boolean isMatchingPilot(PhysicalCard pilot, PhysicalCard ship) {
        if (pilot == null || ship == null) return false;
        SwccgCardBlueprint shipBlueprint = ship.getBlueprint();
        if (shipBlueprint == null) return false;

        Filter matchingPilotFilter = shipBlueprint.getMatchingPilotFilter();
        if (matchingPilotFilter == null) return false;

        // Check if pilot matches the filter
        return Filters.and(matchingPilotFilter).accepts(null, pilot);
    }

    /**
     * Checks if a ship matches a pilot (reverse lookup).
     * @param ship the ship card
     * @param pilot the pilot card
     * @return true if the ship matches the pilot
     */
    public static boolean isMatchingShip(PhysicalCard ship, PhysicalCard pilot) {
        if (ship == null || pilot == null) return false;
        SwccgCardBlueprint pilotBlueprint = pilot.getBlueprint();
        if (pilotBlueprint == null) return false;

        Filter matchingStarshipFilter = pilotBlueprint.getMatchingStarshipFilter();
        if (matchingStarshipFilter == null) return false;

        return Filters.and(matchingStarshipFilter).accepts(null, ship);
    }

    /**
     * Checks if a weapon can deploy on a target character.
     * @param weapon the weapon card
     * @param target the target character
     * @return true if the weapon can deploy on the target
     */
    public static boolean canWeaponDeployOn(PhysicalCard weapon, PhysicalCard target) {
        if (weapon == null || target == null) return false;
        SwccgCardBlueprint targetBlueprint = target.getBlueprint();
        if (targetBlueprint == null) return false;

        Filter matchingWeaponFilter = targetBlueprint.getMatchingWeaponFilter();
        if (matchingWeaponFilter == null) return true; // No restriction = can deploy

        return Filters.and(matchingWeaponFilter).accepts(null, weapon);
    }

    // =========================================================================
    // Live Game Values (includes modifiers!)
    // =========================================================================

    /**
     * Gets the modified (in-game) power of a card, including all active modifiers.
     * This is BETTER than static printed values because it includes bonuses from
     * piloting, locations, effects, etc.
     * @param game the current game
     * @param card the card to query
     * @return modified power value
     */
    public static int getModifiedPower(SwccgGame game, PhysicalCard card) {
        if (game == null || card == null) return 0;
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) return getPower(card);

        return (int) modifiersQuerying.getPower(gameState, card);
    }

    /**
     * Gets the modified (in-game) forfeit of a card, including all active modifiers.
     * @param game the current game
     * @param card the card to query
     * @return modified forfeit value
     */
    public static int getModifiedForfeit(SwccgGame game, PhysicalCard card) {
        if (game == null || card == null) return 0;
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) return getForfeit(card);

        return (int) modifiersQuerying.getForfeit(gameState, card);
    }

    /**
     * Gets the modified (in-game) deploy cost of a card, including all active modifiers.
     * @param game the current game
     * @param card the card to query
     * @return modified deploy cost
     */
    public static int getModifiedDeployCost(SwccgGame game, PhysicalCard card) {
        if (game == null || card == null) return 0;
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        if (gameState == null || modifiersQuerying == null) return getDeployCost(card);

        return (int) modifiersQuerying.getDeployCost(gameState, card);
    }

    // =========================================================================
    // Card Information
    // =========================================================================

    /**
     * Gets the title of a card.
     * @param card the card to query
     * @return the card title, or empty string if not available
     */
    public static String getTitle(PhysicalCard card) {
        if (card == null) return "";
        return card.getTitle();
    }

    /**
     * Gets the gametext of a card.
     * @param card the card to query
     * @return the gametext, or empty string if not available
     */
    public static String getGameText(PhysicalCard card) {
        if (card == null) return "";
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return "";
        String gametext = blueprint.getGameText();
        return gametext != null ? gametext : "";
    }

    /**
     * Gets the side (Dark/Light) of a card.
     * @param card the card to query
     * @return the side, or null if not available
     */
    public static Side getSide(PhysicalCard card) {
        if (card == null) return null;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return null;
        return blueprint.getSide();
    }

    /**
     * Checks if a card is unique.
     * @param card the card to query
     * @return true if the card is unique
     */
    public static boolean isUnique(PhysicalCard card) {
        if (card == null) return false;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return false;
        return blueprint.getUniqueness() != null &&
               blueprint.getUniqueness().isPerSystem() == false;
    }

    // =========================================================================
    // Persona Checking (for "dead card" detection)
    // =========================================================================

    /**
     * Checks if a card's persona is already deployed on the table.
     * In SWCCG, only one version of a persona can be on the table at a time.
     * For example, if "Luke Skywalker, Jedi Knight" is already deployed,
     * "Luke Skywalker, Rebel Scout" cannot be deployed (they share the Luke persona).
     *
     * @param card the card to check
     * @param game the current game
     * @param playerId the player to check for (only checks their cards)
     * @return true if any persona on this card is already deployed
     */
    public static boolean isPersonaAlreadyDeployed(PhysicalCard card, SwccgGame game, String playerId) {
        if (card == null || game == null || playerId == null) return false;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return false;

        // Get personas from the card
        Set<Persona> cardPersonas = blueprint.getPersonas();
        if (cardPersonas == null || cardPersonas.isEmpty()) return false;

        // Use Filters to check if any card with matching persona is on table
        // Filter for: owner matches AND has persona AND not the same card
        for (Persona cardPersona : cardPersonas) {
            // Find cards on table with this persona belonging to this player
            Collection<PhysicalCard> cardsWithPersona = Filters.filterAllOnTable(game,
                Filters.and(Filters.owner(playerId), Filters.persona(cardPersona)));

            // Check if we found any (excluding the card itself)
            for (PhysicalCard foundCard : cardsWithPersona) {
                if (foundCard.getCardId() != card.getCardId()) {
                    return true;
                }
            }

            // Also check crossed-over personas (e.g., Luke <-> Son Of Vader)
            Persona crossedOver = cardPersona.getCrossedOverPersona();
            if (crossedOver != null && !crossedOver.equals(cardPersona)) {
                Collection<PhysicalCard> crossedOverCards = Filters.filterAllOnTable(game,
                    Filters.and(Filters.owner(playerId), Filters.persona(crossedOver)));
                for (PhysicalCard foundCard : crossedOverCards) {
                    if (foundCard.getCardId() != card.getCardId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks if a card is a "dead card" - cannot be deployed due to persona rule.
     * This is a convenience wrapper around isPersonaAlreadyDeployed that also
     * checks if the card is deployable (character, starship, vehicle).
     *
     * @param card the card to check
     * @param game the current game
     * @param playerId the player to check for
     * @return true if the card is a dead card (persona already deployed)
     */
    public static boolean isDeadCard(PhysicalCard card, SwccgGame game, String playerId) {
        if (card == null) return false;
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (blueprint == null) return false;

        // Only check deployable cards with personas
        CardCategory category = blueprint.getCardCategory();
        if (category != CardCategory.CHARACTER &&
            category != CardCategory.STARSHIP &&
            category != CardCategory.VEHICLE) {
            return false;
        }

        return isPersonaAlreadyDeployed(card, game, playerId);
    }
}
