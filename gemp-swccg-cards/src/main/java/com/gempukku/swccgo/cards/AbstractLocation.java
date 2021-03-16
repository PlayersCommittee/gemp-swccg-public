package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;

/**
 * The abstract class providing the common implementation for location cards.
 */
public abstract class AbstractLocation extends AbstractSwccgCardBlueprint {
    private String _locationDsText;
    private String _locationLsText;
    private Set<String> _mayNotBePartOfSystem = new HashSet<String>();
    private Set<SpecialRule> _specialRulesInEffectHere = new HashSet<SpecialRule>();

    /**
     * Creates a blueprint for a location card.
     * @param side the side of the Force
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractLocation(Side side, String title, Uniqueness uniqueness) {
        super(side, 0f, title, uniqueness);
        setCardCategory(CardCategory.LOCATION);
        addCardType(CardType.LOCATION);
    }

    @Override
    public Float getDeployCost() {
        return 0f;
    }

    @Override
    public final String getLocationDarkSideGameText() {
        return _locationDsText;
    }

    /**
     * Sets the Dark side game text of the location card.
     * @param text the Dark side game text
     */
    protected final void setLocationDarkSideGameText(String text) {
        _locationDsText = text;
    }

    @Override
    public final String getLocationLightSideGameText() {
        return _locationLsText;
    }

    /**
     * Sets the Light side game text of the location card.
     * @param text the Light side game text
     */
    protected final void setLocationLightSideGameText(String text) {
        _locationLsText = text;
    }

    @Override
    public boolean mayNotBePartOfSystem(String system) {
        return _mayNotBePartOfSystem.contains(system);
    }

    /**
     * Add system names that the generic location may not be a part of.
     * @param systems the system names
     */
    protected void addMayNotBePartOfSystem(String... systems) {
        _mayNotBePartOfSystem.addAll(Arrays.asList(systems));
    }

    @Override
    public boolean isSpecialRuleInEffectHere(SpecialRule rule, PhysicalCard self) {
        return self.getZone() == Zone.LOCATIONS && !self.isBlownAway() && _specialRulesInEffectHere.contains(rule);
    }

    /**
     * Add special rules that are in effect for this location.
     * @param rules the special rules
     */
    protected void addSpecialRulesInEffectHere(SpecialRule... rules) {
        _specialRulesInEffectHere.addAll(Arrays.asList(rules));
    }

    @Override
    public final boolean isCardTypeDeployed() {
        return true;
    }

    @Override
    public Persona getRelatedStarshipOrVehiclePersona() {
        return null;
    }

    /**
     * Determines if the card can be played.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param playCardOption the play card option, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if card can be played, otherwise false
     */
    @Override
    protected boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)
                && reactActionOption == null
                && !game.getModifiersQuerying().isUniquenessOnTableLimitReached(game.getGameState(), self)
                && checkPlayRequirementsByCheckingGameText(playerId, game, self, deploymentRestrictionsOption);
    }

    /**
     * Determines if the deploy requirements from the card's game text are met (or are ignored).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @return true if requirements met or ignored, otherwise false
     */
    protected boolean checkPlayRequirementsByCheckingGameText(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption) {
        return (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isIgnoreGameTextDeploymentRestrictions())
                || game.getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(game.getGameState(), self)
                || checkGameTextDeployRequirements(playerId, game, self);
    }

    /**
     * Determines if the card can be played during the current phase.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card can be played during the current phase, otherwise false
     */
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY);
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
    public List<PlayCardAction> getPlayCardActions(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        PlayCardAction action = getPlayCardAction(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
        if (action != null)
            return Collections.singletonList(action);
        else
            return Collections.emptyList();
    }

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
    @Override
    public PlayCardAction getPlayCardAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        if (!checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, null, reactActionOption))
            return null;

        List<LocationPlacement> locationPlacements = game.getGameState().getLocationPlacement(game, self, null, specialLocationConditions);
        if (locationPlacements == null || locationPlacements.isEmpty()) {
            return null;
        }

        return new PlayLocationAction(sourceCard, self, locationPlacements);
    }

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
    @Override
    public PlayCardAction getPlayLocationToSystemAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, String system, Filter specialLocationConditions) {
        if (!checkPlayRequirements(playerId, game, self, null, null, null))
            return null;

        List<LocationPlacement> locationPlacements = game.getGameState().getLocationPlacement(game, self, system, specialLocationConditions);
        if (locationPlacements == null || locationPlacements.isEmpty()) {
            return null;
        }

        return new PlayLocationAction(sourceCard, self, locationPlacements);
    }

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public final List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelActions(playerId, game, self);

        // Play card
        if (self.getZone() != Zone.STACKED && canPlayCardDuringCurrentPhase(playerId, game, self)) {
            List<PlayCardAction> playCardActions = getPlayCardActions(playerId, game, self, self, true, 0, null, null, null, null, null, false, 0, Filters.any, null);
            if (playCardActions != null) {
                actions.addAll(playCardActions);
            }
        }

        // If location is not in play, then skip the other actions.
        if (!self.getZone().isInPlay()) {
            return actions;
        }

        // Initiate Force drain
        if (GameConditions.canInitiateForceDrainAtLocation(game, playerId, self)) {
            actions.add(new InitiateForceDrainAction(playerId, self));
        }

        // Initiate attack creature
        if (GameConditions.canInitiateAttackCreatureAtLocation(playerId, game, self)) {
            actions.add(new InitiateAttackCreatureAction(playerId, self));
        }

        // Initiate battle
        if (GameConditions.canInitiateBattleAtLocation(playerId, game, self)) {
            actions.add(new InitiateBattleAction(playerId, self));
        }

        // Docking bay transit
        if (GameConditions.canDockingBayTransitFromLocation(playerId, game, self)) {
            actions.add(new DockingBayTransitAction(playerId, game, self, false));
        }

        // Search party
        if (GameConditions.canFormSearchPartyAtLocation(game, playerId, self)) {
            actions.add(new SearchPartyAction(playerId, self));
        }

        // Release unattended 'frozen' captive
        if (GameConditions.canReleaseUnattendedFrozenCaptiveAtLocation(game, playerId, self)) {
            actions.add(new ReleaseUnattendedFrozenCaptiveAction(playerId, self));
        }

        // Bluff rules (stack 'bluff card')
        if (GameConditions.canStackBluffCardAtLocation(playerId, game, self))
            actions.add(new StackBluffCardAction(playerId, self));

        // Bluff rules (flip 'bluff card')
        if (GameConditions.canFlipBluffCardAtLocation(playerId, game, self))
            actions.add(new FlipBluffCardAction(playerId, self));

        // If location if blown away, then skip the other actions.
        if (self.isBlownAway())
            return actions;

        // Move location (only for mobile systems)
        List<Action> moveCardActions = getMoveCardActions(playerId, game, self);
        if (moveCardActions != null)
            actions.addAll(moveCardActions);

        // If location has canceled game text, then no more actions.
        if (game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
            return actions;
        }

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                List<TopLevelGameTextAction> darkSidePhaseActions = getGameTextDarkSideTopLevelActions(playerOnDarkSideOfLocation, game, self, self.getCardId());
                if (darkSidePhaseActions != null)
                    actions.addAll(darkSidePhaseActions);
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                List<TopLevelGameTextAction> lightSidePhaseActions = getGameTextLightSideTopLevelActions(playerOnLightSideOfLocation, game, self, self.getCardId());
                if (lightSidePhaseActions != null)
                    actions.addAll(lightSidePhaseActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
                if (locationExpandedDSToMyDS != null) {
                    List<TopLevelGameTextAction> expandedDarkSidePhaseActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideTopLevelActions(playerOnDarkSideOfLocation, game, self, locationExpandedDSToMyDS.getCardId());
                    if (expandedDarkSidePhaseActions != null)
                        actions.addAll(expandedDarkSidePhaseActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
                if (locationExpandedLSToMyLS != null) {
                    List<TopLevelGameTextAction> expandedLightSidePhaseActions = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideTopLevelActions(playerOnLightSideOfLocation, game, self, locationExpandedLSToMyLS.getCardId());
                    if (expandedLightSidePhaseActions != null)
                        actions.addAll(expandedLightSidePhaseActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
                if (locationExpandedDSToMyLS != null) {
                    List<TopLevelGameTextAction> expandedDarkSidePhaseActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideTopLevelActions(playerOnLightSideOfLocation, game, self, locationExpandedDSToMyLS.getCardId());
                    if (expandedDarkSidePhaseActions != null)
                        actions.addAll(expandedDarkSidePhaseActions);
                }
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
                if (locationExpandedLSToMyDS != null) {
                    List<TopLevelGameTextAction> expandedLightSidePhaseActions = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideTopLevelActions(playerOnDarkSideOfLocation, game, self, locationExpandedLSToMyDS.getCardId());
                    if (expandedLightSidePhaseActions != null)
                        actions.addAll(expandedLightSidePhaseActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets actions involving card movement that can be performed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    protected List<Action> getMoveCardActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (Filters.mobile_system.accepts(game, self)) {
            Action moveUsingHyperspeedAction = getMoveUsingHyperspeedAction(playerId, game, self, false, false, false, false, false, Filters.any);
            if (moveUsingHyperspeedAction != null) {
                return Collections.singletonList(moveUsingHyperspeedAction);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public final List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return Collections.emptyList();
    }

    @Override
    public final List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return Collections.emptyList();
    }

    @Override
    public final List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // If location is not in play, or blown away, or has canceled game text, then no actions.
        if (!self.getZone().isInPlay() || self.isBlownAway() || game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
            return actions;
        }

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraDarkSideActions = getGameTextDarkSideRequiredBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, self.getCardId());
            if (extraDarkSideActions != null)
                actions.addAll(extraDarkSideActions);
        }

        if (!isLightSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraLightSideActions = getGameTextLightSideRequiredBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, self.getCardId());
            if (extraLightSideActions != null)
                actions.addAll(extraLightSideActions);
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
            if (locationExpandedDSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideRequiredBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, locationExpandedDSToMyDS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
            if (locationExpandedLSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideTriggers = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideRequiredBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, locationExpandedLSToMyLS.getCardId());
                if (expandedLightSideTriggers != null)
                    actions.addAll(expandedLightSideTriggers);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
            if (locationExpandedDSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideRequiredBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, locationExpandedDSToMyLS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
            if (locationExpandedLSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideTriggers = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideRequiredBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, locationExpandedLSToMyDS.getCardId());
                if (expandedLightSideTriggers != null)
                    actions.addAll(expandedLightSideTriggers);
            }
        }

        return actions;
    }

    @Override
    public final List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // If location is not in play, or blown away, or has canceled game text, then no actions.
        if (!self.getZone().isInPlay() || self.isBlownAway() || game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
            return actions;
        }

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                List<OptionalGameTextTriggerAction> extraDarkSideActions = getGameTextDarkSideOptionalBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, self.getCardId());
                if (extraDarkSideActions != null)
                    actions.addAll(extraDarkSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                List<OptionalGameTextTriggerAction> extraLightSideActions = getGameTextLightSideOptionalBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, self.getCardId());
                if (extraLightSideActions != null)
                    actions.addAll(extraLightSideActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
                if (locationExpandedDSToMyDS != null) {
                    List<OptionalGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideOptionalBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, locationExpandedDSToMyDS.getCardId());
                    if (expandedDarkSideActions != null)
                        actions.addAll(expandedDarkSideActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
                if (locationExpandedLSToMyLS != null) {
                    List<OptionalGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideOptionalBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, locationExpandedLSToMyLS.getCardId());
                    if (expandedLightSideActions != null)
                        actions.addAll(expandedLightSideActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
                if (locationExpandedDSToMyLS != null) {
                    List<OptionalGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideOptionalBeforeTriggers(playerOnLightSideOfLocation, game, effect, self, locationExpandedDSToMyLS.getCardId());
                    if (expandedDarkSideActions != null)
                        actions.addAll(expandedDarkSideActions);
                }
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
                if (locationExpandedLSToMyDS != null) {
                    List<OptionalGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideOptionalBeforeTriggers(playerOnDarkSideOfLocation, game, effect, self, locationExpandedLSToMyDS.getCardId());
                    if (expandedLightSideActions != null)
                        actions.addAll(expandedLightSideActions);
                }
            }
        }

        return actions;
    }

    @Override
    public final List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // If location is not in play, or blown away, or has canceled game text, then no actions.
        if (!self.getZone().isInPlay() || self.isBlownAway() || game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
            return actions;
        }

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraDarkSideActions = getGameTextDarkSideRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, self.getCardId());
            if (extraDarkSideActions != null)
                actions.addAll(extraDarkSideActions);
        }

        if (!isLightSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraLightSideActions = getGameTextLightSideRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, self.getCardId());
            if (extraLightSideActions != null)
                actions.addAll(extraLightSideActions);
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
            if (locationExpandedDSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedDSToMyDS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
            if (locationExpandedLSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedLSToMyLS.getCardId());
                if (expandedLightSideActions != null)
                    actions.addAll(expandedLightSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
            if (locationExpandedDSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedDSToMyLS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
            if (locationExpandedLSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedLSToMyDS.getCardId());
                if (expandedLightSideActions != null)
                    actions.addAll(expandedLightSideActions);
            }
        }

        return actions;
    }

    @Override
    public final List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // If location is not in play, or blown away, or has canceled game text, then no actions.
        if (!self.getZone().isInPlay() || self.isBlownAway() || game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
            return actions;
        }

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                List<OptionalGameTextTriggerAction> extraDarkSideActions = getGameTextDarkSideOptionalAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, self.getCardId());
                if (extraDarkSideActions != null)
                    actions.addAll(extraDarkSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                List<OptionalGameTextTriggerAction> extraLightSideActions = getGameTextLightSideOptionalAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, self.getCardId());
                if (extraLightSideActions != null)
                    actions.addAll(extraLightSideActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
                if (locationExpandedDSToMyDS != null) {
                    List<OptionalGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideOptionalAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedDSToMyDS.getCardId());
                    if (expandedDarkSideActions != null)
                        actions.addAll(expandedDarkSideActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
                if (locationExpandedLSToMyLS != null) {
                    List<OptionalGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideOptionalAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedLSToMyLS.getCardId());
                    if (expandedLightSideActions != null)
                        actions.addAll(expandedLightSideActions);
                }
            }
        }

        if (!isLightSideTextCanceled) {
            if (playerOnLightSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
                if (locationExpandedDSToMyLS != null) {
                    List<OptionalGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideOptionalAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedDSToMyLS.getCardId());
                    if (expandedDarkSideActions != null)
                        actions.addAll(expandedDarkSideActions);
                }
            }
        }

        if (!isDarkSideTextCanceled) {
            if (playerOnDarkSideOfLocation.equals(playerId)) {
                // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
                PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
                if (locationExpandedLSToMyDS != null) {
                    List<OptionalGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideOptionalAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedLSToMyDS.getCardId());
                    if (expandedLightSideActions != null)
                        actions.addAll(expandedLightSideActions);
                }
            }
        }

        // Check for 'react' actions
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, game.getOpponent(playerId))
                || TriggerConditions.battleInitiated(game, effectResult, game.getOpponent(playerId))) {

            // Deploy other cards as 'react'
            List<TriggerAction> deployOtherCardsAsReactActions = getDeployOtherCardsAsReactAction(playerId, game, self);
            if (deployOtherCardsAsReactActions != null) {
                for (TriggerAction deployOtherCardsAsReactAction : deployOtherCardsAsReactActions) {
                    actions.add(deployOtherCardsAsReactAction);
                }
            }

            // Move other cards as 'react'
            TriggerAction moveOtherCardsAsReactAction = getMoveOtherCardsAsReactAction(playerId, game, self);
            if (moveOtherCardsAsReactAction != null) {
                actions.add(moveOtherCardsAsReactAction);
            }

            // Move other cards away as 'react'
            TriggerAction moveOtherCardsAwayAsReactAction = getMoveOtherCardsAwayAsReactAction(playerId, game, self);
            if (moveOtherCardsAwayAsReactAction != null) {
                actions.add(moveOtherCardsAwayAsReactAction);
            }
        }

        return actions;
    }

    @Override
    public final List<TriggerAction> getBlownAwayRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);
        boolean isDarkSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnDarkSideOfLocation);
        boolean isLightSideTextCanceled = game.getModifiersQuerying().isLocationGameTextCanceledForPlayer(game.getGameState(), self, playerOnLightSideOfLocation);

        if (!isDarkSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraDarkSideActions = getGameTextDarkSideBlownAwayRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, self.getCardId());
            if (extraDarkSideActions != null)
                actions.addAll(extraDarkSideActions);
        }

        if (!isLightSideTextCanceled) {
            List<RequiredGameTextTriggerAction> extraLightSideActions = getGameTextLightSideBlownAwayRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, self.getCardId());
            if (extraLightSideActions != null)
                actions.addAll(extraLightSideActions);
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
            if (locationExpandedDSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideBlownAwayRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedDSToMyDS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
            if (locationExpandedLSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideBlownAwayRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedLSToMyLS.getCardId());
                if (expandedLightSideActions != null)
                    actions.addAll(expandedLightSideActions);
            }
        }

        if (!isLightSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
            if (locationExpandedDSToMyLS != null) {
                List<RequiredGameTextTriggerAction> expandedDarkSideActions = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideBlownAwayRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, locationExpandedDSToMyLS.getCardId());
                if (expandedDarkSideActions != null)
                    actions.addAll(expandedDarkSideActions);
            }
        }

        if (!isDarkSideTextCanceled) {
            // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
            PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
            if (locationExpandedLSToMyDS != null) {
                List<RequiredGameTextTriggerAction> expandedLightSideActions = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideBlownAwayRequiredAfterTriggers(playerOnDarkSideOfLocation, game, effectResult, self, locationExpandedLSToMyDS.getCardId());
                if (expandedLightSideActions != null)
                    actions.addAll(expandedLightSideActions);
            }
        }

        return actions;
    }

    @Override
    public final List<Modifier> getWhileInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // The rest of the actions are per side of the location, so get the player on each side of location.
        String playerOnDarkSideOfLocation = getPlayerOnSideOfLocation(Side.DARK, game, self);
        String playerOnLightSideOfLocation = getPlayerOnSideOfLocation(Side.LIGHT, game, self);

        List<Modifier> extraDarkSideModifiers = getGameTextDarkSideWhileActiveModifiers(playerOnDarkSideOfLocation, game, self);
        if (extraDarkSideModifiers != null) {
            for (Modifier modifier : extraDarkSideModifiers) {
                modifier.setLocationSidePlayer(playerOnDarkSideOfLocation);
            }
            modifiers.addAll(extraDarkSideModifiers);
        }

        List<Modifier> extraLightSideModifiers = getGameTextLightSideWhileActiveModifiers(playerOnLightSideOfLocation, game, self);
        if (extraLightSideModifiers != null) {
            for (Modifier modifier : extraLightSideModifiers) {
                modifier.setLocationSidePlayer(playerOnLightSideOfLocation);
            }
            modifiers.addAll(extraLightSideModifiers);
        }

        // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
        PhysicalCard locationExpandedDSToMyDS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.DARK);
        if (locationExpandedDSToMyDS != null) {
            List<Modifier> expandedDarkSideModifiers = ((AbstractLocation) locationExpandedDSToMyDS.getBlueprint()).getGameTextDarkSideWhileActiveModifiers(playerOnDarkSideOfLocation, game, self);
            if (expandedDarkSideModifiers != null) {
                for (Modifier modifier : expandedDarkSideModifiers) {
                    modifier.setLocationSidePlayer(playerOnDarkSideOfLocation);
                }
                modifiers.addAll(expandedDarkSideModifiers);
            }
        }

        // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
        PhysicalCard locationExpandedLSToMyLS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.LIGHT);
        if (locationExpandedLSToMyLS != null) {
            List<Modifier> expandedLightSideModifiers = ((AbstractLocation) locationExpandedLSToMyLS.getBlueprint()).getGameTextLightSideWhileActiveModifiers(playerOnLightSideOfLocation, game, self);
            if (expandedLightSideModifiers != null) {
                for (Modifier modifier : expandedLightSideModifiers) {
                    modifier.setLocationSidePlayer(playerOnLightSideOfLocation);
                }
                modifiers.addAll(expandedLightSideModifiers);
            }
        }

        // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
        PhysicalCard locationExpandedDSToMyLS = getLocationWithExpandedGameText(game, self, Side.DARK, Side.LIGHT);
        if (locationExpandedDSToMyLS != null) {
            List<Modifier> expandedDarkSideModifiers = ((AbstractLocation) locationExpandedDSToMyLS.getBlueprint()).getGameTextDarkSideWhileActiveModifiers(playerOnLightSideOfLocation, game, self);
            if (expandedDarkSideModifiers != null) {
                for (Modifier modifier : expandedDarkSideModifiers) {
                    modifier.setLocationSidePlayer(playerOnLightSideOfLocation);
                }
                modifiers.addAll(expandedDarkSideModifiers);
            }
        }

        // If game text has been expanded from another location, append those actions (need to check for combinations in case either location is rotated)
        PhysicalCard locationExpandedLSToMyDS = getLocationWithExpandedGameText(game, self, Side.LIGHT, Side.DARK);
        if (locationExpandedLSToMyDS != null) {
            List<Modifier> expandedLightSideModifiers = ((AbstractLocation) locationExpandedLSToMyDS.getBlueprint()).getGameTextLightSideWhileActiveModifiers(playerOnDarkSideOfLocation, game, self);
            if (expandedLightSideModifiers != null) {
                for (Modifier modifier : expandedLightSideModifiers) {
                    modifier.setLocationSidePlayer(playerOnDarkSideOfLocation);
                }
                modifiers.addAll(expandedLightSideModifiers);
            }
        }

        return modifiers;
    }

    /**
     * Gets modifiers from the card that are always in effect.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Modifiers from 'bluff cards'
        List<Modifier> bluffRulesModifiers = getBluffRulesModifiers(game, self);
        if (bluffRulesModifiers != null) {
            modifiers.addAll(bluffRulesModifiers);
        }

        return modifiers;
    }

    /**
     * Gets an modifiers from 'bluff' cards stacked on the location.
     * @param game the game
     * @param self this location
     * @return modifiers from 'bluff' cards
     */
    private List<Modifier> getBluffRulesModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // If 'bluff rules' in effect, add cumulative Force drain modifier for each 'bluff card'
        if (isSpecialRuleInEffectHere(SpecialRule.BLUFF_RULES, self)) {
            Collection<PhysicalCard> bluffCards = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.bluffCard);
            for (PhysicalCard bluffCard : bluffCards) {
                modifiers.add(new ForceDrainModifier(bluffCard, self, 1, bluffCard.getOwner(), true));
            }
        }

        return modifiers;
    }

    /**
     * Gets the location that is expanding its specified side of the location game text to the specified side of this location.
     * @param game the game
     * @param self this location
     * @param sideExpandedFrom the side of the other location where the expanded game text if coming from
     * @param sideExpandedTo the side of this location where the expanded game text is expanded to
     * @return the other location expanding its game text to this location
     */
    private PhysicalCard getLocationWithExpandedGameText(SwccgGame game, PhysicalCard self, Side sideExpandedFrom, Side sideExpandedTo) {
        if (self.getLocationGameTextExpandedToSideFromSide(sideExpandedTo) == sideExpandedFrom) {
            Integer expandedFromCardId = self.getLocationGameTextExpandedToSideFromCardId(sideExpandedTo);
            if (expandedFromCardId != null) {
                return game.getGameState().findCardById(expandedFromCardId);
            }
        }
        return null;
    }

    /**
     * Gets the player on the specified side of the location.
     * @param side the side of the location
     * @param game the game
     * @param self the location
     * @return the player
     */
    private String getPlayerOnSideOfLocation(Side side, SwccgGame game, PhysicalCard self) {
        if (game.getModifiersQuerying().isRotatedLocation(game.getGameState(), self))
            return game.getOpponent(game.getPlayer(side));
        else
            return game.getPlayer(side);
    }

    //
    // This section defines common methods that are used by individual cards to define actions, etc.
    // that are defined by a card's game text.
    //

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player on the Dark side of the location.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player on the Light side of the location.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect on the
     * Dark side of the location.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect on the
     * Light side of the location.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredBeforeTriggers(String playerOnLightSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect on the
     * Dark side of the location that can be performed by player on that side of the location.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalBeforeTriggers(String playerOnDarkSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect on the
     * Dark side of the location that can be performed by player on that side of the location.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalBeforeTriggers(String playerOnLightSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * on Dark side of the location.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * on Light side of the location.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers from the Dark side of the location
     * when the location is 'blown away'.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextDarkSideBlownAwayRequiredAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers from the Light side of the location
     * when the location is 'blown away'.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideBlownAwayRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * on the Dark side of the location that can be performed by the specified player on that side of the location.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * on the Light side of the location that can be performed by the specified player on that side of the location.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextLightSideOptionalAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers on the Dark side of the location that are in
     * effect while the location card is in play.
     * @param playerOnDarkSideOfLocation the player on Dark side of location
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers on the Light side of the location that are in
     * effect while the location card is in play.
     * @param playerOnLightSideOfLocation the player on Light side of location
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify card deploy requirements.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if requirements met, otherwise false
     */
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return true;
    }
}
