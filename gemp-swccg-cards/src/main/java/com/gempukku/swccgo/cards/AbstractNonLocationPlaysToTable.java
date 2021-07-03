package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.*;

import java.util.*;

/**
 * The abstract class providing the common implementation for non-location cards that are played/deployed to the table
 * (or on cards that are on the table).
 */
public abstract class AbstractNonLocationPlaysToTable extends AbstractSwccgCardBlueprint {
    protected Float _deployCost;
    protected PlayCardZoneOption _playCardZoneOption;

    /**
     * Creates a blueprint for a non-location card that plays to the table.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param deployCost the deploy cost
     * @param title the card title
     */
    protected AbstractNonLocationPlaysToTable(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title) {
        this(side, destiny, playCardZoneOption, deployCost, title, null);
    }

    /**
     * Creates a blueprint for a non-location card that plays to the table.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param deployCost the deploy cost
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractNonLocationPlaysToTable(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title, Uniqueness uniqueness) {
        super(side, destiny, title, uniqueness);
        _deployCost = deployCost;
        _playCardZoneOption = playCardZoneOption;
    }

    /**
     * Gets the deploy cost.
     * @return the deploy cost
     */
    @Override
    public final Float getDeployCost() {
        return _deployCost;
    }

    /**
     * Gets the only play card zone option for this card. This is only set by cards that are only played to one zone.
     * @return the play card zone option, or null
     */
    @Override
    public final PlayCardZoneOption getSinglePlayCardZoneOption() {
        return _playCardZoneOption;
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
    public final PlayCardAction getPlayCardAction(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, boolean forFree, float changeInCost, DeploymentOption deploymentOption, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, PhysicalCard cardToDeployWith, boolean cardToDeployWithForFree, float cardToDeployWithChangeInCost, Filter deployTargetFilter, Filter specialLocationConditions) {
        if (!forFree) {
            forFree = isCardTypeAlwaysPlayedForFree() || game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
        }

        // Only characters, devices, starships, vehicles, and weapons can deploy as a 'react'
        if (reactActionOption != null) {
            if (!Filters.or(Filters.character, Filters.device, Filters.starship, Filters.vehicle, Filters.weapon).accepts(game, self)) {
                return null;
            }
        }

        List<PlayCardAction> playCardActions = getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
        if (playCardActions.isEmpty())
            return null;

        if (playCardActions.size() == 1)
            return playCardActions.get(0);

        return new PlayChoiceAction(playerId, sourceCard, self, playCardActions);
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

        for (PlayCardOption playCardOption : getPlayCardOptions(playerId, game)) {
            if (checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)) {

                Zone playToZone = playCardOption.getZone();
                PlayCardAction playCardAction = null;

                // If the play option is to play to a location or attach to a card, then get use the filter for where card can be played
                if (playToZone == Zone.AT_LOCATION || playToZone == Zone.ATTACHED) {
                    Filter completeTargetFilter = Filters.and(deployTargetFilter, getValidDeployTargetFilter(playerId, game, self, sourceCard, playCardOption, forFree, changeInCost, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, false, false));

                    // Determine the spot override to use when playing the card using this play card option
                    Map<InactiveReason, Boolean> spotOverrides = self.getBlueprint().getDeployTargetSpotOverride(playCardOption.getId());

                    //check if the subtype has been modified
                    CardSubtype subtype = self.getBlueprint().getCardSubtype();
                    if (game.getModifiersQuerying().getModifiedSubtype(game.getGameState(), self) != null)
                        subtype = game.getModifiersQuerying().getModifiedSubtype(game.getGameState(), self);

                    // For Utinni Effects, need to also determine if valid targets can be found
                    if (subtype == CardSubtype.UTINNI) {
                        List<PhysicalCard> validDeployOnTargets = new LinkedList<PhysicalCard>();
                        Collection<PhysicalCard> possibleDeployOnTargets = Filters.filterActive(game, self, spotOverrides, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter);

                        for (PhysicalCard deployOnTarget : possibleDeployOnTargets) {
                            boolean targetFound = false;
                            boolean targetNotFound = false;

                            List<TargetId> targetIds = self.getBlueprint().getUtinniEffectTargetIds(playerId, game, self);
                            for (TargetId targetId : targetIds) {

                                // Determine the filter and spot override to use when target the Utinni Effect target
                                Filter utinniEffectTargetFilter = self.getBlueprint().getValidUtinniEffectTargetFilter(playerId, game, self, deployOnTarget, targetId);
                                Map<InactiveReason, Boolean> utinniEffectTargetSpotOverrides = self.getBlueprint().getTargetSpotOverride(targetId);

                                if (Filters.canSpot(game, self, utinniEffectTargetSpotOverrides, utinniEffectTargetFilter)) {
                                    targetFound = true;
                                }
                                else {
                                    targetNotFound = true;
                                    break;
                                }
                            }

                            if (targetIds.isEmpty() || (targetFound && !targetNotFound)) {
                                validDeployOnTargets.add(deployOnTarget);
                            }
                        }

                        if (!validDeployOnTargets.isEmpty()) {
                            if (playToZone == Zone.ATTACHED) {
                                playCardAction = new PlayCardAsAttachedAction(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption, spotOverrides, Filters.in(validDeployOnTargets));
                            }
                            else {
                                playCardAction = new PlayCardToLocationAction(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption, Filters.in(validDeployOnTargets));
                            }
                        }
                    }
                    // For Jedi Test, need to also determine if valid targets can be found (or deployed from hand)
                    else if (self.getBlueprint().getCardCategory() == CardCategory.JEDI_TEST) {
                        List<PhysicalCard> validDeployOnTargets = new LinkedList<PhysicalCard>();
                        Collection<PhysicalCard> possibleDeployOnTargets = Filters.filterActive(game, self, spotOverrides, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter);

                        for (PhysicalCard deployOnTarget : possibleDeployOnTargets) {
                            // Determine the filter to use when targeting the mentor (including finding a valid apprentice to target or deploy)
                            if (Filters.canSpot(game, self, self.getBlueprint().getValidJediTestMentorTargetFilter(playerId, game, self, deployOnTarget))) {
                                validDeployOnTargets.add(deployOnTarget);
                            }
                        }

                        if (!validDeployOnTargets.isEmpty()) {
                            playCardAction = new PlayCardAsAttachedAction(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption, spotOverrides, Filters.in(validDeployOnTargets));
                        }
                    }
                    else if (playToZone == Zone.ATTACHED) {
                        // Check that a valid target to deploy to as attached can be found
                        if (Filters.canSpot(game, self, spotOverrides, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter)) {
                            playCardAction = new PlayCardAsAttachedAction(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption, spotOverrides, completeTargetFilter);
                        }

                    }
                    else {
                        // Check that a valid location to deploy to can be found
                        if (Filters.canSpot(game, self, completeTargetFilter)) {
                            playCardAction = new PlayCardToLocationAction(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption, completeTargetFilter);
                        }
                    }
                }
                else {
                    if (self.getBlueprint().getCardCategory() == CardCategory.JEDI_TEST) {
                        // Determine the filter to use when targeting the apprentice
                        if (Filters.canSpot(game, self, self.getBlueprint().getValidJediTestApprenticeTargetFilter(playerId, game, self, null, null, false))) {
                            playCardAction = new PlayCardToZoneAction(sourceCard, self, playCardOption, forFree, changeInCost);
                        }
                    }
                    else {
                        // Just check if use Force cost can be paid
                        if (GameConditions.canUseForceToDeployCard(game, self, sourceCard, playCardOption, forFree, changeInCost, false)) {
                            playCardAction = new PlayCardToZoneAction(sourceCard, self, playCardOption, forFree, changeInCost);
                        }
                    }
                }

                // Set the text (if needed) and add the play card action to the list
                if (playCardAction != null) {
                    if (playCardOption.getText() != null) {
                        playCardAction.setText(playCardOption.getText());
                    }
                    playCardActions.add(playCardAction);
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
     * Determines if the card is an Effect that deploys on another card.
     * @param game the game
     * @param self the card
     * @return true if card is an Effect that deploys on another card
     */
    @Override
    public boolean isEffectThatDeploysOnAnotherCard(SwccgGame game, PhysicalCard self) {
        if (!Filters.Effect.accepts(game, self)) {
            return false;
        }
        List<PlayCardOption> playCardOptions = getPlayCardOptions(self.getOwner(), game);
        for (PlayCardOption playCardOption : playCardOptions) {
            if (playCardOption.getZone() == Zone.ATTACHED) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the play card options
     * @param playerId the player
     * @param game the game
     * @return the play card options
     */
    protected List<PlayCardOption> getPlayCardOptions(String playerId, SwccgGame game) {
        List<PlayCardOption> playCardOptions = new ArrayList<PlayCardOption>();

        List<PlayCardOption> gameTextPlayCardOptions = getGameTextPlayCardOptions();
        if (gameTextPlayCardOptions != null) {
            for (PlayCardOption playCardOption : gameTextPlayCardOptions) {
                if (playCardOption.isValidOption(playerId, game.getGameState())) {
                    playCardOptions.add(playCardOption);
                }
            }

            if (!playCardOptions.isEmpty())
                return playCardOptions;
        }

        if (_playCardZoneOption == null)
            throw new UnsupportedOperationException(GameUtils.getFullName(this) + " does not have playCardZoneOption set");

        PlayCardOptionId playCardOptionId = _playCardZoneOption.isAsInsertCard() ? PlayCardOptionId.PLAY_AS_INSERT_CARD : PlayCardOptionId.PLAY_CARD_OPTION_1;
        PlayCardOption playCardOption = new PlayCardOption(playCardOptionId, _playCardZoneOption, null);
        if (playCardOption.isValidOption(playerId, game.getGameState())) {
            playCardOptions.add(playCardOption);
        }

        return playCardOptions;
    }

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
    @Override
    public List<TargetingEffect> getTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return getGameTextTargetCardsWhenDeployedEffects(action, playerId, game, self, target, playCardOption);
    }

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
    @Override
    public final StandardEffect getSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return getGameTextSpecialDeployCostEffect(action, playerId, game, self, target, playCardOption);
    }

    /**
     * Determines if the card can be deploy simultaneously as attached.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card cannot be deploy simultaneously as attached, otherwise false
     */
    @Override
    public boolean mayDeploySimultaneouslyAsAttachedRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return checkPlayRequirements(playerId, game, self, null, null, null);
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
                && !game.getModifiersQuerying().isUniquenessOnTableLimitReached(game.getGameState(), self)
                && checkPlayRequirementsByCheckingGameText(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption);
    }

    /**
     * Determines if the deploy requirements from the card's game text are met (or are ignored).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param playCardOption the play card option, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return true if requirements met or ignored, otherwise false
     */
    protected boolean checkPlayRequirementsByCheckingGameText(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return (deploymentRestrictionsOption != null && deploymentRestrictionsOption.isIgnoreGameTextDeploymentRestrictions())
                || game.getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(game.getGameState(), self)
                || checkGameTextDeployRequirements(playerId, game, self, playCardOption != null ? playCardOption.getId() : null, reactActionOption != null);
    }

    /**
     * Gets the target filter for where a card can be deployed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param sourceCard the card to initiate the deployment
     * @param playCardOption the play card option, or null
     * @param forFree true if playing card for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required
     * @param deploymentRestrictionsOption specifies which deployment restrictions are to be ignored, or null
     * @param deployAsCaptiveOption specifies the way to deploy the card as a captive, or null
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @param isSimDeployAttached true if during simultaneous deployment of pilot/weapon, otherwise false
     * @param ignorePresenceOrForceIcons true if this deployment ignores presence or Force icons requirement
     * @return the target filter
     */
    protected Filter getValidDeployTargetFilter(String playerId, final SwccgGame game, final PhysicalCard self, PhysicalCard sourceCard, PlayCardOption playCardOption, boolean forFree, float changeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons) {
        Zone playToZone = playCardOption != null ? playCardOption.getZone() : null;

        if (playToZone != null && playToZone != Zone.ATTACHED && playToZone != Zone.AT_LOCATION)
            return Filters.none;

        // Filter cards that this card is not prohibited from being at or deploying to
        Filter filter = Filters.and(Filters.not(Filters.holosite), Filters.notProhibitedFromTarget(self), Filters.notProhibitedFromDeployingTo(self, deploymentRestrictionsOption));
        if (playToZone == Zone.ATTACHED) {
            filter = Filters.and(filter, Filters.notProhibitedFromCarrying(self), Filters.canBeTargetedBy(self));
        }

        // Filter locations to deploy as 'react' to
        if (reactActionOption != null) {
            filter = Filters.and(filter, reactActionOption.getTargetFilter(), Filters.locationAndCardsAtLocation(Filters.or(Filters.forceDrainLocation, Filters.battleLocation)));
        }

        // Filter cards that the cost can be paid to deploy to
        if (!isSimDeployAttached) {
            filter = Filters.and(filter, Filters.canUseForceToDeployToTarget(sourceCard, self, playCardOption, forFree, changeInCost, reactActionOption));
        }

        // Filter cards that a this type of card can deploy to (based on game rules for that card type/subtype, etc.)
        filter = Filters.and(filter, getValidDeployTargetFilterForCardType(playerId, game, self, isSimDeployAttached, ignorePresenceOrForceIcons, deploymentRestrictionsOption, deployAsCaptiveOption));

        // Filter cards that this card is allowed to deploy to from game text or explicit grant to deploy to
        if (deploymentRestrictionsOption == null || !deploymentRestrictionsOption.isIgnoreLocationDeploymentRestrictions()
                || !Filters.or(Filters.character, Filters.device, Filters.starship, Filters.vehicle, Filters.weapon).accepts(game, self)) {
            filter = Filters.and(filter, Filters.or(getValidDeployTargetFilterByCheckingGameText(game, self, playCardOption, reactActionOption), Filters.grantedToDeployTo(self, reactActionOption)));
        }

        return filter;
    }

    /**
     * Gets the target filter for where a card can be deployed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param playCardOption the play card option, or null
     * @param forFree true if playing card for free, otherwise false
     * @return the target filter
     */
    protected Filter getValidTransferTargetFilter(String playerId, final SwccgGame game, final PhysicalCard self, PlayCardOption playCardOption, boolean forFree) {
        Filter filter = getValidDeployTargetFilter(playerId, game, self, self, playCardOption, true, 0, null, null, null, false, false);
        // Filter cards that the cost can be paid to transfer to
        if (!forFree) {
            filter = Filters.and(filter, Filters.canUseForceToTransferToTarget(self, playCardOption));
        }
        return filter;
    }

    /**
     * Gets the valid target filter that the card can remain attached to after the attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Jedi_Test, getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(game, self, self.getPlayCardOptionId()));
    }

    /**
     * Gets the valid target filter that the card can remain attached to. If the card becomes attached to a card that is
     * not accepted by this filter, then the attached card will be lost by rule.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(getGameTextValidTargetFilterToRemainAttachedTo(game, self), Filters.canTargetCard(self));
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
    protected Filter getValidDeployTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption) {
        return Filters.any;
    }

    /**
     * Gets the valid deploy target filter from the card's game text.
     * @param game the game
     * @param self the card
     * @param playCardOption the play card option
     * @param reactActionOption a 'react' action option, or null if not a 'react'
     * @return the deploy to target filter
     */
    protected Filter getValidDeployTargetFilterByCheckingGameText(final SwccgGame game, final PhysicalCard self, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        if (Filters.or(Filters.character, Filters.vehicle, Filters.starship).accepts(game, self)) {
            if (game.getModifiersQuerying().ignoresGameTextLocationDeploymentRestrictions(game.getGameState(), self))
                return Filters.any;
            else
                return Filters.or(Filters.ignoresLocationDeployRestrictionsWhenDeployingTo(self), getGameTextValidDeployTargetFilter(game, self, playCardOption != null ? playCardOption.getId() : null, reactActionOption != null));
        }
        else {
            return getGameTextValidDeployTargetFilter(game, self, playCardOption != null ? playCardOption.getId() : null, reactActionOption != null);
        }
    }

    /**
     * Determines if the card can be played during the current phase.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card can be played during the current phase, otherwise false
     */
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return true;
    }

    /**
     * Determines if the card type, subtype, etc. always plays for free.
     * @return true if card type card type, subtype, etc. always plays for free, otherwise false
     */
    protected boolean isCardTypeAlwaysPlayedForFree() {
        return false;
    }

    /**
     * Gets the valid filter for targets to transfer the card to another card during character replacement.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidTransferDuringCharacterReplacementTargetFilter(final SwccgGame game, final PhysicalCard self) {
        // Filter cards that this card is not prohibited from being at
        Filter filter = Filters.and(Filters.notProhibitedFromTarget(self), Filters.notProhibitedFromCarrying(self), Filters.canBeTargetedBy(self));

        // Filter cards that a this type of card is allowed by deploy to based on its game text
        filter = Filters.and(filter, getValidDeployTargetFilterByCheckingGameText(game, self, null, null));

        return filter;
    }

    /**
     * Gets the valid filter for targets to place the card when the specified card is placed from off table.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getValidPlaceCardTargetFilter(final SwccgGame game, final PhysicalCard self) {
        // Filter cards that this card is not prohibited from being at
        Filter filter = Filters.and(Filters.not(Filters.holosite), Filters.notProhibitedFromTarget(self), Filters.notProhibitedFromCarrying(self), Filters.canBeTargetedBy(self));

        // Filter cards that a this type of card can be placed (based on game rules for that card type/subtype, etc.)
        filter = Filters.and(filter, getValidPlaceCardTargetFilterForCardType(game, self));

        // If Dark Side card, may not be placed at locations under Hoth Energy shield
        if (self.getOwner().equals(game.getDarkPlayer())) {
            filter = Filters.and(filter, Filters.not(Filters.locationAndCardsAtLocation(Filters.underHothEnergyShield())));
        }

        return filter;
    }

    /**
     * Gets the valid place card target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can be placed to.
     * @param game the game
     * @param self the card
     * @return the place card to target filter based on the card type, subtype, etc.
     */
    protected Filter getValidPlaceCardTargetFilterForCardType(final SwccgGame game, final PhysicalCard self) {
        return Filters.none;
    }

    /**
     * Gets the fire weapon action for the weapon (or card with permanent weapon) if it can be fired. If the weapon can
     * be fired in multiple ways, then this will return an action that has the player choose which way to fire the card,
     * then fires the card that way.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon action
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    @Override
    public final FireWeaponAction getFireWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> fireWeaponActions = getFireWeaponActions(playerId, game, self, forFree, extraForceRequired, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        if (fireWeaponActions.isEmpty())
            return null;

        if (fireWeaponActions.size() == 1)
            return fireWeaponActions.get(0);

        return new FireWeaponChoiceAction(playerId, self, fireWeaponActions);
    }

    /**
     * Gets the fire weapon actions for each way the weapon (or card with permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon actions
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    @Override
    public final List<FireWeaponAction> getFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> fireWeaponActions = new ArrayList<FireWeaponAction>();

        if (!checkFireWeaponRequirements(playerId, game, self, repeatedFiring))
            return fireWeaponActions;

        // Include any extra cost to fire weapon card if not a repeated firing
        int extraCostForWeaponCard = (repeatedFiring ? extraForceRequired : (extraForceRequired + game.getModifiersQuerying().getExtraForceRequiredToFireWeapon(game.getGameState(), self)));
        List<FireWeaponAction> gameTextActions = getGameTextFireWeaponActions(playerId, game, self, forFree, extraCostForWeaponCard, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        if (gameTextActions != null) {
            fireWeaponActions.addAll(gameTextActions);
        }

        List<FireWeaponAction> permanentWeaponActions = getPermanentWeaponFireWeaponActions(playerId, game, self, forFree, extraForceRequired, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        if (permanentWeaponActions != null) {
            fireWeaponActions.addAll(permanentWeaponActions);
        }

        return fireWeaponActions;
    }

    /**
     * Gets the fire weapon actions for each way this card's permanent weapon can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon actions
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    protected List<FireWeaponAction> getPermanentWeaponFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return Collections.emptyList();
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @return true if card can be fired, otherwise false
     */
    protected boolean checkFireWeaponRequirements(String playerId, SwccgGame game, PhysicalCard self, boolean repeatedFiring) {
        return false;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelActions(playerId, game, self);

        // Play card
        if (canPlayCardDuringCurrentPhase(playerId, game, self)
                && (self.getZone() != Zone.STACKED || game.getModifiersQuerying().mayDeployAsIfFromHand(game.getGameState(), self))) {
            boolean forFree = isCardTypeAlwaysPlayedForFree() || game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS;
            List<PlayCardAction> playCardActions = getPlayCardActions(playerId, game, self, self, forFree, 0, null, null, null, null, null, false, 0, Filters.any, null);
            if (playCardActions != null) {
                actions.addAll(playCardActions);
            }
        }

        // Actions from game text when in play
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, true, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelActions(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
                else {
                    List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelActionsWhenInactiveInPlay(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelActionsEvenIfUnpiloted(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        // Actions from game text when in hand
        if (self.getZone() == Zone.HAND) {
            List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelInHandActions(playerId, game, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        // Actions from game text when stacked
        if (self.getZone() == Zone.STACKED) {
            List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelWhileStackedActions(playerId, game, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelAttackRunActions(playerId, game, self);

        // Actions from game text
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<TopLevelGameTextAction> gameTextActions = getGameTextTopLevelAttackRunActions(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the top-level actions that can be performed by the specified player (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    @Override
    public List<Action> getOpponentsCardTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Actions from game text
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false);
            
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<TopLevelGameTextAction> gameTextActions = getOpponentsCardGameTextTopLevelActions(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
                else {
                    List<TopLevelGameTextAction> gameTextActions = getOpponentsCardGameTextTopLevelActionsWhenInactiveInPlay(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<TopLevelGameTextAction> gameTextActions = getOpponentsCardGameTextTopLevelActionsEvenIfUnpiloted(playerId, game, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the required "before" triggers for the specified effect if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredOutsideOfDeckBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredOutsideOfDeckBeforeTriggers(game, effect, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the required "before" triggers for the specified effect.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Get the actions based on the card state
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, true, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredBeforeTriggers(game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredBeforeTriggersWhenInactiveInPlay(game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredBeforeTriggersEvenIfUnpiloted(game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Get the actions based on the card state
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, true, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalBeforeTriggers(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalBeforeTriggersWhenInactiveInPlay(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalBeforeTriggersEvenIfUnpiloted(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the optional "before" triggers for the specified effect that can be performed by the specified player (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOpponentsCardOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Get the actions based on the card state
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalBeforeTriggers(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalBeforeTriggersWhenInactiveInPlay(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalBeforeTriggersEvenIfUnpiloted(playerId, game, effect, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the optional "before" actions for the specified effect that can be performed by the specified player. This includes
     * actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Actions from game text
        List<PlayCardAction> gameTextActions = getGameTextOptionalBeforeActions(playerId, game, effect, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the required "after" triggers for the specified effect result if the card is 'outside of deck' during start of game.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredOutsideOfDeckAfterTriggers(game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        if (self.getZone().isInPlay()) {
            // Get the actions based on the card state
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredAfterTriggers(game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredAfterTriggersWhenInactiveInPlay(game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    // Get any maintenance cost actions
                    if (TriggerConditions.isEndOfYourTurn(game, effectResult, self.getOwner())) {
                        RequiredGameTextTriggerAction gameTextAction = getGameTextMaintenanceCostAction(self.getOwner(), game, self, self.getCardId());
                        if (gameTextAction != null)
                            actions.add(gameTextAction);
                    }

                    List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredAfterTriggersEvenIfUnpiloted(game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }

            List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredAfterTriggersAlwaysWhenInPlay(game, effectResult, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        // Actions from game text when stacked
        if (self.getZone() == Zone.STACKED) {
            List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredAfterTriggersWhileStacked(game, effectResult, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }
        return actions;
    }

    /**
     * Gets the required "after" triggers when the specified card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions = getGameTextRequiredDrawnAsDestinyTriggers(game, effectResult, self, self.getCardId());
        if (gameTextActions != null) {
            actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers when the specified card is drawn for destiny that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalDrawnAsDestinyTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null) {
            actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, true, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalAfterTriggers(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalAfterTriggersWhenInactiveInPlay(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalAfterTriggersEvenIfUnpiloted(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }
        if (self.getZone() == Zone.STACKED) {
            List<OptionalGameTextTriggerAction> gameTextActions = getGameTextOptionalAfterTriggersWhenStacked(playerId, game, effectResult, self, self.getCardId());
            if (gameTextActions != null)
                actions.addAll(gameTextActions);
        }

        return actions;
    }

    /**
     * Gets the optional "after" triggers for the specified effect result that can be performed by the specified player
     * (from opponent's card).
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<TriggerAction> getOpponentsCardOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Get the actions based on the card state
        if (self.getZone().isInPlay()) {
            boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false);

            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalAfterTriggers(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                } else {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalAfterTriggersWhenInactiveInPlay(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self, false, true)) {
                if (inPlayActive) {
                    List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextOptionalAfterTriggersEvenIfUnpiloted(playerId, game, effectResult, self, self.getCardId());
                    if (gameTextActions != null)
                        actions.addAll(gameTextActions);
                }
            }
        }

        return actions;
    }

    /**
     * Gets the optional "after" actions for the specified effect result that can be performed by the specified player.
     * This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Actions from game text
        List<PlayCardAction> gameTextActions = getGameTextOptionalAfterActions(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the required triggers from a card when that card leaves table.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<RequiredGameTextTriggerAction> gameTextActions = getGameTextLeavesTableRequiredTriggers(game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the optional triggers from a card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getGameTextLeavesTableOptionalTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the optional triggers from an opponent's card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOpponentsCardLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextLeavesTableOptionalTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the optional triggers from a card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getGameTextLostFromLifeForceOptionalTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets the optional triggers from an opponent's card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getOpponentsCardLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<TriggerAction> actions = new LinkedList<TriggerAction>();

        // Actions from game text
        List<OptionalGameTextTriggerAction> gameTextActions = getOpponentsCardGameTextLostFromLifeForceOptionalTriggers(playerId, game, effectResult, self, self.getCardId());
        if (gameTextActions != null)
            actions.addAll(gameTextActions);

        return actions;
    }

    /**
     * Gets modifiers from the card that are always in effect (even if game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Modifiers from game text
        List<Modifier> modifiersFromGameText = getGameTextAlwaysOnModifiers(game, self);
        if (modifiersFromGameText != null) {
            modifiers.addAll(modifiersFromGameText);
        }

        return modifiers;
    }

    /**
     * Gets modifiers from the card that are in effect while the card is in play (unless game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<Modifier>();

        // Modifiers from game text
        List<Modifier> modifiersFromGameText = getGameTextWhileActiveInPlayModifiers(game, self);
        if (modifiersFromGameText != null) {
            modifiers.addAll(modifiersFromGameText);
        }
        modifiersFromGameText = getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(game, self);
        if (modifiersFromGameText != null) {
            for (Modifier modifierFromGameText : modifiersFromGameText) {
                modifierFromGameText.setPersistent(true);
                modifiers.add(modifierFromGameText);
            }
        }
        modifiersFromGameText = getGameTextWhileInactiveInPlayModifiers(game, self);
        if (modifiersFromGameText != null) {
            for (Modifier modifierFromGameText : modifiersFromGameText) {
                modifierFromGameText.setWhileInactiveInPlay(true);
                modifiers.add(modifierFromGameText);
            }
        }
        modifiersFromGameText = getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(game, self);
        if (modifiersFromGameText != null) {
            for (Modifier modifierFromGameText : modifiersFromGameText) {
                modifierFromGameText.setEvenIfUnpilotedInPlay(true);
                modifiers.add(modifierFromGameText);
            }
        }

        // Get modifiers from permanents pilots aboard
        List<SwccgBuiltInCardBlueprint> permanentPilots = game.getModifiersQuerying().getPermanentPilotsAboard(game.getGameState(), self);
        for (SwccgBuiltInCardBlueprint permanentPilot : permanentPilots) {
            List<Modifier> permanentPilotModifiers = game.getModifiersQuerying().getModifiersFromPermanentBuiltIn(game.getGameState(), permanentPilot);
            if (permanentPilotModifiers != null) {
                for (Modifier permanentPilotModifier : permanentPilotModifiers) {
                    permanentPilotModifier.setFromPermanentPilot(true);
                    modifiers.add(permanentPilotModifier);
                }
            }
        }

        // Get modifiers from permanent astromechs aboard
        List<SwccgBuiltInCardBlueprint> permanentAstromechs = game.getModifiersQuerying().getPermanentAstromechsAboard(game.getGameState(), self);
        for (SwccgBuiltInCardBlueprint permanentAstromech : permanentAstromechs) {
            List<Modifier> permanentAstromechModifiers = game.getModifiersQuerying().getModifiersFromPermanentBuiltIn(game.getGameState(), permanentAstromech);
            if (permanentAstromechModifiers != null) {
                for (Modifier permanentAstromechModifier : permanentAstromechModifiers) {
                    permanentAstromechModifier.setFromPermanentAstromech(true);
                    modifiers.add(permanentAstromechModifier);
                }
            }
        }

        return modifiers;
    }

    /**
     * Gets modifiers that are from this card that are in effect while the card is out of play.
     *
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileOutOfPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<Modifier>();

        // Modifiers from game text
        List<Modifier> modifiersFromGameText = getGameTextWhileOutOfPlayModifiers(game, self);
        if (modifiersFromGameText != null) {
            modifiers.addAll(modifiersFromGameText);
        }

        return modifiers;
    }

    /**
     * Gets modifiers that are from this card that are in effect while the card is stacked (face up) on another card.
     * @param game the game
     * @param self the card
     * @return the modifiers
     */
    @Override
    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new ArrayList<Modifier>();

        // Modifiers from game text
        List<Modifier> modifiersFromGameText = getGameTextWhileStackedModifiers(game, self);
        if (modifiersFromGameText != null) {
            modifiers.addAll(modifiersFromGameText);
        }

        return modifiers;
    }


    //
    // This section defines common methods that are used by individual cards to define actions, etc.
    // that are defined by a card's game text.
    //

    /**
     * This method is overridden by individual cards to specify modifiers that are always in effect (unless game text is
     * canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is active
     * (or only undercover) in play.
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is in play
     * (active, inactive, or even if game text is canceled).
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is inactive
     * (except when only undercover) in play.
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is active
     * (or only undercover) in play (even if it is an unpiloted starship/vehicle) in play.
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is stacked
     * (face up) on another card.
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify modifiers that are in effect while the card is out of play.
     *
     * @param game the game
     * @param self the card
     * @return the modifiers, or null
     */
    protected List<Modifier> getGameTextWhileOutOfPlayModifiers(SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect when
     * the card is 'outside of deck' during the start of the game.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect when
     * the card is active (or only undercover) in play.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect when
     * the card is inactive (except when only undercover) in play.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggersWhenInactiveInPlay(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "before" triggers to the specified effect when
     * the card is active (even if it is an unpiloted starship/vehicle) in play.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggersEvenIfUnpiloted(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect that
     * can be performed by the specified player when the card is active (or only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect when
     * the card is inactive (except when only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggersWhenInactiveInPlay(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect that
     * can be performed by the specified player when the card is active (even if it is an unpiloted starship/vehicle) in play.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggersEvenIfUnpiloted(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect that
     * can be performed by the specified player when the opponent's card is active in play. This does not include undercover
     * cards.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect that
     * can be performed by the specified player when the opponent's card is inactive in play. This does include undercover
     * cards.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggersWhenInactiveInPlay(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" triggers to the specified effect that
     * can be performed by the specified player when the opponent's card is active (even if it is an unpiloted starship/vehicle)
     * in play.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggersEvenIfUnpiloted(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * when the card is 'outside of deck' during the start of the game.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * when the card is active (or only undercover) in play.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * the card is inactive (except when only undercover) in play.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhenInactiveInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * when the card is active (even if it is an unpiloted starship/vehicle) in play.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersEvenIfUnpiloted(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * the card is in play in any card state (even if card inactive or game text is canceled).
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers to the specified effect result
     * the card is stacked on another card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersWhileStacked(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to require a maintenance cost action that triggers at the end of the
     * card owner's turn.
     * @param playerId the card owner
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected RequiredGameTextTriggerAction getGameTextMaintenanceCostAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        final StandardEffect maintainCost = getGameTextMaintenanceMaintainCost(action, playerId);
        final StandardEffect recycleCost = getGameTextMaintenanceRecycleCost(action, playerId);
        if (maintainCost == null && recycleCost == null)
            return null;

        action.setText("Satisfy maintenance cost");
        final boolean isMaintainValid = (maintainCost != null && maintainCost.isPlayableInFull(game));
        final boolean isRecycleValid = (recycleCost != null && recycleCost.isPlayableInFull(game));

        if (isMaintainValid || isRecycleValid) {
            List<String> optionsTextList = new ArrayList<String>();
            if (isMaintainValid) {
                optionsTextList.add(maintainCost.getText(game) + " to maintain");
            }
            if (isRecycleValid) {
                optionsTextList.add(recycleCost.getText(game) + " to recycle");
            }
            optionsTextList.add("Sacrifice");
            String[] optionTextArray = new String[optionsTextList.size()];
            optionsTextList.toArray(optionTextArray);

            // Perform result(s)
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new MultipleChoiceAwaitingDecision("Choose maintenance option for " + GameUtils.getCardLink(self), optionTextArray) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (isMaintainValid && index == 0) {
                                        game.getGameState().sendMessage(playerId + " chooses maintain option for " + GameUtils.getCardLink(self) + "'s maintenance");
                                        action.appendEffect(maintainCost);
                                    } else if (isRecycleValid && (index == 0 || (isMaintainValid && index == 1)))  {
                                        game.getGameState().sendMessage(playerId + " chooses recycle option for " + GameUtils.getCardLink(self) + "'s maintenance");
                                        action.appendEffect(recycleCost);
                                        action.appendEffect(
                                                new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.USED_PILE));
                                    } else {
                                        game.getGameState().sendMessage(playerId + " chooses sacrifice option for " + GameUtils.getCardLink(self) + "'s maintenance");
                                        action.appendEffect(
                                                new PlaceCardOutOfPlayFromTableEffect(action, self));
                                    }
                                }
                            }
                    )
            );
        }
        else {
            action.appendEffect(
                    new SendMessageEffect(action, "Only sacrifice option is available for " + GameUtils.getCardLink(self) + "'s maintenance"));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
        }

        return action;
    }

    /**
     * This method is overridden by individual cards to specify a maintenance cost for the maintain option.
     * @param action the action used to satisfy maintenance costs
     * @param playerId the card owner
     * @return the actions, or null
     */
    protected StandardEffect getGameTextMaintenanceMaintainCost(Action action, final String playerId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify a maintenance cost for the recycle option.
     * @param action the action used to satisfy maintenance costs
     * @param playerId the card owner
     * @return the actions, or null
     */
    protected StandardEffect getGameTextMaintenanceRecycleCost(Action action, final String playerId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required "after" triggers when the card is drawn for destiny.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers when the card is drawn for destiny
     * that can be performed by the specified player.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the card is active (or only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the card is inactive (except when only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenInactiveInPlay(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the card is active (even if it is an unpiloted starship/vehicle)
     * in play.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersEvenIfUnpiloted(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the card is stacked (face up) on another card
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenStacked(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the opponent's card is active in play. This does not include
     * undercover cards.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the opponent's card is inactive in play. This does include
     * undercover cards.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggersWhenInactiveInPlay(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" triggers to the specified effect result
     * that can be performed by the specified player when the opponent's card is active (even if it is an unpiloted starship/vehicle)
     * in play.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions, or null
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggersEvenIfUnpiloted(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is active (or only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is active during an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelAttackRunActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is inactive (except when only undercover) in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is active (even if it is an unpiloted starship/vehicle) in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is in hand.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelInHandActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the card is stacked (face up) on another card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the opponent's card is active in play. This does not include undercover cards.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the opponent's card is inactive in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActionsWhenInactiveInPlay(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions that can be performed by the specified
     * player when the opponent's card is active (even if it is an unpiloted starship/vehicle) in play.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the actions, or null
     */
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActionsEvenIfUnpiloted(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" play card actions to the specified
     * effect that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the play card actions, or null
     */
    protected List<PlayCardAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" play card actions to the specified effect
     * result that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the play card actions, or null
     */
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify required triggers from the card when that card leaves table.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions
     */
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional triggers from the card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions
     */
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional triggers from the card when that card leaves table.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextLeavesTableOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional triggers from the card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions
     */
    protected List<OptionalGameTextTriggerAction> getGameTextLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional triggers from the card when that card is lost from life force.
     * @param playerId the owner of the card
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @param gameTextSourceCardId the card id of the game text for this action comes from (when copied from another card)
     * @return the trigger actions
     */
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify card deploy requirements.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param playCardOptionId the play card option id
     * @param asReact true if as a 'react', otherwise false
     * @return true if requirements met, otherwise false
     */
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return true;
    }

    /**
     * This method is overridden by individual cards to specify the filter for valid deploy targets.
     * @param game the game
     * @param self the card
     * @param playCardOptionId the play card option id
     * @param asReact true if as a 'react', otherwise false
     * @return the filter
     */
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.any;
    }

    /**
     * This method is overridden by individual cards to valid target filter that the card can remain attached to after the
     * attached to card is crossed-over.
     * @param game the game
     * @param self the card
     * @param playCardOptionId the play card option id
     * @return the filter
     */
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.any;
    }

    /**
     * This method is overridden by individual cards to valid target filter that the card can remain attached to. If the
     * card becomes attached to a card that is not accepted by this filter, then the attached card will be lost by rule.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    /**
     * This method is overridden by individual cards to specify play card options.
     * @return the play card options, or null
     */
    protected List<PlayCardOption> getGameTextPlayCardOptions() {
        return null;
    }

    /**
     * This method is overridden by individual cards to get the fire weapon actions for each way the weapon (or card with
     * permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if playing card for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard the card to initiate the firing
     * @param repeatedFiring true if repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     * @return the fire weapon actions
     */
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return null;
    }

    /**
     * This method if overridden by individual cards to specify the filter for valid cards to use the weapon.
     * @param game the card
     * @param self the card
     * @return Filter
     */
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    /**
     * This method if overridden by individual cards to specify the filter for valid cards to use the device.
     * @param game the card
     * @param self the card
     * @return Filter
     */
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    /**
     * This method if overridden by individual cards to get effects (to be performed in order) that set any targeted cards
     * when the card is being deployed.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the targeting effects, or null
     */
    protected List<TargetingEffect> getGameTextTargetCardsWhenDeployedEffects(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }

    /**
     * This method if overridden by individual cards to get a special deploy cost (instead of using Force) that is used to deploy the card.
     * @param action the action to perform the effect
     * @param playerId the performing player
     * @param game the game
     * @param self the card
     * @param target the target to where the card is being deployed, or null (if side of table or card pile)
     * @param playCardOption the play card option chosen
     * @return the special deploy cost, or null
     */
    protected StandardEffect getGameTextSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return null;
    }
}
