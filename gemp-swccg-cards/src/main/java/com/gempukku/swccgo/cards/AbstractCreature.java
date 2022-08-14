package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayCreatureAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.DetachParasiteEffect;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class providing the common implementation for creatures.
 */
public abstract class AbstractCreature extends AbstractDeployable {
    private float _landspeed;
    private float _forfeit;
    private float _defenseValue;
    private Float _ferocity;

    /**
     * Creates a blueprint for a creature.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param ferocity the ferocity
     * @param defenseValue the defense value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractCreature(Side side, float destiny, float deployCost, float ferocity, float defenseValue, float forfeit, String title) {
        this(side, destiny, deployCost, ferocity, defenseValue, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a creature.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param ferocity the ferocity
     * @param defenseValue the defense value
     * @param forfeit the forfeit value
     * @param title the card title
     */
    protected AbstractCreature(Side side, float destiny, float deployCost, Float ferocity, float defenseValue, float forfeit, String title) {
        this(side, destiny, deployCost, ferocity, defenseValue, forfeit, title, null);
    }

    /**
     * Creates a blueprint for a creature.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param ferocity the ferocity
     * @param defenseValue the defense value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCreature(Side side, float destiny, float deployCost, float ferocity, float defenseValue, float forfeit, String title, Uniqueness uniqueness) {
        this(side, destiny, deployCost, (Float) ferocity, defenseValue, forfeit, title, uniqueness);
    }

    /**
     * Creates a blueprint for a creature.
     * @param side the side of the Force
     * @param destiny the destiny value
     * @param deployCost the deploy cost
     * @param ferocity the ferocity
     * @param defenseValue the defense value
     * @param forfeit the forfeit value
     * @param title the card title
     * @param uniqueness the uniqueness
     */
    protected AbstractCreature(Side side, float destiny, float deployCost, Float ferocity, float defenseValue, float forfeit, String title, Uniqueness uniqueness) {
        super(side, destiny, null, deployCost, title, uniqueness);
        _ferocity = ferocity;
        _defenseValue = defenseValue;
        _landspeed = 1;
        _forfeit = forfeit;
        setCardCategory(CardCategory.CREATURE);
        addCardType(CardType.CREATURE);
        addIcon(Icon.CREATURE);
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
        if (habitatIncludesAboardStarship())
            return Filters.and(Filters.or(Filters.location, Filters.starship), getGameTextHabitatFilter(playerId, game, self));
        else
            return Filters.and(Filters.location, getGameTextHabitatFilter(playerId, game, self));
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
        return Filters.and(Filters.location, getGameTextHabitatFilter(playerId, game, self));
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

            Filter completeTargetFilter = Filters.and(deployTargetFilter, getValidDeployTargetFilter(playerId, game, self, sourceCard, null, forFree, changeInCost, deploymentRestrictionsOption, deployAsCaptiveOption, reactActionOption, false, false));

            // Check that a valid target to deploy to can be found
            if (Filters.canSpot(game, self, TargetingReason.TO_BE_DEPLOYED_ON, completeTargetFilter)) {
                playCardActions.add(new PlayCreatureAction(game, sourceCard, self, forFree, changeInCost, completeTargetFilter));
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
     * Determines if this has ferocity attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean hasFerocityAttribute() {
        return true;
    }

    /**
     * Gets the ferocity.
     * @return the ferocity
     */
    @Override
    public final Float getFerocity() {
        return _ferocity;
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
     * Determines if this has a special defense value attribute.
     * @return true if has attribute, otherwise false
     */
    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    /**
     * Gets the special defense value.
     * @return the special defense value
     */
    @Override
    public final float getSpecialDefenseValue() {
        return _defenseValue;
    }

    /**
     * This method is overridden by individual cards to specify the filter for the creature's habitat.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the filter
     */
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.any;
    }

    /**
     * Gets the habitat filter for the specified creature card.
     * @param game the game
     * @param self the card
     * @return the filter
     */
    @Override
    public Filter getHabitatFilter(final SwccgGame game, final PhysicalCard self) {
        return getGameTextHabitatFilter(self.getOwner(), game, self);
    }

    /**
     * Determines if this creature's habitat includes aboard a starship.
     * @return true if has attribute, otherwise false
     */
    @Override
    public boolean habitatIncludesAboardStarship() {
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
    public final List<Action> getTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<Action> actions = super.getTopLevelActions(playerId, game, self);

        // Creatures may attack a non-creature if they are present with a valid target during owner's battle phase
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.BATTLE)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            if (!modifiersQuerying.hasParticipatedInAttackOnNonCreatureThisTurn(self)) {
                PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self);
                if (location != null) {
                    if (!modifiersQuerying.mayNotInitiateAttacksAtLocation(gameState, location, playerId)
                            && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, Filters.nonCreatureCanBeAttackedByCreature(self, false))) {
                        actions.add(new InitiateAttackNonCreatureAction(self));
                    }
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
    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<TriggerAction> actions = super.getRequiredAfterTriggers(game, effectResult, self);
        String playerId = self.getOwner();

        // Creatures attack a non-creature if they are present with a valid target at end of owner's battle phase
        // (unless parasite is attached to a host)
        if (TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.BATTLE, playerId)
                && !GameConditions.isDuringAttack(game)
                && !GameConditions.isDuringBattle(game)
                && self.getAttachedTo() == null) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            if (!modifiersQuerying.hasParticipatedInAttackOnNonCreatureThisTurn(self)) {
                PhysicalCard location = modifiersQuerying.getLocationThatCardIsPresentAt(gameState, self);
                if (location != null) {
                    if (!modifiersQuerying.mayNotInitiateAttacksAtLocation(gameState, location, playerId)
                            && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, Filters.nonCreatureCanBeAttackedByCreature(self, false))) {
                        actions.add(new InitiateAttackNonCreatureAction(self));
                    }
                }
            }
        }

        // Check condition(s)
        PhysicalCard host = self.getAttachedTo();
        if (host != null && TriggerConditions.justEatenBy(game, effectResult, host, Filters.any)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, self.getCardId());
            action.setText("Detach from host");
            action.setActionMsg("Detach " + GameUtils.getCardLink(self) + " from  " + GameUtils.getCardLink(host));
            // Perform result(s)
            action.appendEffect(
                    new DetachParasiteEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}
