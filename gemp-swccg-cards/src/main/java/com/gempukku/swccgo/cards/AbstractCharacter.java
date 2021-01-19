package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayCharacterAction;
import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayCardAction;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class providing the common implementation for characters.
 */
public abstract class AbstractCharacter extends AbstractDeployable {
    private Float _power;
    private float _ability;
    private float _politics;
    private float _landspeed;
    private Float _forfeit;
    private Float _armor;
    private Float _maneuver;
    private Species _species;
    private boolean _onlyDeploysAsUndercoverSpy;
    private boolean _onlyDeploysAsCapturedPrisoner;
    private boolean _mayDeployAsUndercoverSpy;

    /**
     * Creates a blueprint for a character.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param ability the ability value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCharacter(Side side, Float destiny, Float deployCost, Float power, float ability, Float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, null, deployCost, title, uniqueness);
        _power = power;
        _ability = ability;
        _landspeed = 1;
        _forfeit = forfeit;
        _species = null;
        setCardCategory(CardCategory.CHARACTER);
    }

    /**
     * Gets a filter for the cards that are matching starships for this.
     * @return the filter
     */
    @Override
    public final Filter getMatchingStarshipFilter() {
        return _matchingStarshipFilter;
    }

    /**
     * Sets the matching starship filter.
     * @param filter the filter
     */
    protected final void setMatchingStarshipFilter(Filter filter) {
        _matchingStarshipFilter = filter;
    }

    /**
     * Gets a filter for the cards that are vehicle starships for this.
     * @return the filter
     */
    @Override
    public final Filter getMatchingVehicleFilter() {
        return _matchingVehicleFilter;
    }

    /**
     * Sets the matching vehicle filter.
     * @param filter the filter
     */
    protected final void setMatchingVehicleFilter(Filter filter) {
        _matchingVehicleFilter = filter;
    }

    /**
     * Gets a filter for the cards that are matching weapons for this.
     * @return the filter
     */
    @Override
    public final Filter getMatchingWeaponFilter() {
        return _matchingWeaponFilter;
    }

    /**
     * Sets the matching weapon filter.
     * @param filter the filter
     */
    protected final void setMatchingWeaponFilter(Filter filter) {
        _matchingWeaponFilter = filter;
    }

    /**
     * Gets the title of the matching system for this. Used for Operatives to determine matching system.
     * @return the system name, or null
     */
    @Override
    public String getMatchingSystem() {
        return _matchingSystem;
    }

    /**
     * Gets the title of the matching system. Used for Operatives to set matching system.
     * @param system the system name
     */
    protected final void setMatchingSystem(String system) {
        _matchingSystem = system;
    }

    /**
     * Determines if this has a power attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasPowerAttribute() {
        return true;
    }

    /**
     * Gets the power.
     * @return the power
     */
    @Override
    public final Float getPower() {
        return _power;
    }

    /**
     * Determines if this has an ability attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasAbilityAttribute() {
        return true;
    }

    /**
     * Gets the ability.
     * @return the ability
     */
    @Override
    public final Float getAbility() {
        return _ability;
    }

    /**
     * Determines if this has a politics attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasPoliticsAttribute() {
        return true;
    }

    /**
     * Gets the politics value.
     * @return the politics
     */
    @Override
    public final float getPolitics() {
        return _politics;
    }

    /**
     * Sets the politics value.
     * @param politics the politics value
     */
    protected final void setPolitics(float politics) {
        _politics = politics;
    }

    /**
     * Determines if this has a landspeed attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasLandspeedAttribute() {
        return true;
    }

    /**
     * Gets the landspeed.
     * @return the landspeed
     */
    @Override
    public final Float getLandspeed() {
        return _landspeed;
    }

    /**
     * Determines if this has a forfeit attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasForfeitAttribute() {
        return true;
    }

    /**
     * Gets the forfeit value.
     * @return the forfeit value
     */
    @Override
    public final Float getForfeit() {
        return _forfeit;
    }

    /**
     * Determines if this has an armor attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasArmorAttribute() {
        return true;
    }

    /**
     * Gets the armor.
     * @return the armor
     */
    @Override
    public final Float getArmor() {
        return _armor;
    }

    /**
     * Sets the armor value.
     * @param armor the armor value
     */
    protected final void setArmor(float armor) {
        _armor = armor;
    }

    /**
     * Determines if this has a maneuver attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasManeuverAttribute() {
        return _maneuver != null && _maneuver > 0;
    }

    /**
     * Gets the maneuver.
     * @return the maneuver
     */
    @Override
    public final Float getManeuver() {
        return _maneuver;
    }

    /**
     * Sets the maneuver value.
     * @param maneuver the maneuver value
     */
    protected final void setManeuver(float maneuver) {
        _maneuver = maneuver;
    }

    /**
     * Determines if this has a species attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasSpeciesAttribute() {
        return true;
    }

    /**
     * Gets the species, or null.
     * @return the species, or null if no species
     */
    @Override
    public final Species getSpecies() {
        return _species;
    }

    /**
     * Sets the species.
     * @param species the species
     */
    protected final void setSpecies(Species species) {
        _species = species;
    }

    /**
     * Determines if has immunity to attrition attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasImmunityToAttritionAttribute() {
        return true;
    }

    /**
     * Sets that the character deploys only as an Undercover spy.
     * @param value true or false
     */
    protected final void setDeploysAsUndercoverSpy(boolean value) {
        _onlyDeploysAsUndercoverSpy = value;
    }

    /**
     * Sets that the character deploys only as an captured prisoner.
     * @param value true or false
     */
    protected final void setDeploysAsCapturedPrisoner(boolean value) {
        _onlyDeploysAsCapturedPrisoner = value;
    }

    /**
     * Determines if the card only deploys as an undercover spy.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    @Override
    public final boolean isOnlyDeploysAsUndercoverSpy(SwccgGame game, PhysicalCard self) {
        return _onlyDeploysAsUndercoverSpy;
    }

    /**
     * Determines if the card only deploys as an captured prisoner.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    @Override
    public final boolean isOnlyDeploysAsEscortedCaptive(SwccgGame game, PhysicalCard self) {
        return _onlyDeploysAsCapturedPrisoner;
    }

    /**
     * Sets that the character may deploy as an Undercover spy.
     * @param value true or false
     */
    protected final void setMayDeployAsUndercoverSpy(boolean value) {
        _mayDeployAsUndercoverSpy = value;
    }

    /**
     * Determines if the card may deploy as an undercover spy.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    @Override
    public final boolean mayDeployAsUndercoverSpy(SwccgGame game, PhysicalCard self) {
        return _mayDeployAsUndercoverSpy;
    }

    /**
     * Gets the valid deploy target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can deploy to.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param isSimDeployAttached true if during simultaneous deployment of pilot/weapon, otherwise false
     * @param ignorePresenceOrForceIcons true if this deployment ignores presence or Force icons requirement
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @return the deploy to target filter based on the card type, subtype, etc.
     */
    @Override
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        // Card is being simultaneously deployed as pilot/driver, so skip normal filtering for where card can deploy, since
        // starship/vehicle is not yet in play and checking that card can deploy aboard that starship/vehicle is already
        // checked elsewhere.
        if (isSimDeployAttached) {
            return Filters.any;
        }

        // Get locations that character can deploy to
        // Do not need to check for presence or Force icons when deploying as captive
        Filter combinedFilter;
        if (deployAsCaptiveOption != null && deployAsCaptiveOption.getCaptureOption() == CaptureOption.IMPRISONMENT) {
            if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self)) {
                return Filters.none;
            }
            combinedFilter = Filters.prison;
        }
        else if (deployAsCaptiveOption != null && deployAsCaptiveOption.getCaptureOption() == CaptureOption.SEIZE) {
            if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self)) {
                return Filters.none;
            }
            combinedFilter = Filters.canEscortCaptive(self);
        }
        else if (deployAsCaptiveOption != null && deployAsCaptiveOption.getCaptureOption() == CaptureOption.LEAVE_UNATTENDED) {
            if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self)) {
                return Filters.none;
            }
            combinedFilter = Filters.site;
        }
        else if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self)) {
            combinedFilter = Filters.site;
        }
        else {
            combinedFilter = Filters.or(Filters.site, Filters.and(Filters.owner(playerId), Filters.or(Filters.hasAvailablePilotCapacity(self), Filters.hasAvailablePassengerCapacity(self))));
            if (!ignorePresenceOrForceIcons && (deploymentRestrictionsOption == null || (!deploymentRestrictionsOption.isEvenWithoutPresenceOrForceIcons() && !deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions()))) {
                combinedFilter = Filters.and(combinedFilter, Filters.sufficientPresenceOrForceIconsToDeployTo(self));
            }
        }

        return combinedFilter;
    }

    /**
     * Gets the valid move target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can move to.
     *
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the move to target filter based on the card type, subtype, etc.
     */
    @Override
    protected Filter getValidMoveTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self) {
        // Get locations that character can be placed
        Filter filter1 = Filters.site;
        // Get starships or vehicles that character can be pilot or passenger of
        Filter filter2 = Filters.and(Filters.your(self), Filters.or(Filters.hasAvailablePilotCapacity(self), Filters.hasAvailablePassengerCapacity(self)));

        return Filters.or(filter1, filter2);
    }

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
    @Override
    public final List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        List<PlayCardAction> playCardActions = new ArrayList<PlayCardAction>();

        // If this is a steal and deploy, then temporarily change owner while determining where cards can deploy
        String originalCardOwner = self.getOwner();
        self.setOwner(playerId);
        String originalDeployWithCardOwner = cardToDeployWith != null ? cardToDeployWith.getOwner() : null;
        if (originalDeployWithCardOwner != null) {
            cardToDeployWith.setOwner(playerId);
        }

        if (!forFree) {
            forFree = isCardTypeAlwaysPlayedForFree() || game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
        }

        if (checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, null, reactActionOption)) {

            // Check if explicit starship/vehicle to deploy simultaneously with was specified
            if (cardToDeployWith != null) {
                if (Filters.or(Filters.starship, Filters.vehicle).accepts(game, cardToDeployWith)) {
                    SwccgCardBlueprint cardToDeployWithBlueprint = cardToDeployWith.getBlueprint();
                    if (!isOnlyDeploysAsUndercoverSpy(game, self)
                            && (deployAsCaptiveOption == null || deployAsCaptiveOption.getCaptureOption() == null)
                            && reactActionOption == null
                            && ((Filters.piloted.accepts(game, cardToDeployWith) && Filters.hasAvailablePassengerCapacity(self).accepts(game, cardToDeployWith)) || (cardToDeployWithBlueprint.getValidPilotFilter(playerId, game, cardToDeployWith, true).accepts(game, self) && Filters.hasAvailablePilotCapacity(self).accepts(game, cardToDeployWith)))
                            && mayDeploySimultaneouslyAsAttachedRequirements(playerId, game, self)
                            && Filters.canSpot(game, self, Filters.and(deployTargetFilter, cardToDeployWithBlueprint.getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, cardToDeployWith, sourceCard, cardToDeployWithForFree, cardToDeployWithChangeInCost, self, forFree, changeInCost, deploymentRestrictionsOption, null)))) {

                        // If they can be deployed simultaneously with starship, then include that filter.
                        Filter deployWithSeparatePilotTargetFilter = cardToDeployWithBlueprint.getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, cardToDeployWith, sourceCard, cardToDeployWithForFree, cardToDeployWithChangeInCost, self, forFree, changeInCost, deploymentRestrictionsOption, null);
                        playCardActions.add(new PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction(game, sourceCard, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployWithSeparatePilotTargetFilter, self, forFree, changeInCost, deploymentRestrictionsOption));
                    }
                }
            }
            else {
                Filter completeTargetFilter = Filters.and(deployTargetFilter, getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, false, false));

                // Check that a valid target to deploy to can be found
                if (Filters.canSpot(game, self, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter)) {
                    playCardActions.add(new PlayCharacterAction(game, sourceCard, self, forFree, changeInCost, deployAsCaptiveOption, reactActionOption, completeTargetFilter));
                }
            }
        }

        // If this is a steal and deploy, then change owner back after determining where cards can deploy
        self.setOwner(originalCardOwner);
        if (originalDeployWithCardOwner != null) {
            cardToDeployWith.setOwner(originalDeployWithCardOwner);
        }

        return playCardActions;
    }

    /**
     * This method is for checking which starships or vehicles the character can deploy to as pilot simultaneously regardless
     * of location and deploy cost. It is to be used by other methods (that factor in the valid locations and deploy costs)
     * to figure out which starship/vehicles and pilots/drivers can deploy simultaneously.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public final Filter getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        if (game.getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(game.getGameState(), self))
            return Filters.any;
        else
            return getGameTextValidStarshipOrVehicleSimultaneousDeployTargetFilter(playerId, game, self);
    }

    /**
     * This method is overridden by individual cards to special the filter for valid starship/vehicle deploy targets.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidStarshipOrVehicleSimultaneousDeployTargetFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.none;
    }

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
    @Override
    public final Filter getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption) {
        Filter filter = Filters.and(Filters.location, getValidDeployTargetFilter(playerId, game, self, sourceCard, null, true, 0, deploymentRestrictionsOption, null, reactActionOption, true, true));
        if (deploymentRestrictionsOption == null || (!deploymentRestrictionsOption.isEvenWithoutPresenceOrForceIcons() && !deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions())) {
            filter = Filters.and(filter, Filters.sufficientPresenceOrForceIconsToDeployTo(self));
        }
        return filter;
    }
}
