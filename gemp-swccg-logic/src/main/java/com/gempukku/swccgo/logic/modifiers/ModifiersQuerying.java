package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

// This interface provides methods to query
// game values, card stats, etc. with modifiers applied.
//
public interface ModifiersQuerying {


    LimitCounter getUntilStartOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase);

    LimitCounter getUntilEndOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase);

    LimitCounter getUntilEndOfBattleLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);

    LimitCounter getUntilEndOfDuelLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);

    LimitCounter getUntilEndOfTurnLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);

    LimitCounter getUntilEndOfTurnForCardTitleLimitCounter(String title, GameTextActionId gameTextActionId);

    LimitCounter getUntilEndOfCaptivityLimitCounter(String title, GameTextActionId cardAction, PhysicalCard captive);

    LimitCounter getUntilEndOfForceDrainLimitCounter(String title, GameTextActionId cardAction);

    LimitCounter getUntilEndOfForceLossLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);

    LimitCounter getPerRaceTotalLimitCounter(String title, GameTextActionId cardAction, float raceTotal);

    LimitCounter getUntilEndOfGameLimitCounter(String title, GameTextActionId cardAction);

    LimitCounter getCardTitlePlayedTurnLimitCounter(String title);

    /**
     * Gets all the cards that are targeting the specified card. This is used for the card info screen on the user interface.
     * @param gameState the game state
     * @param card the card
     * @return the cards targeting the specified card
     */
    List<PhysicalCard> getAllCardsTargetingCard(GameState gameState, PhysicalCard card);

    /**
     * Gets all the cards on table that are targeting the specified card.
     * @param gameState the game state
     * @param card the card
     * @return the cards on table targeting the specified card
     */
    List<PhysicalCard> getCardsOnTableTargetingCard(GameState gameState, PhysicalCard card);

    /**
     * Gets all the existing persistent modifiers that affect the specified card (skipping an extra checking).
     * This is used to determine which modifiers to exclude the card from when "restoring to normal".
     * @param gameState the game state
     * @param card the card
     * @return the modifiers
     */
    List<Modifier> getPersistentModifiersAffectingCard(GameState gameState, PhysicalCard card);

    Collection<Modifier> getModifiersAffecting(GameState gameState, PhysicalCard card);

    // Game flags
    boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag);

    boolean hasFlagActive(GameState gameState, ModifierFlag modifierFlag, String playerId);

    int getFlagActiveCount(GameState gameState, ModifierFlag modifierFlag, String playerId);

    /**
     * Excludes the specified card from being affected by the modifier.
     * This is typically used when a card is 'restored' to normal.
     * @param modifier the modifier
     * @param card the card
     */
    void excludeFromBeingAffected(Modifier modifier, PhysicalCard card);

    /**
     * Determines if the specified card is excluded from being affected by the modifier.
     * @param modifier the modifier
     * @param card the card
     * @return true or false
     */
    boolean isExcludedFromBeingAffected(Modifier modifier, PhysicalCard card);

    // Card Stats
    boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword);

    boolean hasKeyword(GameState gameState, PhysicalCard physicalCard, Keyword keyword, ModifierCollector modifierCollector);

    boolean hasLightAndDarkForceIcons(GameState gameState, PhysicalCard physicalCard, PhysicalCard ignoreForceIconsFromCard);

    boolean hasIcon(GameState gameState, PhysicalCard physicalCard, Icon icon);

    int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon);

    int getIconCount(GameState gameState, PhysicalCard physicalCard, Icon icon, ModifierCollector modifierCollector);

    boolean isSpecies(GameState gameState, PhysicalCard physicalCard, Species species);

    /**
     * Gets the personas of the specified card.
     * @param gameState the game state
     * @param card the card
     * @return personas
     */
    Set<Persona> getPersonas(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card has the specified persona.
     * @param gameState the game state
     * @param card the card
     * @param persona the persona
     * @return true or false
     */
    boolean hasPersona(GameState gameState, PhysicalCard card, Persona persona);

    /**
     * Gets the number of cards the specified player draws in starting hand.
     * @param gameState the game state
     * @param playerId the player
     * @return the number of cards
     */
    int getNumCardsToDrawInStartingHand(GameState gameState, String playerId);

    /**
     * Determines if the card has the specified political agenda.
     * @param gameState the game state
     * @param card the card
     * @param agenda the agenda
     * @return true if card has the agenda, otherwise false
     */
    boolean hasAgenda(GameState gameState, PhysicalCard card, Agenda agenda);

    /**
     * Gets the political agendas of the specified card.
     * @param gameState the game state
     * @param card the card
     * @return the political agendas
     */
    List<Agenda> getAgendas(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's current ferocity.
     * @param gameState the game state
     * @param physicalCard a card
     * @param ferocityDestinyTotal the ferocity destiny total, or null
     * @return the card's ferocity
     */
    float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal);

    /**
     * Gets a card's current ferocity.
     * @param gameState the game state
     * @param physicalCard a card
     * @param ferocityDestinyTotal the ferocity destiny total, or null
     * @param modifierCollector collector of affecting modifiers
     * @return the card's ferocity
     */
    float getFerocity(GameState gameState, PhysicalCard physicalCard, Float ferocityDestinyTotal, ModifierCollector modifierCollector);

    /**
     * Gets the card's number of ferocity destiny to draw.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's number of ferocity destiny
     */
    int getNumFerocityDestiny(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if the player may not draw race destiny.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    boolean mayNotDrawRaceDestiny(GameState gameState, String playerId);

    /**
     * Gets the Podracer's number of race destiny to draw.
     * @param gameState the game state
     * @param playerId the player to draw race destiny
     * @param physicalCard a card
     * @return the Podracer's number of race destiny to draw
     */
    int getNumRaceDestinyToDraw(GameState gameState, String playerId, PhysicalCard physicalCard);

    /**
     * Gets the Podracer's number of race destiny to choose (in case of draw X and choose Y).
     * @param gameState the game state
     * @param playerId the player to draw race destiny
     * @param physicalCard a card
     * @return the Podracer's number of race destiny to choose (in case of draw X and choose Y), otherwise 0
     */
    int getNumRaceDestinyToChoose(GameState gameState, String playerId, PhysicalCard physicalCard);

    /**
     * Gets a card's current power.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's power
     */
    float getPower(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's current power.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the card's power
     */
    float getPower(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Determines if a card's power is less than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the power value
     * @return true if card's power is less than the specified value, otherwise false
     */
    boolean hasPowerLessThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's power is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the power value
     * @return true if card's power is equal to the specified value, otherwise false
     */
    boolean hasPowerEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's power may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @return true if card's power may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Determines if a card's power may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's power may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingPowerReduced(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector);

    /**
     * Determines if a card's power may not be increased by certain cards.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param increasedByCard the card to check if its ability to increase power is being restricted
     * @return true if card's power may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard);

    /**
     * Determines if a card's power may not be increased by certain pilots.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param increasedByCard the card to check if its ability to increase power is being restricted
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's power may not be increased by pilots, otherwise false
     */
    boolean isProhibitedFromHavingPowerIncreasedByCard(GameState gameState, PhysicalCard card, String playerId, PhysicalCard increasedByCard, ModifierCollector modifierCollector);

    /**
     * Determines if a character's politics used for that card's power.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if character's politics is used for power, otherwise false
     */
    boolean isPoliticsUsedForPower(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's defense value may not be increased by the specified player.
     *
     * @param gameState         the game state
     * @param card              a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's defense value may not be increased, otherwise false
     */
    boolean isProhibitedFromHavingDefenseValueIncreasedBeyondPrinted(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's defense value may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @return true if card's defense value may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingDefenseValueReduced(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Determines if a card's defense value may not be reduced by the specified player.
     * @param gameState the game state
     * @param card a card
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's defense value may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingDefenseValueReduced(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector);

    float getAbility(GameState gameState, PhysicalCard physicalCard);

    float getAbility(GameState gameState, PhysicalCard physicalCard, PhysicalCard cardTargetingMe);

    float getAbility(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots);

    float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots, ModifierCollector modifierCollector);

    float getAbility(GameState gameState, PhysicalCard physicalCard, boolean includePermPilots, ModifierCollector modifierCollector, PhysicalCard cardTargetingMe);

    float getAbilityForBattleDestiny(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if the card has ability.
     *
     * @param gameState the game state
     * @param card a card
     * @param includePermPilots true if including permanent pilots, otherwise false
     * @return true if card has ability, otherwise false
     */
    boolean hasAbility(GameState gameState, PhysicalCard card, boolean includePermPilots);

    /**
     * Determines if a card's ability is less than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the ability value
     * @return true if card's ability is less than the specified value, otherwise false
     */
    boolean hasAbilityLessThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's ability is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the ability value
     * @return true if card's ability is equal to the specified value, otherwise false
     */
    boolean hasAbilityEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's ability is more than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the ability value
     * @param includePermPilots true if ability of permanent pilots is included, otherwise false
     * @return true if card's ability is more than the specified value, otherwise false
     */
    boolean hasAbilityMoreThan(GameState gameState, PhysicalCard card, float value, boolean includePermPilots);

    /**
     * Determines if a card has its ability-1 permanent pilot replaced.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    boolean isAbility1PermanentPilotReplaced(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's current politics.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the card's politics
     */
    float getPolitics(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's current politics.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the card's politics
     */
    float getPolitics(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Determines if a card's politics is more than a specified value.
     *
     * @param gameState the game state
     * @param card a card
     * @param value the maneuver value
     * @return true if card's politics is more than the specified value, otherwise false
     */
    boolean hasPoliticsMoreThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's politics is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the politics value
     * @return true if card's politics is equal to the specified value, otherwise false
     */
    boolean hasPoliticsEqualTo(GameState gameState, PhysicalCard card, float value);

    float getHighestAbilityPiloting(GameState gameState, PhysicalCard physicalCard, boolean onlyPermanentPilots, boolean excludePermPilots);

    List<Float> getAbilityOfPilotsAboard(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if the specified card may be forfeited in battle.
     * @param gameState the game state
     * @param physicalCard a card
     * @return true if card may be forfeited, otherwise false
     */
    boolean mayBeForfeitedInBattle(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if a card satisfies all battle damage when forfeited.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the forfeit value
     */
    boolean isSatisfyAllBattleDamageWhenForfeited(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if a card satisfies all attrition when forfeited.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the forfeit value
     */
    boolean isSatisfyAllAttritionWhenForfeited(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's current forfeit value to use when forfeiting card.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the forfeit value to use when forfeiting card
     */
    float getForfeitWhenForfeiting(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's current forfeit value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the forfeit value
     */
    float getForfeit(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's current forfeit value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the forfeit value
     */
    float getForfeit(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Determines if a card's forfeit value is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the forfeit value
     * @return true if card's forfeit value is equal to the specified value, otherwise false
     */
    boolean hasForfeitValueEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's forfeit value is more than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the forfeit value
     * @return true if card's forfeit value is more than the specified value, otherwise false
     */
    boolean hasForfeitValueMoreThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card remains in play and reduces it's forfeit when 'forfeited'.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    boolean isRemainsInPlayAndReducesForfeitWhenForfeited(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card's forfeit may not be reduced.
     * @param gameState the game state
     * @param card a card
     * @return true if card's forfeit may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card's forfeit may not be reduced.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's forfeit may not be reduced, otherwise false
     */
    boolean isProhibitedFromHavingForfeitReduced(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's forfeit may not be increased above printed value
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's forfeit may not be increased above printed values
     */
    boolean isProhibitedFromHavingForfeitIncreasedBeyondPrinted(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's forfeit may not be increased
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return true if card's forfeit may not be increased
     */
    boolean isProhibitedFromHavingForfeitValueIncreased(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's game text may not be canceled.
     * @param gameState the game state
     * @param card a card
     * @return true if card's game text may not be canceled, otherwise false
     */
    boolean isProhibitedFromHavingGameTextCanceled(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card may not be suspended.
     * @param gameState the game state
     * @param card a card
     * @return true if card may not be suspended, otherwise false
     */
    boolean isProhibitedFromBeingSuspended(GameState gameState, PhysicalCard card);

    /**
     * Determines if a Revolution card has its effects canceled.
     * @param gameState the game state
     * @param card a card
     * @return true or false
     */
    boolean isEffectsOfRevolutionCanceled(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card is lost anytime it is about to be stolen.
     * @param gameState the game state
     * @param card a card
     * @return true if card is lost anytime it is about to be stolen, otherwise false
     */
    boolean isLostIfAboutToBeStolen(GameState gameState, PhysicalCard card);

    /**
     * Determines if the card is granted the ability to use the device.
     * @param gameState the game state
     * @param card a card
     * @param device a device
     * @return true if card is granted ability to use the device, otherwise false
     */
    boolean grantedToUseDevice(GameState gameState, PhysicalCard card, PhysicalCard device);

    /**
     * Determines if the card is granted the ability to use the weapon.
     * @param gameState the game state
     * @param card a card
     * @param weapon a weapon
     * @return true if card is granted ability to use the weapon, otherwise false
     */
    boolean grantedToUseWeapon(GameState gameState, PhysicalCard card, PhysicalCard weapon);

    /**
     * Gets a card's current destiny value.
     * @param gameState the game state
     * @param card a card
     * @return the destiny value
     */
    float getDestiny(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's current destiny value.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return the destiny value
     */
    float getDestiny(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's destiny value is less than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the destiny value
     * @return true if card's destiny value is less than the specified value, otherwise false
     */
    boolean hasDestinyLessThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's destiny value is less than or equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the destiny value
     * @return true if card's destiny value is less than or equal to the specified value, otherwise false
     */
    boolean hasDestinyLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if a card's destiny value is equal to a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the destiny value
     * @return true if card's destiny value is equal to the specified value, otherwise false
     */
    boolean hasDestinyEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Gets the destiny value when a card is drawn for destiny.
     * @param gameState the game state
     * @param card a card
     * @param destinyDrawActionSource the source card for the draw destiny action
     * @return the destiny value
     */
    float getDestinyForDestinyDraw(GameState gameState, PhysicalCard card, PhysicalCard destinyDrawActionSource);

    /**
     * Determines if the card has landspeed.
     *
     * @param gameState the game state
     * @param card a card
     * @return true if card has landspeed, otherwise false
     */
    boolean hasLandspeed(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card's landspeed is more than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the landspeed value
     * @return true if card's landspeed is more than the specified value, otherwise false
     */
    boolean hasLandspeedMoreThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Gets a card's landspeed value.
     * @param gameState the game state
     * @param card a card
     * @return the landspeed value
     */
    float getLandspeed(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's landspeed value.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return the landspeed value
     */
    float getLandspeed(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if a card's landspeed may not be increased.
     * @param gameState the game state
     * @param card a card
     * @return true if card's landspeed may not be increased, otherwise false
     */
    boolean isProhibitedFromHavingLandspeedIncreased(GameState gameState, PhysicalCard card);

    /**
     * Gets landspeed required to move the card to the specified site using landspeed.
     * @param gameState the game state
     * @param card a card
     * @param toSite the site to move to
     * @return the landspeed required to move, or null if not valid to calculate
     */
    Integer getLandspeedRequired(GameState gameState, PhysicalCard card, PhysicalCard toSite);

    /**
     * Determines if the specified card is immune to landspeed requirements from the specified source card.
     * @param gameState the game state
     * @param card the card
     * @param sourceCard the source of the modifier
     * @return true if card is immune, otherwise false
     */
    boolean isImmuneToLandspeedRequirementsFromCard(GameState gameState, PhysicalCard card, PhysicalCard sourceCard);

    /**
     * Determines if the card does not have a hyperdrive.
     * @param gameState the game state
     * @param card a card
     * @return true if card does not have a hyperdrive, otherwise false
     */
    boolean hasNoHyperdrive(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's hyperspeed value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the hyperspeed value
     */
    float getHyperspeed(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's hyperspeed value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the hyperspeed value
     */
    float getHyperspeed(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Gets a card's hyperspeed value when moving to the specified system.
     * @param gameState the game state
     * @param card a card
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @return the hyperspeed value
     */
    float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem);

    /**
     * Gets a card's hyperspeed value when moving to the specified system.
     * @param gameState the game state
     * @param card a card
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param modifierCollector collector of affecting modifiers
     * @return the hyperspeed value
     */
    float getHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, ModifierCollector modifierCollector);

    /**
     * Determines if the card has maneuver.
     *
     * @param gameState the game state
     * @param card      a card
     * @return true if card has maneuver, otherwise false
     */
    boolean hasManeuver(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card's maneuver is more than a specified value.
     *
     * @param gameState the game state
     * @param card      a card
     * @param value     the maneuver value
     * @return true if card's maneuver is more than the specified value, otherwise false
     */
    boolean hasManeuverMoreThan(GameState gameState, PhysicalCard card, float value);

    /**
     * Gets a card's maneuver value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the maneuver value
     */
    float getManeuver(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's maneuver value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the maneuver value
     */
    float getManeuver(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Determines if the card has armor.
     *
     * @param gameState the game state
     * @param card      a card
     * @return true if card has armor, otherwise false
     */
    boolean hasArmor(GameState gameState, PhysicalCard card);

    /**
     * Gets a card's armor value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the armor value
     */
    float getArmor(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's armor value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the armor value
     */
    float getArmor(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    float getSpecialDefenseValue(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's defense value.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the defense value
     */
    float getDefenseValue(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets a card's defense value.
     * @param gameState the game state
     * @param physicalCard a card
     * @param modifierCollector collector of affecting modifiers
     * @return the defense value
     */
    float getDefenseValue(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    Statistic getDefenseValueStatistic(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the permanent weapon built into the card, not including if card is disarmed or game text canceled.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent weapon
     */
    SwccgBuiltInCardBlueprint getPermanentWeapon(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the permanent pilots and astromechs aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent pilots and astromechs
     */
    List<SwccgBuiltInCardBlueprint> getPermanentsAboard(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the permanent pilots aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent pilots
     */
    List<SwccgBuiltInCardBlueprint> getPermanentPilotsAboard(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the permanent astromechs aboard the card, not including any that are removed/suspended.
     * @param gameState the game state
     * @param physicalCard a card
     * @return the permanent astromechs
     */
    List<SwccgBuiltInCardBlueprint> getPermanentAstromechsAboard(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the modifiers from the specified permanent built-in.
     * @param gameState the game state
     * @param permanentBuiltIn the permanent built-in
     * @return the modifiers
     */
    List<Modifier> getModifiersFromPermanentBuiltIn(GameState gameState, SwccgBuiltInCardBlueprint permanentBuiltIn);

    /**
     * Gets the sabacc total for the player.
     *
     * @param gameState the game state
     * @param playerId  the player
     * @return the sabacc total
     */
    float getSabaccTotal(GameState gameState, String playerId);

    /**
     * Determines if the card may have its destiny number cloned in sabacc by specified player when not in sabacc hand.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true or false
     */
    boolean mayHaveDestinyNumberClonedInSabacc(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Gets the amount of Force generation at a location for the player.
     * @param gameState the game state
     * @param physicalCard the location
     * @param playerId the player
     * @return the amount of Force generation
     */
    float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the amount of Force generation at a location for the player.
     * @param gameState the game state
     * @param physicalCard the location
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return the amount of Force generation
     */
    float getForceGenerationFromLocation(GameState gameState, PhysicalCard physicalCard, String playerId, ModifierCollector modifierCollector);

    /**
     * Determines if the specified player is explicitly not allowed to activate Force due to existence of a "can't activate Force"
     * modifier affecting the player.
     *
     * @param gameState the game state
     * @param playerId  the player
     * @return true if player not allowed to activate Force, otherwise false
     */
    boolean isActivatingForceProhibited(GameState gameState, String playerId);

    /**
     * Determines if the specified player has activated Force (via Force generation) to reach the limit of Force generation.
     *
     * @param gameState the game state
     * @param playerId  the player
     * @return true if player has reached limit, otherwise false
     */
    boolean isActivateForceFromForceGenerationLimitReached(GameState gameState, String playerId);

    /**
     * Gets the total Force generation for the specified player.
     *
     * @param gameState the game state
     * @param playerId  the player
     * @return the total Force generation for the player
     */
    float getTotalForceGeneration(GameState gameState, String playerId);

    /**
     * Gets the total Force Icon count for the specified player.
     *
     * @param gameState the game state
     * @param playerId  the player
     */
    float getTotalForceIconCount(GameState gameState, String playerId);

    /**
     * Determines if this deploys and moves like a starfighter.
     * @param gameState the game state
     * @param card the card
     * @return true if deploys and moves like a starfighter, otherwise false
     */
    boolean isDeploysAndMovesLikeStarfighter(GameState gameState, PhysicalCard card);

    /**
     * Determines if this deploys and moves like a starfighter at cloud sectors.
     * @param gameState the game state
     * @param card the card
     * @return true if deploys and moves like a starfighter at cloud sectors, otherwise false
     */
    boolean isDeploysAndMovesLikeStarfighterAtCloudSectors(GameState gameState, PhysicalCard card);

    /**
     * Gets the sites marker number.
     * @param gameState the game state
     * @param physicalCard a marker site
     * @return the marker number, or null if not a marker site
     */
    Integer getMarkerNumber(GameState gameState, PhysicalCard physicalCard);

    boolean isVehicleSlotOfStarshipCompatible(GameState gameState, PhysicalCard card);

    /**
     * Increments the amount of Force that has been activated by the player.
     * @param playerId the player
     * @param fromForceGeneration true if Force was activated due to Force generation, otherwise false
     */
    void forceActivated(String playerId, boolean fromForceGeneration);

    /**
     * Gets the amount of Force the player has activated this turn.
     * @param playerId the player
     * @param onlyFromForceGeneration true if only Force activate due to Force generation is counted
     * @return the amount of Force
     */
    int getForceActivatedThisTurn(String playerId, boolean onlyFromForceGeneration);

    /**
     * Gets the amount of Force the player has activated this phase.
     * @param playerId the player
     * @return the amount of Force
     */
    int getForceActivatedThisPhase(String playerId);

    /**
     * Gets the amount of Force the player has available to use.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    int getForceAvailableToUse(GameState gameState, String playerId);

    /**
     * Gets the amount of opponent's Force the player can has available to use.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    int getOpponentsForceAvailableToUse(GameState gameState, String playerId);

    /**
     * Gets the maximum number of Force the player may use from opponent's Force Pile via the specified card.
     * @param gameState the game state
     * @param playerId the player
     * @param card the card
     * @param opponentsForceAlreadyToBeUsed the amount of opponent's Force already reserved to be used
     * @param minOpponentForceToUse the minimum amount of total opponent's Force that must be used
     * @return the amount of Force
     */
    int getMaxOpponentsForceToUseViaCard(GameState gameState, String playerId, PhysicalCard card, int opponentsForceAlreadyToBeUsed, int minOpponentForceToUse);

    /**
     * Gets the amount of extra Force required to fire the specified weapon (or permanent weapon).
     * @param gameState the game state
     * @param weaponCard the weapon card, or null if permanent weapon
     * @return the amount of Force
     */
    int getExtraForceRequiredToFireWeapon(GameState gameState, PhysicalCard weaponCard);

    /**
     * Gets the amount of extra Force required to deploy the specified card to the specified target.
     * @param gameState the game state
     * @param cardToDeploy the card to deploy
     * @param target the deploy target, or null
     * @param targetOfAttachedTo if deploying simultaneously on another card, the target for the card this card will be attached to, otherwise null
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if deploy card explicitly for free, otherwise false
     * @return the amount of Force
     */
    int getExtraForceRequiredToDeployToTarget(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard target, PhysicalCard targetOfAttachedTo, PhysicalCard sourceCard, boolean forFree);

    /**
     * Gets the amount of extra Force required to play the specified Interrupt.
     * @param gameState the game state
     * @param card the Interrupt card
     * @return the amount of Force
     */
    int getExtraForceRequiredToPlayInterrupt(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified interrupt plays for free.
     * @param gameState the game state
     * @param card the card
     * @return true if interrupt plays for free, otherwise false
     */
    boolean isInterruptPlayForFree(GameState gameState, PhysicalCard card);

    /**
     * Records that a 'bluff card' was stacked.
     */
    void bluffCardStacked();

    /**
     * Determines if a bluff card was stacked this turn.
     * @return true or false
     */
    boolean isBluffCardStackedThisTurn();

    /**
     * Records that the specified card being played (or being deployed).
     * @param card the card
     */
    void cardBeingPlayed(PhysicalCard card);

    /**
     * Records that the specified card was just deployed on table.
     * @param card the card
     */
    void cardJustDeployed(PhysicalCard card);

    /**
     * Gets the starting location of the specified player.
     * @param playerId the player
     * @return the starting location, or null
     */
    PhysicalCard getStartingLocation(String playerId);

    /**
     * Gets the cards that were played (or deployed) this game by the specified player.
     * @param playerId the player
     * @return the cards
     */
    List<PhysicalCard> getCardsPlayedThisGame(String playerId);

    /**
     * Gets the cards that were played (or deployed) this turn by the specified player.
     * @param playerId the player
     * @return the cards
     */
    List<PhysicalCard> getCardsPlayedThisTurn(String playerId);

    /**
     * Gets the cards with ability that were deployed this turn by the specified player.
     * @param playerId the player
     * @return the cards
     */
    List<PhysicalCard> getCardsWithAbilityDeployedThisTurn(String playerId);

    /**
     * Gets the cards that were played (or deployed) this turn by the specified player to the specified location.
     * @param playerId the player
     * @param location the location
     * @return the cards
     */
    List<PhysicalCard> getCardsPlayedThisTurnToLocation(String playerId, PhysicalCard location);

    /**
     * Gets the cost for the specified player to initiate a Force drain at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return the cost
     */
    float getInitiateForceDrainCost(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Records that a Force drain was initiated (or that something was done "instead of Force draining") at the specified
     * location.
     *
     * @param location the location
     */
    void forceDrainAttempted(PhysicalCard location);

    /**
     * Determines if a Force drain has been initiated (or if something was done "instead of Force draining") at the specified
     * location this turn.
     *
     * @param location the location
     * @return true or false
     */
    boolean isForceDrainAttemptedThisTurn(PhysicalCard location);

    /**
     * Records that a Force drain of the specified amount of Force was setFulfilledByOtherAction at the specified location.
     *
     * @param location the location
     * @param amount   the amount of Force
     */
    void forceDrainPerformed(PhysicalCard location, Float amount);

    /**
     * Gets the number of Force drains initiated this turn.
     * @return the amount of Force
     */
    float getNumForceDrainsInitiatedThisTurn();

    /**
     * Gets the total amount of Force that has been Force drained this turn.
     *
     * @return the amount of Force
     */
    float getTotalForceDrainedThisTurn();

    /**
     * Records that a card was forfeited from the specified location.
     * @param location the location
     * @param forfeitedCard the forfeited card
     */
    void forfeitedFromLocation(PhysicalCard location, PhysicalCard forfeitedCard);

    /**
     * Gets the map of locations and cards that were forfeited from those locations this turn.
     * @return the location and forfeited cards map
     */
    Map<PhysicalCard, Set<PhysicalCard>> getForfeitedFromLocationsThisTurn();

    /**
     * Records that the specified player used a combat card.
     *
     * @param playerId the player
     */
    void combatCardUsed(String playerId);

    /**
     * Determines if the player has used a combat card this turn.
     * @param playerId the player
     * @return true or false
     */
    boolean isCombatCardUsedThisTurn(String playerId);

    /**
     * Records that the specified character card won a game of sabacc.
     *
     * @param character the character that won sabacc game
     */
    void wonSabaccGame(PhysicalCard character);

    /**
     * Determines if a character accepted by the specified filter has won a sabacc game.
     *
     * @param gameState the game state
     * @param filters   the filter
     * @return true or false
     */
    boolean hasWonSabaccGame(GameState gameState, Filterable filters);

    /**
     * Records that the specified persona has 'crossed over'.
     *
     * @param persona the persona that was 'crossed over'
     */
    void crossedOver(Persona persona);

    /**
     * Determines if the specified persona has 'crossed over'.
     *
     * @param gameState the game state
     * @param persona   the persona
     * @return true or false
     */
    boolean isCrossedOver(GameState gameState, Persona persona);

    /**
     * Records that the specified card was 'blown away'.
     *
     * @param card the card that was 'blown away'
     */
    void blownAway(PhysicalCard card);

    /**
     * Determines if a card accepted by the specified filter has been 'blown away'.
     *
     * @param gameState the game state
     * @param filters   the filter
     * @return true or false
     */
    boolean isBlownAway(GameState gameState, Filterable filters);

    /**
     * Gets the amount of Force loss for the specified player due to the current blown away action.
     * @param gameState the game state
     * @param playerId the player
     * @return the amount of Force
     */
    float getBlownAwayForceLoss(GameState gameState, String playerId);

    /**
     * Gets the Force loss amount for losing lightsaber combat.
     * @param gameState the game state
     * @param baseForceLoss the base Force loss amount
     * @return the lightsaber combat destiny value
     */
    float getLightsaberCombatForceLoss(GameState gameState, float baseForceLoss);

    /**
     * Determines if the specified card may use the specified combat card.
     * @param gameState the game state
     * @param character the character
     * @param combatCard the combat card
     * @return true or false
     */
    boolean mayUseCombatCard(GameState gameState, PhysicalCard character, PhysicalCard combatCard);

    /**
     * Gets the Force retrieval amount for winning a Podrace.
     * @param gameState the game state
     * @param baseForceRetrieval the base Force retrieval amount
     * @return the amount of Force
     */
    float getPodraceForceRetrieval(GameState gameState, float baseForceRetrieval);

    /**
     * Gets the Force loss amount for losing a Podrace.
     * @param gameState the game state
     * @param baseForceLoss the base Force loss amount
     * @return the amount of Force
     */
    float getPodraceForceLoss(GameState gameState, float baseForceLoss);

    /**
     * Records that the specified Utinni Effect has been completed.
     * @param playerId the player that completed the Utinni Effect
     * @param utinniEffect the Utinni Effect that was completed
     */
    void completedUtinniEffect(String playerId, PhysicalCard utinniEffect);

    /**
     * Determines if at least a specified number of Utinni Effects accepted by the filter have been completed by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @param count the count
     * @param filters the filter
     * @return true or false
     */
    boolean hasCompletedUtinniEffect(GameState gameState, String playerId, int count, Filterable filters);

    /**
     * Records that the specified Jedi Test has been attempted by the specified character.
     * @param jediTest the Jedi Test that was attempted
     * @param attemptedBy the character that attempted the Jedi Test
     */
    void attemptedJediTest(PhysicalCard jediTest, PhysicalCard attemptedBy);

    /**
     * Determines if any Jedi Tests have been attempted this turn.
     * @return true or false
     */
    boolean hasAttemptedJediTests();

    /**
     * Records that the specified Jedi Test has been completed by the specified character.
     * @param jediTest the Jedi Test that was completed
     * @param completedBy the character that completed the Jedi Test
     */
    void completedJediTest(PhysicalCard jediTest, PhysicalCard completedBy);

    /**
     * Records that the specified card has participated in a Force drain.
     *
     * @param card the card
     */
    void participatedInForceDrain(PhysicalCard card);

    /**
     * Determines if the specified card has participated in a Force drain this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    boolean hasParticipatedInForceDrainThisTurn(PhysicalCard card);

    /**
     * Records that the specified starship has had asteroid destiny drawn against it at the specified location.
     *
     * @param starship the starship
     * @param location the location with "Asteroid Rules"
     */
    void asteroidDestinyDrawnAgainst(PhysicalCard starship, PhysicalCard location);

    /**
     * Determines if the specified starship has had asteroid destiny drawn against it at the specified location this turn.
     *
     * @param starship the starship
     * @param location the location with "Asteroid Rules"
     * @return true if starship has already had asteroid destiny drawn against it at the location, otherwise false
     */
    boolean hadAsteroidDestinyDrawnAgainstThisTurn(PhysicalCard starship, PhysicalCard location);

    /**
     * Gets the amount of the Force drain.
     *
     * @param gameState          the game state
     * @param location           the Force drain location
     * @param performingPlayerId the player performing the Force drain
     * @return the amount of the Force drain
     */
    float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId);

    /**
     * Gets the amount of the Force drain.
     *
     * @param gameState          the game state
     * @param location           the Force drain location
     * @param performingPlayerId the player performing the Force drain
     * @param modifierCollector collector of affecting modifiers
     * @return the amount of the Force drain
     */
    float getForceDrainAmount(GameState gameState, PhysicalCard location, String performingPlayerId, ModifierCollector modifierCollector);

    boolean mayForceDrain(GameState gameState, PhysicalCard physicalCard);


    /**
     * Determines if the specified player is prohibited from Force draining at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if player is not allowed to Force drain at location, otherwise false
     */
    boolean isProhibitedFromForceDrainingAtLocation(GameState gameState, PhysicalCard location, String playerId);

    boolean cantCancelForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard cardCanceling, String playerCanceling, String playerDraining);

    boolean cantModifyForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard cardModifying, String playerModifying, String playerDraining);

    boolean cantReduceForceDrainAtLocation(GameState gameState, PhysicalCard location, PhysicalCard cardReducing, String playerReducing, String playerDraining);

    boolean cantReduceForceLossFromForceDrainAtLocation(GameState gameState, PhysicalCard location, String playerReducing, String playerDraining);

    /**
     * Determines if a Force generation is immune to limit.
     * @param gameState the game state
     * @param playerId the player whose Force generation is being checked
     * @param location the location
     * @param source the source of the limit
     * @return true if Force generation at location for player is immune to limit, otherwise false
     */
    boolean isImmuneToForceGenerationLimit(GameState gameState, String playerId, PhysicalCard location, PhysicalCard source);

    /**
     * Determines if a Force generation is immune to cancel.
     * @param gameState the game state
     * @param playerId the player whose Force generation is being checked
     * @param location the location
     * @param source the source of the cancel
     * @return true if Force generation at location for player is immune to cancel, otherwise false
     */
    boolean isImmuneToForceGenerationCancel(GameState gameState, String playerId, PhysicalCard location, PhysicalCard source);

    /**
     * Determines if a Force drain modifier is canceled.
     * @param gameState the game state
     * @param location the Force drain location
     * @param source the source of the modifier
     * @param playerModifying the owner of the source card
     * @param playerDraining the player Force draining
     * @param amount the amount of the modifier
     * @return true if modifier is canceled, otherwise false
     */
    boolean isForceDrainModifierCanceled(GameState gameState, PhysicalCard location, PhysicalCard source, String playerModifying, String playerDraining, float amount);

    /**
     * Gets the cost for the specified player to initiate a battle at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return the cost
     */
    float getInitiateBattleCost(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Gets the losing Force cost for the specified player to initiate a battle at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return the cost
     */
    float getInitiateBattleCostAsLoseForce(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Determines the player to take the first weapons segment action in the current battle.
     * @param gameState the game state
     * @return the player to take the first weapons segment action in battle
     */
    String getPlayerToTakeFirstBattleWeaponsSegmentAction(GameState gameState);

    /**
     * Determines if the specified player is prohibited from initiating attacks at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if player is not allowed to initiate attacks at location, otherwise false
     */
    boolean mayNotInitiateAttacksAtLocation(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Determines if the card is placed out of play when eaten by the specified card.
     * @param gameState the game state
     * @param cardEaten the card eaten
     * @param cardEatenBy the card that ate the card
     * @return true or false
     */
    boolean isEatenByPlacedOutOfPlay(GameState gameState, PhysicalCard cardEaten, PhysicalCard cardEatenBy);

    /**
     * Records that an attack on a creature was initiated at the specified location.
     *
     * @param location the location
     */
    void attackOnCreatureInitiatedAtLocation(PhysicalCard location);

    /**
     * Determines if an attack on a creature has been initiated at the specified location this turn.
     *
     * @param location the location
     * @return true or false
     */
    boolean isAttackOnCreatureOccurredAtLocationThisTurn(PhysicalCard location);

    /**
     * Records that the specified card has participated in an attack on a creature.
     *
     * @param card the card
     */
    void participatedInAttackOnCreature(PhysicalCard card);

    /**
     * Determines if the specified card has participated in an attack on a creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    boolean hasParticipatedInAttackOnCreatureThisTurn(PhysicalCard card);

    /**
     * Records that the specified card has participated in an attack on a non-creature.
     *
     * @param card the card
     */
    void participatedInAttackOnNonCreature(PhysicalCard card);

    /**
     * Determines if the specified card has participated in an attack on a non-creature this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    boolean hasParticipatedInAttackOnNonCreatureThisTurn(PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from attacking the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @return true is prohibited from attacking the specified target, otherwise false
     */
    boolean isProhibitedFromAttackingTarget(GameState gameState, PhysicalCard card, PhysicalCard target);

    /**
     * Determines if the specified card is granted to attack the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @return true is granted to attack the specified target, otherwise false
     */
    boolean grantedToAttackTarget(GameState gameState, PhysicalCard card, PhysicalCard target);

    /**
     * Determines if the specified player is prohibited from initiating battle at the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if player is not allowed to initiate battle at location, otherwise false
     */
    boolean mayNotInitiateBattleAtLocation(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Records that a battle was initiated at the specified location by the specified player.
     *
     * @param playerId the player
     * @param location the location
     */
    void battleInitiatedAtLocation(String playerId, PhysicalCard location);

    /**
     * Gets the number of battles that have been initiated this turn by the specified player.
     * @param playerId the player
     * @return the number of battles
     */
    int getNumBattlesInitiatedThisTurn(String playerId);

    /**
     * Determines if a battle has been initiated at the specified location this turn.
     *
     * @param location the location
     * @return true or false
     */
    boolean isBattleOccurredAtLocationThisTurn(PhysicalCard location);

    /**
     * Records that the specified card has participated in a battle at the specified location.
     *
     * @param card the card
     * @param location the location
     */
    void participatedInBattle(PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified card has participated in a battle this turn.
     *
     * @param card the card
     * @return true if card has participated, otherwise false
     */
    boolean hasParticipatedInBattle(PhysicalCard card);

    /**
     * Determines if the specified card has participated in a battle at another location this turn.
     *
     * @param card the card
     * @param location the location
     * @return true if card has participated, otherwise false
     */
    boolean hasParticipatedInBattleAtOtherLocation(PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified card is prohibited from participating in battle.
     * @param gameState the game state
     * @param card the card
     * @param playerInitiatingBattle the player initiating battle
     * @return true is prohibited from participating in battle, otherwise false
     */
    boolean isProhibitedFromParticipatingInBattle(GameState gameState, PhysicalCard card, String playerInitiatingBattle);

    /**
     * Determines if the specified card may not be excluded from battle.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be excluded from battle, otherwise false
     */
    boolean mayNotBeExcludedFromBattle(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is currently excluded from battle.
     * @param gameState the game state
     * @param card the card
     * @return true if excluded from battle, otherwise false
     */
    boolean isExcludedFromBattle(GameState gameState, PhysicalCard card);

    /**
     * Gets the card causes the specified card to be excluded from battle, or null if the card is either not excluded from
     * battle or is excluded from battle by rule.
     * @param gameState the game state
     * @param card the card
     * @return the card causing the exclusion from battle, or null
     */
    PhysicalCard getCardCausingExclusionFromBattle(GameState gameState, PhysicalCard card);

    boolean isPermanentPilotsNotAbleToApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card);

    boolean cannotApplyAbilityForBattleDestiny(GameState gameState, PhysicalCard card);

    boolean passengerAppliesAbilityForBattleDestiny(GameState gameState, PhysicalCard card);

    boolean mayNotApplyAbilityForSenseOrAlterDestiny(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified player may not add destiny draws to power.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destinies draws to power may not be added, otherwise false
     */
    boolean mayNotAddDestinyDrawsToPower(GameState gameState, String playerId);

    /**
     * Determines if the specified player may not add destiny draws to attrition.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destinies draws to attrition may not be added, otherwise false
     */
    boolean mayNotAddDestinyDrawsToAttrition(GameState gameState, String playerId);

    /**
     * Determines if the specified card may not add battle destiny draws.
     * @param gameState the game state
     * @param card the card
     * @return true if battle destinies draws may not be added, otherwise false
     */
    boolean mayNotAddBattleDestinyDraws(GameState gameState, PhysicalCard card);

    /**
     * Determines if battle destiny draws by a specified player may not be canceled by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToCancel the player to cancel battle destiny
     * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
     * @return true if battle destinies may not be canceled, otherwise false
     */
    boolean mayNotCancelBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToCancel, boolean isCancelAndRedraw);

    /**
     * Determines if the current destiny draw may not be canceled by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @param isCancelAndRedraw true if cancel and redraw, otherwise cancel only
     * @return true if destiny may not be canceled, otherwise false
     */
    boolean mayNotCancelDestinyDraw(GameState gameState, String playerId, boolean isCancelAndRedraw);

    /**
     * Determines if total battle destiny for a specified player may not be modified by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player with total battle destiny
     * @param playerToModify the player to modify total battle destiny
     * @return true if total battle destiny may not be modified, otherwise false
     */
    boolean mayNotModifyTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToModify);

    /**
     * Determines if battle destiny draws by a specified player may not be modified by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @return true if battle destinies may not be modified, otherwise false
     */
    boolean mayNotModifyBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToModify);

    /**
     * Determines if the current destiny draw may not be modified by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destiny may not be modified, otherwise false
     */
    boolean mayNotModifyDestinyDraw(GameState gameState, String playerId);

    /**
     * Determines if total battle destiny for a specified player may not be reset by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player with total battle destiny
     * @param playerToReset the player to reset total battle destiny
     * @return true if total battle destiny may not be reset, otherwise false
     */
    boolean mayNotResetTotalBattleDestiny(GameState gameState, String playerDrawingDestiny, String playerToReset);

    /**
     * Determines if battle destiny draws by a specified player may not be reset by the other specified player.
     * @param gameState the game state
     * @param playerDrawingDestiny the player drawing battle destiny
     * @param playerToReset the player to reset battle destiny
     * @return true if battle destinies may not be reset, otherwise false
     */
    boolean mayNotResetBattleDestinyDraws(GameState gameState, String playerDrawingDestiny, String playerToReset);

    /**
     * Determines if the current destiny draw may not be reset by the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return true if destiny may not be reset, otherwise false
     */
    boolean mayNotResetDestinyDraw(GameState gameState, String playerId);

    /**
     * Determines if battle destiny draws by a specified player may not be substituted.
     * @param gameState the game state
     * @param playerId the player drawing battle destiny
     * @return true if battle destinies may not be substituted, otherwise false
     */
    boolean mayNotSubstituteBattleDestinyDraws(GameState gameState, String playerId);

    /**
     * Determines if the current destiny draw may not be substituted.
     * @param gameState the game state
     * @return true if destiny may not be substituted, otherwise false
     */
    boolean mayNotSubstituteDestinyDraw(GameState gameState);

    /**
     * Determines if the number of battle destinies the specified player may draw if unable to otherwise.
     * @param gameState the game state
     * @param player the player
     * @return the number of battle destinies
     */
    int getNumBattleDestinyDrawsIfUnableToOtherwise(GameState gameState, String player);

    /**
     * Gets the number of destinies that the specified player can draw for battle destiny.
     * @param gameState the game state
     * @param player the player
     * @param isGetLimit if true, gets the limit to the number of draws, otherwise gets then the number that can be attempted
     *                   when ignoring limit.
     * @param isForGui if true, gets the number of destiny draws to show on user interface that can be attempted
     * @return the number of battle destinies
     */
    int getNumBattleDestinyDraws(GameState gameState, String player, boolean isGetLimit, boolean isForGui);

    /**
     * Gets the number of destinies that the specified player can draw to total power.
     * @param gameState the game state
     * @param player the player
     * @param isGetLimit if true, gets the limit to the number of draws, otherwise gets then the number that can be attempted
     *                   when ignoring limit.
     * @param isForGui if true, gets the number of destiny draws to show on user interface that can be attempted
     * @return the number of destinies to total power
     */
    int getNumDestinyDrawsToTotalPowerOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui);

    /**
     * Gets the number of destinies that the specified player can draw to attrition.
     * @param gameState the game state
     * @param player the player
     * @param isGetLimit if true, gets the limit to the number of draws, otherwise gets then the number that can be attempted
     *                   when ignoring limit.
     * @param isForGui if true, gets the number of destiny draws to show on user interface that can be attempted
     * @return the number of destinies to attrition
     */
    int getNumDestinyDrawsToAttritionOnly(GameState gameState, String player, boolean isGetLimit, boolean isForGui);

    boolean mayInitiateBattle(GameState gameState, PhysicalCard physicalCard);

    boolean mayBeBattled(GameState gameState, PhysicalCard physicalCard);

    boolean mayNotBeBattled(GameState gameState, PhysicalCard physicalCard);


    // Total at location
    float getTotalPowerAtLocation(GameState gameState, PhysicalCard location, String playerId, boolean inBattle, boolean onlyPresent);

    /**
     * Gets the total power (or ferocity) in the attack.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the total power (or ferocity)
     */
    float getAttackTotalPowerOrFerocity(GameState gameState, boolean defender);

    /**
     * Gets the total ability in the attack.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the total ability
     */
    float getAttackTotalAbility(GameState gameState, boolean defender);

    /**
     * Determines if a specified player's total ability at the specified location may not be reduced.
     * @param gameState the game state
     * @param location the location
     * @param playerId the player
     * @return true or false
     */
    boolean isProhibitedFromHavingTotalAbilityReduced(GameState gameState, PhysicalCard location, String playerId);

    float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard);

    float getTotalAbilityAtLocation(GameState gameState, String player, PhysicalCard physicalCard, boolean forPresence, boolean forControl, boolean forBattle,
                                    String playerInitiatingBattle, boolean forBattleDestiny, boolean onlyPiloting, Map<InactiveReason, Boolean> spotOverrides);

    /**
     * Gets the total ability the specified player has present at the specified location.
     * @param gameState the game state
     * @param player the player
     * @param location the location
     * @return the total ability present
     */
    float getTotalAbilityPresentAtLocation(GameState gameState, String player, PhysicalCard location);

    /**
     * Gets the player that has a "senate majority", or null if neither player does.
     * @param gameState the game state
     * @return the player with a "senate majority", or null
     */
    String getPlayerWithSenateMajority(GameState gameState);

    /**
     * Gets the player's total politics at Galactic Senate.
     * @param gameState the game state
     * @param playerId the player
     * @return the politics total
     */
    float getTotalPoliticsAtGalacticSenate(GameState gameState, String playerId);

    /**
     * Determines if the specified player takes no battle damage.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    boolean isTakesNoBattleDamage(GameState gameState, String playerId);

    // Battle damage, attrition, and Force loss
    float getTotalBattleDamage(GameState gameState, String playerId);

    float getTotalAttrition(GameState gameState, String playerId);

    /**
     * Gets the number of duel destiny draws for the specified player.
     * @param gameState the game state
     * @param player the player
     * @return the number duel destiny draws
     */
    int getNumDuelDestinyDraws(GameState gameState, String player);

    /**
     * Gets the number of lightsaber combat destiny draws for the specified player.
     * @param gameState the game state
     * @param player the player
     * @return the number lightsaber combat destiny draws
     */
    int getNumLightsaberCombatDestinyDraws(GameState gameState, String player);

    /**
     * Gets the attack total.
     * @param gameState the game state
     * @param defender true if total for defender, otherwise total for attacker
     * @return the attacker total
     */
    float getAttackTotal(GameState gameState, boolean defender);

    /**
     * Gets the duel total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the duel total
     */
    float getDuelTotal(GameState gameState, String playerId);

    /**
     * Gets the lightsaber combat total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the lightsaber combat total
     */
    float getLightsaberCombatTotal(GameState gameState, String playerId);

    /**
     * Gets the crossover attempt total when attempting to cross over the specified character.
     * @param gameState the game state
     * @param character the character to attempt to cross over
     * @param baseValue the initial value of the cross over attempt total
     * @return the duel total
     */
    float getCrossoverAttemptTotal(GameState gameState, PhysicalCard character, float baseValue);

    /**
     * Determines if the card has any immunity to attrition.
     * @param gameState the game state
     * @param card      a card
     * @return true if card has any immunity to attrition, otherwise false
     */
    boolean hasAnyImmunityToAttrition(GameState gameState, PhysicalCard card);

    /**
     * Determines if the card already has immunity to attrition (when ignoring modifiers from specified card).
     * @param gameState the game state
     * @param card      a card
     * @param sourceToIgnore source card to ignore modifiers from
     * @return true if card already has any immunity to attrition, otherwise false
     */
    boolean alreadyHasImmunityToAttrition(GameState gameState, PhysicalCard card, Filterable sourceToIgnore);

    /**
     * Gets the amount of attrition the specified card is immune to less than.
     * @param gameState the game state
     * @param card      a card
     * @return the immunity to attrition less than value
     */
    float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard card);

    /**
     * Gets the amount of attrition the specified card is immune to less than.
     * @param gameState the game state
     * @param card      a card
     * @param modifierCollector collector of affecting modifiers
     * @return the immunity to attrition less than value
     */
    float getImmunityToAttritionLessThan(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Gets the amount of attrition the specified card is immune to exactly only.
     * @param gameState the game state
     * @param card      a card
     * @return the immunity to attrition of exactly value
     */
    float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard card);

    /**
     * Gets the amount of attrition the specified card is immune to exactly only.
     * @param gameState the game state
     * @param card      a card
     * @param modifierCollector collector of affecting modifiers
     * @return the immunity to attrition of exactly value
     */
    float getImmunityToAttritionOfExactly(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Gets the amount of Force for the specified player to lose.
     * @param gameState the game state
     * @param playerId  the player to lose Force
     * @param isCost    true if Force loss is a cost, otherwise false
     * @param baseValue the initial value of the Force loss
     * @return the amount of Force to lose
     */
    float getForceToLose(GameState gameState, String playerId, boolean isCost, float baseValue);

    /**
     * Gets the maximum amount of Force that the specified player can lose from the specified source card.
     * @param gameState the game state
     * @param playerId  the player to lose Force
     * @param source    the source card of the Force loss
     * @return the maximum amount of Force to lose
     */
    float getForceToLoseFromCardLimit(GameState gameState, String playerId, PhysicalCard source);

    /**
     * Gets the maximum amount of Force that the specified player can lose from a Force drain at the specified location.
     * @param gameState the game state
     * @param playerId  the player to lose Force
     * @param location  the Force drain location
     * @return the maximum amount of Force to lose
     */
    float getForceToLoseFromForceDrainLimit(GameState gameState, String playerId, PhysicalCard location);

    /**
     * Gets the maximum amount of Force that the specified player can lose from an 'insert' card.
     * @param gameState the game state
     * @param playerId the player to lose Force
     * @return the maximum amount of Force to lose
     */
    float getForceToLoseFromInsertCardLimit(GameState gameState, String playerId);

    /**
     * Determines if Force retrieval from the specified card is immune to Secret Plans.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean isForceRetrievalImmuneToSecretPlans(GameState gameState, PhysicalCard card);

    /**
     * Gets the initial calculated amount of Force for the specified player to retrieve when collecting a bounty.
     * @param gameState the game state
     * @param playerId the player to retrieve Force
     * @param bountyHunterToCollect the bounty hunter to collect the bounty
     * @param baseValue the initial value of the Force retrieval
     * @return the amount of Force to retrieve
     */
    float getForceToRetrieveForBounty(GameState gameState, String playerId, PhysicalCard bountyHunterToCollect, float baseValue);

    /**
     * Gets the amount of Force for the specified player to retrieve.
     * @param gameState the game state
     * @param playerId the player to retrieve Force
     * @param source the source card of the Force retrieval
     * @param baseValue the initial value of the Force retrieval
     * @return the amount of Force to retrieve
     */
    float getForceToRetrieve(GameState gameState, String playerId, PhysicalCard source, float baseValue);

    /**
     * Determines if the specified player is explicitly not allowed to retrieve Force for initiating a battle.
     * @param gameState the game state
     * @param playerId the player
     * @return true or false
     */
    boolean mayNotRetrieveForceForInitiatingBattle(GameState gameState, String playerId);

    /**
     * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean mayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is explicitly not allowed to contribute to Force retrieval.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean playersCardsAtLocationMayNotContributeToForceRetrieval(GameState gameState, PhysicalCard card, String playerId);



    boolean cannotSatisfyAttrition(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if the specified card is not allowed to steal other cards.
     * @param gameState the game state
     * @param card the card
     * @return true if card is not allowed to steal other cards, otherwise false
     */
    boolean cannotSteal(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card goes to Used Pile when forfeited.
     * @param gameState    the game state
     * @param physicalCard the card
     * @return true if card goes to Used Pile when forfeited, otherwise false
     */
    boolean isForfeitedToUsedPile(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the value of a drawn destiny to power.
     * @param gameState the game state
     * @param physicalCard the card drawn for destiny to power
     * @param playerId the player drawing destiny to power
     * @return the destiny to power value
     */
    float getDestinyToPower(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total destiny to power value after applying modifiers to the base total destiny to power.
     * @param gameState the game state
     * @param playerId the player with the destiny to power
     * @param baseTotalDestiny the base total destiny to power
     * @return the total destiny to power
     */
    float getTotalDestinyToPower(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn battle destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for battle destiny
     * @param playerId the player drawing battle destiny
     * @return the battle destiny value
     */
    float getBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);


    /**
     * Checks to see if we should be drawing destiny from the bottom of the deck instead of top
     * @param gameState
     * @param playerId
     * @return
     */
    boolean shouldDrawDestinyFromBottomOfDeck(GameState gameState, String playerId);

    /**
     * Checks to see if we should be drawing destiny from the bottom of the deck instead of top
     * @param gameState
     * @param playerId
     * @return
     */
    PhysicalCard getDrawsDestinyFromBottomOfDeckModiferSource(GameState gameState, String playerId);


    /**
     * Gets the player's battle destiny modifiers and the modifier amount for the specified card drawn for battle destiny,
     * even if the battle destiny cannot be modified. This is used when having the battle destiny modifiers affect total
     * battle destiny instead.
     * @param gameState the game state
     * @param physicalCard the card drawn for battle destiny
     * @param playerId the player drawing battle destiny
     * @return the list of source card to modifier amount
     */
    List<Map<PhysicalCard, Float>> getPlayersBattleDestinyModifiersToApplyToTotalBattleDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total battle destiny value after applying modifiers to the base total battle destiny.
     * @param gameState the game state
     * @param playerId the player with the battle destiny
     * @param baseTotalDestiny the base total battle destiny
     * @return the total battle destiny
     */
    float getTotalBattleDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn destiny to attrition.
     * @param gameState the game state
     * @param physicalCard the card drawn for destiny to attrition
     * @param playerId the player drawing destiny to attrition
     * @return the destiny to attrition value
     */
    float getDestinyToAttrition(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total destiny to attrition value after applying modifiers to the base total destiny to attrition.
     * @param gameState the game state
     * @param playerId the player with the destiny to attrition
     * @param baseTotalDestiny the base total destiny to attrition
     * @return the total destiny to attrition
     */
    float getTotalDestinyToAttrition(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn carbon-freezing destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for carbon-freezing destiny
     * @return the total battle destiny
     */
    float getCarbonFreezingDestiny(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the total carbon-freezing destiny value after applying modifiers to the base carbon-freezing destiny.
     * @param gameState the game state
     * @param playerId the player with the carbon-freezing destiny
     * @param baseTotalDestiny the base total carbon-freezing destiny
     * @return the total battle destiny
     */
    float getTotalCarbonFreezingDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn asteroid destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for asteroid destiny
     * @param playerId the player drawing asteroid destiny
     * @return the asteroid destiny value
     */
    float getAsteroidDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total asteroid destiny value after applying modifiers to the base total asteroid destiny.
     * @param gameState the game state
     * @param playerId the player with the asteroid destiny
     * @param baseTotalDestiny the base total asteroid destiny
     * @return the total asteroid destiny
     */
    float getTotalAsteroidDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn search party destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for search party destiny
     * @param playerId the player with the search party destiny
     * @return the total battle destiny
     */
    float getSearchPartyDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total search party destiny value after applying modifiers to the base search party destiny.
     * @param gameState the game state
     * @param playerId the player with the search party destiny
     * @param baseTotalDestiny the base total search party destiny
     * @return the total battle destiny
     */
    float getTotalSearchPartyDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn tractor beam destiny.
     * @param gameState the game state
     * @param tractorBeam the tractor beam
     * @param physicalCard the card drawn for tractor beam destiny
     * @param playerId the player with the tractor beam destiny
     * @return the tractor beam destiny draw value
     */
    float getTractorBeamDestiny(GameState gameState, PhysicalCard tractorBeam, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the value of a drawn training destiny.
     * @param gameState the game state
     * @param jediTest the Jedi Test
     * @param physicalCard the card drawn for training destiny
     * @param playerId the player with the training destiny
     * @return the training destiny draw value
     */
    float getTrainingDestiny(GameState gameState, PhysicalCard jediTest, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total training destiny value after applying modifiers to the base training destiny.
     * @param gameState the game state
     * @param jediTest the Jedi Test
     * @param baseTotalDestiny the base total training destiny
     * @return the total battle destiny
     */
    float getTotalTrainingDestiny(GameState gameState, PhysicalCard jediTest, float baseTotalDestiny);

    /**
     * Gets the total training destiny value after applying modifiers to the base tractor beam destiny.
     * @param gameState the game state
     * @param tractorBeam the tractor beam
     * @param baseTotalDestiny the base total tractor beam destiny
     * @return the total battle destiny
     */
    float getTotalTractorBeamDestiny(GameState gameState, PhysicalCard tractorBeam, float baseTotalDestiny);

    /**
     * Gets the total movement destiny value after applying modifiers to the base total movement destiny.
     * @param gameState the game state
     * @param playerId the player with the movement destiny
     * @param baseTotalDestiny the base total movement destiny
     * @return the total movement destiny
     */
    float getTotalMovementDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    /**
     * Gets the value of a drawn duel destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for duel destiny
     * @param playerId the player drawing duel destiny
     * @return the duel destiny value
     */
    float getDuelDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the value of a drawn lightsaber combat destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for lightsaber combat destiny
     * @param playerId the player drawing lightsaber combat destiny
     * @return the lightsaber combat destiny value
     */
    float getLightsaberCombatDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the value of a drawn epic event destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for epic event destiny
     * @param playerId the player drawing epic event destiny
     * @return the epic event destiny value
     */
    float getEpicEventDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the value of a drawn epic event and weapon destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for epic event and weapon destiny
     * @param playerId the player drawing epic event and weapon destiny
     * @return the epic event and weapon destiny value
     */
    float getEpicEventAndWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the value of a drawn weapon destiny.
     * @param gameState the game state
     * @param physicalCard the card drawn for weapon destiny
     * @param playerId the player drawing weapon destiny
     * @return the weapon destiny value
     */
    float getWeaponDestiny(GameState gameState, PhysicalCard physicalCard, String playerId);

    /**
     * Gets the total weapon destiny value after applying modifiers to the base total weapon destiny.
     * @param gameState the game state
     * @param playerId the player with the weapon destiny
     * @param baseTotalDestiny the base total weapon destiny
     * @return the total weapon destiny
     */
    float getTotalWeaponDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    float getTotalWeaponDestinyForCombinedFiring(GameState gameState, String playerId, PhysicalCard weaponTarget, float baseTotalDestiny);

    /**
     * Gets the total destiny value after applying modifiers to the base total destiny.
     * @param gameState the game state
     * @param playerId the player with the destiny
     * @param baseTotalDestiny the base total destiny
     * @return the total destiny
     */
    float getTotalDestiny(GameState gameState, String playerId, float baseTotalDestiny);

    // Deploying and transferring
    boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier);

    /**
     * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
     * being deployed to may be specified.
     * @param gameState the game state
     * @param card a card
     * @param target the target, or null is not deploying to specific target
     * @return true if card is granted the ability to deploy for free to target, otherwise false
     */
    boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard target);

    /**
     * Determines if a card is explicitly granted the ability to deploy for free when deployed. The target the card is
     * being deployed to may be specified.
     * @param gameState the game state
     * @param card a card
     * @param target the target, or null is not deploying to specific target
     * @param modifierCollector collector of affecting modifiers
     * @return true if card is granted the ability to deploy for free to target, otherwise false
     */
    boolean grantedDeployForFree(GameState gameState, PhysicalCard card, PhysicalCard target, ModifierCollector modifierCollector);

    /**
     * Determines if a card deploys for free.
     * @param gameState the game state
     * @param card      a card
     * @return true if card deploys for free, otherwise false
     */
    boolean deploysForFree(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card's deploy cost is less than or equal to a specified cost.
     * @param gameState the game state
     * @param card      a card
     * @param value     the cost
     * @return true if card's deploy cost is less than or equal to the specified cost, otherwise false
     */
    boolean hasDeployCostLessThanOrEqualTo(GameState gameState, PhysicalCard card, float value);

    /**
     * Determines if the specified card is explicitly granted the ability to be deployed during the current phase.
     * @param gameState the game state
     * @param card a card
     * @return true if card is granted the ability to deploy during the current phase, otherwise false
     */
    boolean grantedDeployDuringCurrentPhase(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card's deploy cost is not allowed to be modified.
     * @param gameState the game state
     * @param card the card
     * @param playerModifyingCost the player to modify the deploy cost
     * @return true if deploy cost may not be modified, otherwise false
     */
    boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost);

    /**
     * Determines if the specified card's deploy cost is not allowed to be modified.
     * @param gameState the game state
     * @param card the card
     * @param playerModifyingCost the player to modify the deploy cost
     * @param modifierCollector collector of affecting modifiers
     * @return true if deploy cost may not be modified, otherwise false
     */
    boolean isDeployCostNotAllowedToBeModified(GameState gameState, PhysicalCard card, String playerModifyingCost, ModifierCollector modifierCollector);

    /**
     * Determines if the specified card's deploy cost is not allowed to be increased.
     * @param gameState the game state
     * @param card the card
     * @param playerIncreasingCost the player to increase the deploy cost
     * @return true if deploy cost may not be increased, otherwise false
     */
    boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost);

    /**
     * Determines if the specified card's deploy cost is not allowed to be increased.
     * @param gameState the game state
     * @param card the card
     * @param playerIncreasingCost the player to increase the deploy cost
     * @param modifierCollector collector of affecting modifiers
     * @return true if deploy cost may not be increased, otherwise false
     */
    boolean isDeployCostNotAllowedToBeIncreased(GameState gameState, PhysicalCard card, String playerIncreasingCost, ModifierCollector modifierCollector);

    /**
     * Determines if the specified card's own deployment modifiers are applied at any location.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean appliesOwnDeploymentModifiersAtAnyLocation(GameState gameState, PhysicalCard card);

    float getDeployCost(GameState gameState, PhysicalCard physicalCard);

    float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard physicalCard, boolean isDejarikRules, boolean includeExtraCost);

    float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard physicalCard, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost);

    float getDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard physicalCard, PhysicalCard targetCard, boolean isDejarikRules, PlayCardOption playCardOption, boolean forFree, float changeInCost, ReactActionOption reactActionOption, boolean includeExtraCost, ModifierCollector modifierCollector);

    float getSimultaneousDeployCost(GameState gameState, PhysicalCard sourceCard, PhysicalCard starship, boolean starshipForFree, float starshipChangeInCost, PhysicalCard pilot, boolean pilotForFree, float pilotChangeInCost, PhysicalCard targetCard, ReactActionOption reactActionOption, boolean includeExtraCost);

    boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard);

    boolean isDeployUsingBothForcePiles(GameState gameState, PhysicalCard physicalCard, PhysicalCard targetCard);

    /**
     * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
     * @param gameState the game state
     * @param card a card
     * @return the deploy cost as determined by a calculation, otherwise null
     */
    Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card);

    /**
     * Gets the deploy cost for a card if the card's deploy cost is determined by a calculation, instead of normally.
     * @param gameState the game state
     * @param card a card
     * @param modifierCollector collector of affecting modifiers
     * @return the deploy cost as determined by a calculation, otherwise null
     */
    Float getDeployCostFromCalculation(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Gets the amount of Force needed for the card to be transferred to the target.
     * @param gameState the game state
     * @param cardToTransfer the card to be transferred
     * @param target the target
     * @param playCardOption the play card option chosen
     * @return the transfer cost
     */
    float getTransferCost(GameState gameState, PhysicalCard cardToTransfer, PhysicalCard target, PlayCardOption playCardOption);

    /**
     * Determines if the card may deploy to the target without presence or Force icons.
     * @param gameState the game state
     * @param target the target
     * @param cardToDeploy the card to deploy
     * @return true if card can be deployed to the location without presence or Force icons, otherwise false
     */
    boolean mayDeployToTargetWithoutPresenceOrForceIcons(GameState gameState, PhysicalCard target, PhysicalCard cardToDeploy);

    boolean mayDeployAsIfFromHand(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card is allowed to be deployed instead of a starfighter using Combat Response.
     * @param gameState the game state
     * @param card the card
     * @return true if allowed, otherwise false
     */
    boolean mayDeployInsteadOfStarfighterUsingCombatResponse(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card is allowed to be deployed with the specified pilot instead of a matching starfighter using Combat Response.
     * @param gameState the game state
     * @param pilot the pilot
     * @param card the card
     * @return true if allowed, otherwise false
     */
    boolean mayDeployWithInsteadOfMatchingStarfighterUsingCombatResponse(GameState gameState, PhysicalCard pilot, PhysicalCard card);

    /**
     * Determines if the specified Interrupt card is explicitly allowed to be played to cancel a Force drain at the location.
     * @param gameState the game state
     * @param card the Interrupt card
     * @param location the Force drain location
     * @return true if allowed, otherwise false
     */
    boolean mayPlayInterruptToCancelForceDrain(GameState gameState, PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified Interrupt card is explicitly allowed to be played to cancel the specified card (being played or
     * on table).
     * @param gameState the game state
     * @param card the Interrupt card
     * @param targetCard the card being played or on table
     * @return true if allowed, otherwise false
     */
    boolean mayPlayInterruptToCancelCard(GameState gameState, PhysicalCard card, PhysicalCard targetCard);

    /**
     * Determines if the affected cards is prohibited from existing at (deploying or moving to) the specified targeted.
     * @param gameState the game state
     * @param card the card
     * @param target the target card
     * @return true if card may not exist at target, otherwise false
     */
    boolean isProhibitedFromTarget(GameState gameState, PhysicalCard card, PhysicalCard target);

    /**
     * Determines if the affected cards is prohibited from deploying to the specified targeted.
     * @param gameState the game state
     * @param playedCard the card
     * @param target the target card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return true if card may not be deployed to target, otherwise false
     */
    boolean isProhibitedFromDeployingTo(GameState gameState, PhysicalCard playedCard, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption);

    /**
     * Determines if the specified card is prohibited from allowing the specified player to download cards.
     * @param gameState the game state
     * @param card the card
     * @param playerId the playerId
     * @return true or false
     */
    boolean isProhibitedFromAllowingPlayerToDownloadCards(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Determines if the specified player is prohibited from deploying cards as a 'react' to the current battle or Force
     * Drain location.
     * @param gameState the game state
     * @param playerId the player
     * @return true if player may not deploy cards as a 'react', otherwise false
     */
    boolean isProhibitedFromDeployingAsReact(GameState gameState, String playerId);

    /**
     * Gets the 'react' action option if the specified card is allowed to deploy as a 'react'.
     * @param gameState the game state
     * @param card the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param deployTargetFilter the filter for where the card can be played
     * @return 'react' action option, or null
     */
    ReactActionOption getDeployAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter);

    /**
     * Gets the 'react' action option if the player can use the specified card to deploy other cards as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    List<ReactActionOption> getDeployOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card);

    /**
     * Gets the 'react' action option if the specified card is allowed to move as a 'react'.
     * @param gameState the game state
     * @param card the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param asReactAway true if 'react' away, otherwise 'react'
     * @param moveTargetFilter the filter for where the card can be moved
     * @return 'react' action options
     */
    ReactActionOption getMoveAsReactOption(GameState gameState, PhysicalCard card, ReactActionOption reactActionFromOtherCard, boolean asReactAway, Filter moveTargetFilter);

    /**
     * Gets the 'react' action option if the player can use the specified card to move other cards as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    ReactActionOption getMoveOtherCardsAsReactOption(String playerId, GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is able to join the move as 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param sourceCard the source card of the 'react'
     * @param card the card
     * @return true or false
     */
    boolean isCardEligibleToJoinMoveAsReact(String playerId, GameState gameState, PhysicalCard sourceCard, PhysicalCard card);

    /**
     * Gets the 'react' action option if the player can use the specified card to move other cards away as a 'react'.
     * @param playerId the player
     * @param gameState the game state
     * @param card the card
     * @return 'react' action option, or null
     */
    ReactActionOption getMoveOtherCardsAwayAsReactOption(String playerId, GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from participating in a 'react'.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from participating in a 'react', otherwise false
     */
    boolean isProhibitedFromParticipatingInReact(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified captive is prohibited from being transferred.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from being transferred, otherwise false
     */
    boolean mayNotBeTransferred(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card ignores location deployment restrictions when deploying to the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param skipForceIconsOrPresenceCheck the skip checking sufficent Force icons or presence
     * @return true if card ignores location deployment restrictions when deploying to target
     */
    boolean ignoresLocationDeploymentRestrictions(GameState gameState, PhysicalCard card, PhysicalCard target, DeploymentRestrictionsOption deploymentRestrictionsOption, boolean skipForceIconsOrPresenceCheck);

    /**
     * Determines if the specified card ignores objective restrictions when force draining at the specified target, from the specified source card.
     * @param gameState the game state
     * @param location the target card (location)
     * @param sourceCard the source of the modifier
     * @param playerId the player
     * @return true if card ignores objective restrictions when force draining at target
     */
    boolean ignoresObjectiveRestrictionsWhenForceDrainingAtLocation(GameState gameState, PhysicalCard location, PhysicalCard sourceCard, String playerId);

    /**
     * Determines if the specified card ignores objective restrictions when initiating battle at the specified target, from the specified source card.
     * @param gameState the game state
     * @param location the target card (location)
     * @param sourceCard the source of the modifier
     * @param playerId the player
     * @return true if card ignores objective restrictions when force draining at target
     */
    boolean ignoresObjectiveRestrictionsWhenInitiatingBattleAtLocation(GameState gameState, PhysicalCard location, PhysicalCard sourceCard, String playerId);

    /**
     * Determines if the specified card ignores location deployment restrictions from the source card.
     * @param gameState the game state
     * @param cardToDeploy the card to deploy
     * @param sourceCard the source card of the location deployment restriction
     * @return true if card ignores location deployment restrictions in its game text
     */
    boolean ignoresLocationDeploymentRestrictionsFromSource(GameState gameState, PhysicalCard cardToDeploy, PhysicalCard sourceCard);

    /**
     * Determines if the specified card ignores location deployment restrictions in its game text.
     * @param gameState the game state
     * @param card the card
     * @return true if card ignores location deployment restrictions in its game text
     */
    boolean ignoresGameTextLocationDeploymentRestrictions(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is granted the ability to deploy to the specified target.
     * @param gameState the game state
     * @param card the card
     * @param target the target
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card is granted the ability to deploy to the target, otherwise false
     */
    boolean isGrantedToDeployTo(GameState gameState, PhysicalCard card, PhysicalCard target, ReactActionOption reactActionOption);

    /**
     * Determines if the specified card is a squadron that is granted the ability to deploy.
     * @param gameState the game state
     * @param card the card
     * @return true if card is granted the ability to deploy, otherwise false
     */
    boolean isSquadronAllowedToDeploy(GameState gameState, PhysicalCard card);

    boolean grantedToDeployToDagobahTarget(GameState gameState, PhysicalCard playedCard, PhysicalCard target);

    boolean grantedToDeployToAhchToTarget(GameState gameState, PhysicalCard playedCard, PhysicalCard target);

    boolean grantedToDeployToAsLanded(GameState gameState, PhysicalCard playedCard, PhysicalCard target);

    boolean canPerformSpecialBattlegroundDownload(GameState gameState, String playerId);

    void performedSpecialBattlegroundDownload(String playerId);

    /**
     * Records that the specified card has performed a regular move.
     * @param card the card
     */
    void regularMovePerformed(PhysicalCard card);

    /**
     * Determines if the specified card has performed a regular move this turn.
     * @param card the card
     * @return true if card has performed a regular move this turn, otherwise false
     */
    boolean hasPerformedRegularMoveThisTurn(PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from moving.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMove(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from moving except using landspeed.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving except using landspeed, otherwise false
     */
    boolean mayOnlyMoveUsingLandspeed(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from moving from site to site using landspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the site to move from
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationUsingLandspeed(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact);

    /**
     * Determines if the specified card is prohibited from moving using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @return true if card is prohibited from moving using hyperspeed, otherwise false
     */
    boolean mayNotMoveUsingHyperspeed(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from moving from location to location using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact);

    /**
     * Determines if the specified card is prohibited from moving from location to location without using hyperspeed.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationWithoutUsingHyperspeed(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact);

    /**
     * Determines if the specified card is prohibited from moving from location to location using sector movement.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationUsingSectorMovement(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact);

    /**
     * Determines if the specified card is prohibited from moving from location to location using location text.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationUsingLocationText(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from location to location using docking bay transit.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the docking bay to move from
     * @param toLocation the docking bay to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationUsingDockingBayTransit(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from landing from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotLandFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact);

    /**
     * Determines if the specified card is prohibited from taking off from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotTakeOffFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact);

    /**
     * Determines if the specified card is prohibited from moving from location to location to start a Bombing Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationToStartBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from location to location to end a Bombing Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationToEndBombingRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from location to location at start of an Attack Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationAtStartOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from location to location at end of an Attack Run.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromLocationToLocationAtEndOfAttackRun(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the starship/vehicle site to move from
     * @param toStarshipOrVehicle the starship/vehicle to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromSiteToRelatedStarshipOrVehicle(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toStarshipOrVehicle);

    /**
     * Determines if the specified card is prohibited from moving from starship/vehicle site to related starship/vehicle.
     * @param gameState the game state
     * @param card the card
     * @param fromStarshipOrVehicle the starship/vehicle to move from
     * @param toSite the starship/vehicle site to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotMoveFromStarshipOrVehicleToRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromStarshipOrVehicle, PhysicalCard toSite);

    /**
     * Determines if the specified card is prohibited from entering the starship/vehicle site from a site.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the site to move from
     * @param toSite the starship/vehicle site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotEnterStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact);

    /**
     * Determines if the specified card is prohibited from exit the starship/vehicle site to a site.
     * @param gameState the game state
     * @param card the card
     * @param fromSite the starship/vehicle site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotExitStarshipOrVehicleSite(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact);

    /**
     * Determines if the specified card is prohibited from shuttling from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
     * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotShuttleFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from embarking from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotEmbarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from disembarking from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotDisembarkFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from relocating from location to location.
     * @param gameState the game state
     * @param card the card
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param allowDagobah true if relocating from/to Dagobah locations is allowed, otherwise false
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayNotRelocateFromLocationToLocation(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean allowDagobah, boolean allowAhchTo);

    /**
     * Determines if the specified card is moved by opponent instead of owner.
     * @param gameState the game state
     * @param card the card
     * @return true if card is moved by opponent instead of owner
     */
    boolean isMovedOnlyByOpponent(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card moves using landspeed only during deploy phase.
     * @param gameState the game state
     * @param card the card
     * @return true if card is moved by opponent instead of owner
     */
    boolean isMovesUsingLandspeedOnlyDuringDeployPhase(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is a location the player may shuttle, transfer, embark, and disembark at for
     * free instead of the related starship. (Example: Star Destroyer: Launch Bay)
     * @param gameState the game state
     * @param playerId the player
     * @param location the card
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayShuttleTransferLandAndTakeOffForFreeAtInsteadOfRelatedStarship(GameState gameState, String playerId, PhysicalCard location);

    /**
     * Determines if the specified card can shuttle directly from the specified location to the other specified location.
     * @param gameState the game state
     * @param cardToMove the card to move
     * @param fromLocation the location to shuttle from
     * @param toLocation the location to shuttle to
     * @return true if card is prohibited from moving, otherwise false
     */
    boolean mayShuttleDirectlyFromLocationToLocation(GameState gameState, PhysicalCard cardToMove, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Gets the amount of Force needed to move the card using landspeed.
     * @param gameState the game state
     * @param card the card to move using landspeed
     * @param fromSite the site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveUsingLandspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed to move the card using hyperspeed.
     * @param gameState the game state
     * @param card the card to move using hyperspeed
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed to move the card without using hyperspeed.
     * @param gameState the game state
     * @param card the card to move without using hyperspeed
     * @param fromSystem the system to move from
     * @param toSystem the system to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveWithoutUsingHyperspeedCost(GameState gameState, PhysicalCard card, PhysicalCard fromSystem, PhysicalCard toSystem, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed to move the card using sector movement.
     * @param gameState the game state
     * @param card the card to move using sector movement
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveUsingSectorMovementCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed to land the card.
     * @param gameState the game state
     * @param card the card to land
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getLandingCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to take off.
     * @param gameState the game state
     * @param card the card to take off
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getTakeOffCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to move to start a Bombing Run.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveToStartBombingRunCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to be shuttled.
     * @param gameState the game state
     * @param card the card to be shuttled
     * @param fromLocation the location to shuttle from (or location the starship is at if shuttling from a starship)
     * @param toLocation the location to shuttle to (or location the starship is at if shuttling to a starship)
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getShuttleCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to enter the starship/vehicle site.
     * @param gameState the game state
     * @param card the card to move
     * @param fromSite the site to move from
     * @param toSite the starship/vehicle site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getEnterStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to exit the starship/vehicle site.
     * @param gameState the game state
     * @param card the card to move
     * @param fromSite the starship/vehicle site to move from
     * @param toSite the site to move to
     * @param asReact true if the movement is for a 'react' movement, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getExitStarshipOrVehicleSiteCost(GameState gameState, PhysicalCard card, PhysicalCard fromSite, PhysicalCard toSite, boolean asReact, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to embark.
     * @param gameState the game state
     * @param card the card to move
     * @param moveTo the card to embark on
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getEmbarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to disembark.
     * @param gameState the game state
     * @param card the card to move
     * @param moveTo the card to disembark to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getDisembarkingCost(GameState gameState, PhysicalCard card, PhysicalCard moveTo, float changeInCost);

    /**
     * Determines if characters aboard vehicle may "jump off" when vehicle is about to be lost.
     * @param gameState the game state
     * @param card the vehicle
     * @return true if characters may "jump off", otherwise false
     */
    boolean allowsCharactersAboardToJumpOff(GameState gameState, PhysicalCard card);

    /**
     * Gets the amount of Force needed for the cards to ship-dock.
     * @param gameState the game state
     * @param starship1 a starship to ship-dock
     * @param starship2 a starship to ship-dock
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the ship-docking cost
     */
    float getShipdockingCost(GameState gameState, PhysicalCard starship1, PhysicalCard starship2, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to move using location text.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to move from (or location the starship/vehicle is at if move from a starship/vehicle)
     * @param toLocation the location to move to (or location the starship/vehicle is at if move to a starship/vehicle)
     * @param baseCost base cost in amount of Force required to perform the movement
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getMoveUsingLocationTextCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to move using docking bay transit.
     * @param gameState the game state
     * @param card the card to move
     * @param fromDockingBay the docking bay to move from
     * @param toDockingBay the docking bay to move to
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @return the move cost
     */
    float getDockingBayTransitCost(GameState gameState, PhysicalCard card, PhysicalCard fromDockingBay, PhysicalCard toDockingBay, float changeInCost);

    /**
     * Gets the amount of Force needed for the card to relocate between locations.
     * @param gameState the game state
     * @param card the card to move
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return the move cost
     */
    float getRelocateBetweenLocationsCost(GameState gameState, PhysicalCard card, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost);

    /**
     * Gets the amount of Force needed for the cards to relocate between locations.
     * @param gameState the game state
     * @param cards the cards to move
     * @param fromLocation the location to relocate from
     * @param toLocation the location to relocate to
     * @param baseCost the base cost (as defined by the card performing the relocation)
     * @return the move cost
     */
    float getRelocateBetweenLocationsCost(GameState gameState, Collection<PhysicalCard> cards, PhysicalCard fromLocation, PhysicalCard toLocation, float baseCost);

    int getPilotCapacity(GameState gameState, PhysicalCard physicalCard);

    int getAstromechCapacity(GameState gameState, PhysicalCard physicalCard);

    boolean canCarryPassengerAsIfCreatureVehicle(GameState gameState, PhysicalCard physicalCard, PhysicalCard passenger);

    boolean hasCapacityForCardsToRelocate(GameState gameState, PhysicalCard physicalCard, Collection<PhysicalCard> cards);

    // Using, driving, piloting
    boolean cannotDriveOrPilot(GameState gameState, PhysicalCard playedCard);

    boolean mayNotBeUsed(GameState gameState, PhysicalCard deviceOrWeapon);

    boolean mayNotBeUsed(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon);

    /**
     * Determines if the card is piloted.
     * @param gameState the game state
     * @param card the card
     * @param forStarshipTakeoff true if checking if starship is piloted for takeoff, otherwise false
     * @return true if piloted, otherwise false
     */
    boolean isPiloted(GameState gameState, PhysicalCard card, boolean forStarshipTakeoff);

    boolean isLanded(GameState gameState, PhysicalCard physicalCard);

    boolean cannotAddToPowerOfPilotedBy(GameState gameState, PhysicalCard physicalCard);

    boolean hasAstromech(GameState gameState, PhysicalCard physicalCard);

    /**
     * Determines if the starship has an astromech or nav computer.
     * @param gameState the game state
     * @param card the starship
     * @return true or false
     */
    boolean hasAstromechOrNavComputer(GameState gameState, PhysicalCard card);

    // Devices
    void deviceUsedBy(PhysicalCard user, PhysicalCard device);

    List<Integer> otherDevicesUsed(PhysicalCard user, PhysicalCard weapon);

    int numDevicesAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded);

    // Weapons
    void weaponUsedBy(PhysicalCard user, PhysicalCard weapon);

    List<Integer> otherWeaponsUsed(PhysicalCard user, PhysicalCard weapon);

    List<Integer> otherWeaponsUsed(PhysicalCard user, SwccgBuiltInCardBlueprint permanentWeapon);

    int numWeaponsAllowedToUse(GameState gameState, PhysicalCard card, boolean allowLanded);

    /**
     * Determines if the specified card is not prohibited from firing weapons.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean notProhibitedFromFiringWeapons(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is allowed to fire any number of weapons.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean mayFireAnyNumberOfWeapons(GameState gameState, PhysicalCard card);

    /**
     * Records that the specified weapon has been fired in attack.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInAttack(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Records that the specified permanent weapon has been fired in attack.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInAttack(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Gets the number of times the weapon has been fired in the current attack.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInAttack(PhysicalCard card, boolean completeOnly);

    /**
     * Gets the number of times the permanent weapon has been fired in the current attack.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInAttack(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

    /**
     * Gets the weapons that have been fired in the current attack by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<PhysicalCard> getWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly);

    /**
     * Gets the permanent weapons that have been fired in the current attack by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByPlayer(String playerId, boolean completeOnly);

    /**
     * Gets the weapons that have been fired in the current attack by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<PhysicalCard> getWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

    /**
     * Gets the permanent weapons that have been fired in the current attack by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInAttackByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

    /**
     * Records that the specified weapon has been fired in battle.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInBattle(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Records that the specified permanent weapon has been fired in battle.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInBattle(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Gets the number of times the weapon has been fired in the current battle.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInBattle(PhysicalCard card, boolean completeOnly);

    /**
     * Gets the number of times the permanent weapon has been fired in the current battle.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInBattle(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

    /**
     * Gets the weapons that have been fired in the current battle by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<PhysicalCard> getWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly);

    /**
     * Gets the permanent weapons that have been fired in the current battle by the specified player.
     * @param playerId the player
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByPlayer(String playerId, boolean completeOnly);

    /**
     * Gets the weapons that have been fired in the current battle by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<PhysicalCard> getWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

    /**
     * Gets the permanent weapons that have been fired in the current battle by the specified weapon user.
     * @param weaponUser the weapon user
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     */
    List<SwccgBuiltInCardBlueprint> getPermanentWeaponsFiredInBattleByWeaponUser(PhysicalCard weaponUser, boolean completeOnly);

    /**
     * Records that the specified weapon has been fired during Attack Run.
     * @param card the card
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInAttackRun(PhysicalCard card, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Records that the specified permanent weapon has been fired during Attack Run.
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the weapon
     * @param complete true if firing completed, false if targeted but not actually fired yet
     */
    void firedInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean complete);

    /**
     * Gets the number of times the weapon has been fired in the during current Attack Run.
     * @param card the card
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInAttackRun(PhysicalCard card, boolean completeOnly);

    /**
     * Gets the number of times the permanent weapon has been fired during current Attack Run.
     * @param permanentWeapon the permanent weapon
     * @param completeOnly true if only counts firing completed, false if targeted but not actually fired yet is counted
     * @return the number of times the weapon has been fired
     */
    int numTimesFiredInAttackRun(SwccgBuiltInCardBlueprint permanentWeapon, boolean completeOnly);

    /**
     * Determines if the specified device or weapon is allowed to be used by a landed starship.
     * @param gameState the game state
     * @param deviceOrWeapon the device or weapon
     * @return true or false
     */
    boolean mayBeUsedByLandedStarship(GameState gameState, PhysicalCard deviceOrWeapon);

    boolean canWeaponTargetAdjacentSite(GameState gameState, PhysicalCard weapon);
    boolean canWeaponTargetAdjacentSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon);

    boolean canWeaponTargetTwoSitesAway(GameState gameState, PhysicalCard weapon);
    boolean canWeaponTargetTwoSitesAway(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon);

    boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, PhysicalCard weapon);
    boolean canWeaponTargetNearestRelatedExteriorSite(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon);

    /**
     * Gets the cost to fire the weapon.
     * @param gameState the game state
     * @param weapon the weapon
     * @param cardFiringWeapon the card firing the weapon, or null if weapon is not fired by another card
     * @param target the card targeted by the weapon, or null if no target specified
     * @param baseCost the base cost (as defined by the weapon game text)
     * @return the cost to fire the weapon
     */
    float getFireWeaponCost(GameState gameState, PhysicalCard weapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost);

    /**
     * Gets the cost to fire the permanent weapon.
     * @param gameState the game state
     * @param permanentWeapon the permanent weapon
     * @param cardFiringWeapon the card firing the permanent weapon
     * @param target the card targeted by the permanent weapon, or null if no target specified
     * @param baseCost the base cost (as defined by the permanent weapon game text)
     * @return the cost to fire the weapon
     */
    float getFireWeaponCost(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, PhysicalCard target, int baseCost);

    /**
     * Determines if the specified weapon may be fired repeatedly.
     * @param gameState the game state
     * @param weapon the weapon
     * @return true or false
     */
    boolean mayFireWeaponRepeatedly(GameState gameState, PhysicalCard weapon);

    /**
     * Gets the cost to fire the weapon repeatedly.
     * @param gameState the game state
     * @param weapon the weapon
     * @return the cost for fire the weapon repeatedly
     */
    float getFireWeaponRepeatedlyCost(GameState gameState, PhysicalCard weapon);

    /**
     * Determines if the specified artillery weapon may be fired without a warrior present.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    boolean mayFireArtilleryWeaponWithoutWarriorPresent(GameState gameState, PhysicalCard artilleryWeapon);

    /**
     * Determines if the specified artillery weapon is powered.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    boolean isPowered(GameState gameState, PhysicalCard artilleryWeapon);

    /**
     * Determines if the specified artillery weapon is does not require a power source.
     * @param gameState the game state
     * @param artilleryWeapon the artillery weapon
     * @return true or false
     */
    boolean doesNotRequirePowerSource(GameState gameState, PhysicalCard artilleryWeapon);

    /**
     * Determines if the specified weapon is granted ability to fire twice per battle.
     * @param gameState the game state
     * @param weapon the weapon
     * @return true or false
     */
    boolean mayBeFiredTwicePerBattle(GameState gameState, PhysicalCard weapon);

    /**
     * Determines if the specified card is granted ability to fire the specified weapon twice per battle.
     * @param gameState the game state
     * @param weaponUser the weapon user
     * @param weapon the weapon
     * @return true or false
     */
    boolean mayFireWeaponTwicePerBattle(GameState gameState, PhysicalCard weaponUser, PhysicalCard weapon);

    /**
     * Records that the specified target card was targeted by the specified weapon.
     * @param target the target
     * @param weapon the weapon
     */
    void targetedByWeapon(PhysicalCard target, PhysicalCard weapon);

    /**
     * Records that the specified target card was targeted by the specified permanent weapon.
     * @param target the target
     * @param permanentWeapon the permanent weapon
     */
    void targetedByPermanentWeapon(PhysicalCard target, SwccgBuiltInCardBlueprint permanentWeapon);

    /**
     * Gets the weapons that have targeted the specified target this turn.
     * @param target the target
     * @return the weapons that have targeted the specified target this turn
     */
    List<PhysicalCard> weaponsTargetedByThisTurn(PhysicalCard target);

    /**
     * Gets the permanent weapons that have targeted the specified target this turn.
     * @param target the target
     * @return the permanent weapons that have targeted the specified target this turn
     */
    List<SwccgBuiltInCardBlueprint> permanentWeaponsTargetedByThisTurn(PhysicalCard target);


    boolean mayNotBeFired(GameState gameState, PhysicalCard weapon);

    boolean mayNotBeFired(GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon);


    // Prepositions

    /**
     * Gets the card that the specified card is present at. This represents the location if the specified card
     * is "present" at the location, otherwise it represents the "enclosed" card the specified card is attached to.
     *
     * @param gameState the game state
     * @param card      the card
     * @return card the specified card is present at, otherwise null
     */
    PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard card);

    PhysicalCard getCardIsPresentAt(GameState gameState, PhysicalCard physicalCard, boolean includeMovesLikeCharacter, boolean includeEnclosedInPrison);


    boolean isPresentAt(GameState gameState, PhysicalCard physicalCard, PhysicalCard atTarget);

    /**
     * Determines if the two cards are "with" each other.
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @return true if cards are "with" each other, otherwise false
     */
    boolean isWith(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Determines if the two cards are "present with" each other.
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @return true if cards are "present with" each other, otherwise false
     */
    boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Determines if the two cards are "present with" each other. Optionally includes cards that "move like a character".
     *
     * @param gameState the game state
     * @param card1 a card
     * @param card2 a card
     * @param includeMovesLikeCharacter true if including cards that "move like a character", otherwise false
     * @return true if cards are "present with" each other, otherwise false
     */
    boolean isPresentWith(GameState gameState, PhysicalCard card1, PhysicalCard card2, boolean includeMovesLikeCharacter);

    // boolean isOnPlanet(GameState gameState, PhysicalCard physicalCard, String planet);

    boolean isPlaceToBePresentOnPlanet(GameState gameState, PhysicalCard physicalCard, String planet);

    // boolean isAtPlanet(GameState gameState, PhysicalCard physicalCard, String planet);

    // boolean isAtPlanetSite(GameState gameState, PhysicalCard physicalCard, String planet);


    /**
     * Gets the planet that the specified card is "at".
     *
     * @param gameState the game state
     * @param card      the card
     * @return name of the planet the specified card is "at", otherwise null
     */
    String getSystemThatCardIsAt(GameState gameState, PhysicalCard card);

    /**
     * Gets the planet that the specified card is "on".
     *
     * @param gameState the game state
     * @param card      the card
     * @return name of the planet the specified card is "on", otherwise null
     */
    String getSystemThatCardIsOn(GameState gameState, PhysicalCard card);

    /**
     * Determines if the two specified cards are adjacent sectors.
     *
     * @param gameState the game state
     * @param sector1     a sector
     * @param sector2     a sector
     * @return true if specified cards are adjacent sectors, otherwise false
     */
    boolean isAdjacentSectors(GameState gameState, PhysicalCard sector1, PhysicalCard sector2);

    /**
     * Gets the distance between the sector (or the sectors the cards are "at"), or null if determining a distance is not valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the distance between the sectors, or null
     */
    Integer getDistanceBetweenSectors(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Gets the sectors in order between the cards (or between the locations the cards are "at").
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the sectors in order between card1 and card2, or null if not valid to determine
     */
    List<PhysicalCard> getSectorsBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Determines if the two specified cards are adjacent sites.
     *
     * @param gameState the game state
     * @param site1     a card
     * @param site2     a card
     * @return true if specified cards are adjacent sites, otherwise false
     */
    boolean isAdjacentSites(GameState gameState, PhysicalCard site1, PhysicalCard site2);

    /**
     * Determines if the sites are part of the same system, starship, or vehicle.
     *
     * @param gameState the game state
     * @param site1     a site
     * @param site2     a site
     * @return the sites in order between card1 and card2
     */
    boolean isSitesWithSameParent(GameState gameState, PhysicalCard site1, PhysicalCard site2);

    /**
     * Gets the distance between the sites (or the sites the cards are "at"), or null if determining a distance is not valid.
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the distance between the sites, or null
     */
    Integer getDistanceBetweenSites(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Gets the sites in order between the cards (or between the locations the cards are "at").
     *
     * @param gameState the game state
     * @param card1     a card
     * @param card2     a card
     * @return the sites in order between card1 and card2, or null if not valid to determine
     */
    List<PhysicalCard> getSitesBetween(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Gets the location that the specified card is "present" at.
     *
     * @param gameState the game state
     * @param card      the card
     * @return location that the specified card is "present" at, otherwise null
     */
    PhysicalCard getLocationThatCardIsPresentAt(GameState gameState, PhysicalCard card);

    /**
     * Gets the locations that the specified cards are "present" at.
     *
     * @param gameState the game state
     * @param cards     the cards
     * @return locations that the specified cards are "present" at
     */
    Collection<PhysicalCard> getLocationsThatCardsArePresentAt(GameState gameState, Collection<PhysicalCard> cards);

    /**
     * Gets the location that the specified card is "at".
     *
     * @param gameState the game state
     * @param card      the card
     * @return location that the specified card is "at", otherwise null
     */
    PhysicalCard getLocationThatCardIsAt(GameState gameState, PhysicalCard card);

    /**
     * Gets the locations that the specified cards are "at".
     *
     * @param gameState the game state
     * @param cards     the cards
     * @return locations that the specified cards are "at"
     */
    Collection<PhysicalCard> getLocationsThatCardsAreAt(GameState gameState, Collection<PhysicalCard> cards);

    /**
     * Gets the location itself or the location the specified card is "at".
     *
     * @param gameState the game state
     * @param card      the card
     * @return the location or the location the card is "at", otherwise null
     */
    PhysicalCard getLocationHere(GameState gameState, PhysicalCard card);

    /**
     * Gets the locations that the specified cards are "at" as well as any
     * locations in the specified cards.
     *
     * @param gameState the game state
     * @param cards     the cards
     * @return locations accepted by the specified filter as well as locations
     * that cards accepted by the specified filter are "at"
     */
    Collection<PhysicalCard> getLocationsHere(GameState gameState, Collection<PhysicalCard> cards);


    int getParsecNumber(GameState gameState, PhysicalCard physicalCard);

    boolean isAtSite(GameState gameState, PhysicalCard physicalCard, PhysicalCard site);

    boolean isAboard(GameState gameState, PhysicalCard physicalCard, PhysicalCard starshipOrVehicle, boolean includeAboardCargoOf, boolean includeRelatedSites);

    boolean isAtVehicleSite(GameState gameState, PhysicalCard physicalCard);

    boolean isAtStarshipSite(GameState gameState, PhysicalCard physicalCard);

    boolean isAtStarshipSiteOrVehicleSiteOfPersona(GameState gameState, PhysicalCard physicalCard, Persona starshipOrVehicle);

    boolean isStarshipOrVehicleAlone(GameState gameState, PhysicalCard physicalCard);

    boolean isCharacterAlone(GameState gameState, PhysicalCard physicalCard);

    boolean isAlone(GameState gameState, PhysicalCard physicalCard);

    boolean hasPermanentPilotAlone(GameState gameState, PhysicalCard physicalCard);

    boolean hasPermanentPilot(GameState gameState, PhysicalCard physicalCard);

    boolean hasPermanentAstromech(GameState gameState, PhysicalCard physicalCard);

    boolean hasPresenceAt(GameState gameState, String player, PhysicalCard physicalCard, boolean forBattle, String playerInitiatingBattle, Map<InactiveReason, Boolean> spotOverrides);


    /**
     * Determines if the specified player occupies the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if the player occupies the location, otherwise false
     */
    boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Determines if the specified player occupies the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return true if the player occupies the location, otherwise false
     */
    boolean occupiesLocation(GameState gameState, PhysicalCard location, String playerId, final Map<InactiveReason, Boolean> spotOverrides);

    /**
     * Determines if the specified player controls the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @return true if the player controls the location, otherwise false
     */
    boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId);

    /**
     * Determines if the specified player controls the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param playerId  the player
     * @param spotOverrides overrides for which inactive cards are visible to the query
     * @return true if the player controls the location, otherwise false
     */
    boolean controlsLocation(GameState gameState, PhysicalCard location, String playerId, final Map<InactiveReason, Boolean> spotOverrides);

    /**
     * Determines if the specified location is a battleground.
     *
     * @param gameState the game state
     * @param location  the location
     * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
     * @return true if location is a battleground, otherwise false
     */
    boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard);

    /**
     * Determines if the specified location is a battleground.
     *
     * @param gameState the game state
     * @param location  the location
     * @param ignoreForceIconsFromCard the card from which added Force icons are ignored when checking if battleground
     * @param modifierCollector collector of affecting modifiers
     * @return true if location is a battleground, otherwise false
     */
    boolean isBattleground(GameState gameState, PhysicalCard location, PhysicalCard ignoreForceIconsFromCard, ModifierCollector modifierCollector);

    /**
     * Determines if the specified starship can deploy as landed to the specified location.
     *
     * @param gameState the game state
     * @param location  the location
     * @param starship  the starship
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return true if starship may deploy as landed to the location, otherwise false
     */
    boolean isLocationStarshipMayDeployToAsLanded(GameState gameState, PhysicalCard location, PhysicalCard starship, DeploymentRestrictionsOption deploymentRestrictionsOption);

    /**
     * Determines if the specified location is a docking bay.
     *
     * @param gameState the game state
     * @param location  the location
     * @return true if location is a docking bay, otherwise false
     */
    boolean isDockingBay(GameState gameState, PhysicalCard location);

    /**
     * Determines if the specified location is the battle location.
     *
     * @param gameState the game state
     * @param location  the location
     * @return true if location is the battle location, otherwise false
     */
    boolean isBattleLocation(GameState gameState, PhysicalCard location);

    /**
     * Determines if the specified location is the Force drain location.
     *
     * @param gameState the game state
     * @param location  the location
     * @return true if location is the Force drain location, otherwise false
     */
    boolean isForceDrainLocation(GameState gameState, PhysicalCard location);

    /**
     * Determines if the specified locations are related locations.
     *
     * @param gameState the game state
     * @param location1 a location
     * @param location2 a location
     * @return true if the locations are related locations, otherwise false
     */
    boolean isRelatedLocations(GameState gameState, PhysicalCard location1, PhysicalCard location2);

    /**
     * Determines if the specified location is a starship or vehicle site of the specified starship or vehicle.
     *
     * @param gameState         the game state
     * @param starshipOrVehicle a starship or vehicle
     * @param location          a location
     * @return true if the locations are related locations, otherwise false
     */
    boolean isRelatedStarshipOrVehicleSite(GameState gameState, PhysicalCard starshipOrVehicle, PhysicalCard location);

    /**
     * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if supposed to be canceled, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card);

    /**
     * Gets the cards (if any) that indicate that the specified card's game text is supposed to be canceled.
     * @param gameState the game state
     * @param card the card
     * @param modifierCollector collector of affecting modifiers
     * @return true if supposed to be canceled, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingGameTextCanceled(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if the specified card's game text is canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if canceled, otherwise false
     */
    boolean isGameTextCanceled(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card's game text is canceled.
     * @param gameState the game state
     * @param card the card
     * @param allowIfOnlyLanded true if game text is still allowed to be enabled if the card is landed (but still piloted for takeoff)
     * @param skipUnpilotedCheck true if game text is still allowed to be enabled if the card is unpiloted
     * @return true if canceled, otherwise false
     */
    boolean isGameTextCanceled(GameState gameState, PhysicalCard card, boolean allowIfOnlyLanded, boolean skipUnpilotedCheck);

    /**
     * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
     * facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true if canceled, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Gets the cards (if any) that indicate that the specified location's game text is supposed to be canceled on the side
     * facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @param modifierCollector collector of affecting modifiers
     * @return true if canceled, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId, ModifierCollector modifierCollector);

    /**
     * Determines if the specified location's game text is canceled on the side facing the specified player.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @return true if canceled, otherwise false
     */
    boolean isLocationGameTextCanceledForPlayer(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
     * @param gameState the game state
     * @param card the card
     * @return true if supposed to be suspended, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card);

    /**
     * Gets the cards (if any) that indicate that the specified card is supposed to be suspended.
     * @param gameState the game state
     * @param card the card
     * @param modifierCollector collector of affecting modifiers
     * @return true if supposed to be suspended, otherwise false
     */
    Collection<PhysicalCard> getCardsMarkingCardSuspended(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if the card has the specified gametext modification.
     * @param gameState the game state
     * @param card the card
     * @param type the gametext modification type
     * @return true if card has the modification, otherwise false
     */
    boolean hasGameTextModification(GameState gameState, PhysicalCard card, ModifyGameTextType type);

    /**
     * Gets the number of times the card has the specified gametext modification applied cumulatively.
     * @param gameState the game state
     * @param card the card
     * @param type the gametext modification type
     * @return the number of times the card has the specified gametext modification
     */
    int getGameTextModificationCount(GameState gameState, PhysicalCard card, ModifyGameTextType type);

    /**
     * Gets the value of a race destiny.
     * @param gameState the game state
     * @param physicalCard the race destiny card
     * @return the race destiny value
     */
    float getRaceDestiny(GameState gameState, PhysicalCard physicalCard);

    /**
     * Gets the value of a race destiny.
     * @param gameState the game state
     * @param physicalCard the race destiny card
     * @param modifierCollector collector of affecting modifiers
     * @return the race destiny value
     */
    float getRaceDestiny(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector);

    /**
     * Gets the race total for the specified player.
     * @param gameState the game state
     * @param playerId the player
     * @return the race total
     */
    float getHighestRaceTotal(GameState gameState, String playerId);

    /**
     * Gets the race total for the specified Podracer.
     * @param gameState the game state
     * @param podracer the Podracer
     * @return the race total
     */
    float getPodracerRaceTotal(GameState gameState, PhysicalCard podracer);

    /**
     * Gets the Podracer cards that are leading the Podrace.
     * @param gameState the game state
     * @return the Podracer cards leading the Podrace
     */
    Collection<PhysicalCard> getPodracersLeadingPodrace(GameState gameState);

    /**
     * Gets the Podracer cards that are behind in the Podrace.
     * @param gameState the game state
     * @return the Podracer cards behind in the Podrace
     */
    Collection<PhysicalCard> getPodracersBehindInPodrace(GameState gameState);


    CardState getCardState(GameState gameState, PhysicalCard physicalCard, boolean includeExcludedFromBattle, boolean includeUndercover, boolean includeCaptives,
                           boolean includeConcealed, boolean includeWeaponsForStealing, boolean includeMissing, boolean includeBinaryOff, boolean includeSuspended);

    Uniqueness getUniqueness(GameState gameState, PhysicalCard card);

    CardSubtype getInterruptType(GameState gameState, PhysicalCard card);

    // Choosing targets
    String getPlayerToChooseCardTargetAtLocation(GameState gameState, PhysicalCard card, PhysicalCard location, String defaultPlayerId);

    /**
     * Determines if the specified card is explicitly not allowed to be played due to existence of a "can't play" modifier
     * affecting the card.
     *
     * @param gameState the game state
     * @param card      the card
     * @param isDejarikRules true if playing using Dejarik Rules, otherwise false
     * @return true if card cannot be played, otherwise false
     */
    boolean isPlayingCardProhibited(GameState gameState, PhysicalCard card, boolean isDejarikRules);

    /**
     * Determines if the limit of how may times a card with any titles of the specified card can be played per turn has been reached.
     *
     * @param gameState the game state
     * @param card      the card
     * @return true if the limit has been reached, otherwise false
     */
    boolean isPlayingCardTitleTurnLimitReached(GameState gameState, PhysicalCard card);

    /**
     * Determines if the limit of the number of cards on table (or out of play) with same title or persona as the specified
     * card has been reached.
     *
     * @param gameState the game state
     * @param card      the card
     * @return true if the limit has been reached, otherwise false
     */
    boolean isUniquenessOnTableLimitReached(GameState gameState, PhysicalCard card);

    /**
     * Determines if the player is explicitly not allowed to search the card pile using the game text action on the specified
     * card.
     * @param gameState the game state
     * @param card the card
     * @param playerId the player
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param gameTextActionId the game text action id
     * @return true if card pile is not allowed to be searched, otherwise false
     */
    boolean isSearchingCardPileProhibited(GameState gameState, PhysicalCard card, String playerId, Zone cardPile,
                                          String cardPileOwner, GameTextActionId gameTextActionId);

    boolean prohibitedFromCarrying(GameState gameState, PhysicalCard character, PhysicalCard cardToBeCarried);

    boolean prohibitedFromPiloting(GameState gameState, PhysicalCard pilot, PhysicalCard starshipOrVehicle);

    /**
     * Determines if a card may be replaced (character converted) by opponent.
     * @param gameState the game state
     * @param card the card
     * @return true if allowed, otherwise false
     */
    boolean mayBeReplacedByOpponent(GameState gameState, PhysicalCard card);

    // Location text
    PhysicalCard hasExpandedGameTextFromLocation(GameState gameState, Side sideExpandedFrom, PhysicalCard expandedToLocation, Side sideExpandedTo);

    boolean isRotatedLocation(GameState gameState, PhysicalCard physicalCard);

    // Converting locations
    boolean cannotBeConverted(GameState gameState, PhysicalCard location);

    // Converting card by deployment of another card
    boolean canBeConvertedByDeployment(GameState gameState, PhysicalCard card, String playerId);

    /**
     * Determines if the specified player can remove cards from opponent hand using the specified card.
     * @param gameState the game state
     * @param actionSource the source card of the action
     * @param playerId the player
     * @return true or false
     */
    boolean mayNotRemoveCardsFromOpponentsHand(GameState gameState, PhysicalCard actionSource, String playerId);

    boolean mayNotBeGrabbed(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card may not be canceled.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be canceled, otherwise false
     */
    boolean mayNotBeCanceled(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card may not be placed out of play.
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be placed out of play, otherwise false
     */
    boolean mayNotBePlacedOutOfPlay(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card may not be targeted by weapons used by the specified card.
     * @param gameState the game state
     * @param cardTargeted the card targeted
     * @param weaponUser the card use
     * @return true if card may be targeted, otherwise false
     */
    boolean mayNotBeTargetedByWeaponUser(GameState gameState, PhysicalCard cardTargeted, PhysicalCard weaponUser);

    /**
     * Determines if the specified card may not be removed (unless attached to card is Disarmed).
     * @param gameState the game state
     * @param card the card
     * @return true if card may not be removed, otherwise false
     */
    boolean mayNotRemoveDeviceUnlessDisarmed(GameState gameState, PhysicalCard card);

    // Targeting
    boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting);

    boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, PhysicalCard cardDoingTargeting, Set<TargetingReason> targetingReasons);

    boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeapon);

    boolean canBeTargetedBy(GameState gameState, PhysicalCard cardToTarget, SwccgBuiltInCardBlueprint permanentWeapon, Set<TargetingReason> targetingReasons);

    boolean isImmuneToCardTitle(GameState gameState, PhysicalCard card, String cardTitle);

    /**
     * Determines if the modifiers from the source card are suspended from affecting the specified card.
     * @param gameState the game state
     * @param source the source card
     * @param affectedCard the affected card
     * @return true if effects from modifier are suspended, otherwise false
     */
    boolean isEffectsFromModifierToCardSuspended(GameState gameState, PhysicalCard source, PhysicalCard affectedCard);
    
    /**
     * Determines if a card was granted to targeted by the specified card.
     * @param gameState the game state
     * @param cardTargeted the card targeted
     * @param cardTargeting the card doing the targeting
     * @return true if card may be targeted, otherwise false
     */
    boolean grantedMayBeTargetedBy(GameState gameState, PhysicalCard cardTargeted, PhysicalCard cardTargeting);

    /**
     * Determines if a card is explicitly allowed to be placed on owner's Political Effect.
     * @param gameState the game state
     * @param card the card
     * @return true if card may be placed on Political Effects, otherwise false
     */
    boolean grantedMayBePlaceOnOwnersPoliticalEffect(GameState gameState, PhysicalCard card);

    /**
     * Gets the number of captives a character may escort.
     * @param gameState the game state
     * @param escort the escort
     * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
     * @return true or false
     */
    int getNumCaptivesAllowedToEscort(GameState gameState, PhysicalCard escort, boolean skipWarriorCheck);

    /**
     * Determines if a specified card can escort another specified card as a captive.
     * @param gameState the game state
     * @param escort the escort
     * @param captive the captive
     * @param skipWarriorCheck true if checking that escort is a warrior, etc. is skipped, otherwise false
     * @return true or false
     */
    boolean canEscortCaptive(GameState gameState, PhysicalCard escort, PhysicalCard captive, boolean skipWarriorCheck);

    // Card variables
    float getVariableValue(GameState gameState, PhysicalCard physicalCard, Variable variable, float baseValue);

    boolean ignoreDuringEpicEventCalculation(GameState gameState, PhysicalCard card);

    float getEpicEventCalculationTotal(GameState gameState, PhysicalCard physicalCard, float baseTotal);

    /**
     * Gets the calculation total.
     * @param gameState the game state
     * @param calculationSource the source card during the calculation
     * @param baseTotal the base total
     * @return the calculation total
     */
    float getCalculationTotal(GameState gameState, PhysicalCard calculationSource, float baseTotal);

    /**
     * Gets the calculation total when targeting a specified card.
     * @param gameState the game state
     * @param calculationSource the source card during the calculation
     * @param target the target
     * @param baseTotal the base total
     * @return the calculation total
     */
    float getCalculationTotalTargetingCard(GameState gameState, PhysicalCard calculationSource, PhysicalCard target, float baseTotal);

    /**
     * Gets the 'blow away' Blockade Flagship attempt total.
     * @param gameState the game state
     * @param baseTotal the base total
     * @return the total
     */
    float getBlowAwayBlockadeFlagshipAttemptTotal(GameState gameState, float baseTotal);

    /**
     * Determines if the specified spy may not 'break cover' during deploy using normal Undercover rules.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean mayNotBreakOwnCoverDuringDeployPhase(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card may not attempt Jedi Tests.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    boolean mayNotAttemptJediTests(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified Jedi Test is placed on table when completed.
     * @param gameState the game state
     * @param card the Jedi Test
     * @return true or false
     */
    boolean isJediTestPlacedOnTableWhenCompleted(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified Jedi Test is suspended instead of lost when target not on table.
     * @param gameState the game state
     * @param card the Jedi Test
     * @return true or false
     */
    boolean isJediTestSuspendedInsteadOfLost(GameState gameState, PhysicalCard card);

    /**
     * Gets the Jedi Test number of the specified Jedi Test.
     * @param gameState the game state
     * @param card the card
     * @return true or false
     */
    int getJediTestNumber(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card is a player's highest ability character on the the table. If multiple characters have the
     * highest ability, then all of them are considered to be a highest ability character.
     *
     * @param gameState the game state
     * @param source    the card that is performing this query
     * @param card      a card
     * @return true if the card is the owning player's highest ability character on the table, otherwise false
     */
    boolean isPlayersHighestAbilityCharacter(GameState gameState, PhysicalCard source, PhysicalCard card, String playerId);

    /**
     * Determines if the specified card is excluded from being the highest-ability character from the perspective of the
     * card performing the query.
     * @param gameState the game state
     * @param cardPerformingQuery the card performing the query
     * @param card the card
     * @return true if not allowed, otherwise false
     */
    boolean excludedFromBeingHighestAbilityCharacter(GameState gameState, PhysicalCard cardPerformingQuery, PhysicalCard card);

    /**
     * Determines if sites are prevented from deploying between the specified sites.
     * @param gameState the game state
     * @param site1 a site on one side
     * @param site2 a site on the other side
     * @return true if allowed, otherwise false
     */
    boolean isSitePreventedFromDeployingBetweenSites(GameState gameState, PhysicalCard site1, PhysicalCard site2);

    /**
     * Determines if specified card is not allowed to be used to transport to or from specified location.
     * @param gameState the game state
     * @param card the card
     * @param location the location
     * @return true if not allowed, otherwise false
     */
    boolean prohibitedFromUsingCardToTransportToOrFromLocation(GameState gameState, PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified Operative is prevented from deploying to or moving to location.
     * @param gameState the game state
     * @param card the Operative
     * @param location the location
     * @return true if Operative cannot deploy or move to location, otherwise false
     */
    boolean isOperativePreventedFromDeployingToOrMovingToLocation(GameState gameState, PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified Sith Probe Droid is prevented from deploying to or moving to location.
     * @param gameState the game state
     * @param card the Sith Probe Droid
     * @param location the location
     * @return true if Sith Probe Droid cannot deploy or move to location, otherwise false
     */
    boolean isSithProbeDroidPreventedFromDeployingToOrMovingToLocation(GameState gameState, PhysicalCard card, PhysicalCard location);

    /**
     * Determines if the specified card is explicitly not allowed to 'cloak'.
     * @param gameState the game state
     * @param card the card
     * @return true if card cannot 'cloak', otherwise false
     */
    boolean isCloakingCardProhibited(GameState gameState, PhysicalCard card);

    /**
     * Determines if the specified card is explicitly not allowed to 'attach'.
     * @param gameState the game state
     * @param card the card
     * @return true if card cannot 'attach', otherwise false
     */
    boolean isAttachingCardProhibited(GameState gameState, PhysicalCard card);

    /**
     * Determines if a card is allowed to make a Kessel Run if not a smuggler.
     * @param gameState the game state
     * @param card a card
     * @return true if not allowed, otherwise false
     */
    boolean isAllowedToMakeKesselRunWhenNotSmuggler(GameState gameState, PhysicalCard card);

    /**
     * Determines if a location is under the "Hoth Energy Shield"
     * @param gameState the game state
     * @param location a location
     * @return true if under "Hoth Energy Shield", otherwise false
     */
    boolean isLocationUnderHothEnergyShield(GameState gameState, PhysicalCard location);

    boolean cannotJoinSearchParty(GameState gameState, PhysicalCard card);

    boolean isDoubled(GameState gameState, PhysicalCard card);

    boolean isDoubled(GameState gameState, PhysicalCard card, ModifierCollector modifierCollector);

    /**
     * Determines if the card deploys and moves like an undercover spy (including if card is an undercover spy)).
     * @param gameState the game state
     * @param card a card
     * @return true if card is an undercover spy or deploys and moves like an undercover spy, otherwise false
     */
    boolean isDeploysAndMovesLikeUndercoverSpy(GameState gameState, PhysicalCard card);

    /**
     * Determines if the card is placed in Used Pile (instead of Lost Pile) when canceled by the specified player and card.
     * @param gameState the game state
     * @param card the card being canceled
     * @param canceledByPlayerId the player canceling the card
     * @param canceledByCard the card canceling the card
     * @return true if card is to be placed in Used Pile, otherwise false
     */
    boolean isPlacedInUsedPileWhenCanceled(GameState gameState, PhysicalCard card, String canceledByPlayerId, PhysicalCard canceledByCard);

    boolean permanentPilotsSuspended(GameState gameState, PhysicalCard card);

    boolean permanentAstromechsSuspended(GameState gameState, PhysicalCard card);

    boolean isMatchingPair(GameState gameState, PhysicalCard character, PhysicalCard starshipVehicleOrWeapon);

    // Objectives
    boolean cannotBeFlipped(GameState gameState, PhysicalCard card);

    boolean cannotTurnOnBinaryDroid(GameState gameState, PhysicalCard card);

    boolean notImmediatelyLostIfAsteroidSectorDrawnForAsteroidDestiny(GameState gameState, PhysicalCard card);

    // Moved from Filters

    /**
     * Determines if the two cards have the same card title. For combo cards, each title is checked.
     *
     * @param gameState          the game state
     * @param card1              a card
     * @param card2              a card
     * @return true if cards have same card title, otherwise false
     */
    boolean cardTitlesMatch(GameState gameState, PhysicalCard card1, PhysicalCard card2);

    /**
     * Determines if the card is deployable.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param card the card
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    boolean isDeployable(GameState gameState, PhysicalCard sourceCard, PhysicalCard card, boolean includePlayable, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost);

    /**
     * Determines if the card is deployable to target.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param card the card
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param targetFilter the target filter
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    boolean isDeployableToTarget(GameState gameState, PhysicalCard sourceCard, PhysicalCard card, boolean includePlayable, Filter targetFilter, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost);

    /**
     * Determines if the card is deployable to the system.
     * @param gameState the game state
     * @param sourceCard the card to initiate the deployment
     * @param card the card
     * @param includePlayable true if includes playable cards, false if only deployable cards
     * @param systemName the system name
     * @param targetFilter the target filter
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @param forFree true if playing the card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param changeInCostCardFilter the card filter for cards that are affected by the change in cost, or null for all
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @return true if card can be played or deployed, otherwise false
     */
    boolean isDeployableToSystem(GameState gameState, PhysicalCard sourceCard, PhysicalCard card, boolean includePlayable, String systemName, Filter targetFilter, Filter specialLocationConditions, boolean forFree, float changeInCost, Filter changeInCostCardFilter, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost);

    boolean canBeTargetedByWeaponsAsIfPresent(GameState gameState, PhysicalCard card);

    boolean canBeTargetedByWeaponsAsStarfighter(GameState gameState, PhysicalCard card);

    /**
     * Gets the starship or vehicle of which the specified card is in a pilot slot.
     * Note: The specified card must be able to pilot or this will return null.
     *
     * @param gameState the game state
     * @param card      the card
     * @return starship or vehicle piloted by the specified card, otherwise null
     */
    PhysicalCard getIsPilotOf(GameState gameState, PhysicalCard card);

    /**
     * Gets the transport vehicle of which the specified card is in a driver slot.
     * Note: Specified card must be able to drive or this will return null.
     *
     * @param gameState the game state
     * @param card      the card
     * @return transport vehicle driven by the specified card, otherwise null
     */
    PhysicalCard getIsDriverOf(GameState gameState, PhysicalCard card);

    /**
     * Gets actions that the specified card is currently able to perform to initiate an epic duel.
     *
     * @param gameState the game state
     * @param card      a card
     * @return true or false
     */
    List<PlayInterruptAction> getInitiateEpicDuelActions(GameState gameState, PhysicalCard card);

     /**
     * Gets a filter that accepts cards that can be dark side participants in a duel initiated by the specified card.
     *
     * @param gameState the game state
     * @param card the card initiating the duel
     * @param side the side of the Force of the participant
     * @return the filter
     */
    Filter getValidDuelParticipant(GameState gameState, PhysicalCard card, Side side);

    /**
     * Gets the cards under which a captured starship can go when captured by the specified tractor beam
     * @param gameState the game state
     * @param tractorBeam the tractor beam that captured the starship
     * @return the collection of cards
     */
    Collection<PhysicalCard> getDestinationForCapturedStarships(GameState gameState, PhysicalCard tractorBeam);

    boolean hasMindscannedCharacter(GameState gameState, PhysicalCard card);

    SwccgCardBlueprint getMindscannedCharacterBlueprint(GameState gameState, PhysicalCard card);

    boolean mindscannedCharacterGameTextWasCanceled(GameState gameState, PhysicalCard card);
    CardSubtype getModifiedSubtype(GameState gameState, PhysicalCard card);
    boolean isCommuning(GameState gameState, PhysicalCard card);
    Collection<PhysicalCard> getCardsConsideredOutOfPlay(GameState gameState);
    Collection<PhysicalCard> getActiveCardsAffectedByModifier(GameState gameState, ModifierType modifierType);
}
