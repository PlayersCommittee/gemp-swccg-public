package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleAction;
import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The abstract class providing the common implementation for starships.
 */
public abstract class AbstractStarship extends AbstractDeployable {
    private float _power;
    private Float _armor;
    private Float _maneuver;
    private float _forfeit;
    private Float _hyperspeed;
    private int _pilotCapacity;
    private int _pilotOrPassengerCapacity;
    private int _passengerCapacity;
    private int _astromechCapacity;
    private int _vehicleCapacity;
    private Filter _vehicleCapacityFilter;
    private int _starfighterOrTIECapacity;
    private Filter _starfighterOrTIEFilter;
    private int _capitalStarshipCapacity;
    private Filter _capitalStarshipCapacityFilter;

    /**
     * Creates a blueprint for a starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractStarship(Side side, Float destiny, float deployCost, float power, Float armor, Float maneuver, Float hyperspeed, float forfeit, String title) {
        this(side, destiny, deployCost, power, armor, maneuver, hyperspeed, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a starship.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param power the power value
     * @param armor the armor value
     * @param maneuver the maneuver value
     * @param hyperspeed the hyperspeed value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractStarship(Side side, Float destiny, Float deployCost, float power, Float armor, Float maneuver, Float hyperspeed, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, null, deployCost, title, uniqueness);
        _power = power;
        _armor = armor;
        _maneuver = maneuver;
        _hyperspeed = hyperspeed;
        _forfeit = forfeit;
        _pilotCapacity = 0;
        _pilotOrPassengerCapacity = 0;
        _passengerCapacity = 0;
        _vehicleCapacity = 0;
        _vehicleCapacityFilter = Filters.none;
        _starfighterOrTIECapacity = 0;
        _starfighterOrTIEFilter = Filters.none;
        _capitalStarshipCapacity = 0;
        _capitalStarshipCapacityFilter = Filters.none;
        setCardCategory(CardCategory.STARSHIP);
        addCardType(CardType.STARSHIP);
        addIcon(Icon.STARSHIP);
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
    public boolean hasForfeitAttribute() {
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
     * Determines if has hyperspeed attribute
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasHyperspeedAttribute() {
        return _hyperspeed != null;
    }

    /**
     * Gets the hyperspeed.
     * @return the hyperspeed
     */
    @Override
    public final Float getHyperspeed() {
        return _hyperspeed;
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
     * Gets the pilot capacity.
     * @return the pilot capacity
     */
    @Override
    public final int getPilotCapacity() {
        return _pilotCapacity;
    }

    /**
     * Sets the pilot capacity.
     * @param capacity the pilot capacity
     */
    protected final void setPilotCapacity(int capacity) {
        _pilotCapacity = capacity;
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
     * Sets the pilot or passenger capacity.
     * @param capacity the pilot or passenger capacity
     */
    protected final void setPilotOrPassengerCapacity(int capacity) {
        _pilotOrPassengerCapacity = capacity;
    }

    /**
     * Gets the passenger capacity.
     * @return the passenger capacity.
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
    public final int getAstromechCapacity() {
        return _astromechCapacity;
    }

    /**
     * Sets the astromech capacity.
     * @param capacity the astromech capacity
     */
    protected final void setAstromechCapacity(int capacity) {
        _astromechCapacity = capacity;
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
     * Sets the vehicle capacity.
     * @param capacity the vehicle capacity
     */
    protected final void setVehicleCapacity(int capacity) {
        setVehicleCapacity(capacity, Filters.any);
    }

    /**
     * Sets the vehicle capacity that only accepts cards accepted by the filter.
     * @param capacity the vehicle capacity
     * @param filter the filter
     */
    protected final void setVehicleCapacity(int capacity, Filterable filter) {
        _vehicleCapacity = capacity;
        _vehicleCapacityFilter = Filters.and(filter);
    }

    /**
     * Gets the starfighter or TIE capacity.
     * @return the starfighter or TIE capacity
     */
    @Override
    public final int getStarfighterOrTIECapacity() {
        return _starfighterOrTIECapacity;
    }

    /**
     * Gets the filter for cards that can go in a starfighter or TIE capacity slot.
     * @return the starfighter or TIE capacity filter
     */
    @Override
    public final Filter getStarfighterOrTIECapacityFilter() {
        return _starfighterOrTIEFilter;
    }

    /**
     * Sets the starfighter capacity (and also allows squadrons).
     * @param capacity the starfighter (or squadron) capacity
     */
    protected final void setStarfighterCapacity(int capacity) {
        _starfighterOrTIECapacity = capacity;
        _starfighterOrTIEFilter = Filters.or(Filters.starfighter, Filters.and(Filters.squadron, Filters.any_model_type));
    }

    /**
     * Sets the starfighter capacity that only accepts starfighters accepted by the filter.
     * @param capacity the starfighter capacity
     * @param filter the filter
     */
    protected final void setStarfighterCapacity(int capacity, Filterable filter) {
        _starfighterOrTIECapacity = capacity;
        _starfighterOrTIEFilter = Filters.and(Filters.starfighter, filter);
    }

    /**
     * Sets the TIE capacity.
     * @param capacity the TIE capacity
     */
    protected final void setTIECapacity(int capacity) {
        _starfighterOrTIECapacity = capacity;
        _starfighterOrTIEFilter = Filters.TIE;
    }

    /**
     * Gets the capital starship capacity.
     * @return the capital starship capacity
     */
    @Override
    public int getCapitalStarshipCapacity() {
        return _capitalStarshipCapacity;
    }

    /**
     * Gets the filter for cards that can go in a capital starship capacity slot.
     * @return the capital starship capacity filter
     */
    @Override
    public Filter getCapitalStarshipCapacityFilter() {
        return _capitalStarshipCapacityFilter;
    }

    /**
     * Sets the capital starship capacity.
     * @param capacity the capital starship capacity
     */
    protected final void setCapitalStarshipCapacity(int capacity) {
        setCapitalStarshipCapacity(capacity, Filters.any);
    }

    /**
     * Sets the capital starship capacity that only accepts capital starships accepted by the filter.
     * @param capacity the capital starship capacity
     * @param filter the filter
     */
    protected final void setCapitalStarshipCapacity(int capacity, Filterable filter) {
        _capitalStarshipCapacity = capacity;
        _capitalStarshipCapacityFilter = Filters.and(Filters.capital_starship, filter);
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
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // If this is a steal and deploy, then temporarily change owner while determining where cards can deploy
        String originalCardOwner = self.getOwner();
        self.setOwner(playerId);
        String originalDeployWithCardOwner = cardToDeployWith != null ? cardToDeployWith.getOwner() : null;
        if (originalDeployWithCardOwner != null) {
            cardToDeployWith.setOwner(playerId);
        }

        if (!forFree) {
            forFree = isCardTypeAlwaysPlayedForFree() || gameState.getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
        }

        // Check for squadrons without a printed deploy cost, since they may not be deployed unless explicitly allowed
        boolean mayNotDeploySquadron = Filters.squadron.accepts(game, self) && self.getBlueprint().getDeployCost() == null && (deploymentOption == null || !deploymentOption.isAllowDeploymentOfSquadronWithoutDeployCost());
        if (mayNotDeploySquadron && modifiersQuerying.isSquadronAllowedToDeploy(gameState, self)) {
            mayNotDeploySquadron = false;
            deployTargetFilter = Filters.and(deployTargetFilter, Filters.grantedToDeployTo(self, reactActionOption));
        }

        if (!mayNotDeploySquadron && checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, null, reactActionOption)) {

            // Get the filter for where this starship can deploy (if it has a permanent pilot)
            Filter deployWithoutSeparatePilotTargetFilter = Filters.and(deployTargetFilter, getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, false, false));
            Filter deployWithSeparatePilotTargetFilter = Filters.none;
            Set<PhysicalCard> validPilotsFromHand = new HashSet<PhysicalCard>();

            // Check if explicit character to deploy simultaneously with was specified
            if (cardToDeployWith != null) {
                if (!cardToDeployWith.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, cardToDeployWith)
                        && reactActionOption == null
                        && ((Filters.piloted.accepts(game, self) && Filters.hasAvailablePassengerCapacity(cardToDeployWith).accepts(game, self)) || (getValidPilotFilter(playerId, game, self, true).accepts(game, cardToDeployWith) && Filters.hasAvailablePilotCapacity(cardToDeployWith).accepts(game, self)))
                        && cardToDeployWith.getBlueprint().mayDeploySimultaneouslyAsAttachedRequirements(playerId, game, cardToDeployWith)
                        && Filters.canSpot(game, self, Filters.and(deployTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, cardToDeployWithForFree, cardToDeployWithChangeInCost, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, null)))) {

                    // If they can be deployed simultaneously with starship, then include that filter.
                    deployWithSeparatePilotTargetFilter = getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, null);
                    playCardActions.add(new PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction(game, sourceCard, self, forFree, changeInCost, deployWithSeparatePilotTargetFilter, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption));
                }
            }
            else {
                // If no permanent pilot, then additional filtering for valid target is needed
                if (!game.getModifiersQuerying().hasPermanentPilot(game.getGameState(), self)) {

                    deployWithSeparatePilotTargetFilter = Filters.and(deployWithoutSeparatePilotTargetFilter);

                    // Check any pilots in hand (or that may be deployed as if from hand) that can be deployed simultaneously with starship.
                    List<PhysicalCard> cardsFromHand = new ArrayList<PhysicalCard>();
                    cardsFromHand.addAll(game.getGameState().getHand(playerId));
                    cardsFromHand.addAll(Filters.filter(game.getGameState().getAllStackedCards(), game, Filters.and(Filters.owner(playerId), Filters.canDeployAsIfFromHand)));
                    for (PhysicalCard cardFromHand : cardsFromHand) {
                        if (!cardFromHand.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, cardFromHand)
                                && (reactActionOption == null || reactActionOption.getDeployWithPilotOrDriverFilter() == null || reactActionOption.getDeployWithPilotOrDriverFilter().accepts(game, cardFromHand))
                                && getValidPilotFilter(playerId, game, self, true).accepts(game, cardFromHand) && Filters.hasAvailablePilotCapacity(cardFromHand).accepts(game, self)
                                && cardFromHand.getBlueprint().mayDeploySimultaneouslyAsAttachedRequirements(playerId, game, cardFromHand)
                                && Filters.canSpot(game, self, Filters.and(deployTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardFromHand, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, reactActionOption)))) {

                            // If they can be deployed simultaneously with starship, then include that filter.
                            validPilotsFromHand.add(cardFromHand);
                            deployWithSeparatePilotTargetFilter = Filters.or(deployWithSeparatePilotTargetFilter, getValidDeployTargetWithPilotOrPassengerFilter(playerId, game, self, sourceCard, forFree, changeInCost, cardFromHand, cardToDeployWithForFree, cardToDeployWithChangeInCost, deploymentRestrictionsOption, reactActionOption));
                        }
                    }
                    deployWithSeparatePilotTargetFilter = Filters.and(deployWithSeparatePilotTargetFilter, deployTargetFilter);

                    // If deploying as 'react' with required pilot or driver
                    if (reactActionOption != null && reactActionOption.getDeployWithPilotOrDriverFilter() != null) {
                        deployWithoutSeparatePilotTargetFilter = Filters.none;
                    } else {
                        deployWithoutSeparatePilotTargetFilter = Filters.and(deployWithoutSeparatePilotTargetFilter, Filters.or(Filters.locationStarshipMayDeployAsLanded(self, deploymentRestrictionsOption), Filters.starship));
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
     *
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
        // filter1: General starship rule
        Filter filter1 = Filters.or(Filters.system, Filters.asteroid_sector);
        // filter2: Additional landed starfighter rule
        Filter filter2 = Filters.locationStarshipMayDeployAsLanded(self, deploymentRestrictionsOption);
        // filter3: May be carried aboard starship as vehicle
        Filter filter3 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableVehicleCapacity(self));
        // filter4: May be carried aboard starship as starfighter or TIE
        Filter filter4 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableStarfighterOrTIECapacity(self));
        // filter5: May be carried aboard starship as capital starship
        Filter filter5 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableCapitalStarshipCapacity(self));
        // filter6: Additional cloud sector rule
        Filter filter6 = Filters.cloud_sector;

        // Return filter based on ship type, etc.
        Filter combinedFilter = filter1;

        boolean deploysLikeStarfighter = game.getModifiersQuerying().isDeploysAndMovesLikeStarfighter(game.getGameState(), self);

        if (Filters.starfighter.accepts(game.getGameState(), game.getModifiersQuerying(), self) || deploysLikeStarfighter)
            combinedFilter = Filters.or(combinedFilter, filter2);

        if (game.getModifiersQuerying().isVehicleSlotOfStarshipCompatible(game.getGameState(), self))
            combinedFilter = Filters.or(combinedFilter, filter3);

        if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(game.getGameState(), game.getModifiersQuerying(), self))
            combinedFilter = Filters.or(combinedFilter, filter4);

        if (Filters.capital_starship.accepts(game.getGameState(), game.getModifiersQuerying(), self))
            combinedFilter = Filters.or(combinedFilter, filter5);

        if (Filters.or(Filters.starfighter, Filters.squadron).accepts(game.getGameState(), game.getModifiersQuerying(), self) || deploysLikeStarfighter)
            combinedFilter = Filters.or(combinedFilter, filter6);

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
     * @param reactActionOption a 'react' action option, or null if not a 'react'   @return the filter
     */
    @Override
    public Filter getValidDeployTargetWithPilotOrPassengerFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, PhysicalCard character, boolean characterForFree, float characterChangeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, ReactActionOption reactActionOption) {
        Filter pilotTargetFilter = Filters.any;
        boolean spyPilot = Filters.spy.accepts(game, character);


        // By default, no 'deploymentRestrictionOption's are passed in. However, we want to honor the ability
        // to deploy without presence or force icons on some starships. For those cases, build a DeploymentRestrictionOption
        // on the fly (or update the one that was passed in (if any))
        DeploymentRestrictionsOption updatedDeployementRestrictionOption = deploymentRestrictionsOption;
        for (Modifier modifier: game.getModifiersQuerying().getModifiersAffecting(game.getGameState(), self)) {
            if (modifier.getModifierType() == ModifierType.MAY_DEPLOY_WITHOUT_PRESENCE_OR_FORCE_ICONS) {
                if (updatedDeployementRestrictionOption == null) {
                    updatedDeployementRestrictionOption = DeploymentRestrictionsOption.evenWithoutPresenceOrForceIcons();
                } else {
                    updatedDeployementRestrictionOption.setEvenWithoutPresenceOrForceIcons(true);
                }
            }
        }


        // Check if pilot can deploy to this card (regardless of its location), if not then need to check for locations that pilot is allowed to deploy to
        if (!character.getBlueprint().getValidSimultaneouslyDeployingStarshipOrVehicleToAnyLocationFilter(playerId, game, character).accepts(game.getGameState(), game.getModifiersQuerying(), self)) {
            pilotTargetFilter = Filters.locationAndCardsAtLocation(character.getBlueprint().getValidLocationForSimultaneouslyDeployingAsPilotOrPassengerFilter(playerId, game, character, sourceCard, updatedDeployementRestrictionOption, reactActionOption));
        }

        pilotTargetFilter = Filters.and(pilotTargetFilter, Filters.canUseForceToDeploySimultaneouslyToTarget(sourceCard, self, forFree, changeInCost, character, characterForFree, characterChangeInCost, reactActionOption));

        return Filters.and(getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, updatedDeployementRestrictionOption, null, reactActionOption, true, spyPilot), pilotTargetFilter);
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
        // filter1: General starship rule
        Filter filter1 = Filters.or(Filters.system, Filters.asteroid_sector);
        // filter2: Exterior site rule
        Filter filter2 = Filters.exterior_site;
        // filter3: May be carried aboard starship as vehicle
        Filter filter3 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableVehicleCapacity(self));
        // filter4: May be carried aboard starship as starfighter or TIE
        Filter filter4 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableStarfighterOrTIECapacity(self));
        // filter5: May be carried aboard starship as capital starship
        Filter filter5 = Filters.and(Filters.owner(playerId), Filters.starship, Filters.hasAvailableCapitalStarshipCapacity(self));
        // filter6: Additional cloud sector rule
        Filter filter6 = Filters.cloud_sector;
        // filter7: Additional Death Star II sector rule
        Filter filter7 = Filters.Death_Star_II_sector;

        // Return filter based on ship type, etc.
        Filter combinedFilter = filter1;

        boolean movesLikeStarfighter = game.getModifiersQuerying().isDeploysAndMovesLikeStarfighter(game.getGameState(), self);

        if (Filters.starfighter.accepts(game.getGameState(), game.getModifiersQuerying(), self) || movesLikeStarfighter)
            combinedFilter = Filters.or(combinedFilter, filter2);

        if (game.getModifiersQuerying().isVehicleSlotOfStarshipCompatible(game.getGameState(), self))
            combinedFilter = Filters.or(combinedFilter, filter3);

        if (Filters.or(Filters.starfighter, Filters.squadron, Filters.TIE).accepts(game.getGameState(), game.getModifiersQuerying(), self))
            combinedFilter = Filters.or(combinedFilter, filter4);

        if (Filters.capital_starship.accepts(game.getGameState(), game.getModifiersQuerying(), self))
            combinedFilter = Filters.or(combinedFilter, filter5);

        if (Filters.or(Filters.starfighter, Filters.squadron).accepts(game.getGameState(), game.getModifiersQuerying(), self) || movesLikeStarfighter)
            combinedFilter = Filters.or(combinedFilter, filter6);

        if (Filters.starfighter.accepts(game.getGameState(), game.getModifiersQuerying(), self))
            combinedFilter = Filters.or(combinedFilter, filter7);

        return combinedFilter;
    }

    /**
     * Gets a filter for the cards that are valid to be pilots of the specified card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forDeployment true if checking for deployment, otherwise false
     * @return the filter
     */
    @Override
    public final Filter getValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forDeployment) {
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
}
