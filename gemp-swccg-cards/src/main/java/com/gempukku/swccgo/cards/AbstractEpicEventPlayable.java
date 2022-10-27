package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.cards.actions.PlayChoiceAction;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The abstract class providing the common implementation for Epic Events that are played.
 */
public abstract class AbstractEpicEventPlayable extends AbstractSwccgCardBlueprint {

    /**
     * Creates a blueprint for an Epic Event that is played.
     * @param side the side of the Force
     * @param title the card title
     * @param expansionSet the expansionSet
     * @param rarity the rarity
     */
    protected AbstractEpicEventPlayable(Side side, String title, ExpansionSet expansionSet, Rarity rarity) {
        super(side, 0f, title, Uniqueness.UNRESTRICTED, expansionSet, rarity);
        setCardCategory(CardCategory.EPIC_EVENT);
        addCardType(CardType.EPIC_EVENT);
        addIcon(Icon.EPIC_EVENT);
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
    protected final boolean checkPlayRequirements(String playerId, SwccgGame game, PhysicalCard self, DeploymentRestrictionsOption deploymentRestrictionsOption, PlayCardOption playCardOption, ReactActionOption reactActionOption) {
        return super.checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, playCardOption, reactActionOption)
                && reactActionOption == null
                && checkGameTextPlayRequirements(playerId, game, self);
    }

    /**
     * Determines if this type of card is deployed or played
     * @return true if card is "deployed", false if card is "played"
     */
    @Override
    public final boolean isCardTypeDeployed() {
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

        if (self.getZone() != Zone.STACKED && checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayEpicEventAction> actionList1 = getGameTextTopLevelActions(playerId, game, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
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
    public final List<Action> getOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<Action> actions = new LinkedList<Action>();

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayEpicEventAction> actionList1 = getGameTextOptionalBeforeActions(playerId, game, effect, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
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
        List<Action> actions = new LinkedList<Action>();

        if (checkPlayRequirements(playerId, game, self, null, null, null)) {
            List<PlayEpicEventAction> actionList1 = getGameTextOptionalAfterActions(playerId, game, effectResult, self);
            if (actionList1 != null) {
                actions.addAll(actionList1);
            }
        }

        return actions;
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
        List<PlayCardAction> playCardActions = getPlayCardActions(playerId, game, self, sourceCard, forFree, changeInCost, deploymentOption, deploymentRestrictionsOption, null, reactActionOption, cardToDeployWith, cardToDeployWithForFree, cardToDeployWithChangeInCost, deployTargetFilter, specialLocationConditions);
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
        if (!checkPlayRequirements(playerId, game, self, deploymentRestrictionsOption, null, reactActionOption))
            return Collections.emptyList();

        List<PlayCardAction> playCardActions = new LinkedList<PlayCardAction>();

        List<PlayEpicEventAction> actionList1 = getGameTextTopLevelActions(playerId, game, self);
        if (actionList1 != null) {
            playCardActions.addAll(actionList1);
        }

        return playCardActions;
    }

    /**
     * Gets the required "before" triggers for the specified effect.
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self) {
        // Not used for playable Epic Events
        return null;
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
    public final List<TriggerAction> getOptionalBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Not used for playable Epic Events
        return null;
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
    public final List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Not used for playable Epic Events
        return null;
    }

    /**
     * Gets the required "after" triggers for the specified effect result.
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the trigger actions
     */
    @Override
    public final List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        // Not used for playable Epic Events
        return null;
    }

    /**
     * This method is overridden by individual cards to specify card play requirements.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return true if requirements met, otherwise false
     */
    protected boolean checkGameTextPlayRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return true;
    }

    /**
     * This method is overridden by individual cards to specify top-level actions.
     * @param playerId the player
     * @param game the game
     * @param self the card
     * @return the actions, or null
     */
    protected List<PlayEpicEventAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "before" play card actions to the specified
     * effect that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effect the effect
     * @param self the card
     * @return the play card actions, or null
     */
    protected List<PlayEpicEventAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        return null;
    }

    /**
     * This method is overridden by individual cards to specify optional "after" play card actions to the specified effect
     * result that can be performed by the specified player. This includes actions like playing the card from hand.
     * @param playerId the player
     * @param game the game
     * @param effectResult the effect result
     * @param self the card
     * @return the play card actions, or null
     */
    protected List<PlayEpicEventAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        return null;
    }
}
