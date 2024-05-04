package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleAction;
import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract class providing the common implementation for vehicles.
 */
public abstract class AbstractVehicle extends AbstractDeployable {
    private float _power;
    private Float _armor;
    private Float _maneuver;
    private Float _landspeed;
    private float _forfeit;
    protected int _pilotCapacity;
    protected int _pilotOrPassengerCapacity;
    private int _passengerCapacity;
    private int _vehicleCapacity;
    private Filter _vehicleCapacityFilter;
    private boolean _isInteriorSiteVehicle;

    /**
     * Creates a blueprint for a vehicle.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param landspeed the landspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
￼    * @param rarity the rarity
     */
    protected AbstractVehicle(Side side, Float destiny, float deployCost, float power, Float armor, Float maneuver, Float landspeed, float forfeit, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, null, deployCost, title, uniqueness, expansionSet, rarity);
        _power = power;
        _armor = armor;
        _maneuver = maneuver;
        _landspeed = landspeed;
        _forfeit = forfeit;
        _pilotCapacity = 0;
        _pilotOrPassengerCapacity = 0;
        _passengerCapacity = 0;
        _vehicleCapacity = 0;
        _vehicleCapacityFilter = Filters.none;
        setCardCategory(CardCategory.VEHICLE);
        addCardType(CardType.VEHICLE);
        addIcon(Icon.VEHICLE);
    }

    /**
     * Gets a filter for the cards that are matching pilots (or drivers) for this.
     * @return the filter
     */
    @Override
    public final Filter getMatchingPilotFilter() {
        return _matchingPilotFilter;
    }

    /**
     * Sets the matching pilot filter.
     * @param filter the filter
     */
    protected final void setMatchingPilotFilter(Filter filter) {
        _matchingPilotFilter = filter;
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
     * Determines if this has a maneuver attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasManeuverAttribute() {
        return true;
    }

    /**
     * Gets the maneuver.
     * @return the maneuver.
     */
    @Override
    public final Float getManeuver() {
        return _maneuver;
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
     * Determines if has immunity to attrition attribute
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasImmunityToAttritionAttribute() {
        return true;
    }

    /**
     * Gets the pilot capacity.
     * @return the pilot capacity
     */
    @Override
    public final int getPilotCapacity() {
        return _pilotCapacity;
    }

    /**
     * Gets the pilot or passenger capacity.
     * @return the pilot or passenger capacity
     */
    @Override
    public final int getPilotOrPassengerCapacity() {
        return _pilotOrPassengerCapacity;
    }

    /**
     * Gets the passenger capacity.
     * @return the passenger capacity
     */
    @Override
    public final int getPassengerCapacity() {
        return _passengerCapacity;
    }

    /**
     * Sets the passenger capacity.
     * @param capacity the passenger capacity
     */
    protected final void setPassengerCapacity(int capacity) {
        _passengerCapacity = capacity;
    }

    /**
     * Gets the astromech capacity.
     * @return the astromech capacity
     */
    @Override
    public int getAstromechCapacity() {
        return 0;
    }

    /**
     * Gets the vehicle capacity.
     * @return the vehicle capacity
     */
    @Override
    public final int getVehicleCapacity() {
        return _vehicleCapacity;
    }

    /**
     * Gets the filter for cards that can go in a vehicle vehicle slot.
     * @return the vehicle capacity filter
     */
    @Override
    public final Filter getVehicleCapacityFilter() {
        return _vehicleCapacityFilter;
    }

    /**
     * Sets the vehicle capacity that only accepts cards accepted by the filter.
     * @param capacity the vehicle capacity
     * @param filter the filter
     */
    protected final void setVehicleCapacity(int capacity, Filter filter) {
        _vehicleCapacity = capacity;
        _vehicleCapacityFilter = filter;
    }

    /**
     * Sets the vehicles as in interior site vehicle.
     * @param interiorSiteVehicle true if deploys and moves to interior sites, otherwise false
     */
    protected final void setInteriorSiteVehicle(boolean interiorSiteVehicle) {
        _isInteriorSiteVehicle = interiorSiteVehicle;
    }

    /**
     * Determines if this is a vehicle that deploys and moves to interior sites (instead of exterior sites).
     * @return true if deploys and moves to interior sites, otherwise false
     */
    protected final boolean isInteriorSiteVehicle() {
        return _isInteriorSiteVehicle;
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

            // Get the filter for where this vehicle can deploy (if it has a permanent pilot or is considered always piloted)
            Filter deployWithoutSeparatePilotTargetFilter = Filters.and(deployTargetFilter, getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, false, false));
            Filter deployWithSeparatePilotTargetFilter = Filters.none;
            Set<PhysicalCard> validPilotsFromHand = new HashSet<PhysicalCard>();

            // Check if explicit pilot/driver to deploy simultaneously with was specified
            if (cardToDeployWith != null) {
                if (!cardToDeployWith.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, cardToDeployWith)
                        && reactActionOption == null
                        && ((Filters.piloted.accepts(game, self) && Filters.hasAvailablePassengerCapacity(cardToDeployWith).accepts(game, self)) || (getValidPilotFilter(playerId, game, self, true).accepts(game, cardToDeployWith) && Filters.hasAvailablePilotCapacity(cardToDeployWith).accepts(game, self)))
                        && cardToDeployWith.getBlueprint().mayDeploySimultaneouslyAsAttachedRequirements(playerId, game, cardToDeployWith)
                        && Filters.canSpot(game, self, Filters.and(deployTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, null)))) {

                    // If they can be deployed simultaneously with starship, then include that filter.
                    deployWithSeparatePilotTargetFilter = getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, null);
                    playCardActions.add(new PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction(game, sourceCard, self, forFree, changeInCost, deployWithSeparatePilotTargetFilter, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption));
                }
            }
            else {
                // If unpiloted, then additional filtering for valid target is needed
                if (!game.getModifiersQuerying().hasPermanentPilot(game.getGameState(), self)
                        && (reactActionOption != null
                        || Filters.deploysLikeStarfighterAtCloudSectors.accepts(game, self))) {
                    Filter extraTargetFilter = reactActionOption != null ? Filters.any : Filters.cloud_sector;

                    deployWithSeparatePilotTargetFilter = Filters.and(deployWithoutSeparatePilotTargetFilter);

                    // Check any pilots in hand (or that may be deployed as if from hand) that can be deployed simultaneously with vehicle.
                    List<PhysicalCard> cardsFromHand = new ArrayList<PhysicalCard>();
                    cardsFromHand.addAll(game.getGameState().getHand(playerId));
                    cardsFromHand.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game, Filters.and(Filters.owner(playerId), Filters.canDeployAsIfFromHand)));
                    for (PhysicalCard cardFromHand : cardsFromHand) {
                        if (!cardFromHand.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, cardFromHand)
                                && (reactActionOption == null || reactActionOption.getDeployWithPilotOrDriverFilter() == null || reactActionOption.getDeployWithPilotOrDriverFilter().accepts(game, cardFromHand))
                                && getValidPilotFilter(playerId, game, self, true).accepts(game, cardFromHand) && Filters.hasAvailablePilotCapacity(cardFromHand).accepts(game, self)
                                && cardFromHand.getBlueprint().mayDeploySimultaneouslyAsAttachedRequirements(playerId, game, cardFromHand)
                                && Filters.canSpot(game, self, Filters.and(extraTargetFilter, deployTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardFromHand, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, reactActionOption)))) {

                            // If they can be deployed simultaneously with vehicle, then include that filter.
                            validPilotsFromHand.add(cardFromHand);
                            deployWithSeparatePilotTargetFilter = Filters.or(deployWithSeparatePilotTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardFromHand, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, reactActionOption));
                        }
                    }
                    deployWithSeparatePilotTargetFilter = Filters.and(deployWithSeparatePilotTargetFilter, deployTargetFilter);

                    // If deploying as 'react' with required pilot or driver
                    if (reactActionOption != null && reactActionOption.getDeployWithPilotOrDriverFilter() != null) {
                        deployWithoutSeparatePilotTargetFilter = Filters.none;
                    } else {
                        deployWithoutSeparatePilotTargetFilter = Filters.and(Filters.not(Filters.cloud_sector), deployWithoutSeparatePilotTargetFilter);
                    }
                }

                // Check that a valid target to deploy to can be found
                if (!validPilotsFromHand.isEmpty() || Filters.canSpot(game, self, deployWithoutSeparatePilotTargetFilter)) {
                    playCardActions.add(new PlayStarshipOrVehicleAction(game, sourceCard, self, forFree, changeInCost, deploymentRestrictionsOption, reactActionOption, deployWithoutSeparatePilotTargetFilter, deployWithSeparatePilotTargetFilter, validPilotsFromHand));
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
    protected final Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        // filter1: General vehicle rule
        Filter filter1 = isInteriorSiteVehicle() ? Filters.interior_site : Filters.exterior_site;
        // filter2: Cloud sector rule
        Filter filter2 = Filters.cloud_sector;
        // filter3: May be carried aboard in cargo hold
        Filter filter3 = Filters.and(Filters.owner(playerId), Filters.or(Filters.starship, Filters.vehicle), Filters.hasAvailableVehicleCapacity(self));

        // Return filter based on vehicle type, etc.
        Filter combinedFilter = Filters.or(filter1, filter3);

        if (game.getModifiersQuerying().isDeploysLikeStarfighterAtCloudSectors(game.getGameState(), self))
            combinedFilter = Filters.or(combinedFilter, filter2);

        if (!ignorePresenceOrForceIcons && (deploymentRestrictionsOption == null || (!deploymentRestrictionsOption.isEvenWithoutPresenceOrForceIcons() && !deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions())))
            combinedFilter = Filters.and(combinedFilter, Filters.sufficientPresenceOrForceIconsToDeployTo(self));

        return combinedFilter;
    }

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
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the filter
     */
    public Filter getValidDeployTargetWithPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, PhysicalCard character, boolean characterForFree, float characterChangeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption) {
        Filter characterTargetFilter = Filters.any;
        boolean spyPilot = !Filters.piloted.accepts(game, self) && Filters.spy.accepts(game, character);

        // Check if character can deploy to this card (regardless of its location), if not then need to check for locations that character is allowed to deploy to
        if (!character.getBlueprint().getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(playerId, game, character).accepts(game, self)) {
            characterTargetFilter = Filters.locationAndCardsAtLocation(character.getBlueprint().getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(playerId, game, character, sourceCard, deploymentRestrictionsOption, reactActionOption));
        }

        characterTargetFilter = Filters.and(characterTargetFilter, Filters.canUseForceToDeploySimultaneouslyToTarget(sourceCard, self, forFree, changeInCost, character, characterForFree, characterChangeInCost, reactActionOption));

        return Filters.and(getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, deploymentRestrictionsOption, null, reactActionOption, true, spyPilot), characterTargetFilter);
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
    protected final Filter getValidMoveTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self) {
        // filter1: General vehicle rule
        Filter filter1 = isInteriorSiteVehicle() ? Filters.interior_site : Filters.exterior_site;
        // filter2: Cloud sector rule
        Filter filter2 = Filters.cloud_sector;
        // filter3: May be carried aboard in cargo hold
        Filter filter3 = Filters.and(Filters.owner(playerId), Filters.hasAvailableVehicleCapacity(self));

        // Return filter based on vehicle type, etc.
        Filter combinedFilter = Filters.or(filter1, filter3);

        if (game.getModifiersQuerying().isMovesLikeStarfighterAtCloudSectors(game.getGameState(), self))
            combinedFilter = Filters.or(combinedFilter, filter2);

        return combinedFilter;
    }

    /**
     * Gets a filter for the cards that are valid to be pilots (or drivers) of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public Filter getValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        Filter filter =  Filters.and(Filters.pilot, Filters.notProhibitedFromHavingAtTarget(self), Filters.notProhibitedFromPiloting(self), getGameTextValidPilotFilter(playerId, game, self));
        if (forDeployment) {
            filter = Filters.and(filter, Filters.notProhibitedFromHavingDeployedTo(self));
        }
        return filter;
    }

    /**
     * This method is overridden by individual cards to provide a filter for valid pilots.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.any;
    }

    /**
     * Gets a filter for the cards that are valid to be passengers of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public Filter getValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
        Filter filter =  Filters.and(Filters.notProhibitedFromHavingAtTarget(self), getGameTextValidPassengerFilter(playerId, game, self));
        if (forDeployment) {
            filter = Filters.and(filter, Filters.notProhibitedFromHavingDeployedTo(self));
        }
        return filter;
    }

    /**
     * This method is overridden by individual cards to provide a filter for valid pilots.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.any;
    }
}
