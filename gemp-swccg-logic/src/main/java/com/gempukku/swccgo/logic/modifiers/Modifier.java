package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


// This interface represents the methods
// that can be called on a modifier to see
// "if" and "how much" a particular modifier
// affects a card or the game.
//
public interface Modifier {

    PhysicalCard getSource(GameState gameState);
    void setPersistent(boolean value);
    boolean isPersistent();
    void skipSettingNotRemovedOnRestoreToNormal();
    void setNotRemovedOnRestoreToNormal(boolean value);
    boolean isNotRemovedOnRestoreToNormal();
    void setWhileInactiveInPlay(boolean value);
    boolean isWhileInactiveInPlay();
    void setEvenIfUnpilotedInPlay(boolean value);
    boolean isEvenIfUnpilotedInPlay();
    String getLocationSidePlayer();
    void setLocationSidePlayer(String player);
    void setFromPermanentPilot(boolean value);
    boolean isFromPermanentPilot();
    void setFromPermanentAstromech(boolean value);
    boolean isFromPermanentAstromech();
    boolean isCumulative();
    String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self);
    ModifierType getModifierType();
    Condition getCondition();
    Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self);
    void appendCondition(Condition addCondition);

    /**
     * Sets the condition that, when fulfilled, causes the modifier to be automatically removed.
     * @param expireCondition the condition, or null
     */
    void setExpireCondition(Condition expireCondition);

    /**
     * Gets the condition that, when fulfilled, causes the modifier to be automatically removed.
     * @return the condition, or null
     */
    Condition getExpireCondition();

    boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag);
    boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag, String playerId);

    boolean affectsCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    /**
     * Determines if this modifier is targeting the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the card
     * @return true or false
     */
    boolean isTargetingCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Determines if this modifier affects the specified player.
     * @param playerId the player
     * @return true or false
     */
    boolean isForPlayer(String playerId);

    /**
     * Gets which player this modifier affects if this modifier only affect a specified player, otherwise null.
     * @return the player, or null
     */
    String getForPlayer();

    /**
     * Determines if this modifier cancels game text on the side of the location facing the specified player.
     * @param playerId the player
     * @return true or false
     */
    boolean isCanceledTextForPlayer(String playerId);

    /**
     * Determines if this modifier affects the current draw destiny effect.
     * @param gameState the game state
     * @return true or false
     */
    boolean isForTopDrawDestinyEffect(GameState gameState);

    /**
     * Determines if this modifier affects the current blow away effect.
     * @param gameState the game state
     * @return true or false
     */
    boolean isForTopBlowAwayEffect(GameState gameState);

    /**
     * Gets the text to show on the User Interface for the action created because of this modifier.
     * @return the text
     */
    String getActionText();

    /**
     * Gets the filter for cards that the source card can have 'react'.
     * @return the filter
     */
    Filter getCardToReactFilter();

    /**
     * Gets the target filter.
     * @return the filter
     */
    Filter getTargetFilter();

    /**
     * Gets the pilot or driver filter.
     * @return the filter
     */
    Filter getPilotOrDriverFilter();

    /**
     * Gets the filter for cards restricted from increasing the power of the affected card.
     * @return the filter
     */
    Filter getCardsRestrictedFromIncreasingPowerFilter();

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the target.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param targetCard the target card
     * @return true or false
     */
    boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard);

    /**
     * Determines if this modifier is in effect for the affected card when this specified permanent weapon is the target.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param targetPermanentWeapon the permanent weapon of target card
     * @return true or false
     */
    boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint targetPermanentWeapon);

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the pilot.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param pilot the pilot card
     * @return true or false
     */
    boolean isAffectedPilot(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot);

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the driver.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param driver the driver card
     * @return true or false
     */
    boolean isAffectedDriver(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard driver);

    /**
     * Determines if this modifier is always in effect.
     * @return true or false
     */
    boolean isAlwaysInEffect();

    boolean isActionSource(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard actionSource);

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param permanentWeapon the affected permanent weapon
     * @return the value
     */
    float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint permanentWeapon);

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card1 the affected card
     * @param card2 the other card
     * @return the value
     */
    float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card1, PhysicalCard card2);

    /**
     * Gets the value of the multiplier modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    float getMultiplierValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Determines if this modifier is in effect for the specified variable.
     * @param variable the variable
     * @return true or false
     */
    boolean isAffectedVariable(Variable variable);

    /**
     * Gets the minimum Force Pile size to be able to use opponent's Force.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    int getMinForcePileSizeToUseOpponentsForce(GameState gameState, ModifiersQuerying modifiersQuerying);

    /**
     * Determines if sites are prevented from deploying between the specified sites.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param site1 a site
     * @param site2 a site
     * @return true or false
     */
    boolean mayNotDeploySiteBetweenSites(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard site1, PhysicalCard site2);

    /**
     * Gets the number of destiny to draw.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    int getNumToDraw(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Gets the number of destiny to choose.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    int getNumToChoose(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    ModifyGameTextType getModifyGameTextType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    boolean isKeywordRemoved(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword);
    boolean hasKeyword(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword);

    boolean hasSpecies(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Species species);

    Icon getIcon();
    int getIconCountModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Icon icon);

    float getForfeitModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getDestinyModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getDestinyDrawFromSourceCardModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard sourceOfDestinyDraw);

    float getPowerModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    int getPowerMultiplierModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getPowerModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getPoliticsModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiablePolitics(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getFerocityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableFerocity(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getAttritionModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    float getTotalPowerModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);
    float getTotalAbilityModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    float getAbilityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableAbility(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getForfeitModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableForfeit(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getImmunityToAttritionLessThanModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getImmunityToAttritionOfExactlyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getImmunityToAttritionChangedModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getImmunityToAttritionCappedAtValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    boolean isExceptForceIconOrPresenceRequirement();

    float getDockingBayTransitFromCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    boolean isPlayCardOption(PlayCardOption playCardOption);

    float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    float getBaseFerocityDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);
    int getNumFerocityDestinyDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    float getHyperspeedModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableHyperspeed(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getManeuverModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableManeuverModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getArmorModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableArmorModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getDefenseValueModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getUnmodifiableDefenseValue(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getDeployCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getMaximumToReduceDeployCostBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    float getUnmodifiableDeployCost(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    float getDeployCostWithPilotModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot);
    float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target);
    float getUnmodifiableDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard cardToDeploy, PhysicalCard target);
    boolean isUnmodifiableDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard cardToDeploy);

    /**
     * Determines if this modifier defines the printed deploy cost of the affected card when deploying to the target card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true or false
     */
    boolean isDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    /**
     * Gets the printed deploy cost when deploying the affected card to the target card as defined by the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return the printed deploy cost
     */
    float getDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    /**
     * Determines if this modifier causes the affected card to deploy free to the target card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true or false
     */
    boolean isDeployFreeToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier);

    /**
     * Determines if the affected cards is prohibited from existing at (deploying or moving to) the specified targeted.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true if card may not exist at target, otherwise false
     */
    boolean isProhibitedFromExistingAt(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    /**
     * Determines if the affected card may deploy to the location without presence or Force icons.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true if card can be deployed to the location without presence or Force icons, otherwise false
     */
    boolean mayDeployToLocationWithoutPresenceOrForceIcons(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    boolean grantedToDeployToDagobahTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);
    boolean grantedToDeployToAhchToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);
    boolean grantedToDeployToAsLanded(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    boolean canMoveAsReactToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target);

    /**
     * Determines if the react is for free.
     * @return true if free, otherwise false
     */
    boolean isReactForFree();

    /**
     * Gets the change in cost (positive or negative) to 'react'.
     * @return the change in cost to 'react'
     */
    float getChangeInCost();

    /**
     * Determines if the modifier also grants deployment to the target.
     * @return true or false
     */
    boolean isGrantedToDeployToTarget();

    /**
     * Gets the ability of the replacement permanent pilots.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    float getReplacementPermanentPilotAbility(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Determines if the specified card is prohibited from moving from the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @return true if card is prohibited from moving from the location, otherwise false
     */
    boolean prohibitedFromMovingFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove);

    /**
     * Determines if the specified card is prohibited from moving away from the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving away from the location, otherwise false
     */
    boolean prohibitedFromMovingAwayFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving from the affected location to the specified location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving from the location, otherwise false
     */
    boolean prohibitedFromMovingFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove, PhysicalCard toLocation);

    /**
     * Determines if the specified card is prohibited from moving to the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @return true if card is prohibited from moving to the location, otherwise false
     */
    boolean prohibitedFromMovingToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove);

    /**
     * Determines if this modifier causes the affected card to move free from the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true or false
     */
    boolean isMoveFreeFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    /**
     * Determines if this modifier causes the affected card to move free from a location to another location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true or false
     */
    boolean isMoveFreeFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Determines if this modifier causes the affected card to move free to the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true or false
     */
    boolean isMoveFreeToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    /**
     * Determines if this modifier allows shuttle from the specified location to another specified location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to shuttle from
     * @param toLocation the location to shuttle to
     * @return true or false
     */
    boolean isGrantedToShuttleFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation);


    float getUnmodifiableTransferCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard target);
    boolean isUnmodifiableTransferCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard target);

    boolean isImmuneToLandspeedRequirementModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard sourceOfModifier);

    /**
     * Gets the amount that the move cost is modified by.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the card to move
     * @return the amount to modify move cost
     */
    float getMoveCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    /**
     * Gets the amount that the move cost is modified by when moving from location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param fromLocation the location to move from
     * @return the amount to modify move cost
     */
    float getMoveCostFromLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard fromLocation);

    /**
     * Gets the amount that the move cost is modified by when moving from a location to another location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return the amount to modify move cost
     */
    float getMoveCostFromLocationToLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard fromLocation, PhysicalCard toLocation);

    /**
     * Gets the amount that the move cost is modified by when moving to location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param toLocation the location to move to
     * @return the amount to modify move cost
     */
    float getMoveCostToLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard toLocation);


    boolean isMovingTowardTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard destination);

    float getForceDrainModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);
    boolean cantForceDrainAtLocation(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    boolean hasCanceledForceDrainBonusesForPlayer(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId);
    boolean cantCancelForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerCanceling, String playerDraining);
    boolean cantModifyForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining);

    boolean cantModifyForceLossFromForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining);

    int getUnmodifiableForceDrainAmount(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    float getTotalForceGenerationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    float getAsteroidDestinyAtLocationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);
    float getAsteroidDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starship);
    float getCarbonFreezingDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);
    float getTrainingDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard jediTest);
    float getEpicEventDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard epicEvent);


    int getMinimumBattleDestinyDrawsModifier(GameState gameState, ModifiersQuerying modifiersQuerying);
    int getMaximumBattleDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    float getUnmodifiableAbilityRequiredToDrawBattleDestiny(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    float getBattleDestinyAtLocationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    float getDestinyWhenDrawnForDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    /**
     * Determines if the specified player's battle destiny draws may not be canceled by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToCancel the player to cancel battle destiny
     * @return true if battle destiny may not be canceled, otherwise false
     */
    boolean mayNotCancelBattleDestiny(String playerDrawing, String playerToCancel);

    /**
     * Determines if the specified player's battle destiny draws may not be modified by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @return true if battle destiny may not be modified, otherwise false
     */
    boolean mayNotModifyBattleDestiny(String playerDrawing, String playerToModify);

    /**
     * Determines if the specified player's battle destiny draws may not be reset by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToReset the player to reset battle destiny
     * @return true if battle destiny may not be reset, otherwise false
     */
    boolean mayNotResetBattleDestiny(String playerDrawing, String playerToReset);

    /**
     * Determines if the specified player's weapon destiny draws not be modified by the specified player for the specified
     * weapon used by the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerDrawing the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @param weaponCard the weapon card
     * @param permanentWeapon the permanent weapon
     * @param weaponUser the weapon user
     * @return true if weapon destiny may not be modified, otherwise false
     */
    boolean mayNotModifyWeaponDestiny(GameState gameState, ModifiersQuerying modifiersQuerying, String playerDrawing, String playerToModify, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponUser);

    /**
     * Determines if the specified player's weapon destiny draws not be canceled by the specified player for the specified
     * weapon used by the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerDrawing the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @param weaponCard the weapon card
     * @param permanentWeapon the permanent weapon
     * @param weaponUser the weapon user
     * @return true if weapon destiny may not be canceled, otherwise false
     */
    boolean mayNotCancelWeaponDestiny(GameState gameState, ModifiersQuerying modifiersQuerying, String playerDrawing, String playerToModify, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponUser);


    float getAbilityUsedForBattleDestinyModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    float getTotalAbilityUsedForBattleDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    float getAbilityRequiredToDrawBattleDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    int getNumDestinyDrawsToPowerOnlyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    int getNumDestinyDrawsToAttritionOnlyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    float getWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets);
    float getTotalWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets);
    float getFireWeaponCostModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    float getUnmodifiableFireWeaponCost(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    float getTotalTractorBeamDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard tractorBeam);


    /**
     * Determines if this modifier defines the printed fire weapon cost.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param weaponUser the card firing the weapon
     * @return true or false
     */
    boolean isDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser);

    /**
     * Gets the printed fire weapon cost when firing the weapon as defined by the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param weaponUser the card firing the weapon
     * @return the printed fire weapon cost
     */
    float getDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser);

    boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action);
    boolean isImmuneToCardModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, SwccgBuiltInCardBlueprint permanentWeapon);
    boolean isImmuneToCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle);

    boolean isMayBeCanceledByCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle);

    /**
     * Determines if the affected card may not be targeted by the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToTarget the card to target
     * @param targetedBy the card doing the targeting
     * @param targetedByPermanentWeapon the permanent weapon doing the targeting
     * @return true if may not be targeted by card, otherwise false
     */
    boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon);



    boolean prohibitedFromCarrying(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard playedCard, PhysicalCard target);
    boolean grantedToBeTargetedByCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card);

    boolean prohibitedFromPiloting(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starshipOrVehicle);


    /**
     * Determines if the player is prohibited from searching the specified card pile using the the specified game text action.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param playerId the player
     * @param cardPile the card pile
     * @param cardPileOwner the card pile owner
     * @param gameTextActionId the game text action id
     * @return true if prohibited, otherwise false
     */
    boolean isProhibitedFromSearchingCardPile(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId,
                                              Zone cardPile, String cardPileOwner, GameTextActionId gameTextActionId);

    int getNumDevicesAllowedToUse(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    int getNumWeaponsAllowedToUse(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);

    /**
     * Gets the maximum amount of Force loss.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return the maximum amount of Force loss
     */
    float getForceLossLimit(GameState gameState, ModifiersQuerying modifiersQuerying);

    /**
     * Gets the minimum amount that Force loss that can be reduced to.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return the minimum amount of Force loss
     */
    float getForceLossMinimum(GameState gameState, ModifiersQuerying modifiersQuerying);


    float getForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying);

    float getLightsaberCombatForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard winningCharacter);

    /**
     * Determines if Elis Helrot or Nabrun Leids may be not used to or from the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true if not allowed to be used, otherwise false
     */
    boolean mayNotUseElisHelrotOrNabrunLeidsAtLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location);

    PhysicalCard includesGameTextFrom(GameState gameState, ModifiersQuerying modifiersQuerying, Side side);
    float getAdditionCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable);
    int getMultiplicationCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable);
    int getPilotCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    int getAstromechCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard);
    float getUnmodifiableAbilityRequiredToControlLocation(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    float getSabaccTotalModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    int getNumDuelDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);
    int getNumLightsaberCombatDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying);

    float getEpicEventCalculationTotalModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard);

    String getPlayerToSelectCardTargetAtLocation(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard location);


    /**
     * Determines if the affected card is placed in Used Pile (instead of Lost Pile) when canceled by the specified player
     * with the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param canceledByPlayerId the player
     * @param canceledByCard the card
     * @return true if canceled card is placed in Used Pile, otherwise false
     */
    boolean isPlacedInUsedPileWhenCanceled(GameState gameState, ModifiersQuerying modifiersQuerying, String canceledByPlayerId, PhysicalCard canceledByCard);

    /**
     * Determines if the affected card has the specified political agenda.
     * @param agenda the political agenda
     * @return true or false
     */
    boolean hasAgenda(Agenda agenda);

    boolean mayNotCancelDestiny(String playerDrawing, String playerToModify);

    void setAffectFilter(Filter affectFilter);
}
