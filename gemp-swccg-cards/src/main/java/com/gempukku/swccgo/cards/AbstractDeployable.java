package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.*;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.AttackRunState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.effects.DrawAsteroidDestinyEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.rules.DeathStarIISectorRule;
import com.gempukku.swccgo.logic.timing.rules.DrawAsteroidDestinyRule;

import java.util.*;

/**
 * The abstract class providing the common implementation for cards that are either deployed to the table (or side of table),
 * deployed to locations, or deployed on other cards.
 */
public abstract class AbstractDeployable extends AbstractNonLocationPlaysToTable {
    protected Filter _matchingCharacterFilter = Filters.none;
    protected Filter _matchingPilotFilter = Filters.none;
    protected Filter _matchingStarshipFilter = Filters.none;
    protected Filter _matchingVehicleFilter = Filters.none;
    protected Filter _matchingWeaponFilter = Filters.none;
    protected String _matchingSystem;
    private Integer _replacementCountForSquadron;
    private Filter _replacementFilterForSquadron;

    /**
     * Creates a blueprint for a deployable card.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param deployCost the deploy cost
     * @param title the card title
     */
    protected AbstractDeployable(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title) {
        this(side, destiny, playCardZoneOption, deployCost, title, null);
    }

    /**
     * Creates a blueprint for a deployable card.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param deployCost the deploy cost
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractDeployable(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title, Uniqueness uniqueness) {
        this(side, destiny, playCardZoneOption, deployCost, title, uniqueness, null, null);
    }

    /**
     * Creates a blueprint for a deployable card.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param playCardZoneOption the zone option for playing the card, or null if card has multiple play options
     * @param deployCost the deploy cost
     * @param title the card title
     * @param uniqueness the uniqueness
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractDeployable(Side side, Float destiny, PlayCardZoneOption playCardZoneOption, Float deployCost, String title, Uniqueness uniqueness, ExpansionSet expansionSet, Rarity rarity) {
        super(side, destiny, playCardZoneOption, deployCost, title, uniqueness, expansionSet, rarity);
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
        return true;
    }

    /**
     * Determines if the card can be played during the current phase.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if card can be played during the current phase, otherwise false
     */
    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY) ||
                game.getModifiersQuerying().grantedDeployDuringCurrentPhase(game.getGameState(), self);
    }

    /**
     * Gets the card blueprint of the permanent weapon.
     * @param self the card
     * @return the card blueprint
     */
    @Override
    public final SwccgBuiltInCardBlueprint getPermanentWeapon(PhysicalCard self) {
        SwccgBuiltInCardBlueprint permanentWeapon = getGameTextPermanentWeapon();
        if (permanentWeapon != null) {
            permanentWeapon.setPhysicalCard(self);
            permanentWeapon.setBuiltInId(1);
        }
        return permanentWeapon;
    }

    /**
     * Gets the card blueprints of permanent pilots and astromechs aboard.
     * @param self the card
     * @return list of card blueprints
     */
    @Override
    public final List<SwccgBuiltInCardBlueprint> getPermanentsAboard(PhysicalCard self) {
        List<? extends AbstractPermanentAboard> permanentsAboard = getGameTextPermanentsAboard();
        if (permanentsAboard != null) {
            int id = 1;
            for (SwccgBuiltInCardBlueprint permanentAboard : permanentsAboard) {
                permanentAboard.setPhysicalCard(self);
                permanentAboard.setBuiltInId(id++);
            }
            return new ArrayList<SwccgBuiltInCardBlueprint>(permanentsAboard);
        }
        return Collections.emptyList();
    }

    /**
     * Gets the deploy as 'react' action for the card if it can deploy as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param deployTargetFilter the filter for where the card can deploy
     * @return the action
     */
    @Override
    public Action getDeployAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter deployTargetFilter) {
        // Check if player's character, device, starship, vehicle, or weapon.
        if (!playerId.equals(self.getOwner())
                || (!Filters.or(Filters.character, Filters.device, Filters.starship, Filters.vehicle, Filters.weapon).accepts(game, self))) {
            return null;
        }

        Filter completeDeployTargetFilter = Filters.and(deployTargetFilter, Filters.locationAndCardsAtLocation(Filters.sameCardId(game.getGameState().getBattleOrForceDrainLocation())));

        // If not 'react' action from another card, then card must deploy as 'react' from hand (or as if from hand).
        if (reactActionFromOtherCard == null
                && !Filters.or(Filters.inHand(playerId), Filters.canDeployAsIfFromHand).accepts(game, self)) {
            return null;
        }

        // Get the react option, if any.
        ReactActionOption reactActionOption = game.getModifiersQuerying().getDeployAsReactOption(game.getGameState(), self, reactActionFromOtherCard, completeDeployTargetFilter);
        if (reactActionOption == null) {
            return null;
        }

        // Get the play card action using the react option, if any.
        PlayCardAction playCardAction = getPlayCardAction(playerId, game, self, reactActionFromOtherCard != null ? reactActionFromOtherCard.getSource() : self,
                reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(game, self)),
                reactActionOption.getChangeInCost(), null, null, null, reactActionOption, null, false, 0, completeDeployTargetFilter, null);
        if (playCardAction == null) {
            return null;
        }

        return playCardAction;
    }

    /**
     * Determines if the device or weapon can be transferred.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param playCardOption the play card option, or null
     * @return true if card can be transferred, otherwise false
     */
    private boolean checkTransferDeviceOrWeaponRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOption playCardOption) {
        return (self.getBlueprint().getCardCategory() == CardCategory.DEVICE || self.getBlueprint().getCardCategory() == CardCategory.WEAPON)
                && playCardOption.getZone() == Zone.ATTACHED
                && self.getAttachedTo() != null && Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)).accepts(game, self.getAttachedTo())
                && checkPlayRequirementsByCheckingGameText(playerId, game, self, null, playCardOption, null);
    }

    /**
     * Gets the transfer device or weapon actions for each way the device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the transfer device or weapon actions
     */
    private List<Action> getTransferDeviceOrWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter transferTargetFilter) {
        List<Action> transferCardActions = new ArrayList<Action>();

        if ((self.getBlueprint().getCardCategory() == CardCategory.DEVICE || self.getBlueprint().getCardCategory() == CardCategory.WEAPON)) {
            for (PlayCardOption playCardOption : getPlayCardOptions(playerId, game)) {
                if (checkTransferDeviceOrWeaponRequirements(playerId, game, self, playCardOption)) {

                    TransferDeviceOrWeaponAction transferDeviceOrWeaponAction = null;

                    // Determine the spot override to use when transferring the card using this play card option
                    Map<InactiveReason, Boolean> spotOverrides = self.getBlueprint().getDeployTargetSpotOverride(playCardOption.getId());

                    Filter completeTargetFilter = getValidTransferDeviceOrWeaponTargetFilter(playerId, game, self, playCardOption, forFree, transferTargetFilter);

                    // Check that a valid target to transfer to as attached can be found
                    if (Filters.canSpot(game, self, spotOverrides, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter)) {
                        transferDeviceOrWeaponAction = new TransferDeviceOrWeaponAction(playerId, self, playCardOption, forFree, completeTargetFilter);
                    }

                    // Set the text (if needed) and add the transfer device or weapon action to the list
                    if (transferDeviceOrWeaponAction != null) {
                        if (playCardOption.getText() != null) {
                            transferDeviceOrWeaponAction.setText(playCardOption.getText().replace("Deploy on", "Transfer to"));
                        }
                        transferCardActions.add(transferDeviceOrWeaponAction);
                    }
                }
            }
        }

        return transferCardActions;
    }

    /**
     * Gets the transfer device or weapon action if the device or weapon can be transferred by the specified player.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param transferTargetFilter the filter for where the card can be transferred
     * @return the transfer device or weapon actions
     */
    @Override
    public Action getTransferDeviceOrWeaponAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter transferTargetFilter) {
        // Get the transfer actions
        List<Action> transferActions = getTransferDeviceOrWeaponActions(playerId, game, self, forFree, transferTargetFilter);

        if (transferActions.isEmpty())
            return null;

        if (transferActions.size() == 1)
            return transferActions.get(0);

        return new ChoiceAction(playerId, "Choose transfer action", transferActions);
    }

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
    @Override
    public Filter getValidTransferDeviceOrWeaponTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PlayCardOption playCardOption, boolean forFree, Filter transferTargetFilter) {
        return Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle), Filters.not(Filters.hasAttached(self)),
                Filters.not(Filters.attachedToWithRecursiveChecking(self)), Filters.atSameLocation(self), transferTargetFilter, getValidTransferTargetFilter(playerId, game, self, playCardOption, forFree));
    }

    /**
     * Determines if the requirements for any type of movement are fulfilled.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    protected boolean checkMoveRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return (self.getZone() == Zone.AT_LOCATION || self.getZone() == Zone.ATTACHED)
                && playerId.equals(game.getModifiersQuerying().isMovedOnlyByOpponent(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())
                && !game.getModifiersQuerying().mayNotMove(game.getGameState(), self);
    }

    /**
     * Determines if the requirements for any type of regular movement are fulfilled.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return true or false
     */
    protected boolean checkRegularMoveRequirements(String playerId, SwccgGame game, PhysicalCard self, boolean asAdditionalMove) {
        return !self.isCaptive() && checkMoveRequirements(playerId, game, self)
                && (asAdditionalMove == game.getModifiersQuerying().hasPerformedRegularMoveThisTurn(self));
    }

    /**
     * Determines if the requirements for any type of unlimited movement are fulfilled.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    protected boolean checkUnlimitedMoveRequirements(String playerId, SwccgGame game, PhysicalCard self, boolean skipMovePhaseCheck) {
        return (skipMovePhaseCheck || GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner()))
                && !self.isCaptive() && !self.isMakingBombingRun() && checkMoveRequirements(playerId, game, self) && !game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self);
    }

    /**
     * Gets a filter for the cards the specified card is not prohibited from moving to.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param allowTrench true if moving to Death Star: Trench is not prevented by "Trench Rules" for this movement
     * @return the filter
     */
    @Override
    public Filter getValidMoveTargetFilter(String playerId, final SwccgGame game, final PhysicalCard self, boolean allowTrench) {
        if (game.getModifiersQuerying().mayNotMove(game.getGameState(), self))
            return Filters.none;

        // Cannot move to holosite
        Filter filter = Filters.and(Filters.not(Filters.holosite), Filters.notProhibitedFromTarget(self));

        // Cannot move to Death Star: Trench unless not prevented for this movement
        if (!allowTrench) {
            filter = Filters.and(filter, Filters.not(Filters.Death_Star_Trench));
        }

        // Filter cards that a this type of card can move to (based on game rules for that card type/subtype, etc.)
        filter = Filters.and(filter, getValidMoveTargetFilterForCardType(playerId, game, self));

        return filter;
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
    protected Filter getValidMoveTargetFilterForCardType(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    /**
     * Gets the valid place card target filter based on the game rules for this type, subtype, etc. of card.
     * This method is overridden when a card type, subtype, etc. has special rules about where it can be placed to.
     * @param game the game
     * @param self the card
     * @return the place card to target filter based on the card type, subtype, etc.
     */
    protected Filter getValidPlaceCardTargetFilterForCardType(final SwccgGame game, final PhysicalCard self) {
        return getValidMoveTargetFilterForCardType(self.getOwner(), game, self);
    }

    /**
     * Gets the move as 'react' action for the card if it can move as a 'react'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param reactActionFromOtherCard a 'react' action if 'react' originates from another card, or null
     * @param moveTargetFilter the filter for where the card can move
     * @return the action
     */
    @Override
    public Action getMoveAsReactAction(String playerId, SwccgGame game, PhysicalCard self, ReactActionOption reactActionFromOtherCard, Filter moveTargetFilter) {
        // Check if player's character, starship, or vehicle.
        if (!playerId.equals(self.getOwner())
                || (!Filters.or(Filters.character, Filters.starship, Filters.vehicle).accepts(game, self))) {
            return null;
        }

        PhysicalCard battleOrForceDrainLocation = game.getGameState().getBattleOrForceDrainLocation();
        if (battleOrForceDrainLocation == null) {
            return null;
        }

        boolean asReactAway = Filters.at(battleOrForceDrainLocation).accepts(game, self);
        Filter completeMoveTargetFilter = Filters.and(moveTargetFilter, Filters.location, asReactAway ? Filters.not(battleOrForceDrainLocation) : battleOrForceDrainLocation);

        // Get the react option, if any.
        ReactActionOption reactActionOption = game.getModifiersQuerying().getMoveAsReactOption(game.getGameState(), self, reactActionFromOtherCard, asReactAway, completeMoveTargetFilter);
        if (reactActionOption == null) {
            return null;
        }
        boolean isReactForFree = reactActionOption.isForFree() || (reactActionOption.getForFreeCardFilter() != null && reactActionOption.getForFreeCardFilter().accepts(game, self));

        List<Action> moveAsReactActions = new ArrayList<Action>();

        // Move as 'react' using landspeed
        Action moveUsingLandspeedAction = getMoveUsingLandspeedAction(playerId, game, self, isReactForFree, reactActionOption.getChangeInCost(), true, reactActionOption.isReactAway(), true, false, null, completeMoveTargetFilter);
        if (moveUsingLandspeedAction != null) {
            Filter moveUsingLandspeedTargetFilter = getMoveUsingLandspeedFilter(playerId, game, self, isReactForFree, reactActionOption.getChangeInCost(), true, reactActionOption.isReactAway(), null, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.MOVING_AS_REACT_USING_LANDSPEED, moveUsingLandspeedTargetFilter));
        }

        // Move as 'react' using hyperspeed
        Action moveUsingHyperspeedAction = getMoveUsingHyperspeedAction(playerId, game, self, isReactForFree, true, reactActionOption.isReactAway(), true, false, completeMoveTargetFilter);
        if (moveUsingHyperspeedAction != null) {
            Filter moveUsingHyperspeedTargetFilter = getMoveUsingHyperspeedFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.MOVING_AS_REACT_USING_HYPERSPEED, moveUsingHyperspeedTargetFilter));
        }

        // Move as 'react' without using hyperspeed
        Action moveWithoutUsingHyperspeedAction = getMoveWithoutUsingHyperspeedAction(playerId, game, self, isReactForFree, true, reactActionOption.isReactAway(), true, false, completeMoveTargetFilter);
        if (moveWithoutUsingHyperspeedAction != null) {
            Filter moveWithoutUsingHyperspeedTargetFilter = getMoveUsingWithoutHyperspeedFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.MOVING_AS_REACT_WITHOUT_USING_HYPERSPEED, moveWithoutUsingHyperspeedTargetFilter));
        }

        // Move as 'react' using sector movement
        Action moveUsingSectorMovementAction = getMoveUsingSectorMovementAction(playerId, game, self, isReactForFree, true, reactActionOption.isReactAway(), true, false, completeMoveTargetFilter);
        if (moveUsingSectorMovementAction != null) {
            Filter moveUsingSectorMovementTargetFilter = getMoveUsingSectorMovementFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.MOVING_AS_REACT_USING_SECTOR_MOVEMENT, moveUsingSectorMovementTargetFilter));
        }

        // Move as 'react' by landing
        Action landAction = getLandAction(playerId, game, self, isReactForFree, true, true, false, completeMoveTargetFilter);
        if (landAction != null) {
            Filter landTargetFilter = getLandFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.LANDING_AS_REACT, landTargetFilter));
        }

        // Move as 'react' by taking off
        Action takeOffAction = getTakeOffAction(playerId, game, self, isReactForFree, true, true, false, completeMoveTargetFilter);
        if (takeOffAction != null) {
            Filter takeOffTargetFilter = getTakeOffFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.TAKING_OFF_AS_REACT, takeOffTargetFilter));
        }

        // Move as 'react' by entering starship or vehicle site
        Action enterAction = getEnterStarshipOrVehicleSiteAction(playerId, game, self, isReactForFree, true, true, false, completeMoveTargetFilter);
        if (enterAction != null) {
            Filter enterTargetFilter = getEnterStarshipOrVehicleSiteFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.ENTERING_STARSHIP_VEHICLE_SITE_AS_REACT, enterTargetFilter));
        }

        // Move as 'react' by exiting starship or vehicle site
        Action exitAction = getExitStarshipOrVehicleSiteAction(playerId, game, self, isReactForFree, true, true, false, completeMoveTargetFilter);
        if (exitAction != null) {
            Filter exitTargetFilter = getExitStarshipOrVehicleSiteFilter(playerId, game, self, isReactForFree, true, completeMoveTargetFilter);
            moveAsReactActions.add(new MoveAsReactAction(playerId, self, reactActionOption, Effect.Type.EXITING_STARSHIP_VEHICLE_SITE_AS_REACT, exitTargetFilter));
        }

        if (moveAsReactActions.isEmpty())
            return null;

        if (moveAsReactActions.size() == 1)
            return moveAsReactActions.get(0);

        return new ChoiceAction(playerId, "Choose move " + (asReactAway ? "away " : "") +  "as 'react' action", moveAsReactActions);
    }

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
    @Override
    public Action getMoveAwayAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, false))
            return null;

        List<Action> moveAwayActions = new ArrayList<Action>();

        // Move away using landspeed
        Action moveUsingLandspeedAction = getMoveUsingLandspeedAction(playerId, game, self, forFree, changeInCost, asReact, true, false, false, null, moveTargetFilter);
        if (moveUsingLandspeedAction != null) {
            moveAwayActions.add(moveUsingLandspeedAction);
        }

        // Move away using hyperspeed
        Action moveUsingHyperspeedAction = getMoveUsingHyperspeedAction(playerId, game, self, forFree, asReact, true, false, false, moveTargetFilter);
        if (moveUsingHyperspeedAction != null) {
            moveAwayActions.add(moveUsingHyperspeedAction);
        }

        // Move away without using hyperspeed
        Action moveWithoutUsingHyperspeedAction = getMoveWithoutUsingHyperspeedAction(playerId, game, self, forFree, asReact, true, false, false, moveTargetFilter);
        if (moveWithoutUsingHyperspeedAction != null) {
            moveAwayActions.add(moveWithoutUsingHyperspeedAction);
        }

        // Move away using sector movement
        Action moveUsingSectorMovementAction = getMoveUsingSectorMovementAction(playerId, game, self, forFree, asReact, true, false, false, moveTargetFilter);
        if (moveUsingSectorMovementAction != null) {
            moveAwayActions.add(moveUsingSectorMovementAction);
        }

        if (moveAwayActions.isEmpty())
            return null;

        if (moveAwayActions.size() == 1)
            return moveAwayActions.get(0);

        return new ChoiceAction(playerId, "Choose move away action", moveAwayActions);
    }

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
    @Override
    public Action getRegularMoveAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        List<Action> regularMoveActions = new ArrayList<Action>();

        // Move using landspeed
        Action moveUsingLandspeedAction = getMoveUsingLandspeedAction(playerId, game, self, forFree, changeInCost, false, true, skipPhaseCheck, asAdditionalMove, null, moveTargetFilter);
        if (moveUsingLandspeedAction != null) {
            regularMoveActions.add(moveUsingLandspeedAction);
        }

        // Move using hyperspeed
        Action moveUsingHyperspeedAction = getMoveUsingHyperspeedAction(playerId, game, self, forFree, false, true, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (moveUsingHyperspeedAction != null) {
            regularMoveActions.add(moveUsingHyperspeedAction);
        }

        // Move without using hyperspeed
        Action moveWithoutUsingHyperspeedAction = getMoveWithoutUsingHyperspeedAction(playerId, game, self, forFree, false, true, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (moveWithoutUsingHyperspeedAction != null) {
            regularMoveActions.add(moveWithoutUsingHyperspeedAction);
        }

        // Move using sector movement
        Action moveUsingSectorMovementAction = getMoveUsingSectorMovementAction(playerId, game, self, forFree, false, true, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (moveUsingSectorMovementAction != null) {
            regularMoveActions.add(moveUsingSectorMovementAction);
        }

        // Land
        Action landAction = getLandAction(playerId, game, self, forFree, false, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (landAction != null) {
            regularMoveActions.add(landAction);
        }

        // Take off
        Action takeOffAction = getTakeOffAction(playerId, game, self, forFree, false, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (takeOffAction != null) {
            regularMoveActions.add(takeOffAction);
        }

        // Start Bombing Run
        Action startBombingRunAction = getMoveToStartBombingRunAction(playerId, game, self, skipPhaseCheck, asAdditionalMove);
        if (startBombingRunAction != null) {
            regularMoveActions.add(startBombingRunAction);
        }

        // End Bombing Run
        Action endBombingRunAction = getMoveToEndBombingRunAction(playerId, game, self, skipPhaseCheck, asAdditionalMove);
        if (endBombingRunAction != null) {
            regularMoveActions.add(endBombingRunAction);
        }

        // Move to related starship/vehicle
        Action moveToRelatedStarshipOrVehicleAction = getMoveToRelatedStarshipOrVehicleAction(playerId, game, self, skipPhaseCheck, asAdditionalMove);
        if (moveToRelatedStarshipOrVehicleAction != null) {
            regularMoveActions.add(moveToRelatedStarshipOrVehicleAction);
        }

        // Move to related starship/vehicle site
        Action moveToRelatedStarshipOrVehicleSiteAction = getMoveToRelatedStarshipOrVehicleSiteAction(playerId, game, self, skipPhaseCheck, asAdditionalMove);
        if (moveToRelatedStarshipOrVehicleSiteAction != null) {
            regularMoveActions.add(moveToRelatedStarshipOrVehicleSiteAction);
        }

        // Enter starship/vehicle site
        Action enterStarshipOrVehicleSiteAction = getEnterStarshipOrVehicleSiteAction(playerId, game, self, forFree, false, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (enterStarshipOrVehicleSiteAction != null) {
            regularMoveActions.add(enterStarshipOrVehicleSiteAction);
        }

        // Exit starship/vehicle site
        Action exitStarshipOrVehicleSiteAction = getExitStarshipOrVehicleSiteAction(playerId, game, self, forFree, false, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (exitStarshipOrVehicleSiteAction != null) {
            regularMoveActions.add(exitStarshipOrVehicleSiteAction);
        }

        // Shuttle
        Action shuttleAction = getShuttleAction(playerId, game, self, forFree, skipPhaseCheck, asAdditionalMove, moveTargetFilter);
        if (shuttleAction != null) {
            regularMoveActions.add(shuttleAction);
        }

        if (regularMoveActions.isEmpty())
            return null;

        if (regularMoveActions.size() == 1)
            return regularMoveActions.get(0);

        return new ChoiceAction(playerId, "Choose regular move action", regularMoveActions);
    }

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
    @Override
    public Action getMoveUsingLandspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Integer landspeedOverride, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !asMoveAway && !skipPhaseCheck) {
            if (game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self)) {
                if (!GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getOpponent(self.getOwner()))) {
                    return null;
                }
            }
            else {
                if (!GameConditions.isPhaseForPlayer(game, (game.getModifiersQuerying().isMovesUsingLandspeedOnlyDuringDeployPhase(game.getGameState(), self) ? Phase.DEPLOY : Phase.MOVE), self.getOwner())) {
                    return null;
                }
            }
        }

        Filter completeTargetFilter = getMoveUsingLandspeedFilter(playerId, game, self, forFree, changeInCost, asReact, asMoveAway, landspeedOverride, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveUsingLandspeedAction(playerId, game, self, forFree, changeInCost, asReact, asMoveAway, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can move to using landspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param changeInCost change in amount of Force (can be positive or negative) required to perform the movement
     * @param asReact true if moving as a 'react', otherwise false
     * @param asMoveAway true if moving as a move away, otherwise false
     * @param landspeedOverride the specified landspeed to use for this movement, or null if using normal landspeed
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getMoveUsingLandspeedFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, float changeInCost, boolean asReact, boolean asMoveAway, Integer landspeedOverride, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToUsingLandspeed(playerId, self, asReact, asMoveAway, forFree, changeInCost, landspeedOverride));
    }

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
     * @return the action
     */
    @Override
    public Action getMoveUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !asMoveAway && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getMoveUsingHyperspeedFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveStarshipUsingHyperspeedAction(playerId, self, forFree, asReact, asMoveAway, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can move to using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getMoveUsingHyperspeedFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToUsingHyperspeed(playerId, self, asReact, forFree, 0));
    }

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
    @Override
    public Action getMoveWithoutUsingHyperspeedAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !asMoveAway && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getMoveUsingWithoutHyperspeedFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveStarshipWithoutUsingHyperspeedAction(playerId, self, forFree, asReact, asMoveAway, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can move to using without using hyperspeed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getMoveUsingWithoutHyperspeedFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToWithoutUsingHyperspeed(playerId, self, asReact, forFree, 0));
    }

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
    @Override
    public Action getMoveUsingSectorMovementAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean asMoveAway, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !asMoveAway && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getMoveUsingSectorMovementFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveUsingSectorMovementAction(playerId, self, forFree, asReact, asMoveAway, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the move using sector movement action for the card if it can move using sector movement during 'escape' from
     * Death Star II being 'blown away'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveUsingEscapeFromDeathStarIIMovementAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, true))
            return null;

        Filter completeTargetFilter = getMoveUsingSectorMovementFilter(playerId, game, self, true, false, Filters.or(Filters.Death_Star_II_system, Filters.Death_Star_II_sector));

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveUsingSectorMovementAction(playerId, self, true, false, false, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can move to using without using sector movement.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getMoveUsingSectorMovementFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToUsingSectorMovement(playerId, self, asReact, forFree, 0));
    }

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
    @Override
    public Action getLandAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (game.getModifiersQuerying().landsAsUnlimitedMove(game.getGameState(), self)) {
            if (!checkUnlimitedMoveRequirements(playerId, game, self, asAdditionalMove))
                return null;
        } else if(!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove)) {
            return null;
        }

        if (!asReact && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getLandFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new LandAction(playerId, self, forFree, asReact, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can land.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getLandFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canLandToLocation(playerId, self, asReact, forFree, 0));
    }

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
    @Override
    public Action getTakeOffAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (game.getModifiersQuerying().takesOffAsUnlimitedMove(game.getGameState(), self)) {
            if (!checkUnlimitedMoveRequirements(playerId, game, self, asAdditionalMove))
                return null;
        } else if(!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove)) {
            return null;
        } 

        if (!asReact && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getTakeOffFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new TakeOffAction(playerId, self, forFree, asReact, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can take off.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getTakeOffFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canTakeOffToLocation(playerId, self, asReact, forFree, 0));
    }

    /**
     * Gets the move action for a bomber at the start of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToStartBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!Filters.bomber.accepts(game, self))
            return null;

        if (!skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToLocationToStartBombingRun(playerId, self, false, 0));

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveToStartBombingRunAction(playerId, self, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the move action for a bomber at the end of a Bombing Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToEndBombingRunAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToLocationToEndBombingRun(playerId, self));

        // Check that a valid location to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveToEndBombingRunAction(playerId, self, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the move action for a starfighter or squadron at the start of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveAtStartOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (self.getOwner().equals(game.getLightPlayer())) {
            if (!checkRegularMoveRequirements(playerId, game, self, false)) {
                return null;
            }
        }
        else {
            if (!checkUnlimitedMoveRequirements(playerId, game, self, true)) {
                return null;
            }
        }

        if (!Filters.or(Filters.starfighter, Filters.squadron).accepts(game, self)) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(Filters.Death_Star_Trench, getValidMoveTargetFilter(playerId, game, self, true), Filters.canMoveToLocationAtStartOfAttackRun(playerId, self));

        // Check that a valid location to move to can be found
        PhysicalCard trench = Filters.findFirstFromTopLocationsOnTable(game, completeTargetFilter);
        if (trench != null) {
            return new MoveAtStartOfAttackRunAction(playerId, self, trench);
        }

        return null;
    }

    /**
     * Gets the move action for a starfighter at the end of an Attack Run.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveAtEndOfAttackRunAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, true))
            return null;

        if (!Filters.starfighter.accepts(game, self)) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(Filters.Death_Star_system, getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToLocationAtEndOfAttackRun(playerId, self));

        // Check that a valid location to move to can be found
        PhysicalCard deathStar = Filters.findFirstFromTopLocationsOnTable(game, completeTargetFilter);
        if (deathStar != null) {
            return new MoveAtEndOfAttackRunAction(playerId, self, deathStar);
        }

        return null;
    }


    /**
     * Gets the action to move to the related starship/vehicle from a starship/vehicle site.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToRelatedStarshipOrVehicleAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToRelatedStarshipOrVehicle(playerId, self));

        // Check that a valid starship/vehicle to move to can be found
        if (Filters.canSpot(game, null, completeTargetFilter)) {
            return new MoveToRelatedStarshipOrVehicleAction(playerId, game, self, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the action to move to a related starship/vehicle site from the starship/vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getMoveToRelatedStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean skipPhaseCheck, boolean asAdditionalMove) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(getValidMoveTargetFilter(playerId, game, self, false), Filters.canMoveToRelatedStarshipOrVehicleSite(playerId, self));

        // Check that a valid starship/vehicle site to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new MoveToRelatedStarshipOrVehicleSiteAction(playerId, self, completeTargetFilter);
        }

        return null;
    }

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
     */
    @Override
    public Action getEnterStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getEnterStarshipOrVehicleSiteFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid starship/vehicle site to enter from current site can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new EnterStarshipOrVehicleSiteAction(playerId, self, forFree, asReact, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can enter a starship or vehicle site.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getEnterStarshipOrVehicleSiteFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canEnterStarshipOrVehicleSite(playerId, self, asReact, forFree, 0));
    }

    /**
     * Gets the action to exit a starship/vehicle site to the site the starship or vehicle is present at.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param skipPhaseCheck true if checking for valid phase to move is skipped, otherwise false
     * @param asAdditionalMove true if the move is as an additional move, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getExitStarshipOrVehicleSiteAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!asReact && !skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = getExitStarshipOrVehicleSiteFilter(playerId, game, self, forFree, asReact, moveTargetFilter);

        // Check that a valid site to exit current starship/vehicle site to move to can be found
        if (Filters.canSpotFromTopLocationsOnTable(game, completeTargetFilter)) {
            return new ExitStarshipOrVehicleSiteAction(playerId, self, forFree, asReact, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the filter for where the card can exit a starship or vehicle site.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param asReact true if moving as a 'react', otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the filter
     */
    private Filter getExitStarshipOrVehicleSiteFilter(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asReact, Filter moveTargetFilter) {
        return Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canExitStarshipOrVehicleSite(playerId, self, asReact, forFree, 0));
    }

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
    @Override
    public Action getShuttleAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean skipPhaseCheck, boolean asAdditionalMove, Filter moveTargetFilter) {
        if (!checkRegularMoveRequirements(playerId, game, self, asAdditionalMove))
            return null;

        if (!skipPhaseCheck
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        Filter completeTargetFilter = Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canShuttleTo(playerId, self, forFree, 0));

        // Check that a valid card to shuttle to can be found
        if (Filters.canSpot(game, null, completeTargetFilter)) {
            return new ShuttleAction(playerId, game, self, forFree, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the action to shuttle characters from a site to a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getShuttleUpUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkRegularMoveRequirements(playerId, game, self, false))
            return null;

        if (!GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        // Check that a valid card to shuttle using shuttle vehicle can be found
        if (Filters.shuttle_vehicle.accepts(game, self)
                && playerId.equals(self.getOwner())
                && game.getModifiersQuerying().isPiloted(game.getGameState(), self, false)
                && Filters.canSpot(game, null, Filters.canShuttleUpUsingShuttleVehicle(self, Filters.any))) {
            return new ShuttleUpUsingShuttleVehicleAction(playerId, game, self);
        }

        return null;
    }

    /**
     * Gets the action to shuttle characters to a site from a starship at the related system using a shuttle vehicle.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getShuttleDownUsingShuttleVehicleAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkRegularMoveRequirements(playerId, game, self, false))
            return null;

        if (!GameConditions.isPhaseForPlayer(game, Phase.MOVE, game.getModifiersQuerying().isDeploysAndMovesLikeUndercoverSpy(game.getGameState(), self) ? game.getOpponent(self.getOwner()) : self.getOwner())) {
            return null;
        }

        if (game.getModifiersQuerying().mayOnlyMoveUsingLandspeed(game.getGameState(), self)) {
            return null;
        }

        // Check that a valid card to shuttle using shuttle vehicle can be found
        if (Filters.shuttle_vehicle.accepts(game, self)
                && playerId.equals(self.getOwner())
                && game.getModifiersQuerying().isPiloted(game.getGameState(), self, false)
                && Filters.canSpot(game, null, Filters.canShuttleDownUsingShuttleVehicle(self, Filters.any))) {
            return new ShuttleDownUsingShuttleVehicleAction(playerId, game, self);
        }

        return null;
    }

    /**
     * Gets the action to ship-dock a starship with another starship.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @return the action, or null
     */
    @Override
    public Action getShipdockAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, false))
            return null;

        // Check that a valid starship to ship-dock with can be found
        if (self.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                && playerId.equals(self.getOwner())) {
            Filter completeTargetFilter = Filters.canShipdockWith(self, forFree, 0);

            if (Filters.canSpot(game, null, completeTargetFilter)) {
                return new ShipdockingAction(playerId, self, forFree, completeTargetFilter);
            }
        }

        return null;
    }

    /**
     * Gets the action to embark on a card (or to a location).
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if moving for free, otherwise false
     * @param moveTargetFilter the filter for where the card can move
     * @return the action, or null
     */
    @Override
    public Action getEmbarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, Filter moveTargetFilter) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, game.getGameState().isDuringMoveAsReact()))
            return null;

        // Captured starships can't embark
        if (self.isCapturedStarship())
            return null;


        Filter completeTargetFilter = Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canEmbarkTo(playerId, self, forFree, 0));

        // Check that a valid card to embark on can be found
        if (Filters.canSpot(game, null, completeTargetFilter)) {
            return new EmbarkAction(playerId, game, self, forFree, completeTargetFilter);
        }

        return null;
    }

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
    @Override
    public Action getDisembarkAction(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, boolean asJumpOff, Filter moveTargetFilter) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, asJumpOff || game.getGameState().isDuringMoveAsReact()))
            return null;

        // Captured starships can't disembark
        if (self.isCapturedStarship())
            return null;

        Filter completeTargetFilter = Filters.and(moveTargetFilter, getValidMoveTargetFilter(playerId, game, self, false), Filters.canDisembarkTo(playerId, self, forFree, 0));

        // Check that a valid card to disembark to can be found
        if (Filters.canSpot(game, null, completeTargetFilter)) {
            return new DisembarkAction(playerId, game, self, forFree, completeTargetFilter);
        }

        return null;
    }

    /**
     * Gets the action to move between the capacity slots of a card.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveBetweenCapacitySlotsAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, true))
            return null;

        if (!GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)
                && !GameConditions.isPhaseForPlayer(game, Phase.MOVE, playerId))
            return null;

        // Check that moving between capacity slots is valid action
        if (playerId.equals(self.getOwner())
                && checkMoveBetweenCapacitySlotsRequirements(game, self)) {
            return new MoveBetweenCapacitySlotsAction(playerId, self);
        }

        return null;
    }

    /**
     * Gets the action to move between ship-docked starships.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getMoveBetweenDockedStarshipsAction(String playerId, SwccgGame game, PhysicalCard self) {
        // Check that moving between capacity slots is valid action
        if (checkMoveBetweenDockedStarshipRequirements(game, self)) {
            return new TransferBetweenDockedStarshipsAction(playerId, game, self);
        }

        return null;
    }

    /**
     * Gets the action to deliver an escorted captive to prison.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getDeliverCaptiveToPrisonAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, false))
            return null;

        if (checkDeliverCaptiveToPrisonRequirements(playerId, game, self)) {
            return new DeliverCaptiveToPrisonAction(game, self);
        }

        return null;
    }

    /**
     * Gets the action to take an imprisoned captive from prison into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getTakeImprisonedCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, false))
            return null;

        if (checkTakeImprisonedCaptiveIntoCustodyRequirements(playerId, game, self)) {
            return new TakeImprisonedCaptiveIntoCustodyAction(game, self);
        }

        return null;
    }

    /**
     * Gets the action to leave an escorted 'frozen' captive as 'unattended'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getLeaveFrozenCaptiveUnattendedAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, false))
            return null;

        if (checkLeaveFrozenCaptiveUnattendedRequirements(playerId, game, self)) {
            return new LeaveFrozenCaptiveUnattendedAction(game, self);
        }

        return null;
    }

    /**
     * Gets the action to take an 'unattended frozen' captive into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the action, or null
     */
    @Override
    public Action getTakeUnattendedFrozenCaptiveIntoCustodyAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!checkUnlimitedMoveRequirements(playerId, game, self, false))
            return null;

        if (checkTakeUnattendedFrozenCaptiveIntoCustodyRequirements(playerId, game, self)) {
            return new TakeUnattendedFrozenCaptiveIntoCustodyAction(game, self);
        }

        return null;
    }

    /**
     * Gets actions involving card movement that can be performed.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions
     */
    protected List<Action> getMoveCardActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        // Move using landspeed
        Action moveUsingLandspeedAction = getMoveUsingLandspeedAction(playerId, game, self, false, 0, false, false, false, false, null, Filters.any);
        if (moveUsingLandspeedAction != null) {
            actions.add(moveUsingLandspeedAction);
        }

        // Move using hyperspeed
        Action moveUsingHyperspeedAction = getMoveUsingHyperspeedAction(playerId, game, self, false, false, false, false, false, Filters.any);
        if (moveUsingHyperspeedAction != null) {
            actions.add(moveUsingHyperspeedAction);
        }

        // Move without using hyperspeed
        Action moveWithoutUsingHyperspeedAction = getMoveWithoutUsingHyperspeedAction(playerId, game, self, false, false, false, false, false, Filters.any);
        if (moveWithoutUsingHyperspeedAction != null) {
            actions.add(moveWithoutUsingHyperspeedAction);
        }

        // Move using sector movement
        Action moveUsingSectorMovementAction = getMoveUsingSectorMovementAction(playerId, game, self, false, false, false, false, false, Filters.any);
        if (moveUsingSectorMovementAction != null) {
            actions.add(moveUsingSectorMovementAction);
        }

        // Land
        Action landAction = getLandAction(playerId, game, self, false, false, false, false, Filters.any);
        if (landAction != null) {
            actions.add(landAction);
        }

        // Take off
        Action takeOffAction = getTakeOffAction(playerId, game, self, false, false, false, false, Filters.any);
        if (takeOffAction != null) {
            actions.add(takeOffAction);
        }

        // Start bombing run
        Action startBombingRunAction = getMoveToStartBombingRunAction(playerId, game, self, false, false);
        if (startBombingRunAction != null) {
            actions.add(startBombingRunAction);
        }

        // Move to related starship/vehicle
        Action moveToRelatedStarshipOrVehicleAction = getMoveToRelatedStarshipOrVehicleAction(playerId, game, self, false, false);
        if (moveToRelatedStarshipOrVehicleAction != null) {
            actions.add(moveToRelatedStarshipOrVehicleAction);
        }

        // Move to related starship/vehicle site
        Action moveToRelatedStarshipOrVehicleSiteAction = getMoveToRelatedStarshipOrVehicleSiteAction(playerId, game, self, false, false);
        if (moveToRelatedStarshipOrVehicleSiteAction != null) {
            actions.add(moveToRelatedStarshipOrVehicleSiteAction);
        }

        // Enter starship/vehicle site
        Action enterStarshipOrVehicleSiteAction = getEnterStarshipOrVehicleSiteAction(playerId, game, self, false, false, false, false, Filters.any);
        if (enterStarshipOrVehicleSiteAction != null) {
            actions.add(enterStarshipOrVehicleSiteAction);
        }

        // Exit starship/vehicle site
        Action exitStarshipOrVehicleSiteAction = getExitStarshipOrVehicleSiteAction(playerId, game, self, false, false, false, false, Filters.any);
        if (exitStarshipOrVehicleSiteAction != null) {
            actions.add(exitStarshipOrVehicleSiteAction);
        }

        // Shuttle
        Action shuttleAction = getShuttleAction(playerId, game, self, false, false, false, Filters.any);
        if (shuttleAction != null) {
            actions.add(shuttleAction);
        }

        // Shuttle characters from site using shuttle vehicle
        Action shuttleFromSiteAction = getShuttleUpUsingShuttleVehicleAction(playerId, game, self);
        if (shuttleFromSiteAction != null) {
            actions.add(shuttleFromSiteAction);
        }

        // Shuttle characters to site using shuttle vehicle
        Action shuttleToSiteAction = getShuttleDownUsingShuttleVehicleAction(playerId, game, self);
        if (shuttleToSiteAction != null) {
            actions.add(shuttleToSiteAction);
        }

        // Embark
        Action embarkAction = getEmbarkAction(playerId, game, self, false, Filters.any);
        if (embarkAction != null) {
            actions.add(embarkAction);
        }

        // Disembark
        Action disembarkAction = getDisembarkAction(playerId, game, self, false, false, Filters.any);
        if (disembarkAction != null) {
            actions.add(disembarkAction);
        }

        // Move between capacity slots
        Action moveBetweenCapacitySlotAction = getMoveBetweenCapacitySlotsAction(playerId, game, self);
        if (moveBetweenCapacitySlotAction != null) {
            actions.add(moveBetweenCapacitySlotAction);
        }

        // Ship-docking
        Action shipdockingAction = getShipdockAction(playerId, game, self, false);
        if (shipdockingAction != null) {
            actions.add(shipdockingAction);
        }

        // Deliver escorted captive to prison
        Action deliverCaptiveAction = getDeliverCaptiveToPrisonAction(playerId, game, self);
        if (deliverCaptiveAction != null) {
            actions.add(deliverCaptiveAction);
        }

        // Take imprisoned captive into custody
        Action takeImprisonedCaptiveIntoCustodyAction = getTakeImprisonedCaptiveIntoCustodyAction(playerId, game, self);
        if (takeImprisonedCaptiveIntoCustodyAction != null) {
            actions.add(takeImprisonedCaptiveIntoCustodyAction);
        }

        // Leave frozen captive unattended
        Action leaveFrozenCaptiveUnattendedAction = getLeaveFrozenCaptiveUnattendedAction(playerId, game, self);
        if (leaveFrozenCaptiveUnattendedAction != null) {
            actions.add(leaveFrozenCaptiveUnattendedAction);
        }

        // Take unattended frozen captive into custody
        Action takeUnattendedFrozenCaptiveIntoCustodyAction = getTakeUnattendedFrozenCaptiveIntoCustodyAction(playerId, game, self);
        if (takeUnattendedFrozenCaptiveIntoCustodyAction != null) {
            actions.add(takeUnattendedFrozenCaptiveIntoCustodyAction);
        }

        return actions;
    }

    /**
     * Determines if there is sufficient capacity to move between capacity slots.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkMoveBetweenCapacitySlotsRequirements(SwccgGame game, PhysicalCard self) {
        PhysicalCard attachedTo = self.getAttachedTo();
        if (attachedTo == null)
            return false;

        if (attachedTo.isCapturedStarship())
            return false;

        if (self.getBlueprint().isMovesLikeCharacter())
            return false;

        if (self.isPilotOf()) {
            return Filters.hasAvailablePassengerCapacity(self).accepts(game, attachedTo);
        }
        if (self.isPassengerOf()) {
            return Filters.hasAvailablePilotCapacity(self).accepts(game, attachedTo);
        }
        if (self.isInCargoHoldAsVehicle()) {
            return Filters.hasAvailableStarfighterOrTIECapacity(self).accepts(game, attachedTo);
        }
        if (self.isInCargoHoldAsStarfighterOrTIE()) {
            return Filters.hasAvailableVehicleCapacity(self).accepts(game, attachedTo);
        }

        return false;
    }

    /**
     * Determines if there is sufficient capacity to move between docked starships.
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkMoveBetweenDockedStarshipRequirements(SwccgGame game, PhysicalCard self) {
        PhysicalCard starship = self.getAttachedTo();
        PhysicalCard atLocation = self.getAtLocation();

        // Attached to starship card
        if (starship != null) {

            if (!self.isPilotOf() && !self.isPassengerOf() && !self.isInCargoHoldAsVehicle() && !self.isInCargoHoldAsStarfighterOrTIE()) {
                return false;
            }
        }
        // At related starship site
        if (atLocation != null) {
            starship = Filters.findFirstFromAllOnTable(game, Filters.relatedStarshipOrVehicle(atLocation));
        }

        if (starship == null) {
            return false;
        }

        PhysicalCard otherShip = starship.getShipdockedWith();
        if (otherShip == null) {
            return false;
        }

        if (self.getBlueprint().isMovesLikeCharacter()) {
            return true;
        }

        if (Filters.canSpotFromTopLocationsOnTable(game,
                Filters.and(Filters.siteOfStarshipOrVehicle(otherShip),
                Filters.starshipSiteToShuttleTransferLandAndTakeOffAtForFreeInsteadOfRelatedStarship(self.getOwner()),
                Filters.notProhibitedFromTarget(self)))) {
            return true;
        }

        if (self.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
            return Filters.or(Filters.hasAvailablePilotCapacity(self), Filters.hasAvailablePassengerCapacity(self)).accepts(game, otherShip);
        }
        else {
            return Filters.or(Filters.hasAvailableVehicleCapacity(self), Filters.hasAvailableStarfighterOrTIECapacity(self)).accepts(game, otherShip);
        }
    }

    /**
     * Determines if the escort can deliver a captive to prison.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkDeliverCaptiveToPrisonRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        if (!playerId.equals(game.getDarkPlayer()) || !self.getOwner().equals(game.getDarkPlayer())
                || self.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return false;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check that card is present at a prison
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self);
        if (location == null || !Filters.prison.accepts(gameState, modifiersQuerying, location)) {
            return false;
        }

        // Check that card is escorting a captive that is not prohibited from moving
        return !Filters.filterCount(gameState.getCaptivesOfEscort(self), game,
                1, Filters.and(Filters.notPreventedFromMoving, Filters.captiveNotProhibitedFromBeingTransferred)).isEmpty();
    }

    /**
     * Determines if the character can take an imprisoned captive into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkTakeImprisonedCaptiveIntoCustodyRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        if (!playerId.equals(game.getDarkPlayer()) || !self.getOwner().equals(game.getDarkPlayer())
                || self.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return false;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check that card is present at a prison
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self);
        if (location == null || !Filters.prison.accepts(gameState, modifiersQuerying, location)) {
            return false;
        }

        // Check that there is an imprisoned captive in the prison that is not prohibited from moving and can be escorted
        return !Filters.filterCount(gameState.getCaptivesInPrison(location), game,
                1, Filters.and(Filters.notPreventedFromMoving, Filters.captiveNotProhibitedFromBeingTransferred, Filters.canBeEscortedBy(self))).isEmpty();
    }

    /**
     * Determines if the escort can leave a 'frozen' captive 'unattended'.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkLeaveFrozenCaptiveUnattendedRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        if (!playerId.equals(game.getDarkPlayer()) || !self.getOwner().equals(game.getDarkPlayer())
                || self.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return false;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check that card is present at a site
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(game.getGameState(), self);
        if (location == null || !Filters.site.accepts(gameState, modifiersQuerying, location)) {
            return false;
        }

        // Check that card is escorting a 'frozen' captive that is not prohibited from moving
        return !Filters.filterCount(gameState.getCaptivesOfEscort(self), game,
                1, Filters.and(Filters.frozenCaptive, Filters.notPreventedFromMoving, Filters.captiveNotProhibitedFromBeingTransferred)).isEmpty();
    }

    /**
     * Determines if the escort can take an 'unattended frozen' captive into custody.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true or false
     */
    private boolean checkTakeUnattendedFrozenCaptiveIntoCustodyRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        if (!playerId.equals(game.getDarkPlayer()) || !self.getOwner().equals(game.getDarkPlayer())
                || self.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
            return false;
        }

        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check that card is present at a site
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(game.getGameState(), self);
        if (location == null || !Filters.site.accepts(gameState, modifiersQuerying, location)) {
            return false;
        }

        // Check that there is an unattended frozen captive that is not prohibited from moving and can be escorted
        return Filters.canSpot(game, null, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.unattendedFrozenCaptive,
                Filters.notPreventedFromMoving, Filters.captiveNotProhibitedFromBeingTransferred, Filters.atLocation(location), Filters.canBeEscortedBy(self)));
    }

    /**
     * Gets the fire weapon actions for each way this card's permanent weapon can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param forFree true if firing weapon for free, otherwise false
     * @param extraForceRequired the extra amount of Force required to perform the fire weapon
     * @param sourceCard true if firing from another action allowing firing, otherwise false
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @param targetedAsCharacter filter for cards may be targeted as characters
     * @param defenseValueAsCharacter defense value to use for cards may be targeted as characters, or null
     * @param fireAtTargetFilter the filter for where the card can be played  @return the fire weapon actions
     * @param ignorePerAttackOrBattleLimit true if per attack/battle firing limit is ignored, otherwise false
     */
    @Override
    protected List<FireWeaponAction> getPermanentWeaponFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        SwccgBuiltInCardBlueprint permWeapon = game.getModifiersQuerying().getPermanentWeapon(game.getGameState(), self);
        if (permWeapon != null) {
            // Include any extra cost to fire permanent weapon if not a repeated firing
            int extraCostForPermanentWeapon = (repeatedFiring ? extraForceRequired : (extraForceRequired + game.getModifiersQuerying().getExtraForceRequiredToFireWeapon(game.getGameState(), self)));
            return permWeapon.getGameTextFireWeaponActions(playerId, game, self, forFree, extraCostForPermanentWeapon, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        }
        return null;
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) can be fired.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @param repeatedFiring true if this is a repeated firing, otherwise false
     * @return true if card can be fired, otherwise false
     */
    @Override
    protected boolean checkFireWeaponRequirements(String playerId, SwccgGame game, PhysicalCard self, boolean repeatedFiring) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // If this card has a permanent weapon, use the fire weapon requirements of that permanent weapon.
        SwccgBuiltInCardBlueprint permWeapon = modifiersQuerying.getPermanentWeapon(gameState, self);
        if (permWeapon != null) {

            // Check if game text is canceled
            if (modifiersQuerying.isGameTextCanceled(gameState, self))
                return false;

            // Check if weapon cannot be used
            if (modifiersQuerying.mayNotBeUsed(gameState, permWeapon))
                return false;

            // Check if weapon cannot be fired
            if (modifiersQuerying.mayNotBeFired(gameState, permWeapon))
                return false;

            // Check if weapon is allowed to fire repeatedly
            if (repeatedFiring && !modifiersQuerying.mayFireWeaponRepeatedly(gameState, self))
                return false;

            // Check that weapon is present at the location
            if (modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self) == null)
                return false;

            return true;
        }

        return false;
    }

    /**
     * Gets a filter for the cards that are valid to use the specified weapon.
     * @param playerId the player
     * @param game the game
     * @param self the weapon
     * @return the filter
     */
    @Override
    public Filter getValidToUseWeaponFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        // For cards that have a permanent weapon, the card itself is a valid user of it.
        return Filters.sameCardId(self);
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) fires during the weapon segment of a battle.
     * @param game the game
     * @param self the card
     * @return true if fires during weapon segment, otherwise false
     */
    protected boolean isFiresDuringWeaponsSegment(SwccgGame game, PhysicalCard self) {
        return true;
    }

    /**
     * Determines if the weapon (or card with a permanent weapon) fires during an Attack Run.
     * @param game the game
     * @param self the card
     * @return true if fires during Attack Run, otherwise false
     */
    protected boolean isFiresDuringAttackRun(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.owner(game.getDarkPlayer()), Filters.or(Filters.Turbolaser_Battery,
                Filters.and(Filters.starship_weapon, Filters.attachedTo(Filters.TIE))), Filters.at(Filters.Death_Star_Trench)).accepts(game, self);
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

        if (self.getZone().isInPlay()) {
            if (GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)
                    && game.getGameState().isCardInPlayActive(self, false, true, false, false, true, false, false, false)) {

                // Break cover (if undercover spy)
                if (self.isUndercover()
                        && !game.getModifiersQuerying().mayNotBreakOwnCoverDuringDeployPhase(game.getGameState(), self)) {
                    PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(game.getGameState(), self);
                    if (location != null && location.getBlueprint().getCardSubtype() == CardSubtype.SITE) {
                        final TopLevelGameTextAction action = new TopLevelGameTextAction(self, self.getCardId());
                        action.setText("'Break cover'");
                        // Perform result(s)
                        action.appendEffect(
                                new BreakCoverEffect(action, self));
                        actions.add(action);
                    }
                }

                // Transfer device or weapon (includes stolen)
                List<Action> transferDeviceOrWeaponActions = getTransferDeviceOrWeaponActions(playerId, game, self, false, Filters.present(self));
                if (transferDeviceOrWeaponActions != null) {
                    actions.addAll(transferDeviceOrWeaponActions);
                }
            }

            // Movement
            if (!game.getModifiersQuerying().isMovedOnlyByOpponent(game.getGameState(), self)) {
                List<Action> moveCardActions = getMoveCardActions(playerId, game, self);
                if (moveCardActions != null) {
                    actions.addAll(moveCardActions);
                }
            }

            // Fire weapon actions from game text
            boolean isInAttack = game.getGameState().isParticipatingInAttack(self);
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)
                    && game.getGameState().isCardInPlayActive(self, false, true, isInAttack, false, false, isInAttack, isInAttack, false)
                    && (game.getGameState().isDuringAttack() || game.getGameState().isDuringBattle())
                    && isFiresDuringWeaponsSegment(game, self)) {
                if (game.getGameState().isDuringAttack()
                        && (isInAttack //permanent weapons
                        || (self.getAttachedTo() != null && game.getGameState().isParticipatingInAttack(self.getAttachedTo())) //weapon cards
                )
                ) {
                    List<FireWeaponAction> fireWeaponActions = getFireWeaponActions(playerId, game, self, false, 0, self, false, Filters.none, null, Filters.participatingInAttack, false);
                    if (fireWeaponActions != null)
                        actions.addAll(fireWeaponActions);
                } else if (game.getGameState().isDuringBattle()){
                    List<FireWeaponAction> fireWeaponActions = getFireWeaponActions(playerId, game, self, false, 0, self, false, Filters.none, null, Filters.any, false);
                    if (fireWeaponActions != null)
                        actions.addAll(fireWeaponActions);
                }
            }
        }
        else if (self.getZone() == Zone.HAND
                && GameConditions.isPhaseForPlayer(game, Phase.DEPLOY, playerId)) {

            // Persona replacement action
            Action personaReplacementAction = getPersonaReplacementAction(playerId, game, self);
            if (personaReplacementAction != null) {
                actions.add(personaReplacementAction);
            }

            // Convert (by replacing character) action
            Action convertCharacterAction = getConvertCharacterAction(playerId, game, self);
            if (convertCharacterAction != null) {
                actions.add(convertCharacterAction);
            }

            // Squadron replacement action
            Action squadronReplacementAction = getSquadronReplacementAction(playerId, game, self);
            if (squadronReplacementAction != null) {
                actions.add(squadronReplacementAction);
            }
        }

        return actions;
    }

    /**
     * Gets the persona replacement action if by the specified player can persona replace with the character.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the persona replacement action
     */
    private Action getPersonaReplacementAction(String playerId, SwccgGame game, PhysicalCard self) {
        // Only unique characters that have a persona defined may be used in persona replacement
        if (!Filters.and(Filters.character, Filters.unique, Filters.hasPersona).accepts(game, self)) {
            return null;
        }

        // Check that a persona of the character is available to be persona-replaced
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        // Check that game text allows character
        if (!checkGameTextDeployRequirements(playerId, game, self, null, false)) {
            return null;
        }

        // Check if uniqueness limit reached in opponent's cards
        if (Filters.canSpotForUniquenessChecking(game, Filters.and(Filters.sameTitle(self), Filters.opponents(self)))) {
            return null;
        }

        float power = modifiersQuerying.getPower(gameState, self);
        float ability = modifiersQuerying.getAbility(gameState, self);
        Collection<PhysicalCard> characterInPlay = Filters.filterActive(game, self,
                SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.your(playerId), Filters.character, Filters.unique, Filters.samePersonaAs(self),
                        Filters.or(Filters.not(Filters.sameTitleAs(self)), Filters.isMissingExpansionIconComparedTo(self)),
                        Filters.powerLessThanOrEqualTo(power), Filters.abilityLessThanOrEqualTo(ability)));
        // If no (or multiple) cards found sharing the same persona then persona replacement cannot be done
        if (characterInPlay.size() != 1) {
            return null;
        }
        // Check that character does not have a permanent weapon that is already on table
        SwccgBuiltInCardBlueprint permanentWeapon = self.getBlueprint().getPermanentWeapon(self);
        if (permanentWeapon != null) {
            if (Filters.canSpot(game, self, SpotOverride.INCLUDE_ALL,
                    Filters.or(Filters.samePersonaAs(permanentWeapon), Filters.hasPermanentWeapon(Filters.samePersonaAs(permanentWeapon))))) {
                return null;
            }
        }
        // Determine the target and if character is not disallowed by own game text
        PhysicalCard characterToReplace = characterInPlay.iterator().next();
        PhysicalCard target = characterToReplace.getAttachedTo() != null ? characterToReplace.getAttachedTo() : characterToReplace.getAtLocation();
        if (target == null) {
            return null;
        }
        if (characterToReplace.isPilotOf()) {
            if (!target.getBlueprint().getValidPilotFilter(playerId, game, target, false).accepts(game, self)) {
                return null;
            }
        }
        if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self) && !Filters.at(Filters.site).accepts(game, characterToReplace)) {
            return null;
        }
        if (!Filters.and(Filters.notProhibitedFromTarget(self), getValidDeployTargetFilterByCheckingGameText(game, self, null, null)).accepts(gameState, modifiersQuerying, target)) {
            return null;
        }

        return new PersonaReplaceCharacterAction(self, characterToReplace);
    }

    /**
     * Gets the convert by replacing character action if by the specified player can convert with the character.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the convert character action
     */
    private Action getConvertCharacterAction(String playerId, SwccgGame game, PhysicalCard self) {
        // Only unique characters that have a persona defined may be used in conversion via replacement
        if (!Filters.and(Filters.character, Filters.unique, Filters.hasPersona).accepts(game, self)) {
            return null;
        }

        // Check that a persona of the character is available to be converted
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        Collection<PhysicalCard> characterInPlay = Filters.filterActive(game, self,  SpotOverride.INCLUDE_UNDERCOVER,
                Filters.and(Filters.opponents(playerId), Filters.character, Filters.unique, Filters.samePersonaAs(self), Filters.mayBeReplacedByOpponent));
        // If no (or multiple) cards found sharing the same persona then conversion cannot be done
        if (characterInPlay.size() != 1) {
            return null;
        }
        // Check that the player doesn't already have this persona on table
        if (Filters.canSpotForUniquenessChecking(game, Filters.and(Filters.samePersonaAs(self), Filters.in(game.getModifiersQuerying().getCardsForPersonaChecking(self.getOwner()))))) {
            return null;
        }
        // Check that the player doesn't already have this persona out of play (only if it's a character, vehicle, or starship)
        List<PhysicalCard> outOfPlay = new LinkedList<>();
        outOfPlay.addAll(gameState.getOutOfPlayPile(playerId));
        outOfPlay.addAll(Filters.filter(modifiersQuerying.getCardsConsideredOutOfPlay(gameState), game, Filters.your(playerId)));
        for (Persona persona : modifiersQuerying.getPersonas(gameState, self)) {
            if (Filters.canSpot(outOfPlay, game, 1, Filters.and(Filters.your(playerId), persona, Filters.or(Filters.character, Filters.vehicle, Filters.starship))))
                return null;
        }
        // Check that character does not have a permanent weapon that is already on table
        SwccgBuiltInCardBlueprint permanentWeapon = self.getBlueprint().getPermanentWeapon(self);
        if (permanentWeapon != null) {
            if (Filters.canSpot(game, self, SpotOverride.INCLUDE_ALL, Filters.or(Filters.samePersonaAs(permanentWeapon), Filters.hasPermanentWeapon(Filters.samePersonaAs(permanentWeapon))))) {
                return null;
            }
        }
        // Determine the target and if character is not disallowed there
        PhysicalCard characterToReplace = characterInPlay.iterator().next();
        PhysicalCard target = characterToReplace.getAttachedTo() != null ? characterToReplace.getAttachedTo() : characterToReplace.getAtLocation();
        if (target == null) {
            return null;
        }
        if (self.getBlueprint().isOnlyDeploysAsUndercoverSpy(game, self) && !Filters.at(Filters.site).accepts(game, characterToReplace)) {
            return null;
        }
        if (!Filters.notProhibitedFromTarget(self).accepts(gameState, modifiersQuerying, target)) {
            return null;
        }

        return new ConvertByReplacingCharacterAction(self, characterToReplace);
    }

    /**
     * Gets the squadron replacement action if by the specified player can replace starfighters with the squadron.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the persona replacement action
     */
    private Action getSquadronReplacementAction(String playerId, SwccgGame game, PhysicalCard self) {
        // Only squadrons that replace instead of deploy may replace
        if (!Filters.or(Filters.squadron, Filters.character).accepts(game, self)) {
            return null;
        }
        if (game.getModifiersQuerying().isUniquenessOnTableLimitReached(game.getGameState(), self)) {
            return null;
        }

        Filter replacementFilter = self.getBlueprint().getReplacementFilterForSquadron();
        Integer replacementCount = self.getBlueprint().getReplacementCountForSquadron();
        if (replacementFilter == null || replacementCount == null) {
            return null;
        }

        // Find locations that have the required number of starfighters present
        replacementFilter = Filters.and(Filters.your(playerId), Filters.or(Filters.starfighter, Filters.character), replacementFilter);
        boolean foundLocation = false;
        Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.wherePresent(self, replacementFilter));
        for (PhysicalCard location : locations) {
            if (Filters.canSpot(game, self, replacementCount, Filters.and(replacementFilter, Filters.present(location)))) {
                foundLocation = true;
                break;
            }
        }
        if (!foundLocation) {
            return null;
        }

        return new ReplacementAction(self);
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

        if (self.getZone().isInPlay()) {

            // Fire weapon actions from game text
            if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)
                    && isFiresDuringAttackRun(game, self)
                    && game.getGameState().isCardInPlayActive(self, false, false, false, false, false, false, false, false)) {
                AttackRunState attackRunState = (AttackRunState) game.getGameState().getEpicEventState();

                //check for wingmen that are still on table
                List<PhysicalCard> wingmen = new LinkedList<>();
                for(PhysicalCard wingman:attackRunState.getWingmen()) {
                    if(wingman.getZone().isInPlay())
                        wingmen.add(wingman);
                }
                Filter validTarget = !wingmen.isEmpty() ? Filters.wingmen_in_Attack_Run : Filters.lead_starfighter_in_Attack_Run;

                List<FireWeaponAction> fireWeaponActions = getFireWeaponActions(playerId, game, self, false, 0, self, false, Filters.none, null, validTarget, false);
                if (fireWeaponActions != null)
                    actions.addAll(fireWeaponActions);
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
    public final List<Action> getOpponentsCardTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getOpponentsCardTopLevelActions(playerId, game, self);

        if (self.getZone().isInPlay()) {
            // Drawing asteroid destiny (against opponent's starship)
            if (GameConditions.canDrawAsteroidDestinyAgainstStarship(game, playerId, self))
                actions.add(new DrawAsteroidDestinyAction(playerId, self));

            // If only opponent can move card, then gather "move card actions" here
            if (game.getModifiersQuerying().isMovedOnlyByOpponent(game.getGameState(), self)) {
                List<Action> moveCardActions = getMoveCardActions(playerId, game, self);
                if (moveCardActions != null) {
                    actions.addAll(moveCardActions);
                }
            }
        }

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
        List<TriggerAction> actions = super.getRequiredAfterTriggers(game, effectResult, self);

        // Drawing asteroid destiny (against opponent's starship)
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, self.getOwner())) {
            String opponent = game.getOpponent(self.getOwner());
            if (GameConditions.canDrawAsteroidDestinyAgainstStarship(game, opponent, self)) {

                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(new DrawAsteroidDestinyRule(), self);
                action.setText("Draw asteroid destiny");
                action.skipInitialMessageAndAnimation();
                action.appendEffect(
                        new DrawAsteroidDestinyEffect(action, opponent, self));
                actions.add(action);
            }
        }

        // Dark side starfighter at Death Star II sector moving toward Death Star II system
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.MOVE, self.getOwner())) {
            String playerId = self.getOwner();
            if (game.getDarkPlayer().equals(playerId)
                    && Filters.and(Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)).accepts(game, self)
                    && !game.getModifiersQuerying().hasPerformedRegularMoveThisTurn(self)
                    && !Filters.canSpot(game, null, Filters.and(Filters.owner(game.getLightPlayer()), Filters.starfighter, Filters.at(Filters.Death_Star_II_sector)))) {
                Action sectorMovementAction = getMoveUsingSectorMovementAction(playerId, game, self, true, false, false, false, false, Filters.any);
                if (sectorMovementAction != null) {

                    RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(new DeathStarIISectorRule(), self);
                    action.setText("Move using sector movement");
                    action.appendEffect(
                            new StackActionEffect(action, sectorMovementAction));
                    actions.add(action);
                }
            }
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
        List<TriggerAction> actions = super.getOptionalAfterTriggers(playerId, game, effectResult, self);

        // Check for 'react' actions
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, game.getOpponent(playerId))
                || TriggerConditions.battleInitiated(game, effectResult, game.getOpponent(playerId))) {

            if (self.getZone() == Zone.STACKED) {
                if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                    // Deploy other cards as 'react'
                    List<TriggerAction> deployOtherCardsAsReactActions = getDeployOtherCardsAsReactAction(playerId, game, self);
                    if (deployOtherCardsAsReactActions != null) {
                        for (TriggerAction deployOtherCardsAsReactAction : deployOtherCardsAsReactActions) {
                            actions.add(deployOtherCardsAsReactAction);
                        }
                    }
                }
            }

            if (self.getZone().isInPlay()) {
                boolean inPlayActive = game.getGameState().isCardInPlayActive(self, false, true, false, false, false, false, false, false);

                if (!game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                    if (inPlayActive) {

                        // If device, need to check that it can be used by the card it is attached to.
                        if (self.getBlueprint().getCardCategory() != CardCategory.DEVICE
                                || !game.getModifiersQuerying().mayNotBeUsed(game.getGameState(), self)
                                || self.getAttachedTo() == null
                                || Filters.canUseDevice(self).accepts(game, self.getOwner().equals(self.getAttachedTo().getOwner()) ? self.getAttachedTo() : self)) {

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
                    }
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
    public final List<Action> getOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<Action> actions = super.getOptionalAfterActions(playerId, game, effectResult, self);

        // Check for 'react' actions
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, game.getOpponent(playerId))
                || TriggerConditions.battleInitiated(game, effectResult, game.getOpponent(playerId))) {

            if (self.getZone().isInPlay()) {
                // Move as 'react'
                Action moveAsReactAction = getMoveAsReactAction(playerId, game, self, null, Filters.any);
                if (moveAsReactAction != null) {
                    actions.add(moveAsReactAction);
                }
            }
            else {
                // Deploy as 'react'
                Action deployAsReactAction = getDeployAsReactAction(playerId, game, self, null, Filters.any);
                if (deployAsReactAction != null) {
                    actions.add(deployAsReactAction);
                }
            }
        }

        return actions;
    }

    //
    // This section defines common methods that are used by individual cards to define actions, etc.
    // that are defined by a card's game text. This is done to avoid a lot of super.foo() methods
    // from needing to be called in the individual cards.
    //

    /**
     * This method is overridden by individual cards to define the permanent weapon.
     */
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return null;
    }

    /**
     * This method is overridden by individual cards to define the permanent pilots and permanent astromechs aboard.
     * @return the permanents aboard
     */
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.emptyList();
    }

    @Override
    public final Integer getReplacementCountForSquadron() {
        return _replacementCountForSquadron;
    }

    @Override
    public final Filter getReplacementFilterForSquadron() {
        return _replacementFilterForSquadron;
    }

    /**
     * Sets the filter and number of starfighters present at a location that are replaced by the squadron.
     *
     * @param count  the number of starfighters
     * @param filter the filter
     */
    protected final void setReplacementForSquadron(int count, Filter filter) {
        _replacementCountForSquadron = count;
        _replacementFilterForSquadron = filter;
    }
}
