package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.AbstractAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The interface represents a printed Star Wars CCG card
 * and provides methods to get statistics of that card.
 *
 * Each PhysicalCard object, which represents a copy
 * of a Star Wars CCG card within a game of Gemp-Swccg,
 * contains a reference to its "blueprint", so it can
 * get stats for the printed card it represents a copy of.
 *
 **/
public interface SwccgCardBlueprint {

    /**
     * Gets the side of the Force
     * @return the side of the Force
     */
    Side getSide();

    /**
     * Gets the card category (e.g. Character, Effect, Interrupt,...).
     * @return the card category
     */
    CardCategory getCardCategory();

    /**
     * Gets the card types (e.g. Alien, Effect, Interrupt, Rebel,...).
     * Some cards may have multiple types.
     * @return the card types
     */
    Set<CardType> getCardTypes();

    /**
     * Determines of the card is the specified card type (e.g. Alien, Effect, Interrupt, Rebel,...).
     * Some cards may have multiple types.
     * @param cardType the card type
     * @return true if the card is the specified card type, otherwise false
     */
    boolean isCardType(CardType cardType);

    /**
     * Gets the card subtype.
     * @return the card subtype
     */
    CardSubtype getCardSubtype();

    /**
     * Gets the uniqueness.
     * @return the uniqueness
     */
    Uniqueness getUniqueness();

    /**
     * Gets the entire card title
     * @return the card title
     */
    String getTitle();

    /**
     * Gets a list of the card's titles. Some combo cards have multiple card titles.
     * @return the card's titles
     */
    List<String> getTitles();

    /**
     * Gets the lore.
     * @return the lore
     */
    String getLore();

    /**
     * Gets the game text.
     * @return the game text
     */
    String getGameText();

    /**
     * Gets the testing text.
     * @return the testing text, or null
     */
    String getTestingText();

    /**
     * Determines if the blueprint is front of a double-sided card.
     * @return true or false
     */
    boolean isFrontOfDoubleSidedCard();

    /**
     * Gets the Dark side game text of a location card.
     * @return the Dark side game text
     */
    String getLocationDarkSideGameText();

    /**
     * Gets the Light side game text of a location card.
     * @return the Light side game text
     */
    String getLocationLightSideGameText();

    /**
     * Determines if the generic location may be part of the specified system.
     * @param name the system name
     * @return true if generic location may be part of system, otherwise false
     */
    boolean mayNotBePartOfSystem(String name);

    /**
     * Determines if a special rule is in effect at this location.
     * @param rule the special rule
     * @param self the location
     * @return true if rule is in effect at this location, otherwise false
     */
    boolean isSpecialRuleInEffectHere(SpecialRule rule, PhysicalCard self);

    /**
     * Gets the card blueprint of the permanent weapon.
     * @param self the card
     * @return the card blueprint
     */
    SwccgBuiltInCardBlueprint getPermanentWeapon(PhysicalCard self);

    /**
     * Gets the card blueprints of permanent pilots and astromechs aboard.
     * @param self the card
     * @return list of card blueprints
     */
    List<SwccgBuiltInCardBlueprint> getPermanentsAboard(PhysicalCard self);

    /**
     * Gets the model types of the card.
     * @return the model types
     */
    List<ModelType> getModelTypes();

    /**
     * Determines if it has the specified keyword.
     * @param keyword the keyword
     * @return true if it has the specified keyword, otherwise false
     */
    boolean hasKeyword(Keyword keyword);

    /**
     * Determines if it has the specified icon.
     * @param icon the icon
     * @return true if it has the specified icon, otherwise false
     */
    boolean hasIcon(Icon icon);

    /**
     * Gets the number of the specified icon it has.
     * @param icon the icon
     * @return the number of the specified icon
     */
    int getIconCount(Icon icon);

    /**
     * Determines if it is always immune to cards with the specified title.
     * @param name the card title
     * @return true if immune, otherwise false
     */
    boolean isImmuneToCardTitle(String name);

    /**
     * Determines if it is always immune to owner's cards with the specified title.
     * @param name the card title
     * @return true if immune, otherwise false
     */
    boolean isImmuneToOwnersCardTitle(String name);

    /**
     * Determines if it is always immune to opponents Objective.
     * @return true if immune, otherwise false
     */
    boolean isImmuneToOpponentsObjective();

    /**
     * Determines if the card type cannot be canceled.
     * @return true if card type may not be canceled, otherwise false
     */
    boolean isCardTypeMayNotBeCanceled();

    /**
     * Determines if this has a species attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasSpeciesAttribute();

    /**
     * Gets the species, or null.
     * @return the species, or null if no species
     */
    Species getSpecies();

    /**
     * Determines if this is a combo card.
     * @return true if combo card, otherwise false
     */
    boolean isComboCard();

    /**
     * Determines if this card is always considered to be stolen.
     * @return true or false
     */
    boolean isAlwaysStolen();

    /**
     * Determines if this card's title has an "(AI)" suffix.
     * @return true if title has suffix, otherwise false
     */
    boolean hasAlternateImageSuffix();

    /**
     * Determines if this card's title has a "(V)" suffix.
     * @return true if title has suffix, otherwise false
     */
    boolean hasVirtualSuffix();

    /**
     * Determines if this card does not count toward deck limit.
     * @return true if this card does not count toward deck limit, otherwise false
     */
    boolean isDoesNotCountTowardDeckLimit();

    /**
     * Determines if this card may not be placed in Reserve Deck.
     * @return true if may not be placed in Reserve Deck, otherwise false
     */
    boolean isMayNotBePlacedInReserveDeck();

    /**
     * Gets the filter for the starfighters present at a location that are replaced by the squadron.
     * @return the filter
     */
    Filter getReplacementFilterForSquadron();

    /**
     * Gets the number of starfighters present at a location that are replaced by the squadron.
     * @return the count
     */
    Integer getReplacementCountForSquadron();

    /**
     * Determines if this moves like a character.
     * @return true if moves like a character, otherwise false
     */
    boolean isMovesLikeCharacter();

    /**
     * Determines if this deploys and moves like a starfighter.
     * @return true if deploys and moves like a starfighter, otherwise false
     */
    boolean isDeploysAndMovesLikeStarfighter();

    /**
     * Determines if this deploys and moves like a starfighter at cloud sectors.
     * @return true if deploys and moves like a starfighter at cloud sectors, otherwise false
     */
    boolean isDeploysAndMovesLikeStarfighterAtCloudSectors();

    /**
     * Determines if this can go in the vehicle capacity slot of a starship.
     * @return true if able to go in vehicle capacity slot, otherwise false
     */
    boolean isVehicleSlotOfStarshipCompatible();

    /**
     * Determines if this has a persona only while on table.
     * @return true if card only has a persona while on table, otherwise false
     */
    boolean hasCharacterPersonaOnlyWhileOnTable();

    /**
     * Determines if this has the specified persona.
     * @param persona the persona
     * @return true if has persona, otherwise false
     */
    boolean hasPersona(Persona persona);

    /**
     * Gets the personas.
     * @return the personas
     */
    Set<Persona> getPersonas();

    /**
     * Gets the deploy cost.
     * @return the deploy cost
     */
    Float getDeployCost();

    /**
     * Determines if this deploys using both Force piles
     * @return true if deploys using both Force piles, otherwise false
     */
    boolean isDeployUsingBothForcePiles();

    /**
     * Determines if this has a power attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasPowerAttribute();

    /**
     * Gets the power.
     * @return the power
     */
    Float getPower();

    /**
     * Determines if this has an ability attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasAbilityAttribute();

    /**
     * Gets the ability.
     * @return the ability
     */
    Float getAbility();

    /**
     * Determines if this has a politics attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasPoliticsAttribute();

    /**
     * Gets the politics value.
     * @return the politics value
     */
    float getPolitics();

    /**
     * Determines if this has a landspeed attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasLandspeedAttribute();

    /**
     * Gets the landspeed.
     * @return the landspeed
     */
    Float getLandspeed();

    /**
     * Determines if this has a forfeit attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasForfeitAttribute();

    /**
     * Gets the forfeit value.
     * @return the forfeit value
     */
    Float getForfeit();

    /**
     * Determines if this has an armor attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasArmorAttribute();

    /**
     * Gets the armor.
     * @return the armor
     */
    Float getArmor();

    /**
     * Determines if this has a maneuver attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasManeuverAttribute();

    /**
     * Gets the maneuver.
     * @return the maneuver.
     */
    Float getManeuver();

    /**
     * Determines if this has a special defense value attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasSpecialDefenseValueAttribute();

    /**
     * Gets the special defense value.
     * @return the special defense value
     */
    float getSpecialDefenseValue();

    /**
     * Determines if this has ferocity attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasFerocityAttribute();

    /**
     * Gets the ferocity.
     * @return the ferocity
     */
    Float getFerocity();

    /**
     * Determines if has hyperspeed attribute
     * @return true if has attribute, otherwise false
     */
    boolean hasHyperspeedAttribute();

    /**
     * Gets the hyperspeed.
     * @return the hyperspeed
     */
    Float getHyperspeed();

    /**
     * Determines if has immunity to attrition attribute.
     * @return true if has attribute, otherwise false
     */
    boolean hasImmunityToAttritionAttribute();

    /**
     * Gets destiny.
     * @return the destiny
     */
    Float getDestiny();

    /**
     * Gets alternate destiny value (for cards that have two values), otherwise same as getDestiny method.
     * @return the alternate destiny value
     */
    Float getAlternateDestiny();

    /**
     * Gets cost in order to use alternate destiny value (for cards that have two values).
     *
     * @return the alternate destiny value
     */
    int getAlternateDestinyCost();

    /**
     * Gets the pilot capacity.
     * @return the pilot capacity
     */
    int getPilotCapacity();

    /**
     * Gets the pilot or passenger capacity.
     * @return the pilot or passenger capacity
     */
    int getPilotOrPassengerCapacity();

    /**
     * Gets the passenger capacity.
     * @return the passenger capacity.
     */
    int getPassengerCapacity();

    /**
     * Gets the astromech capacity.
     * @return the astromech capacity
     */
    int getAstromechCapacity();

    /**
     * Gets the vehicle capacity.
     * @return the vehicle capacity
     */
    int getVehicleCapacity();

    /**
     * Gets the filter for cards that can go in a vehicle capacity slot.
     * @return the vehicle capacity filter
     */
    Filter getVehicleCapacityFilter();

    /**
     * Gets the starfighter or TIE capacity.
     * @return the starship or TIE capacity
     */
    int getStarfighterOrTIECapacity();

    /**
     * Gets the filter for cards that can go in a starfighter or TIE capacity slot.
     * @return the starfighter or TIE capacity filter
     */
    Filter getStarfighterOrTIECapacityFilter();

    /**
     * Gets the capital starship capacity.
     * @return the capital starship capacity
     */
    int getCapitalStarshipCapacity();

    /**
     * Gets the filter for cards that can go in a capital starship capacity slot.
     * @return the capital starship capacity filter
     */
    Filter getCapitalStarshipCapacityFilter();

    /**
     * Gets the system name for the location.
     * @return the system name
     */
    String getSystemName();

    /**
     * Gets the persona of the starship or vehicle the site is related to.
     * @return the vehicle or starship persona the site is related to, otherwise null
     */
    Persona getRelatedStarshipOrVehiclePersona();

    /**
     * Gets the parsec number of the system.
     * @return the parsec number
     */
    int getParsec();

    /**
     * Gets the name of the system the location deploys orbiting.
     * @return the system name, or null
     */
    String getDeploysOrbitingSystem();

    /**
     * Gets modifiers to the card itself that are always in effect.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self);

    /**
     * Gets modifiers that are from this card that are in effect while the card is active in play.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self);

    /**
     * Gets modifiers that are from this card that are in effect while the card is stacked (face up) on another card.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self);

    /**
     * Gets the action playing this card as a starting interrupt.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action
     */
    PlayCardAction getStartingInterruptAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action playing this Interrupt in response to an effect or an effect result.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param effect the effect to response to
     * @param effectResult the effect result to response to
     * @return the action
     */
    PlayCardAction getPlayInterruptAsResponseAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, Effect effect, EffectResult effectResult);

    /**
     * Gets the play card action for the card if it can be played. If the card can be played in multiple ways, then
     * this will return an action that has the player choose which way to play the card, then that plays the card that
     * way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card action
     */
    PlayCardAction getPlayCardAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions);

    /**
     * Gets the play card actions for each way the card can be played by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentOption specifies special deployment options, or null
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param cardToDeployWith the card to deploy with simultaneously, or null
     * @param cardToDeployWithForFree true if the card to deploy with deploys for free, otherwise false
     * @param cardToDeployWithChangeInCost change in amount of Force (can be positive or negative) required for the card to deploy with
     * @param deployTargetFilter the filter for where the card can be played
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null         @return the play card actions
     */
    List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions);

    /**
     * Determines if the card is an Effect that deploys on another card.
     * @param game the game
     * @param self the card
     * @return true if card is an Effect that deploys on another card
     */
    boolean isEffectThatDeploysOnAnotherCard(SwccgGame game, PhysicalCard self);

    /**
     * Gets the play card action for the card if it can be deployed as a dejarik/hologram to a holosite.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @return the play card action
     */
    PlayCardAction getDeployDejarikAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree);

    /**
     * Gets the play card action for the location card if it can be deployed to the specified system.
     * @param playerId the player
     * @param game the game
     * @param self the location card
     * @param sourceCard the card to initiate the deployment
     * @param system the system name
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @return the play card action
     */
    PlayCardAction getPlayLocationToSystemAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, String system, Filter specialLocationConditions);

    /**
     * Gets the valid filter for targets to deploy to when the specified card is deployed simultaneously with the specified pilot, driver, or passenger.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param character the pilot, driver, or passenger
     * @param characterForFree true if playing pilot, driver, or passenger for free, otherwise false
     * @param characterChangeInCost change in amount of Force (can be positive or negative) required to deploy pilot, driver, or passenger
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'   @return the filter
     */
    Filter getValidDeployTargetWithPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, PhysicalCard character, boolean characterForFree, float characterChangeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption);

    /**
     * This method is for checking which starships or vehicles the character can deploy to as pilot simultaneously regardless
     * of location and deploy cost. It is to be used by other methods (that factor in the valid locations and deploy costs)
     * to figure out which starship/vehicles and pilots/drivers can deploy simultaneously.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(String playerId, final SwccgGame game, final PhysicalCard self);

    /**
     * This method is for checking which locations a character can deploy to as pilot, driver, or passenger simultaneously regardless of deploy cost.
     * It is to be used by other methods (that factor in the valid starships/vehicles and deploy costs) to figure out which
     * starship/vehicles and pilots/drivers/passengers can deploy simultaneously.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    Filter getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption);

    /**
     * This method is used for getting the filter for starships or vehicles that can be related to the non-unique starship
     * or vehicle site.
     * @return the filter
     */
    Filter getRelatedStarshipOrVehicleFilter();

    /**
     * Gets the valid filter for targets to transfer the card to another card during character replacement.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidTransferDuringCharacterReplacementTargetFilter(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the valid filter for targets to place the card when the specified card is placed from off table.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidPlaceCardTargetFilter(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the valid filter for targets to relocate the Effect when the specified Effect is relocated.
     * @param playerId the player to relocate the Effect
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidRelocateEffectTargetFilter(final String playerId, final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the valid target filter that the card can remain attached to after the attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the valid target filter that the card can remain attached to. If the card becomes attached to a card that is
     * not accepted by this filter, then the attached card will be lost by rule.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the valid target filter that the specified Utinni Effect may remain targeting. If the card targeted by the Utinni
     * Effect becomes not accepted by this filter, then the Utinni Effect will be lost by rule.
     * @param game the game
     * @param self the card
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    Filter getValidUtinniEffectTargetFilterToRemainTargeting(SwccgGame game, PhysicalCard self, TargetId targetId);

    /**
     * Gets the habitat filter for the specified creature card.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getHabitatFilter(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets the default play card zone option for this card. This is only set by cards that are only played to one zone.
     * @return the play card zone option, or null
     */
    PlayCardZoneOption getSinglePlayCardZoneOption();

    /**
     * Gets the transfer device or weapon action if the device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the transfer device or weapon actions
     */
    Action getTransferDeviceOrWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter transferTargetFilter);

    /**
     * Gets the target filter for where a device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param playCardOption the play card option, or null
     * @param forFree true if transferring for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the target filter
     */
    Filter getValidTransferDeviceOrWeaponTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PlayCardOption playCardOption, boolean forFree, Filter transferTargetFilter);

    /**
     * Gets the deploy as 'react' action for the card if it can deploy as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param deployTargetFilter the filter for where the card can deploy
     * @return the action
     */
    Action getDeployAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter);

    /**
     * Gets the move as 'react' action for the card if it can move as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    Action getMoveAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter moveTargetFilter);

    /**
     * Gets the move away action for a card if it can move away.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    Action getMoveAwayAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, Filter moveTargetFilter);

    /**
     * Gets the regular move action for the card if it can move as a regular move.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    Action getRegularMoveAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the move using landspeed action for the card if it can move using landspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param landspeedOverride the specified landspeed to use for this movement, or null if using normal landspeed
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    Action getMoveUsingLandspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Integer landspeedOverride, Filter moveTargetFilter);

    /**
     * Gets the move using hyperspeed action for the card if it can move using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getMoveUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the move without using hyperspeed action for the card if it can move without using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getMoveWithoutUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the move using sector movement action for the card if it can move using sector movement.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getMoveUsingSectorMovementAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the move using sector movement action for the card if it can move using sector movement during 'escape' from
     * Death Star II being 'blown away'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getMoveUsingEscapeFromDeathStarIIMovementAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the land action for the card if it can land.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getLandAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the take off action for the card if it can take off.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getTakeOffAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the move action for a bomber at the start of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    Action getMoveToStartBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove);

    /**
     * Gets the move action for a bomber at the end of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    Action getMoveToEndBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove);

    /**
     * Gets the move action for a starfighter at the start of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getMoveAtStartOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the move action for a starfighter at the end of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getMoveAtEndOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to move to the related starship/vehicle from a starship/vehicle site.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    Action getMoveToRelatedStarshipOrVehicleAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove);

    /**
     * Gets the action to move to a related starship/vehicle site from the starship/vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    Action getMoveToRelatedStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove);

    /**
     * Gets the action to enter a starship/vehicle site from the site the starship or vehicle is present at.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false  @return the action, or null
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getEnterStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the action to exit a starship/vehicle site to the site the starship or vehicle is present at.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getExitStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the action to shuttle a character/vehicle to/from a capital starship.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getShuttleAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter);

    /**
     * Gets the action to shuttle characters from a site to a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getShuttleUpUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to shuttle characters to a site from a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getShuttleDownUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to ship-dock a starship with another starship.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @return the action, or null
     */
    Action getShipdockAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree);

    /**
     * Gets the action to embark on a card (or to a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getEmbarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter moveTargetFilter);

    /**
     * Gets the action to disembark off of a card (or from a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asJumpOff true if disembarking as "jump off" vehicle, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    Action getDisembarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asJumpOff, Filter moveTargetFilter);

    /**
     * Gets the action to move between the capacity slots of a card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getMoveBetweenCapacitySlotsAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to move between ship-docked starships.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getMoveBetweenDockedStarshipsAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to deliver an escorted captive to prison.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getDeliverCaptiveToPrisonAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to take an imprisoned captive from prison into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getTakeImprisonedCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to leave an escorted 'frozen' captive as 'unattended'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getLeaveFrozenCaptiveUnattendedAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action to take an 'unattended frozen' captive into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getTakeUnattendedFrozenCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the action for when 'insert' card is revealed.
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    Action getInsertCardRevealedAction(SwccgGame game, PhysicalCard self);


    Filter getValidFireAtTargetFilter(String playerId, SwccgGame game, PhysicalCard self, boolean isRepeatedFiring, boolean ignoreFiringCost, int changeInCost);

    Filter getValidAtLocationForFireAtTargetFilter(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets a filter for the cards that are valid to use the specified device.
     * @param playerId the player
     * @param game the game
     * @param self the device
     * @return the filter
     */
    Filter getValidToUseDeviceFilter(String playerId, final SwccgGame game, final PhysicalCard self);

    /**
     * Gets a filter for the cards that are valid to use the specified weapon.
     * @param playerId the player
     * @param game the game
     * @param self the weapon
     * @return the filter
     */
    Filter getValidToUseWeaponFilter(String playerId, final SwccgGame game, final PhysicalCard self);

    /**
     * Determines if the weapon is fired by a character at same location rather than the card it is attached to.
     * @return true or false
     */
    boolean isFiredByCharacterPresentOrHere();

    /**
     * Determines if the card is inactive due to specific conditions even when the card would normally be active.
     * @param game the game
     * @param self the weapon
     * @return true if card is considered inactive instead of active, otherwise false
     */
    boolean isInactiveInsteadOfActive(final SwccgGame game, final PhysicalCard self);

    /**
     * Gets a filter for the cards the specified card is not prohibited from moving to.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param allowTrench true if moving to Death Star: Trench is not prevented by "Trench Rules" for this movement
     * @return the filter
     */
    Filter getValidMoveTargetFilter(String playerId, final SwccgGame game, final PhysicalCard self, boolean allowTrench);

    /**
     * Gets the Utinni Effect target ids used by the Utinni Effect.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the Utinni Effect target ids
     */
    List<TargetId> getUtinniEffectTargetIds(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets a filter for the cards the specified Utinni Effect may target (not including to deploy on).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Utinni Effect on
     * @param targetId the Utinni Effect target id
     * @return the filter
     */
    Filter getValidUtinniEffectTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, TargetId targetId);

    /**
     * Gets a filter for the cards that can be the mentor for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @return the filter
     */
    Filter getValidJediTestMentorTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget);

    /**
     * Gets a filter for the cards that can be the apprentice for the specified Jedi Test.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deployTarget the card to deploy the Jedi Test on
     * @param mentor the mentor for the apprentice
     * @param isDeployFromHand true if for an apprentice being deployed from hand
     * @return the filter
     */
    Filter getValidJediTestApprenticeTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard deployTarget, PhysicalCard mentor, boolean isDeployFromHand);

    /**
     * Gets the spot override to use when deploying the card with the specified play card option id.
     * @param playCardOptionId the play card option id
     * @return the spot override
     */
    Map<InactiveReason, Boolean> getDeployTargetSpotOverride(PlayCardOptionId playCardOptionId);

    /**
     * Gets the spot override to use when targeting a card with the specified target id.
     * @param targetId the target id
     * @return the spot override
     */
    Map<InactiveReason, Boolean> getTargetSpotOverride(TargetId targetId);

    /**
     * Determines if the card can be deploy simultaneously as attached.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card cannot be deploy simultaneously as attached, otherwise false
     */
    boolean mayDeploySimultaneouslyAsAttachedRequirements(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets effects (to be performed in order) that set any targeted cards when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    List<TargetingEffect> getTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption);

    /**
     * This method is called during deployment after the targets have been selected so the target filters can be updated
     * to support re-targeting.
     * @param action the action to perform the effect
     * @param game the game
     * @param self the card
     */
    void updateTargetFiltersAfterTargetsChosen(Action action, SwccgGame game, PhysicalCard self);

    /**
     * This method is called during deployment after the card is on table so the target filters can be updated
     * to support re-targeting.
     * @param game the game
     * @param self the card
     */
    void updateTargetFiltersAfterOnTable(SwccgGame game, PhysicalCard self);

    /**
     * Gets a special deploy cost (instead of using Force) that is used to deploy the card.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the special deploy cost, or null
     */
    StandardEffect getSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption);

    /**
     * Gets the game-rule actions that can be performed by the specified player by clicking on the top card of a card pile.
     * @param playerId the player
     * @param game the game
     * @param self the top card of the card pile
     * @return the actions
     */
    List<Action> getCardPilePhaseActions(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the top-level actions that can be performed by the specified player during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    List<Action> getTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the top-level actions for opponent's card that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    List<Action> getOpponentsCardTopLevelActions(String playerId, SwccgGame game, PhysicalCard self);

    /**
     * Gets the required "before" triggers for the specified effect if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredOutsideOfDeckBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the required "before" triggers when the specified Interrupt itself is being played.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredInterruptPlayedTriggers(SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the required "before" triggers for the specified effect.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOpponentsCardOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the optional "before" actions for the specified effect that can be performed by the specified player. This includes
     * actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self);

    /**
     * Gets the required "after" triggers for the specified effect result if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the required "after" triggers when the specified card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional "after" triggers when the specified card is drawn for destiny that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player
     * (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOpponentsCardOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional "after" actions for the specified effect result that can be performed by the specified player.
     * This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the required "after" triggers from a card when that card is 'blown away'.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getBlownAwayRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the required triggers from a card when that card leaves table.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional triggers from a card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional triggers from an opponent's card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOpponentsCardLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional triggers from a card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets the optional triggers from an opponent's card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    List<TriggerAction> getOpponentsCardLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self);

    /**
     * Gets displayable information about the card.
     * @param game the game
     * @param self the card
     * @return displayable information about the card, or null
     */
    String getDisplayableInformation(SwccgGame game, PhysicalCard self);

    /**
     * Gets the fire weapon action for the weapon (or card with permanent weapon) if it can be fired. If the weapon can
     * be fired in multiple ways, then this will return an action that has the player choose which way to fire the card,
     * then fires the card that way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for cards that may be targeted by the weapon
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the fire weapon action
     */
    FireWeaponAction getFireWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit);

    /**
     * Gets the fire weapon actions for each way the weapon (or card with permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for cards that may be targeted by the weapon
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the fire weapon actions
     */
    List<FireWeaponAction> getFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit);

    int getNumWeaponDestinyDraws(GameState gameState, PhysicalCard self, PhysicalCard target);

    Statistic getStatisticTargetedWhenFiring();

    DrawDestinyEffect getDrawWeaponDestinyEffect(GameState gameState, AbstractAction action, PhysicalCard weapon, PhysicalCard target, PhysicalCard cardFiringWeapon);

    void weaponDestinyDrawComplete(SwccgGame game, final AbstractAction action, final PhysicalCard self, final PhysicalCard target,
                                   PhysicalCard cardFiringWeapon, List<PhysicalCard> destinyCardDraws, List<Integer> destinyDrawValues, Integer totalDestiny);

    void weaponFireWasSuccessful(SwccgGame game, AbstractAction action, PhysicalCard self, PhysicalCard target, PhysicalCard cardFiringWeapon);

    /**
     * Determines if a card is a device or weapon that deploys on characters.
     * @return true if deploys on characters, otherwise false
     */
    boolean canBeDeployedOnCharacter();

    /**
     * Gets a filter for the cards that are valid to be pilots (or drivers) of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    Filter getValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment);

    /**
     * Gets a filter for the cards that are matching characters for this.
     * @return the filter
     */
    Filter getMatchingCharacterFilter();

    /**
     * Gets a filter for the cards that are matching pilots (or drivers) for this.
     * @return the filter
     */
    Filter getMatchingPilotFilter();

    /**
     * Gets a filter for the cards that are matching starships for this.
     * @return the filter
     */
    Filter getMatchingStarshipFilter();

    /**
     * Gets a filter for the cards that are matching vehicles for this.
     * @return the filter
     */
    Filter getMatchingVehicleFilter();

    /**
     * Gets a filter for the cards that are matching weapons for this.
     * @return the filter
     */
    Filter getMatchingWeaponFilter();

    /**
     * Gets the title of the matching system for this. Used for Operatives to determine matching system.
     * @return the system name, or null
     */
    String getMatchingSystem();

    /**
     * Determines if the card only deploys as an undercover spy.
     * @param game the game
     * @param self the card
     * @return the owner of the zone
     */
    boolean isOnlyDeploysAsUndercoverSpy(SwccgGame game, PhysicalCard self);

    /**
     * Determines if the card only deploys as an captured prisoner.
     * @param game the game
     * @param self the card
     * @return the owner of the zone
     */
    boolean isOnlyDeploysAsEscortedCaptive(SwccgGame game, PhysicalCard self);

    /**
     * Determines if the card may deploy as an undercover spy.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    boolean mayDeployAsUndercoverSpy(SwccgGame game, PhysicalCard self);

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    boolean isCardTypeDeployed();

    /**
     * Gets a filter for the cards that are valid duel participants in duels initiated by this card.
     * @param side the side of the Force
     * @param game the game
     * @param self the card
     * @return the filter
     */
    Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self);

    /**
     * Gets the map of cards that can participate against each other in an epic duel initiated by this card.
     * @param game the game
     * @param self the card
     * @param darkSideParticipantFilter the filter for dark side participants
     * @param lightSideParticipantFilter the filter for light side participants
     * @return a map of duel pairings, with the key as a dark side character, and the values as light side characters that
     * can be dueled by that character.
     */
    Map<PhysicalCard, Collection<PhysicalCard>> getInitiateEpicDuelMatchup(SwccgGame game, PhysicalCard self, Filter darkSideParticipantFilter, Filter lightSideParticipantFilter);

    /**
     * Gets duel directions provided by this card.
     * @param game the game
     * @return the duel directions provided by this card, or null
     */
    DuelDirections getDuelDirections(SwccgGame game);
}
