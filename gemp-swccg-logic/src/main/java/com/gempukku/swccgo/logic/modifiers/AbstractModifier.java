package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


// This abstract class represents the default
// behavior for the methods can be called on a
// modifier to see "if" and "how much" a particular
// modifier affects a card or the game.
//
public abstract class AbstractModifier implements Modifier {
    private Integer _permCardId;
    protected String _playerId; // tells which player if affected by the modifier, if it only affects one player
    private String _locationSidePlayer; // tells which players side of location the modifier is from
    private boolean _fromPermanentPilot;
    private boolean _fromPermanentAstromech;
    private boolean _cumulative;
    private boolean _persistent;
    private boolean _skipSettingNotRemovedOnRestoreToNormal;
    private boolean _notRemovedOnRestoreToNormal;
    private Condition _expireCondition;
    private boolean _whileInactiveInPlay;
    private boolean _evenIfUnpilotedInPlay;
    private String _text;
    private Filter _affectFilter;
    private Condition _condition;
    private ModifierType _effect;

    protected AbstractModifier(PhysicalCard source, String text, Filterable affectFilter, ModifierType effect) {
        this(source, text, affectFilter, null, effect, false);
    }

    protected AbstractModifier(PhysicalCard source, String text, Filterable affectFilter, ModifierType effect, boolean cumulative) {
        this(source, text, affectFilter, null, effect, cumulative);
    }

    protected AbstractModifier(PhysicalCard source, String text, Filterable affectFilter, Condition condition, ModifierType effect) {
        this(source, text, affectFilter, condition, effect, false);
    }

    protected AbstractModifier(PhysicalCard source, String text, Filterable affectFilter, Condition condition, ModifierType effect, boolean cumulative) {
        _permCardId = source != null ? source.getPermanentCardId() : null;
        _locationSidePlayer = null;
        _text = text;
        _affectFilter = (affectFilter != null) ? Filters.and(affectFilter) : null;
        _condition = condition;
        _effect = effect;
        _cumulative = cumulative;
    }

    @Override
    public Condition getCondition() {
            return _condition;
    }

    @Override
    public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public PhysicalCard getSource(GameState gameState) {
        return gameState.findCardByPermanentId(_permCardId);
    }

    @Override
    public String getLocationSidePlayer() {
        return _locationSidePlayer;
    }

    @Override
    public void setLocationSidePlayer(String player) {
        _locationSidePlayer = player;
    }

    @Override
    public void setFromPermanentPilot(boolean value) {
        _fromPermanentPilot = value;
    }

    @Override
    public boolean isFromPermanentPilot() {
        return _fromPermanentPilot;
    }

    @Override
    public void setFromPermanentAstromech(boolean value) {
        _fromPermanentAstromech = value;
    }

    @Override
    public boolean isFromPermanentAstromech() {
        return _fromPermanentAstromech;
    }

    @Override
    public boolean isCumulative() {
        return _cumulative;
    }

    @Override
    public void setPersistent(boolean value) {
        _persistent = value;
    }

    @Override
    public boolean isPersistent() {
        return _persistent;
    }

    @Override
    public void skipSettingNotRemovedOnRestoreToNormal() {
        _skipSettingNotRemovedOnRestoreToNormal = true;
    }

    @Override
    public void setNotRemovedOnRestoreToNormal(boolean value) {
        _notRemovedOnRestoreToNormal = !_skipSettingNotRemovedOnRestoreToNormal && value;
    }

    @Override
    public boolean isNotRemovedOnRestoreToNormal() {
        return _notRemovedOnRestoreToNormal;
    }

    @Override
    public void setWhileInactiveInPlay(boolean value) {
        _whileInactiveInPlay = value;
    }

    @Override
    public boolean isWhileInactiveInPlay() {
        return _whileInactiveInPlay;
    }

    @Override
    public void setEvenIfUnpilotedInPlay(boolean value) {
        _evenIfUnpilotedInPlay = value;
    }

    @Override
    public boolean isEvenIfUnpilotedInPlay() {
        return _evenIfUnpilotedInPlay;
    }

    /**
     * Sets the condition that, when fulfilled, causes the modifier to be automatically removed.
     * @param expireCondition the condition, or null
     */
    @Override
    public void setExpireCondition(Condition expireCondition) {
        _expireCondition = expireCondition;
    }

    /**
     * Gets the condition that, when fulfilled, causes the modifier to be automatically removed.
     * @return the condition, or null
     */
    @Override
    public Condition getExpireCondition() {
        return _expireCondition;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return _text;
    }

    @Override
    public ModifierType getModifierType() {
        return _effect;
    }

    /**
     * Determines if this modifier is targeting the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the card
     * @return true or false
     */
    @Override
    public boolean isTargetingCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return affectsCard(gameState, modifiersQuerying, card); // TODO: Need to update this per modifier, but this is default
    }

    @Override
    public boolean affectsCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        if (_affectFilter == null)
            return false;

        if (modifiersQuerying.isExcludedFromBeingAffected(this, physicalCard))
            return false;

        if (getSource(gameState) != null && modifiersQuerying.isEffectsFromModifierToCardSuspended(gameState, getSource(gameState), physicalCard))
            return false;

        return _affectFilter.accepts(gameState, modifiersQuerying, physicalCard);
    }

    /**
     * Determines if this modifier cancels game text on the side of the location facing the specified player.
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean isCanceledTextForPlayer(String playerId) {
        return false;
    }

    /**
     * Gets the text to show on the User Interface for the action created because of this modifier.
     * @return the text
     */
    @Override
    public String getActionText() {
        return null;
    }

    /**
     * Gets the filter for cards that the source card can have 'react'.
     * @return the filter
     */
    @Override
    public Filter getCardToReactFilter() {
        return null;
    }

    /**
     * Gets the target filter.
     * @return the filter
     */
    @Override
    public Filter getTargetFilter() {
        return null;
    }

    /**
     * Gets the pilot or driver filter.
     * @return the filter
     */
    @Override
    public Filter getPilotOrDriverFilter() {
        return null;
    }

    /**
     * Gets the filter for cards restricted from increasing the power of the affected card.
     * @return the filter
     */
    @Override
    public Filter getCardsRestrictedFromIncreasingPowerFilter() {
        return null;
    }

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the target.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param targetCard the target card
     * @return true or false
     */
    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return false;
    }

    /**
     * Determines if this modifier is in effect for the affected card when this specified permanent weapon is the target.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param targetPermanentWeapon the permanent weapon of target card
     * @return true or false
     */
    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint targetPermanentWeapon) {
        return false;
    }

    /**
     * Determines if this modifier affects the specified player.
     * @param playerId the player
     * @return true or false
     */
    @Override
    public boolean isForPlayer(String playerId) {
        return _playerId == null || _playerId.equals(playerId);
    }

    /**
     * Gets which player this modifier affects if this modifier only affect a specified player, otherwise null.
     * @return the player, or null
     */
    @Override
    public String getForPlayer() {
        return _playerId;
    }

    /**
     * Determines if this modifier affects the current draw destiny effect.
     * @param gameState the game state
     * @return true or false
     */
    @Override
    public boolean isForTopDrawDestinyEffect(GameState gameState) {
        return false;
    }

    /**
     * Determines if this modifier affects the current blow away effect.
     * @param gameState the game state
     * @return true or false
     */
    @Override
    public boolean isForTopBlowAwayEffect(GameState gameState) {
        return false;
    }

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the pilot.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param pilot the pilot card
     * @return true or false
     */
    @Override
    public boolean isAffectedPilot(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot) {
        return false;
    }

    /**
     * Determines if this modifier is in effect for the affected card when this specified card is the driver.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param driver the driver card
     * @return true or false
     */
    @Override
    public boolean isAffectedDriver(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard driver) {
        return false;
    }

    /**
     * Determines if this modifier is always in effect.
     * @return true or false
     */
    @Override
    public boolean isAlwaysInEffect() {
        return false;
    }

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param permanentWeapon the affected permanent weapon
     * @return the value
     */
    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, SwccgBuiltInCardBlueprint permanentWeapon) {
        return 0;
    }

    /**
     * Gets the value of the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card1 the affected card
     * @param card2 the other card
     * @return the value
     */
    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card1, PhysicalCard card2) {
        return 0;
    }

    /**
     * Gets the value of the multiplier modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    @Override
    public float getMultiplierValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 1;
    }

    @Override
    public boolean isActionSource(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard actionSource) {
        return false;
    }

    /**
     * Determines if this modifier is in effect for the specified variable.
     * @param variable the variable
     * @return true or false
     */
    @Override
    public boolean isAffectedVariable(Variable variable) {
        return false;
    }

    /**
     * Gets the minimum Force Pile size to be able to use opponent's Force.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    @Override
    public int getMinForcePileSizeToUseOpponentsForce(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 1;
    }

    /**
     * Determines if sites are prevented from deploying between the specified sites.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param site1 a site
     * @param site2 a site
     * @return true or false
     */
    @Override
    public boolean mayNotDeploySiteBetweenSites(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard site1, PhysicalCard site2) {
        return false;
    }

    @Override
    public ModifyGameTextType getModifyGameTextType(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return null;
    }

    /**
     * Gets the number of destiny to draw.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    @Override
    public int getNumToDraw(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    /**
     * Gets the number of destiny to choose.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    @Override
    public int getNumToChoose(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    @Override
    public boolean isKeywordRemoved(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword) {
        return false;
    }

    @Override
    public boolean hasKeyword(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Keyword keyword) {
        return false;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public int getIconCountModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Icon icon) {
        return 0;
    }

    @Override
    public float getPowerModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public int getPowerMultiplierModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 1;
    }

    @Override
    public float getPoliticsModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiablePolitics(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getFerocityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableFerocity(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getAttritionModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getTotalPowerModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getTotalAbilityModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getUnmodifiableAbility(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getAbilityModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableForfeit(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getForfeitModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getDestinyDrawFromSourceCardModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard sourceOfDestinyDraw) {
        return 0;
    }

    @Override
    public float getImmunityToAttritionLessThanModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getImmunityToAttritionOfExactlyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getImmunityToAttritionChangedModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public boolean isExceptForceIconOrPresenceRequirement() {
        return false;
    }

    @Override
    public float getDeployCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getMaximumToReduceDeployCostBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return Float.MAX_VALUE;
    }

    @Override
    public float getDeployCostToTargetModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToDeploy, PhysicalCard target) {
        return 0;
    }

    @Override
    public float getDeployCostWithPilotModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard pilot) {
        return 0;
    }

    @Override
    public float getUnmodifiableDeployCost(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public boolean isUnmodifiableDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard cardToDeploy) {
        return false;
    }

    @Override
    public float getUnmodifiableDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard cardToDeploy, PhysicalCard target) {
        return 0;
    }

    /**
     * Determines if the this modifier defines the printed deploy cost of the affected card when deploying to the target card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true or false
     */
    @Override
    public boolean isDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    /**
     * Gets the printed deploy cost when deploying the affected card to the target card as defined by the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return the printed deploy cost
     */
    @Override
    public float getDefinedDeployCostToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return 0;
    }

    /**
     * Determines if this modifier causes the affected card to deploy free to the target card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true or false
     */
    @Override
    public boolean isDeployFreeToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    @Override
    public boolean isUnmodifiableTransferCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard target) {
        return false;
    }

    @Override
    public float getUnmodifiableTransferCostToTarget(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard target) {
        return 0;
    }

    /**
     * Gets the amount that the move cost is modified by.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the card to move
     * @return the amount to modify move cost
     */
    @Override
    public float getMoveCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    /**
     * Gets the amount that the move cost is modified by when moving from location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param fromLocation the location to move from
     * @return the amount to modify move cost
     */
    @Override
    public float getMoveCostFromLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard fromLocation) {
        return 0;
    }

    /**
     * Gets the amount that the move cost is modified by when moving from a location to another location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return the amount to modify move cost
     */
    @Override
    public float getMoveCostFromLocationToLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return 0;
    }

    /**
     * Gets the amount that the move cost is modified by when moving to location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param physicalCard the move to move
     * @param toLocation the location to move to
     * @return the amount to modify move cost
     */
    @Override
    public float getMoveCostToLocationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard toLocation) {
        return 0;
    }


    @Override
    public boolean isMovingTowardTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard destination) {
        return false;
    }

    @Override
    public float getDockingBayTransitFromCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    @Override
    public boolean isPlayCardOption(PlayCardOption playCardOption) {
        return true;
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getBaseFerocityDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    @Override
    public int getNumFerocityDestinyDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    @Override
    public float getHyperspeedModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableHyperspeed(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getManeuverModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableManeuverModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getArmorModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableArmorModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getDefenseValueModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableDefenseValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getForceDrainModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getTotalForceGenerationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getAsteroidDestinyAtLocationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getAsteroidDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starship) {
        return 0;
    }

    @Override
    public float getEpicEventDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard epicEvent) {
        return 0;
    }

    @Override
    public float getCarbonFreezingDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getTrainingDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard jediTest) {
        return 0;
    }

    @Override
    public int getMinimumBattleDestinyDrawsModifier(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public int getMaximumBattleDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getTotalAbilityUsedForBattleDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getAbilityRequiredToDrawBattleDestinyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getUnmodifiableAbilityRequiredToDrawBattleDestiny(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getBattleDestinyAtLocationModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return 0;
    }

    @Override
    public float getDestinyWhenDrawnForDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    /**
     * Determines if the specified player's battle destiny draws may not be canceled by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToCancel the player to cancel battle destiny
     * @return true if battle destiny may not be canceled, otherwise false
     */
    @Override
    public boolean mayNotCancelBattleDestiny(String playerDrawing, String playerToCancel) {
        return false;
    }

    /**
     * Determines if the specified player's battle destiny draws may not be modified by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToModify the player to modify battle destiny
     * @return true if battle destiny may not be modified, otherwise false
     */
    @Override
    public boolean mayNotModifyBattleDestiny(String playerDrawing, String playerToModify) {
        return false;
    }

    /**
     * Determines if the specified player's battle destiny draws may not be reset by the specified player.
     * @param playerDrawing the player drawing battle destiny
     * @param playerToReset the player to reset battle destiny
     * @return true if battle destiny may not be reset, otherwise false
     */
    @Override
    public boolean mayNotResetBattleDestiny(String playerDrawing, String playerToReset) {
        return false;
    }

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
    @Override
    public boolean mayNotModifyWeaponDestiny(GameState gameState, ModifiersQuerying modifiersQuerying, String playerDrawing, String playerToModify, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponUser) {
        return false;
    }

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
    @Override
    public boolean mayNotCancelWeaponDestiny(GameState gameState, ModifiersQuerying modifiersQuerying, String playerDrawing, String playerToModify, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponUser) {
        return false;
    }

    @Override
    public int getNumDestinyDrawsToPowerOnlyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public int getNumDestinyDrawsToAttritionOnlyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getTotalTractorBeamDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard tractorBeam) {
        return 0;
    }

    @Override
    public float getWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets) {
        return 0;
    }

    @Override
    public float getTotalWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets) {
        return 0;
    }

    @Override
    public float getFireWeaponCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableFireWeaponCost(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    /**
     * Determines if this modifier defines the printed fire weapon cost.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param weaponUser the card firing the weapon
     * @return true or false
     */
    @Override
    public boolean isDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser) {
        return false;
    }

    /**
     * Gets the printed fire weapon cost when firing the weapon as defined by the modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param weapon the weapon card
     * @return the printed fire weapon cost
     */
    @Override
    public float getDefinedFireWeaponCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weapon) {
        return 0;
    }

    @Override
    public float getAdditionCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable) {
        return 0;
    }

    @Override
    public int getMultiplicationCalculationModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Variable variable) {
        return 1;
    }

    @Override
    public boolean prohibitedFromCarrying(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard character, PhysicalCard cardToBeCarried) {
        return false;
    }

    @Override
    public boolean prohibitedFromPiloting(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starshipOrVehicle) {
        return false;
    }

    @Override
    public final boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag) {
        return hasFlagActive(gameState, modifiersQuerying, modifierFlag, null);
    }

    @Override
    public boolean hasFlagActive(GameState gameState, ModifiersQuerying modifiersQuerying, ModifierFlag modifierFlag, String playerId) {
        return false;
    }

    /**
     * Determines if the affected card may deploy to the location without presence or Force icons.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true if card can be deployed to the location without presence or Force icons, otherwise false
     */
    @Override
    public boolean mayDeployToLocationWithoutPresenceOrForceIcons(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return false;
    }

    @Override
    public PhysicalCard includesGameTextFrom(GameState gameState, ModifiersQuerying modifiersQuerying, Side side) {
        return null;
    }

    /**
     * Determines if Elis Helrot or Nabrun Leids may be not used to or from the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true if not allowed to be used, otherwise false
     */
    @Override
    public boolean mayNotUseElisHelrotOrNabrunLeidsAtLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return false;
    }

    /**
     * Gets the maximum amount of Force loss.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return the maximum amount of Force loss
     */
    @Override
    public float getForceLossLimit(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Float.MAX_VALUE;
    }

    /**
     * Gets the minimum amount that Force loss that can be reduced to.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return the minimum amount of Force loss
     */
    @Override
    public float  getForceLossMinimum(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getLightsaberCombatForceLossModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard winningCharacter) {
        return 0;
    }

    @Override
    public int getPilotCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public int getAstromechCapacityModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public boolean isImmuneToCardModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card, SwccgBuiltInCardBlueprint permanentWeapon) {
        return false;
    }

    @Override
    public boolean isImmuneToCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle) {
        return false;
    }

    @Override
    public boolean isMayBeCanceledByCardTitleModifier(GameState gameState, ModifiersQuerying modifiersQuerying, String cardTitle) {
        return false;
    }

    /**
     * Determines if the affected card may not be targeted by the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToTarget the card to target
     * @param targetedBy the card doing the targeting
     * @param targetedByPermanentWeapon the permanent weapon doing the targeting
     * @return true if may not be targeted by card, otherwise false
     */
    @Override
    public boolean mayNotBeTargetedBy(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToTarget, PhysicalCard targetedBy, SwccgBuiltInCardBlueprint targetedByPermanentWeapon) {
        return false;
    }

    @Override
    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action) {
        return true;
    }

    @Override
    public boolean grantedToBeTargetedByCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return false;
    }

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
    @Override
    public boolean isProhibitedFromSearchingCardPile(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId,
                                                     Zone cardPile, String cardPileOwner, GameTextActionId gameTextActionId) {
        return false;
    }

    @Override
    public int getNumDevicesAllowedToUse(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 1;
    }

    @Override
    public int getNumWeaponsAllowedToUse(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 1;
    }

    /**
     * Determines if the affected cards is prohibited from existing at (deploying or moving to) the specified targeted.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param target the target card
     * @return true if card may not exist at target, otherwise false
     */
    @Override
    public boolean isProhibitedFromExistingAt(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    @Override
    public boolean grantedToDeployToAsLanded(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    @Override
    public boolean grantedToDeployToDagobahTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @return true if card is prohibited from moving from the location, otherwise false
     */
    @Override
    public boolean prohibitedFromMovingFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove) {
        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving away from the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving away from the location, otherwise false
     */
    @Override
    public boolean prohibitedFromMovingAwayFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving from the affected location to the specified location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @param toLocation the location to move to
     * @return true if card is prohibited from moving from the location, otherwise false
     */
    @Override
    public boolean prohibitedFromMovingFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove, PhysicalCard toLocation) {
        return false;
    }

    /**
     * Determines if the specified card is prohibited from moving to the affected location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param cardToMove the card to move
     * @return true if card is prohibited from moving to the location, otherwise false
     */
    @Override
    public boolean prohibitedFromMovingToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardToMove) {
        return false;
    }

    /**
     * Determines if this modifier causes the affected card to move free from the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true or false
     */
    @Override
    public boolean isMoveFreeFromLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return false;
    }

    /**
     * Determines if this modifier causes the affected card to move free from a location to another location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to move from
     * @param toLocation the location to move to
     * @return true or false
     */
    @Override
    public boolean isMoveFreeFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return false;
    }

    /**
     * Determines if this modifier causes the affected card to move free to the location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param location the location
     * @return true or false
     */
    @Override
    public boolean isMoveFreeToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return false;
    }

    /**
     * Determines if this modifier allows shuttle from the specified location to another specified location.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param fromLocation the location to shuttle from
     * @param toLocation the location to shuttle to
     * @return true or false
     */
    @Override
    public boolean isGrantedToShuttleFromLocationToLocation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard fromLocation, PhysicalCard toLocation) {
        return false;
    }

    @Override
    public boolean canMoveAsReactToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return false;
    }

    /**
     * Determines if the react is for free.
     * @return true if free, otherwise false
     */
    @Override
    public boolean isReactForFree() {
        return false;
    }

    /**
     * Gets the change in cost (positive or negative) to 'react'.
     * @return the change in cost to 'react'
     */
    @Override
    public float getChangeInCost() {
        return 0;
    }

    /**
     * Determines if the modifier also grants deployment to the target.
     * @return true or false
     */
    @Override
    public boolean isGrantedToDeployToTarget() {
        return false;
    }

    /**
     * Gets the ability of the replacement permanent pilots.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the affected card
     * @return the value
     */
    @Override
    public float getReplacementPermanentPilotAbility(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return 0;
    }

    @Override
    public float getAbilityUsedForBattleDestinyModifier(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public float getUnmodifiableAbilityRequiredToControlLocation(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public float getEpicEventCalculationTotalModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return 0;
    }

    @Override
    public boolean cantForceDrainAtLocation(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return false;
    }

    @Override
    public boolean hasCanceledForceDrainBonusesForPlayer(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        return false;
    }

    @Override
    public boolean cantCancelForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerCanceling, String playerDraining) {
        return false;
    }

    @Override
    public boolean cantModifyForceDrain(GameState gameState, ModifiersQuerying modifiersQuerying, String playerModifying, String playerDraining) {
        return false;
    }

    @Override
    public float getSabaccTotalModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public int getNumDuelDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return 0;
    }

    @Override
    public String getPlayerToSelectCardTargetAtLocation(GameState gameState, ModifiersQuerying modifiersLogic, PhysicalCard location) {
        return null;
    }

    @Override
    public boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier) {
        return false;
    }

    @Override
    public boolean isImmuneToLandspeedRequirementModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard sourceOfModifier) {
        return false;
    }

    /**
     * Determines if the affected card is placed in Used Pile (instead of Lost Pile) when canceled by the specified player
     * with the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param canceledByPlayerId the player
     * @param canceledByCard the card
     * @return true if canceled card is placed in Used Pile, otherwise false
     */
    @Override
    public boolean isPlacedInUsedPileWhenCanceled(GameState gameState, ModifiersQuerying modifiersQuerying, String canceledByPlayerId, PhysicalCard canceledByCard) {
        return false;
    }

    /**
     * Determines if the affected card has the specified political agenda.
     * @param agenda the political agenda
     * @return true or false
     */
    @Override
    public boolean hasAgenda(Agenda agenda) {
        return false;
    }
}